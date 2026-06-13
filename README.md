# Gate-2. Вариант B — Фильтр компаний по отрасли

**Задание.** На списке компаний — фильтр по отрасли: select из активных записей справочника `INDUSTRY` (в порядке `sort_order`), комбинируемый с пагинацией.

**AC.** Given компании трёх отраслей → When фильтрую по одной → Then только её компании; листание сохраняет фильтр; в select — только активные записи в порядке из контракта справочников. Фильтрует БД. MockMvc-тест фильтра и состава select.

---

## Часть 1: Как думать как разработчик

### Общий алгоритм

Когда получаешь новую задачу, думай в таком порядке:

1. **Понять задачу** — перевести с "технического" на человеческий
2. **Посмотреть, что уже есть** — не изобретай велосипед
3. **Начать с данных** — что нужно хранить?
4. **Двигаться снизу вверх:**
   - Модель данных (что храним?)
   - Хранилище (как ищем?)
   - Бизнес-логика (что делаем с данными?)
   - Веб-слой (как обрабатываем HTTP?)
   - UI (как показываем?)
5. **Написать тесты** — как проверим, что работает?

### Почему именно так?

**Почему начинаем с данных?**
Потому что данные — основа. Если нет данных, нечего обрабатывать, нечего показывать. Сначала определяем, **что** мы храним, потом **как** с этим работаем.

**Почему снизу вверх?**
Потому что каждый слой зависит от предыдущего:

- Контроллер использует сервис
- Сервис использует репозиторий
- Репозиторий использует модель

Если начнёшь с контроллера, придётся постоянно возвращаться вниз и менять всё.

---

## Часть 2: Разбор задания

### Исходное задание

> "На списке компаний — фильтр по отрасли: select из активных записей справочника INDUSTRY (в порядке sort_order), комбинируемый с пагинацией."

### Перевод на человеческий

**Что нужно сделать:**

1. На странице со списком лидов добавить выпадающий список (select) с отраслями
2. Отрасли брать из справочника (не из кода, а из отдельного "хранилища")
3. Справочник знает:
   - Какие отрасли существуют
   - Какие из них "активные" (можно использовать)
   - В каком порядке их показывать
4. Когда пользователь выбирает отрасль — показываются только лиды этой отрасли
5. Фильтр по отрасли работает вместе с фильтром по статусу (NEW/CONTACTED/QUALIFIED)

**Пример:**

- Пользователь выбрал статус "NEW" — видит только новых лидов
- Потом выбрал отрасль "IT" — видит только новых лидов в IT
- Если уберёт фильтр по статусу — видит всех лидов в IT

---

## Часть 3: Пошаговая реализация

### Шаг 1: Понять задачу и посмотреть, что уже есть

**Куда смотреть:**

- В проект — какие файлы есть
- В `LeadController` — как работает фильтр по статусу
- В `Lead` record — какие поля есть у лида

**Что проверяем:**

1. Есть ли уже фильтр? — Да, по статусу (`?status=NEW`)
2. Есть ли отрасль у лида? — Нет, только `id`, `contact`, `company`, `status`
3. Есть ли справочник отраслей? — Нет

**Вывод:** нужно добавить отрасль в модель и создать справочник.

---

### Шаг 2: Добавить отрасль в модель данных

**Куда смотреть:** `src/main/java/ru/mentee/power/crm/domain/`

**Почему сюда:** модель данных — это основа. Чтобы фильтровать по отрасли, у лида должна быть отрасль.

#### 2.1. Создаём enum `LeadIndustry`

```java
package ru.mentee.power.crm.domain;

public enum LeadIndustry {
  IT,
  FINANCE,
  RETAIL
}
```

**Почему enum, а не String?**

- Type safety: нельзя написать "ITE" вместо "IT" — компилятор поймает ошибку
- Автодополнение в IDE: пишешь `LeadIndustry.` — видишь список
- Сериализация в URL: `?industry=IT` работает автоматически

**Почему не отдельная сущность (entity)?**

