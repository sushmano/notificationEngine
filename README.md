# Notification Routing — Kafka Streams edition

**Flow**
- REST `POST /events` -> persist event -> publish to `events` topic.
- **Kafka Streams topology** reads `events`, applies dynamic routing rules from `application.yml`,
  and **fan-outs** one record per channel into `notify-send`.
- A dispatcher consumer reads `notify-send` and delivers via channel providers (Email/SMS/Webhook).
  It uses `@RetryableTopic` for exponential retries and DLT.

**Run**
```bash
docker compose up -d
mvn spring-boot:run
```

**Test**
Use `http/calls.http`.

**README.md**
