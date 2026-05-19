# School Microservices

Микросервисная система авторизации и управления пользователями

### Стек технологий:
- **Backend:** Java 21, Spring Boot 4.x, Spring Security (JWT + JWKS)
- **Database:** PostgreSQL
- **DevOps:** Docker, Docker Compose
- **API:** Swagger (OpenAPI)

### Архитектура
Проект разделен на независимые модули:
- **auth-service (порт: 8081)** Управление пользователями, выдача и проверка JWT (RS256)
- **gateway-service (порт: 8080)** Единая точка входа, маршрутизация и базовая фильтрация запросов

### Базовая структура проекта:
- `com.progress.authservice` - главный класс `AuthServiceApplication`
  - `.controller` - классы с `@RestController`. Принимают HTTP-запросы и возвращают ответы.
  - `.exception` - кастомные исключения и `GlobalExceptionHandler`.
  - `.model`
    - `entity` - JPA entities
    - `dto` - объекты передачи данных
    - `enums` - перечисления ролей
  - `.repository` - интерфейсы для работы с БД (`JpaRepository`).
  - `.security` 
    - `.config` - сборка конфига для jwt ключей и security filter
    - `.jwt` - сервис для обработки и генерации jwt ключей
  - `.service` - бизнес-логика. Здесь лежат `@Service`. Обрабатывают данные, выбрасывают исключения.
- `com.progress.gatewayservice` - главный класс `GatewayApplication`
  - `.security.config` - сборка конфига для routes и filter

### Запуск и тестирование

#### Запуск всего окружения:

```bash
docker compose up --build
```

#### Запуск модульных тестов:
```bash
gradle test
```

### API Contract
Все запросы к бизнес-логике проходят через Gateway (порт 8080).

- **Регистрация:** `POST /api/v1/auth/register` 
- **Логин:** `POST /api/v1/auth/login` 
- **Обновление токена:** `POST /api/v1/auth/refresh` 

#### Или Swagger UI для auth-service:
```url
http://localhost:8081/swagger-ui.html
```

### Пример запроса на регистрацию:
```
{
"email": "test@email.com",
"password": "stringst",
"firstName": "string",
"middleName": "string",
"lastName": "string"
}
```

### Пример ответа / ошибки:
#### ОК 201
```
{
"message": "Пользователь зарегистрирован"
}
```

#### Error: response status is 409
```
{
"error": "Пользователь с таким email уже существует"
}
```

#### Error: response status is 400
```
{
  "error": "Ошибка валидации данных",
  "details": {
    "firstName": "Поле 'Имя' не может быть пустым",
    "lastName": "Поле 'Фамилия' не может быть пустым",
    "password": "Минимум 8 символов",
    "email": "Email не может быть пустым"
  }
}
```