Потому что отраслей всего 3, и они фиксированы. Если бы отраслей было 100+ и они менялись бы часто — тогда да, отдельная таблица в БД. Но для 3 значений enum проще.

#### 2.2. Добавляем поле `industry` в `Lead`

```java
public record Lead(
    UUID id,
    Contact contact,
    String company,
    LeadStatus status,
    LeadIndustry industry  // новое поле
) {
  public Lead {
    // ... существующие проверки
    if (industry == null) {
      throw new IllegalArgumentException("Industry не должно быть null");
    }
  }
}
```

**Почему record, а не класс?**

Record — это immutable объект с автоматическими `equals()`, `hashCode()`, `toString()`. Меньше кода, меньше ошибок.

**Почему поле в конце?**

Порядок полей в record — часть API. Логичный порядок: идентификатор, контакт, данные, статусы.

**Что сломается?**

Все тесты, которые создают `Lead` напрямую:

```java
// Было:
new Lead(id, contact, "Company", LeadStatus.NEW)

// Стало:
new Lead(id, contact, "Company", LeadStatus.NEW, LeadIndustry.IT)
```

Это нормально — breaking change при изменении модели неизбежен.

---

### Шаг 3: Создать справочник отраслей

**Куда смотреть:** `src/main/java/ru/mentee/power/crm/domain/`

**Почему сюда:** справочник — это данные, не бизнес-логика. Бизнес-логика в `service/`, данные в `domain/`.

#### 3.1. Создаём интерфейс `Dictionary<T>`

```java
package ru.mentee.power.crm.domain;

import java.util.List;

public interface Dictionary<T> {
  List<T> getActiveItems();
  boolean isActive(T item);
}
```

**Почему интерфейс?**

- Переиспользование: завтра добавишь справочник статусов — просто создашь `StatusDictionary implements Dictionary<LeadStatus>`
- Каждый справочник может иметь свою специфику (у отраслей может быть `parent_id`, у статусов — нет)
- С БД будет проще: каждый справочник — отдельная таблица с разными полями

**Почему generic `<T>`?**

Чтобы интерфейс работал с любым типом: `Dictionary<LeadIndustry>`, `Dictionary<LeadStatus>`, и т.д.

#### 3.2. Создаём реализацию `IndustryDictionary`

```java
package ru.mentee.power.crm.domain;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class IndustryDictionary implements Dictionary<LeadIndustry> {

  private final Map<LeadIndustry, IndustryEntry> entries;

  public IndustryDictionary() {
    this.entries = Map.of(
        LeadIndustry.IT, new IndustryEntry(LeadIndustry.IT, 1, true),
        LeadIndustry.FINANCE, new IndustryEntry(LeadIndustry.FINANCE, 2, true),
        LeadIndustry.RETAIL, new IndustryEntry(LeadIndustry.RETAIL, 3, true)
    );
  }

  @Override
  public List<LeadIndustry> getActiveItems() {
    return entries.values().stream()
        .filter(IndustryEntry::active)
        .sorted(Comparator.comparingInt(IndustryEntry::sortOrder))
        .map(IndustryEntry::industry)
        .toList();
  }

  @Override
  public boolean isActive(LeadIndustry industry) {
    IndustryEntry entry = entries.get(industry);
    return entry != null && entry.active();
  }

  private record IndustryEntry(LeadIndustry industry, int sortOrder, boolean active) {
  }
}
```

**Почему `@Component`, а не `@Service`/`@Repository`?**

Spring имеет специализированные стереотипные аннотации:

| Аннотация | Когда использовать |
|---|---|
| `@Controller` | Класс обрабатывает HTTP-запросы (`@GetMapping`, `@PostMapping`) |
| `@Service` | Класс содержит бизнес-логику (обработка, валидация, трансформация) |
| `@Repository` | Класс работает с хранилищем данных (SQL, HashMap, файл) |
| `@Component` | Класс не подходит ни под одну категорию выше |

Справочник — не бизнес-логика, не хранилище лидов, не контроллер. Это "просто Spring-бин, который делает свою работу".

**`@Bean` vs `@Component`:**

