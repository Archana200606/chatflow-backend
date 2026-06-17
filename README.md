# ChatFlow — Backend

Spring Boot real-time chat backend with JWT auth and WebSockets.

## Stack
- Java 17, Spring Boot 3.2
- Spring Security + JWT (jjwt)
- Spring WebSocket + STOMP
- Spring Data JPA + PostgreSQL
- Lombok

## Setup

### 1. PostgreSQL
```sql
CREATE DATABASE chatapp;
```

### 2. Configure
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/chatapp
spring.datasource.username=your_user
spring.datasource.password=your_password
jwt.secret=YourSuperSecretKeyAtLeast256BitsLong
```

### 3. Run
```bash
mvn spring-boot:run
```
Tables are auto-created by Hibernate (`ddl-auto=update`).

## API Reference

### Auth
| Method | Endpoint | Body |
|--------|----------|------|
| POST | `/api/auth/register` | `{username, email, password}` |
| POST | `/api/auth/login` | `{email, password}` |

Both return `{token, user}`.

### Users (Bearer token required)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users` | All users |
| GET | `/api/users/search?query=` | Search users |

### Chats
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/chats` | My chats |
| POST | `/api/chats` | Create/get direct chat `{userId}` |

### Messages
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/messages/{chatId}` | Chat history |

## WebSocket

Connect to: `ws://localhost:8080/ws` (via SockJS)

**STOMP Headers on CONNECT:**
```
Authorization: Bearer <token>
```

**Publish (send):**
- `/app/chat.send` → `{chatId, content}`
- `/app/chat.typing` → `{chatId}`

**Subscribe (receive):**
- `/topic/chat/{chatId}` → `MessageDto`

## Architecture
```
AuthController       → /api/auth/**
UserController       → /api/users/**
ChatController       → /api/chats/**
MessageController    → /api/messages/**
ChatWebSocketCtrl    → /app/chat.** (WebSocket)

JwtAuthFilter        → validates Bearer token on HTTP
JwtUtil              → generate/validate JWT
SecurityConfig       → Spring Security chain
WebSocketConfig      → STOMP broker + JWT channel interceptor
```

## Database Schema
```
users           (id, username, email, password, status, created_at)
chats           (id, chat_type, name, created_at)
chat_participants(chat_id, user_id)
messages        (id, sender_id, chat_id, content, timestamp)
```
