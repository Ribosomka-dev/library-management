Вот дополненный README:

Library Management System (Система управления библиотекой)

Веб-приложение для автоматизации работы библиотеки: управления книгами, читателями и процессами выдачи/возврата книг. Проект разработан на языке Java с использованием фреймворка Spring Boot.

🛠 Технологический стек

	•	Язык программирования: Java 21
	•	Фреймворк: Spring Boot 3.4.0
	•	Spring Web (REST API)
	•	Spring Data JPA (работа с БД)
	•	Spring Validation (валидация входных данных)
	•	Spring Actuator (мониторинг)
	•	Spring Retry (повторные попытки при сбоях)
	•	База данных: H2 (in-memory, dev профиль)
	•	Сборщик: Maven
	•	Логирование: Logback (SLF4J)

📂 Структура проекта

Проект построен по классической многослойной архитектуре:

	•	controller — обработка HTTP-запросов (REST-контроллеры)
	•	service — бизнес-логика
	•	repository — доступ к данным (Spring Data JPA)
	•	entity — сущности БД (Book, Reader, BookLoan)
	•	dto — объекты передачи данных
	•	exception — глобальная обработка ошибок и кастомные исключения

🚀 Функционал

	•	Управление книгами: добавление, обновление, удаление, поиск по названию и автору
	•	Управление читателями: регистрация, просмотр, обновление, удаление
	•	Выдача книг: фиксация даты выдачи и возврата, отслеживание статуса (ACTIVE, RETURNED, OVERDUE)
	•	Валидация: проверка входных данных, запрет удаления с активными займами, защита от повторного возврата
	•	Пагинация: постраничный вывод книг, читателей и займов

💻 Как запустить

Требования:

	•	JDK 21+
	•	Maven

Шаги:

git clone https://github.com/Ribosomka-dev/library-management.git
cd library-management
mvn spring-boot:run


Приложение запустится на http://localhost:8081

H2 консоль: http://localhost:8081/h2-console

	•	JDBC URL: jdbc:h2:mem:librarydb
	•	Username: sa
	•	Password: (пусто)

📋 API Эндпоинты

Книги /api/books



|Метод |URL                               |Описание                      |
|------|----------------------------------|------------------------------|
|GET   |`/api/books`                      |Список всех книг              |
|GET   |`/api/books/{id}`                 |Книга по ID                   |
|GET   |`/api/books/search?title=&author=`|Поиск по названию и/или автору|
|GET   |`/api/books/paged?page=0&size=10` |Постраничный список           |
|POST  |`/api/books`                      |Создать книгу                 |
|PUT   |`/api/books/{id}`                 |Обновить книгу                |
|DELETE|`/api/books/{id}`                 |Удалить книгу                 |

Читатели /api/readers



|Метод |URL                                |Описание                 |
|------|-----------------------------------|-------------------------|
|GET   |`/api/readers`                     |Список всех читателей    |
|GET   |`/api/readers/{id}`                |Читатель по ID           |
|GET   |`/api/readers/paged?page=0&size=10`|Постраничный список      |
|POST  |`/api/readers`                     |Зарегистрировать читателя|
|PUT   |`/api/readers/{id}`                |Обновить данные          |
|DELETE|`/api/readers/{id}`                |Удалить читателя         |

Займы /api/loans



|Метод|URL                              |Описание           |
|-----|---------------------------------|-------------------|
|GET  |`/api/loans/overdue`             |Просроченные займы |
|GET  |`/api/loans/paged?page=0&size=10`|Постраничный список|
|GET  |`/api/loans/reader/{readerId}`   |Займы читателя     |
|POST |`/api/loans`                     |Выдать книгу       |
|PUT  |`/api/loans/{id}/return`         |Вернуть книгу      |

📝 Примеры запросов

Создать книгу:

curl -X POST http://localhost:8081/api/books \
  -H "Content-Type: application/json" \
  -d '{"title":"Война и мир","author":"Толстой","isbn":"9785040953943","publicationYear":1869,"totalCopies":3}'


Зарегистрировать читателя:

curl -X POST http://localhost:8081/api/readers \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Анна","lastName":"Иванова","email":"anna@example.com","phone":"+79991234567"}'


Выдать книгу:

curl -X POST http://localhost:8081/api/loans \
  -H "Content-Type: application/json" \
  -d '{"bookId":1,"readerId":1,"dueDate":"2026-07-01"}'


Вернуть книгу:

curl -X PUT http://localhost:8081/api/loans/1/return


Поиск книги:

curl "http://localhost:8081/api/books/search?author=Толстой"


🔍 Мониторинг

Actuator эндпоинты доступны в dev профиле:

	•	http://localhost:8081/actuator/health
	•	http://localhost:8081/actuator/info
	•	http://localhost:8081/actuator/metrics