- `@Component` — "Spring, найди этот класс и создай бин автоматически" (на уровне класса)
- `@Bean` — "Spring, вот метод, который создаёт бин" (на уровне метода, в `@Configuration` классе)

```java
// @Component — Spring сам сканирует и создаёт
@Component
public class IndustryDictionary { ... }

// @Bean — мы вручную говорим, как создать
@Configuration
public class AppConfig {
  @Bean
  public IndustryDictionary industryDictionary() {
    return new IndustryDictionary();
  }
}
```

Для нашего случая `@Component` проще — не нужен отдельный конфигурационный класс.

**Что такое `sort_order`?**

Это **порядок отображения**, а не счётчик добавления.

Пример:

```
IT      → sort_order = 1  (показываем первой)
FINANCE → sort_order = 2  (показываем второй)
RETAIL  → sort_order = 3  (показываем третьей)
```

Если удалить FINANCE:

```
IT      → sort_order = 1
RETAIL  → sort_order = 3
```

Порядок всё равно правильный: IT (1) перед RETAIL (3). Пропуски в числах не влияют на сортировку. Это как места в кинотеатре: если место 2 пустует, места 1 и 3 всё равно идут по порядку.

**Что такое `active`?**

Флаг "показывать ли отрасль в select". Это не про "есть ли лиды этой отрасли".

Зачем нужен:

- Представь, что отрасль "Криптовалюты" устарела
- Ты не хочешь удалять её из справочника (есть лиды этой отрасли в истории)
- Но не хочешь, чтобы новые лиды создавались с этой отраслью
- Решение: `active = false` — отрасль не показывается в select для новых лидов, но старые лиды её видят

**Как работает сортировка?**

```java
.sorted(Comparator.comparingInt(IndustryEntry::sortOrder))
```

Разбор по частям:

- `Comparator` — интерфейс Java для сравнения двух объектов
- `comparingInt()` — фабричный метод, который создаёт Comparator по числовому полю
- `IndustryEntry::sortOrder` — method reference (ссылка на метод)

Эквивалент без method reference:

```java
.sorted((a, b) -> Integer.compare(a.sortOrder(), b.sortOrder()))
```

---

### Шаг 4: Добавить метод поиска в репозиторий

**Куда смотреть:** `src/main/java/ru/mentee/power/crm/repository/LeadRepository.java`

**Почему сюда:** репозиторий — это хранилище. Если нужно искать/фильтровать — метод идёт в репозиторий.

#### Что делаем

```java
public List<Lead> findByIndustry(LeadIndustry industry) {
  return storage.values().stream()
      .filter(lead -> lead.industry() == industry)
      .toList();
}
```

**Почему stream filter, а не HashMap-индекс?**

Вариант с индексом:

```java
private final Map<LeadIndustry, Set<UUID>> industryIndex = new HashMap<>();

public Lead save(Lead lead) {
  storage.put(lead.id(), lead);
  industryIndex
      .computeIfAbsent(lead.industry(), k -> new HashSet<>())
      .add(lead.id());  // нужно обновлять индекс
  return lead;
}

public void delete(UUID id) {
  Lead lead = storage.remove(id);
  if (lead != null) {
    Set<UUID> ids = industryIndex.get(lead.industry());
    if (ids != null) {
      ids.remove(id);  // нужно обновлять индекс
    }
  }
}
```

**Проблемы с индексом:**

- `save()` усложняется — нужно обновлять индекс
- `delete()` усложняется — нужно удалять из индекса
- Если забудешь обновить индекс в одном месте — баг (данные в storage и индексе рассинхронизируются)
- Все тесты `LeadRepositoryTest` нужно переписывать

**Вариант без индекса (наш):**

```java
public List<Lead> findByIndustry(LeadIndustry industry) {
  return storage.values().stream()
      .filter(lead -> lead.industry() == industry)
      .toList();
}
```

`save()` и `delete()` **не меняются вообще**. Они ничего не знают про отрасль.

**"Поддерживать индекс"** = писать дополнительный код для синхронизации индекса при каждом изменении данных. Без индекса этот код не нужен.

