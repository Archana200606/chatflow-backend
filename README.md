# ChatFlow — Backend

Real-time chat application backend built with Spring Boot, Spring Security (JWT), WebSockets (STOMP), and MySQL.

## Tech Stack

- **Java 17** + **Spring Boot 3.2**
- **Spring Security** — JWT-based stateless authentication
- **Spring WebSocket** — STOMP protocol over SockJS for real-time messaging
- **Spring Data JPA** + **MySQL** — persistence layer
- **Lombok** — boilerplate reduction

## Features

- User registration & login with JWT issuance
- Stateless authentication via custom `JwtAuthFilter`
- Real-time bidirectional messaging over WebSockets
- Typing indicators broadcast via STOMP
- One-to-one direct chats with persisted history
- User search by username/email

## Architecture

```
controller/      REST endpoints (auth, users, chats, messages)
websocket/        STOMP message handlers (/app/chat.send, /app/chat.typing)
security/         JWT generation, validation, and the auth filter chain
config/           Spring Security + WebSocket configuration
model/            JPA entities: User, Chat, Message
repository/       Spring Data JPA repositories
service/          Business logic (chat creation/retrieval)
dto/              Request/response data transfer objects
```

## Getting Started

### Prerequisites
- Java 17+
- Maven (or use IntelliJ's bundled Maven)
- MySQL 8+

### Database setup
```sql
CREATE DATABASE chatapp CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### Configuration
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/chatapp?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
jwt.secret=YourLongRandomSecretKeyHere
```

Tables are auto-created by Hibernate on first run (`ddl-auto=update`).

### Run
```bash
mvn spring-boot:run
```
Backend starts on `http://localhost:8080`.

## API Reference

### Auth
| Method | Endpoint | Body |
|---|---|---|
| POST | `/api/auth/register` | `{ username, email, password }` |
| POST | `/api/auth/login` | `{ email, password }` |

Both return `{ token, user }`.

### Users (requires `Authorization: Bearer <token>`)
| Method | Endpoint |
|---|---|
| GET | `/api/users` |
| GET | `/api/users/search?query=` |

### Chats
| Method | Endpoint | Body |
|---|---|---|
| GET | `/api/chats` | — |
| POST | `/api/chats` | `{ userId }` |

### Messages
| Method | Endpoint |
|---|---|
| GET | `/api/messages/{chatId}` |

### WebSocket
Connect: `ws://localhost:8080/ws` (SockJS)

STOMP CONNECT header: `Authorization: Bearer <token>`

**Publish:**
- `/app/chat.send` → `{ chatId, content }`
- `/app/chat.typing` → `{ chatId }`

**Subscribe:**
- `/topic/chat/{chatId}` → incoming `MessageDto`

## Known Engineering Notes

WebSocket message handlers run outside the standard HTTP request transaction boundary. Lazy-loaded JPA collections (e.g. `Chat.participants`) must be accessed within an active Hibernate session — handler methods touching these are annotated `@Transactional` to avoid `LazyInitializationException`.

## License
MIT