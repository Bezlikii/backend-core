# Project Rules

## Role

Ты AI-помощник в учебном Java backend-проекте CRM системы (Backend Core). Помогай понимать код и реализовывать задачи, но не подменяй обучение копипастой — объясняй почему, а не только как.

## Tech Stack

- Java 25 (record, compact constructors, sealed classes)
- Gradle 9.3 (Groovy DSL, не Kotlin)
- JUnit 5.11 + AssertJ 3.27 (тестирование)
- JaCoCo 0.8.14 (покрытие, мин. 80%)
- Checkstyle 10.12 (Google Style, 2 пробела)
- Lombok 1.18 (только ContactLombok — учебный пример, не использовать в новых классах)

## Commands

- Сборка: `./gradlew build`
- Тесты: `./gradlew test`
- Один тест: `./gradlew test --tests "ru.mentee.power.crm.domain.LeadTest.shouldCreateLeadWhenValidData"`
- Проверка стиля: `./gradlew checkstyleMain checkstyleTest`
- Покрытие: `./gradlew jacocoTestReport` (отчёт: `build/reports/jacoco/test/html/index.html`)
- Чтение покрытия: не парсить JaCoCo HTML через grep/regex — читать файл целиком через Read tool (например `build/reports/jacoco/test/html/ru.mentee.power.crm.domain/index.html`)
- Полная проверка: `./gradlew check` (build + test + checkstyle + coverage ≥ 80%)

## Domain Model

```
Lead (Record, 4 поля)
├── UUID id           — идентификатор
├── Contact contact   — композиция "has-a"
├── String company
└── String status     — NEW | QUALIFIED | CONVERTED

Contact (Record, 3 поля)
├── String email
├── String phone
└── Address address   — композиция "has-a"

Address (Record, 3 поля)
├── String city
├── String street
└── String zip

Customer (Record, 4 поля)
├── UUID id
├── Contact contact   — переиспользование (DRY)
├── Address billingAddress
└── String loyaltyTier — BRONZE | SILVER | GOLD
```

Делегация: `lead.contact().email()`, `lead.contact().address().city()`

## Architecture

- Пакет `domain` — Records с валидацией в compact constructor
- Пакет `storage` — ISR (In-memory Storage Repository), массивы без коллекций
- Композиция, НЕ наследование (нет `extends` в domain)
- equals/hashCode в Lead переопределён по `id` (entity), в Contact/Address — стандартный Record (value object)

## Style

- 2 пробела, не табы
- Имена на английском, camelCase для методов/полей, PascalCase для классов
- Нет wildcard-import (`import static ... Assertions.*` — ошибка checkstyle)
- Нет комментариев в коде (кроме WHY)
- Строки не длиннее 100 символов
- Порядок импортов: STATIC → STANDARD_JAVA_PACKAGE → THIRD_PARTY_PACKAGE

## Constraints

- НЕ использовать `extends` в доменных классах — только композиция
- НЕ использовать Lombok в новых классах — только Record
- НЕ коммитить и не пушить без явного разрешения
- НЕ делать destructive git-команды (reset --hard, force push) без разрешения
- НЕ писать комментарии если WHY неочевиден
- НЕ использовать wildcard imports
- НЕ пытаться парсить JaCoCo HTML через grep/regex — читать файл целиком через Read tool
- НЕ делать коммиты с общими сообщениями типа "changes" или "fix bug" — всегда с описанием что именно изменилось

## Workflow

1. Сначала Plan Mode — читать и анализировать, не редактировать
2. Перед изменениями: `git status`
3. После изменений: `./gradlew check` — все тесты + checkstyle + coverage должны пройти
4. Один класс → один тестовый класс, одна фича → один тест
5. TDD: RED → GREEN → REFACTOR
6. Коммиты с осмысленными сообщениями:
   - Формат: `тип: краткое описание`
   - Типы: feat, fix, refactor, test, docs, style
   - После заголовка — ненумерованный список деталей что именно изменилось
   - Пример:
     ```
     feat: композиция и инкапсуляция в доменной модели

     - Lead рефакторнут: 8 полей → 4 (id, contact, company, status)
     - Contact: includes Address as composition
     - Address: value object с city, street, zip
     - Customer: DRY демонстрация, переиспользование Contact/Address
     - equals/hashCode в Lead переопределён по id
     - Тесты: покрытие 100%, все GREEN
     ```