**Trade-off:**

- Без индекса: `findByIndustry()` = O(n) — проходим по всем лидам
- С индексом: `findByIndustry()` = O(1) — сразу достаём по ключу

Для 5-100 лидов разница незаметна (микросекунды). Для 100,000+ лидов — индекс нужен.

**Правило:** сначала сделай просто, потом оптимизируй (если нужно).

---

### Шаг 5: Добавить методы в сервис

**Куда смотреть:** `src/main/java/ru/mentee/power/crm/service/LeadService.java`

**Почему сюда:** сервис — посредник между контроллером и репозиторием. Контроллер не должен напрямую вызывать репозиторий.

#### Что делаем

```java
@Service
public class LeadService {
  private final LeadRepository repository;
  private final IndustryDictionary industryDictionary;  // новое

  public LeadService(LeadRepository repository, IndustryDictionary industryDictionary) {
    this.repository = repository;
    this.industryDictionary = industryDictionary; // DI
  }

  public List<Lead> findByIndustry(LeadIndustry industry) {
    return repository.findByIndustry(industry);  // делегируем репозиторию
  }

  public List<LeadIndustry> getActiveIndustries() {
    return industryDictionary.getActiveItems();  // делегируем словарю
  }
}
```

---

### Шаг 6: Обновить контроллер

**Куда смотреть:** `src/main/java/ru/mentee/power/crm/spring/controller/LeadController.java`

**Почему сюда:** контроллер — точка входа HTTP-запросов. `@RequestParam` — параметр из URL.

#### Что делаем

```java
@GetMapping("/leads")
public String showLeads(
    @RequestParam(required = false) LeadStatus status,
    @RequestParam(required = false) LeadIndustry industry,  // новое
    Model model) {

  List<Lead> leads = leadService.findAll();

  if (status != null) {
    leads = leadService.findByStatus(status);
  }

  if (industry != null) {
    List<Lead> byIndustry = leadService.findByIndustry(industry);
    leads = byIndustry.stream()
        .filter(lead -> status == null || lead.status() == status)
        .toList();
  }

  model.addAttribute("leads", leads);
  model.addAttribute("currentStatusFilter", status);
  model.addAttribute("currentIndustryFilter", industry);
  model.addAttribute("industries", leadService.getActiveIndustries());

  return "leads/list";
}
```

**Почему комбо-фильтр в контроллере, а не `findByStatusAndIndustry()` в сервисе?**

Можно было добавить:

```java
public List<Lead> findByStatusAndIndustry(LeadStatus status, LeadIndustry industry) { ... }
```

**Проблемы:**

- Взрыв комбинаций: завтра добавишь фильтр по городу — нужен `findByStatusAndIndustryAndCity()`
- Дублирование: логика уже есть в `findByStatus()` и `findByIndustry()`

**Правило:** не плоди методы — комбинируй существующие.

#### Как работает комбо-фильтр — 4 сценария

**Сценарий 1: `GET /leads` (нет фильтров)**

```
status = null, industry = null

Шаг 1: leads = findAll() → все лиды (5 шт.)
Шаг 2: status == null → пропускаем
Шаг 3: industry == null → пропускаем

Результат: все 5 лидов
```

**Сценарий 2: `GET /leads?status=NEW`**

```
status = NEW, industry = null

Шаг 1: leads = findAll() → все лиды (5 шт.)
Шаг 2: status != null → leads = findByStatus(NEW) → 2 лида
Шаг 3: industry == null → пропускаем

Результат: 2 NEW лида
```

**Сценарий 3: `GET /leads?industry=IT`**

```
status = null, industry = IT

Шаг 1: leads = findAll() → все лиды (5 шт.)
Шаг 2: status == null → пропускаем
Шаг 3: industry != null →
  byIndustry = findByIndustry(IT) → 3 лида
  filter: status == null → все проходят

Результат: 3 IT лида
```

**Сценарий 4: `GET /leads?status=NEW&industry=IT` (комбо)**

