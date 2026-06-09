# NBE10-12-1-Team8

## 로컬 실행 가이드

### 1. 필요한 프로그램

- Java 25
- Node.js
- pnpm
- MySQL

macOS에서 Homebrew를 사용하는 경우:

```bash
brew install mysql
brew services start mysql
```

이미 설치되어 있다면 실행만 하면 됩니다.

```bash
brew services start mysql
```

### 2. MySQL 데이터베이스 생성

터미널에서 MySQL에 접속합니다.

```bash
mysql -u root
```

비밀번호가 있는 경우:

```bash
mysql -u root -p
```

MySQL 콘솔에 들어간 뒤 `team8` 데이터베이스를 생성합니다.

```sql
CREATE DATABASE team8 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
exit;
```

이미 `team8` 데이터베이스가 있고 로컬 DB를 초기화하고 싶다면 아래 명령을 사용합니다.

```sql
DROP DATABASE IF EXISTS team8;
CREATE DATABASE team8 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 백엔드 실행, MySQL 사용

백엔드를 MySQL 운영 프로필로 실행합니다.

```bash
cd backend

DB_URL='jdbc:mysql://localhost:3306/team8?serverTimezone=Asia/Seoul&characterEncoding=UTF-8' \
DB_USERNAME='root' \
DB_PASSWORD='' \
SPRING_PROFILES_ACTIVE=prod \
./gradlew bootRun
```

MySQL root 계정에 비밀번호가 있다면 `DB_PASSWORD` 값을 본인 비밀번호로 바꿔야 합니다.

```bash
DB_PASSWORD='내비밀번호'
```

참고 사항:

- 처음 실행할 때 Flyway가 자동으로 테이블을 생성합니다.
- `prod` 프로필에서는 Hibernate가 테이블을 직접 만들지 않고, 스키마가 엔티티와 맞는지만 검증합니다.
- `prod` 프로필에서는 샘플 데이터가 자동으로 들어가지 않습니다.
- 그래서 처음에는 상품 목록이 비어 있을 수 있습니다. 이 상태는 정상입니다.

### 4. 백엔드 실행, H2 사용

MySQL 없이 개발용 H2 DB로 실행하고 싶다면 기본 dev 프로필로 실행하면 됩니다.

```bash
cd backend
./gradlew bootRun
```

dev 프로필에서는 H2를 사용하고 샘플 데이터가 자동으로 들어갑니다.

### 5. 프론트엔드 실행

백엔드 터미널은 그대로 켜둔 상태에서 새 터미널을 열고 실행합니다.

```bash
cd frontend
pnpm install
pnpm dev
```

브라우저 접속 주소:

```text
http://localhost:3000
```

백엔드 API 주소:

```text
http://localhost:8080
```

Swagger 주소:

```text
http://localhost:8080/swagger-ui/index.html
```

### 6. MySQL 마이그레이션 확인

Flyway 마이그레이션이 정상 적용되었는지 확인하려면 아래 명령을 실행합니다.

```bash
mysql -h 127.0.0.1 -P 3306 -u root team8 -e "SHOW TABLES; SELECT version, description, success FROM flyway_schema_history;"
```

정상이라면 아래 테이블들이 보여야 합니다.

```text
flyway_schema_history
order_item
orders
product
```

Flyway 이력은 아래처럼 보여야 합니다.

```text
1 init schema 1
```

### 7. 문제 해결

`mysql: command not found`가 나오는 경우 MySQL이 설치되어 있지 않거나 PATH에 잡히지 않은 상태입니다.

```bash
brew install mysql
```

MySQL이 실행 중이 아닌 경우:

```bash
brew services start mysql
```

이미 실행 중이면 아래처럼 나올 수 있습니다. 이 경우 정상입니다.

```text
Service `mysql` already started
```

`8080` 포트가 이미 사용 중인 경우 기존 백엔드를 종료하거나 다른 포트로 실행합니다.

```bash
./gradlew bootRun --args='--server.port=8081'
```

Flyway가 실패했거나 로컬 DB를 다시 깨끗하게 만들고 싶은 경우, 로컬 `team8` DB만 초기화합니다.

```bash
mysql -u root -e "DROP DATABASE IF EXISTS team8; CREATE DATABASE team8 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

그 다음 백엔드를 다시 `SPRING_PROFILES_ACTIVE=prod`로 실행하면 됩니다.
