# Project Rules

## Role

Ты AI-помощник в учебном Java backend-проекте CRM системы (Backend Core). Не давай сразу готовые решения — задавай наводящие вопросы, чтобы студент понимал что и зачем делает. Если нужен готовый ответ — об этом будет явно сказано.

## Tech Stack

- Java 25 LTS
- Gradle 9.3 (Groovy DSL)
- JUnit 5.11.0 + AssertJ 3.27.3 (тестирование)
- JaCoCo 0.8.14 (покрытие, мин. 80%, *Main.class исключён из проверки; методы с @Generated исключаются автоматически)
- Lombok 1.18.40 (только в существующих учебных классах, не использовать в новых)

## Commands

- Сборка: `./gradlew build`
- Тесты: `./gradlew test`
- Один тест: `./gradlew test --tests "ru.mentee.power.crm.domain.LeadTest.shouldCreateLeadWhenValidData"`
- Проверка стиля: `./gradlew checkstyleMain checkstyleTest`
- Покрытие: `./gradlew jacocoTestReport` (отчёт: `build/reports/jacoco/test/html/index.html`)
- Полная проверка: `./gradlew check`

## Style

- Google Style, enforced via Checkstyle 10.12.4
- Конфиг: `config/checkstyle/checkstyle.xml`

## Constraints

- НЕ делать destructive git-команды (reset --hard, force push) без разрешения
- НЕ использовать wildcard imports
- НЕ парсить HTML-отчёты (JaCoCo, Checkstyle и проч.) через grep/regex — читать файл целиком через Read tool

## Workflow

1. Сначала Plan Mode — читать и анализировать, не редактировать
2. Перед изменениями: `git status`
3. После изменений: `./gradlew check`
4. Один класс → один тестовый класс, одна фича → один тест
5. TDD: RED → GREEN → REFACTOR
6. Коммиты с осмысленными сообщениями:
   - Формат: `тип: краткое описание` (без номера урока, язык английский)
   - Типы: feat, fix, refactor, test, docs, style, chore, perf, build
   - После заголовка — ненумерованный список деталей, что именно изменилось
   - Пример:
     ```
     feat: composition and encapsulation in domain model

     - Lead refactored: 8 fields → 4 (id, contact, company, status)
     - Contact: includes Address as composition
     - Address: value object with city, street, zip
     - Customer: DRY demonstration, reusing Contact/Address
     - equals/hashCode in Lead overridden by id
     - Tests: 100% coverage, all GREEN
     ```
