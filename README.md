# Backend Core — CRM System

[![CI](https://github.com/Bezlikii/backend-core/actions/workflows/ci.yml/badge.svg)](https://github.com/Bezlikii/backend-core/actions/workflows/ci.yml)

Учебный проект: CRM система с двумя веб-стеками (Servlet и Spring Boot), демонстрирующая эволюцию от ручной настройки к convention over configuration.

## Быстрый старт

### Запуск Servlet стека (порт 8080)
```bash
./gradlew run
```
Открой http://localhost:8080/leads

### Запуск Spring Boot (порт 8081)
```bash
./gradlew bootRun
```
Открой http://localhost:8081/leads

### Проверка качества
```bash
./gradlew check    # Checkstyle + тесты + JaCoCo
./gradlew test     # Только тесты
```

## Технологический стек

| Категория | Технологии |
|---|---|
| **Язык** | Java 25 LTS |
| **Сборка** | Gradle 9.3 (Groovy DSL) |
| **Веб-стеки** | Servlet API 6.1 + Tomcat 11.0.1, Spring Boot 3.5.0 |
| **Шаблонизация** | JTE 3.1.15 + Tailwind CSS |
| **Тестирование** | JUnit 5.11.0, AssertJ 3.27.3, Mockito 5.20.0 |
| **Качество кода** | Checkstyle 10.12.4 (Google Style), JaCoCo 0.8.14 (≥80%) |
| **CI/CD** | GitHub Actions |

### Команды

| Команда | Что делает |
|---|---|
| `./gradlew build` | Полная сборка |
| `./gradlew test` | Все тесты |
| `./gradlew run` | Servlet стек (порт 8080) |
| `./gradlew bootRun` | Spring Boot (порт 8081) |
| `./gradlew check` | Checkstyle + тесты + JaCoCo |
| `./gradlew checkstyleMain checkstyleTest` | Только стиль |
| `./gradlew jacocoTestReport` | Отчёт покрытия |

### Правила
- Стиль: Google Java Style (Checkstyle)
- Коммиты: Conventional Commits (`feat:`, `fix:`, `docs:`)
- Ветки: `feature/BCORE-X`
- PR обязателен для слияния в master

## Архитектура проекта

```
┌─────────────────────────────────────────────────────────────┐
│                    src/main/java/.../crm                     │
├─────────────────────────────────────────────────────────────┤
│  domain/          Модель данных (общая для обоих стеков)    │
│  ├─ Lead          Record, Aggregate Root                    │
│  ├─ Contact       Record, Value Object                      │
│  ├─ Address       Record, Value Object                      │
│  ├─ LeadStatus    Enum                                      │
│  └─ CrudRepository<T>  Интерфейс хранилища                  │
│                                                             │
│  service/         Бизнес-логика (общая)                     │
│  └─ LeadService   @Service (Spring) / new (Servlet)         │
│                                                             │
│  repository/      Хранилище (общее)                         │
│  └─ LeadRepository  @Repository, HashMap<UUID, Lead>        │
│                                                             │
│  servlet/         Servlet стек (порт 8080)                  │
│  └─ LeadListServlet  @WebServlet, ручной рендеринг          │
│                                                             │
│  spring/          Spring Boot стек (порт 8081)              │
│  ├─ Application   @SpringBootApplication, точка входа       │
│  └─ controller/                                             │
│     └─ LeadController  @Controller, @GetMapping              │
│                                                             │
│  Main.java        Точка входа Servlet (порт 8080)           │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                    src/main/jte/ (ОБЩИЕ)                     │
├─────────────────────────────────────────────────────────────┤
│  leads/list.jte   Таблица лидов                             │
│  layout/main.jte  Общий layout (шапка + footer)             │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                    src/main/resources/                       │
├─────────────────────────────────────────────────────────────┤
│  application.yml  Конфигурация Spring Boot                  │
└─────────────────────────────────────────────────────────────┘
```

**Ключевая идея:** модель данных, сервис и репозиторий — общие. Различаются только точки входа и способ обработки HTTP-запросов.

## Модель данных

### Lead (Aggregate Root)

```java
public record Lead(UUID id, Contact contact, String company, LeadStatus status) {
    @Override
    public boolean equals(Object o) { /* по id */ }
    
    @Override
    public int hashCode() { /* по id */ }
}
```

### Contact (Value Object)

```java
public record Contact(String email, String phone, Address address) {}
```

### Address (Value Object)

```java
public record Address(String city, String street, String zip) {}
```

### CrudRepository<T> (интерфейс)

```java
public interface CrudRepository<T> {
    T save(T entity);
    void delete(UUID id);
    Optional<T> findById(UUID id);
    Optional<T> findByEmail(String email);
    List<T> findAll();
}
```

### LeadRepository (реализация)

```java
@Repository
public class LeadRepository implements CrudRepository<Lead> {
    private final Map<UUID, Lead> storage = new HashMap<>();        // O(1) по id
    private final Map<String, UUID> emailIndex = new HashMap<>();   // O(1) по email
    
    @Override
    public Lead save(Lead lead) {
        storage.put(lead.id(), lead);
        emailIndex.put(lead.contact().email(), lead.id());
        return lead;
    }
    // ... остальные методы
}
```

**Итог:** одна модель данных используется обоими стеками.

## Servlet стек (порт 8080)

### Точка входа: Main.java

**Создание объектов вручную:**

```java
public class Main {
    public static void main(String[] args) throws Exception {
        // 1. Создаём хранилище
        LeadRepository repository = new LeadRepository();
        
        // 2. Создаём сервис, передаём репозиторий
        LeadService leadService = new LeadService(repository);
        
        // 3. Добавляем тестовые лиды
        leadService.addLead("ivan@example.com", ...);
        // ... 5 лидов
    }
}
```

**Настройка Tomcat вручную:**

```java
// Создаём Tomcat
Tomcat tomcat = new Tomcat();
tomcat.setPort(8080);
tomcat.setBaseDir(baseDir);

// Создаём контекст
Context context = tomcat.addContext("", baseDir);

// Кладём сервис в ServletContext
context.getServletContext().setAttribute("leadService", leadService);

// Регистрируем сервлет
tomcat.addServlet(context, "LeadListServlet", new LeadListServlet());
context.addServletMappingDecoded("/leads", "LeadListServlet");

// Запускаем
tomcat.start();
```

### Обработчик: LeadListServlet.java

**Инициализация JTE (один раз при старте):**

```java
@WebServlet("/leads")
public class LeadListServlet extends HttpServlet {
    private TemplateEngine templateEngine;
    
    @Override
    public void init() throws ServletException {
        Path templatePath = Path.of("src/main/jte");
        DirectoryCodeResolver codeResolver = new DirectoryCodeResolver(templatePath);
        this.templateEngine = TemplateEngine.create(codeResolver, ContentType.Html);
    }
}
```

**Обработка запроса (при каждом GET):**

```java
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    // 1. Достаём сервис из ServletContext
    LeadService leadService = (LeadService) getServletContext().getAttribute("leadService");
    
    // 2. Получаем данные
    List<Lead> leads = leadService.findAll();
    
    // 3. Кладём в Map
    Map<String, Object> model = new HashMap<>();
    model.put("leads", leads);
    
    // 4. Рендерим вручную
    response.setContentType("text/html; charset=UTF-8");
    templateEngine.render("leads/list.jte", model, new PrintWriterOutput(response.getWriter()));
}
```

### Цепочка запроса

```
Браузер GET /leads
  │
  ▼
Tomcat → LeadListServlet.doGet(request, response)
  │
  ├─ getServletContext().getAttribute("leadService")
  │     └─ LeadService (положили в Main.java)
  │
  ├─ leadService.findAll() → List<Lead>
  │
  ├─ Map<String, Object> model = new HashMap<>()
  │     └─ model.put("leads", leads)
  │
  └─ templateEngine.render("leads/list.jte", model, out)
        │
        ▼
     JTE: leads/list.jte → @for → HTML таблица
        │
        ▼
     response.getWriter() → Браузер
```

**Итог:** всё делается вручную — создание объектов, настройка Tomcat, рендеринг.

## Spring Boot стек (порт 8081)

### Точка входа: Application.java

**Шаблон точки входа:**

```java
@SpringBootApplication(scanBasePackages = "ru.mentee.power.crm")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

**Инициализация тестовых данных:**

```java
@Bean
CommandLineRunner initData(LeadService leadService) {
    return args -> {
        Address addr = new Address("Moscow", "Tverskaya", "125009");
        leadService.addLead("ivan@example.com", ...);
        // ... 5 лидов
    };
}
```

### 7 этапов запуска Spring Boot

```
① Создание ApplicationContext (пустой контейнер для бинов)
  │
  ▼
② Загрузка application.yml (порт 8081, имя приложения)
  │
  ▼
③ Auto-configuration (создание Tomcat, DispatcherServlet из starter-web)
  │
  ▼
④ Component Scanning (поиск @Service, @Repository, @Controller)
  │
  ▼
⑤ Dependency Injection (связывание бинов через конструкторы)
  │
  ▼
⑥ Запуск embedded Tomcat на порту 8081
  │
  ▼
⑦ ApplicationReadyEvent (готово принимать HTTP-запросы)
```

### Контроллер: LeadController.java

**Шаблон контроллера:**

```java
@Controller
public class LeadController {
    private final LeadService leadService;
    
    // DI через конструктор — Spring сам внедрит
    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }
    
    @GetMapping("/leads")
    public String showLeads(Model model) {
        // 1. Получаем данные
        List<Lead> leads = leadService.findAll();
        
        // 2. Кладём в Model
        model.addAttribute("leads", leads);
        
        // 3. Возвращаем имя view — Spring сам найдёт шаблон
        return "leads/list";
    }
}
```

### Конфигурация: application.yml

```yaml
server:
  port: 8081                    # Порт (чтобы не конфликтовать с Servlet на 8080)

gg:
  jte:
    templateLocation: src/main/jte   # Путь к JTE шаблонам
    developmentMode: true            # Hot-reload

logging:
  level:
    org.springframework.web: DEBUG   # Логи запросов
```

### Цепочка запроса

```
Браузер GET /leads
  │
  ▼
DispatcherServlet (автоматически создан Spring Boot)
  │
  ├─ HandlerMapping ищет @GetMapping("/leads")
  │     └─ находит LeadController.showLeads()
  │
  ├─ Spring передаёт Model (автоматически)
  │
  ├─ leadService.findAll() → List<Lead>
  │     └─ LeadService внедрён через конструктор (@Service)
  │
  ├─ model.addAttribute("leads", leads)
  │
  └─ return "leads/list"
        │
        ▼
     JteViewResolver: templateLocation + "leads/list"
        │
        ▼
     src/main/jte/leads/list.jte → @for → HTML таблица
        │
        ▼
     Браузер
```

**Итог:** всё автоматически — DI, ViewResolver, конфигурация из yml.

## Сравнение стеков: Servlet vs Spring Boot

### Подробная таблица

| Критерий | Servlet (Main.java) | Spring Boot (Application.java) |
|---|---|---|
| **Точка входа** | `main()` с ручным Tomcat | `@SpringBootApplication` + `run()` |
| **Строк в main** | ~25 строк | 3 строки |
| **Порт** | `tomcat.setPort(8080)` в коде | `server.port: 8081` в yml |
| **Регистрация обработчика** | `addServlet()` + `addServletMappingDecoded()` | `@Controller` + `@GetMapping` |
| **Создание объектов** | `new LeadRepository()`, `new LeadService(repo)` | `@Repository`, `@Service` — Spring сам |
| **Передача сервиса** | `setAttribute("leadService", ...)` | Конструктор DI |
| **Получение сервиса** | `(LeadService) getAttribute("leadService")` | `private final LeadService` в конструкторе |
| **Модель (view data)** | `Map<String, Object>` + `put("leads", ...)` | `Model.addAttribute("leads", ...)` |
| **Рендеринг** | `templateEngine.render("leads/list.jte", model, out)` | `return "leads/list"` → JteViewResolver |
| **JTE путь** | `Path.of("src/main/jte")` в `init()` | `gg.jte.templateLocation` в yml |
| **Hot-reload** | ❌ Нет | ✅ `developmentMode: true` |
| **Тестовые данные** | `addLead(...)` в `main()` | `CommandLineRunner` |
| **Тестирование** | Mockito + реальный Tomcat | `@SpringBootTest` + `MockMvc` |
| **Время старта** | ~400 ms | ~2500 ms |
| **Конфигурация** | Разбросана в коде | Один application.yml |

### Общее

✅ Одна модель данных: Lead, Contact, Address  
✅ Один LeadRepository (HashMap)  
✅ Один LeadService (бизнес-логика)  
✅ Одни JTE шаблоны (src/main/jte/)  
✅ Оба стека показывают одних и тех же лидов  

### Различия

| Аспект | Servlet | Spring Boot |
|---|---|---|
| **Создание объектов** | Вручную (`new`) | Автоматически (DI) |
| **Конфигурация** | В коде | В application.yml |
| **Рендеринг** | Вручную (`render()`) | Автоматически (ViewResolver) |
| **Расширяемость** | Новый URL = новый класс + правки Main | Добавить метод с `@GetMapping` |

### Метрики (из StackComparisonTest)

| Метрика | Servlet | Spring Boot |
|---|---|---|
| Время старта | < 10 000 мс | < 15 000 мс |
| Количество лидов | 5 | 5 |
| Статус ответа | 200 | 200 |
| HTML таблица | `<table>` | `<table>` |

### Trade-offs

| Критерий | Servlet | Spring Boot |
|---|---|---|
| **Количество кода** | ❌ Много boilerplate | ✅ Минимум |
| **Скорость старта** | ✅ ~400 мс | ❌ ~2500 мс |
| **Расширяемость** | ❌ Новый URL = новый класс + правки Main | ✅ Добавить метод с `@GetMapping` |
| **Тестирование** | ❌ Нужен реальный сервер | ✅ MockMvc без сервера |
| **Конфигурация** | ❌ В коде | ✅ application.yml |
| **Когда использовать** | Простые приложения, микросервисы | Корпоративные приложения, REST API |

### Вывод

**Servlet стек** — простота и скорость старта (400ms).  
**Spring Boot** — convention over configuration (2.5s).

На практике Spring Boot побеждает: 2.5 секунды старта — один раз при деплое, а экономия десятков строк boilerplate — каждый день.

## JTE шаблоны (общие для обоих стеков)

### Структура

```
src/main/jte/
├── leads/list.jte       ← таблица лидов
└── layout/main.jte      ← общий layout (шапка + footer)
```

### layout/main.jte

```jte
@param gg.jte.Content content

<!DOCTYPE html>
<html>
<head>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body>
    <header>CRM System</header>
    <main>${content}</main>
    <footer>&copy; 2025 CRM</footer>
</body>
</html>
```

### leads/list.jte

```jte
@param java.util.List<ru.mentee.power.crm.domain.Lead> leads

@template.layout.main(content = @`
    <table>
        <thead>
            <tr>
                <th>Email</th>
                <th>Company</th>
                <th>Status</th>
            </tr>
        </thead>
        <tbody>
        @for(var lead : leads)
            <tr>
                <td>${lead.contact().email()}</td>
                <td>${lead.company()}</td>
                <td>${lead.status()}</td>
            </tr>
        @endfor
        </tbody>
    </table>
`)
```

### Почему шаблоны общие (DRY)

Оба стека читают из `src/main/jte/`:
- **Servlet:** `Path.of("src/main/jte")` в `init()`
- **Spring:** `gg.jte.templateLocation: src/main/jte` в yml

Один источник правды, никакого дублирования.
