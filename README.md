# School Microservices

### Текущий функционал:
- Регистрация пользователей
### Стек:
- Spring Boot
- Spring Security
- PostgreSql
- Docker
- Swagger
### Базовая структура проекта:
- `com.progress.schoolmicroservices` - главный класс `SchoolMicroservicesApplication`
- `.controller` - классы с `@RestController`. Принимают HTTP-запросы и возвращают ответы.
- `.service` - бизнес-логика. Здесь лежат `@Service`. Обрабатывают данные, выбрасывают исключения.
- `.repository` - интерфейсы для работы с БД (`JpaRepository`).
- `.model`
  - `entity` - JPA entities
  - `dto` - объекты передачи данных
  - `enums` - перечисления ролей
- `.exception` - кастомные исключения и `GlobalExceptionHandler`.

### Запуск проекта

1. Запустить PostgreSQL через Docker:

```bash
docker compose up -d
```

2. Запустить приложение через IntelliJ IDEA:
- открыть `SchoolMicroservicesApplication`
- нажать `Run`

3. Swagger UI:
```text
http://localhost:8080/swagger-ui.html
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
"Пользователь с таким email уже существует"
}
```

#### Error: response status is 400
```
{
  "firstName": "Поле 'Имя' не может быть пустым",
  "lastName": "Поле 'Фамилия' не может быть пустым",
  "password": "Минимум 8 символов",
  "email": "Email не может быть пустым"
}
```