```
status = NEW, industry = IT

Шаг 1: leads = findAll() → все лиды (5 шт.)
Шаг 2: status != null → leads = findByStatus(NEW) → 2 лида (ivan, alex)
Шаг 3: industry != null →
  byIndustry = findByIndustry(IT) → 3 лида (ivan, alex, dmitry)
  filter: status != null → проверяем lead.status() == NEW
    ivan:   status == NEW        → проходит
    alex:   status == NEW        → проходит
    dmitry: status == CONTACTED  → не проходит

Результат: 2 лида (ivan, alex) — IT + NEW
```

---

### Шаг 7: Обновить шаблон

**Куда смотреть:** `src/main/jte/leads/list.jte`

**Почему сюда:** шаблон — это HTML с данными из модели.

#### 7.1. Обновляем `@param` декларацию

```jte
@param java.util.List<ru.mentee.power.crm.domain.Lead> leads
@param ru.mentee.power.crm.domain.LeadStatus currentStatusFilter
@param java.util.List<ru.mentee.power.crm.domain.LeadIndustry> industries
@param ru.mentee.power.crm.domain.LeadIndustry currentIndustryFilter
```

**Почему `@param`?**

JTE — type-safe шаблонизатор. В отличие от Thymeleaf (где переменные — Object), JTE **знает типы** на этапе компиляции.

Если контроллер передаст не тот тип — **ошибка компиляции**, а не runtime. IDE подсказывает методы: `lead.contact().email()` — автокомплит работает.

**Почему полные имена пакетов?**

JTE не поддерживает `import`. Поэтому пишем полные имена: `java.util.List<ru.mentee.power.crm.domain.Lead>`.

**Как связано с контроллером:**

```java
// Контроллер
model.addAttribute("leads", leads);                    // List<Lead>
model.addAttribute("currentStatusFilter", status);     // LeadStatus
model.addAttribute("industries", industries);           // List<LeadIndustry>
model.addAttribute("currentIndustryFilter", industry); // LeadIndustry
```

Имя атрибута в `model.addAttribute("leads", ...)` должно совпадать с именем в `@param`. Тип — тоже должен совпадать.

#### 7.2. Добавляем форму с select

```jte
<form action="/leads" method="get" class="flex gap-2 items-center">
  <label for="industry">Отрасль:</label>
  <select id="industry" name="industry" onchange="this.form.submit()">
    <option value="">Все отрасли</option>
    @for(var industry : industries)
      @if(currentIndustryFilter != null && currentIndustryFilter.equals(industry))
        <option value="${industry}" selected>${industry}</option>
      @else
        <option value="${industry}">${industry}</option>
      @endif
    @endfor
  </select>

  @if(currentStatusFilter != null)
    <input type="hidden" name="status" value="${currentStatusFilter}"/>
  @endif
</form>
```

**Почему form, а не ссылки?**

Ссылки (`<a href="/leads?industry=IT">`) не сохраняют `status`. Form с `<select>` + hidden field — стандартный HTML-паттерн для динамических фильтров. `onchange="this.form.submit()"` — авто-сабмит при выборе (UX).

**Почему hidden field — пошаговая цепочка:**

1. Пользователь выбрал статус NEW — URL: `/leads?status=NEW`
2. Контроллер рендерит страницу с `currentStatusFilter = NEW`
3. JTE рендерит hidden field: `<input type="hidden" name="status" value="NEW"/>`
4. Пользователь выбирает отрасль IT — `onchange` сабмитит форму
5. Браузер собирает **все** поля формы (включая hidden):
   - `select`: name="industry", value="IT"
   - `hidden`: name="status", value="NEW"
6. URL: `/leads?industry=IT&status=NEW`
7. Контроллер получает оба параметра

**Что было бы БЕЗ hidden field:**

1. Пользователь выбрал статус NEW — URL: `/leads?status=NEW`
2. Пользователь выбирает отрасль IT — форма сабмитится
3. Браузер собирает только видимые поля: `select`: name="industry", value="IT"
4. URL: `/leads?industry=IT` — **status потерян!**
5. Контроллер получает `status = null`, показывает всех IT-лидов

