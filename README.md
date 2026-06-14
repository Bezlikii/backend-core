# Backend Core — CRM System

[![CI](https://github.com/Bezlikii/backend-core/actions/workflows/ci.yml/badge.svg)](https://github.com/Bezlikii/backend-core/actions/workflows/ci.yml)

Учебный проект: CRM система на Spring Boot с чистой архитектурой (domain → repository → service → controller).

## Быстрый старт

### Запуск приложения
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
| **Фреймворк** | Spring Boot 3.5.0 (spring-boot-starter-web) |
| **Шаблонизация** | JTE 3.1.15 + Tailwind CSS |
| **Lombok** | 1.18.40 |
| **Тестирование** | JUnit 5.11.0, AssertJ 3.27.3, Mockito 5.20.0 |
| **Качество кода** | Checkstyle 10.12.4 (Google Style), JaCoCo 0.8.14 (≥80%) |
| **CI/CD** | GitHub Actions |

### Команды

| Команда | Что делает |
|---|---|
| `./gradlew build` | Полная сборка |
| `./gradlew test` | Все тесты |
| `./gradlew bootRun` | Запуск Spring Boot (порт 8081) |
| `./gradlew check` | Checkstyle + тесты + JaCoCo |
| `./gradlew checkstyleMain checkstyleTest` | Только стиль |
| `./gradlew jacocoTestReport` | Отчёт покрытия |

### Правила
- Стиль: Google Java Style (Checkstyle)
- Коммиты: Conventional Commits (`feat:`, `fix:`, `docs:`)
- Ветки: `feature/BCORE-X`
- PR обязателен для слияния в master