`<input type="hidden">` — невидимое поле формы. Пользователь его не видит, но при сабмите оно отправляется как обычный параметр.

---

### Шаг 8: Написать тесты

**Куда смотреть:** `src/test/java/ru/mentee/power/crm/spring/controller/LeadControllerTest.java`

**Почему сюда:** MockMvc тесты проверяют HTTP-запросы без реального сервера.

#### Что делаем

```java
@Test
void shouldFilterLeadsByIndustry() throws Exception {
  mockMvc.perform(get("/leads").param("industry", "IT"))
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("TechCorp")))
      .andExpect(content().string(not(containsString("DesignStudio"))));
}

@Test
void shouldShowOnlyActiveIndustriesInSelect() throws Exception {
  mockMvc.perform(get("/leads"))
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("<option value=\"IT\"")))
      .andExpect(content().string(containsString("<option value=\"FINANCE\"")))
      .andExpect(content().string(containsString("<option value=\"RETAIL\"")));
}

@Test
void shouldPreserveStatusFilterWhenIndustrySelected() throws Exception {
  mockMvc.perform(get("/leads")
          .param("status", "NEW")
          .param("industry", "IT"))
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("TechCorp")))
      .andExpect(content().string(not(containsString("WebSoft"))));
}
```

**Что такое `perform()` и `param()`?**

`mockMvc.perform(...)` — выполняет HTTP-запрос через MockMvc (без реального сервера). Принимает `RequestBuilder` — объект, который описывает запрос.

`.param("key", "value")` — добавляет параметр к запросу:

- Для GET — добавляет в query string: `get("/leads").param("industry", "IT")` → `GET /leads?industry=IT`
- Для POST — добавляет в form data: `post("/leads").param("email", "test@test.com")`

Можно chaining-ить:

```java
get("/leads")
    .param("status", "NEW")
    .param("industry", "IT")
// → GET /leads?status=NEW&industry=IT
```

**Полная цепочка теста:**

```java
mockMvc                                          // 1. MockMvc объект
  .perform(                                      // 2. Выполнить запрос
    get("/leads")                                // 3. GET /leads
      .param("industry", "IT")                   // 4. ?industry=IT
  )
  .andExpect(status().isOk())                    // 5. Ожидаем HTTP 200
  .andExpect(                                    // 6. Ожидаем в теле ответа
    content()                                    // 7. Тело ответа
      .string(containsString("TechCorp"))        // 8. Содержит "TechCorp"
  )
  .andExpect(
    content()
      .string(not(containsString("DesignStudio"))) // 9. НЕ содержит "DesignStudio"
  );
```

**Почему 3 теста, а не 1?**

Каждый тест проверяет один аспект. Если один упадёт — сразу видно, что сломалось. Легче читать и поддерживать.

---

## Часть 4: Итоги

### Что мы сделали

1. Добавили `industry` в `Lead` (модель)
2. Создали `Dictionary<T>` интерфейс и `IndustryDictionary` реализацию (справочник)
3. Добавили `findByIndustry()` в `LeadRepository` (хранилище)
4. Добавили `findByIndustry()` и `getActiveIndustries()` в `LeadService` (сервис)
5. Обновили `LeadController` (контроллер)
6. Обновили `list.jte` (шаблон)
7. Написали MockMvc тесты

### Главный алгоритм мышления

1. **Прочитай задание** — переведи на человеческий
2. **Посмотри, что уже есть** — не изобретай велосипед
3. **Начни с данных** — чего не хватает в модели?
4. **Двигайся снизу вверх:**
   - Модель (`domain/`) — что хранится?
   - Справочник (`domain/Dictionary.java`) — какие данные для фильтра?
   - Репозиторий (`repository/`) — как искать?
   - Сервис (`service/`) — какая бизнес-логика?
   - Контроллер (`controller/`) — как обрабатывать HTTP?
   - Шаблон (`jte/`) — как показать?
5. **Напиши тесты** — что проверить?

### Главное правило

**Двигайся снизу вверх (данные — логика — UI) и не усложняй (сначала просто, потом оптимизируй).**
