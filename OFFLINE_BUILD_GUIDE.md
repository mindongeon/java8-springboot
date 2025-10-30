# 오프라인 빌드 가이드

## 문제 원인

오프라인 환경에서 Gradle 빌드가 실패하는 이유는 다음 2가지입니다:

### 1. JAVA_HOME 미설정
Java가 시스템 PATH에 없어서 Gradle이 Java를 찾지 못함

### 2. Gradle 캐시 없음
의존성과 플러그인이 `~/.gradle/caches`에 없어서 오프라인 모드로 빌드 불가

---

## 해결 방법

### ✅ 1단계: Java 환경 설정

프로젝트는 Java 8 툴체인을 사용하지만, Gradle 실행은 Java 17로 가능합니다.

```bash
export JAVA_HOME="/c/Program Files/Java/jdk-17"
export PATH="$JAVA_HOME/bin:$PATH"
```

**영구 설정 (선택사항):**
Windows 환경 변수에 추가하거나 프로젝트 루트에 `setenv.sh` 생성:

```bash
# setenv.sh
#!/bin/bash
export JAVA_HOME="/c/Program Files/Java/jdk-17"
export PATH="$JAVA_HOME/bin:$PATH"
```

사용: `source setenv.sh && ./gradlew build --offline`

---

### ✅ 2단계: 온라인 환경에서 의존성 다운로드

```bash
# Java 환경 설정
export JAVA_HOME="/c/Program Files/Java/jdk-17"
export PATH="$JAVA_HOME/bin:$PATH"

# 모든 의존성 및 플러그인 다운로드
./gradlew build --refresh-dependencies
```

이 명령어는 다음을 다운로드합니다:
- 프로젝트 의존성 (Spring Boot, MyBatis, Lombok 등)
- Gradle 플러그인 (Spring Boot Plugin, Dependency Management Plugin)
- 모든 파일은 `~/.gradle/caches`에 저장됨

---

### ✅ 3단계: Gradle 캐시 백업

```bash
# Gradle 캐시 백업 (약 950MB)
cd ~
tar -czf gradle-cache-backup.tar.gz .gradle/
```

**백업할 파일:**
- `gradle-cache-backup.tar.gz` (~950MB)
- 프로젝트 전체 디렉토리

---

### ✅ 4단계: 오프라인 환경에서 복원

```bash
# Gradle 캐시 복원
tar -xzf gradle-cache-backup.tar.gz -C ~

# 프로젝트로 이동
cd /프로젝트경로

# Java 환경 설정
export JAVA_HOME="/c/Program Files/Java/jdk-17"
export PATH="$JAVA_HOME/bin:$PATH"

# 오프라인 빌드
./gradlew clean build --offline
```

---

## 빌드 명령어

### 온라인 빌드 (의존성 다운로드)
```bash
export JAVA_HOME="/c/Program Files/Java/jdk-17"
export PATH="$JAVA_HOME/bin:$PATH"
./gradlew build --refresh-dependencies
```

### 오프라인 빌드
```bash
export JAVA_HOME="/c/Program Files/Java/jdk-17"
export PATH="$JAVA_HOME/bin:$PATH"
./gradlew clean build --offline
```

**성공 시 출력:**
```
BUILD SUCCESSFUL in 3s
9 actionable tasks: 6 executed, 3 from cache
```

---

## 주의사항

### ❌ gradle-repo 디렉토리는 사용할 수 없음

프로젝트에 `gradle-repo` 디렉토리가 있지만, 이는 **Gradle 캐시 형식**(해시 기반)으로 저장되어 있어 Maven 저장소처럼 직접 사용할 수 없습니다.

**올바른 방법:**
- `~/.gradle/caches` 전체를 백업하고 복원

**잘못된 방법:**
- `gradle-repo`를 Maven 저장소로 설정 (작동하지 않음)

---

## 완전한 오프라인 설정 절차

### 온라인 환경에서:

```bash
# 1. Java 설정
export JAVA_HOME="/c/Program Files/Java/jdk-17"
export PATH="$JAVA_HOME/bin:$PATH"

# 2. 의존성 다운로드
cd /프로젝트경로
./gradlew build --refresh-dependencies

# 3. Gradle 캐시 백업
cd ~
tar -czf gradle-cache.tar.gz .gradle/

# 4. 프로젝트 백업 (build 제외)
cd /프로젝트경로
tar -czf project.tar.gz \
    --exclude='build' \
    --exclude='.gradle' \
    --exclude='.idea' \
    --exclude='*.iml' \
    .
```

### 오프라인 환경으로 전달:
- `gradle-cache.tar.gz` (약 950MB)
- `project.tar.gz` (약 50MB)

### 오프라인 환경에서:

```bash
# 1. Gradle 캐시 복원
tar -xzf gradle-cache.tar.gz -C ~

# 2. 프로젝트 압축 해제
mkdir project
tar -xzf project.tar.gz -C project
cd project

# 3. Java 설정
export JAVA_HOME="/c/Program Files/Java/jdk-17"
export PATH="$JAVA_HOME/bin:$PATH"

# 4. 오프라인 빌드
./gradlew clean build --offline
```

---

## 검증

오프라인 빌드가 성공하면 다음 파일이 생성됩니다:

```bash
ls -lh build/libs/
# core-0.0.1-SNAPSHOT.war (약 25MB)
```

WAR 파일 실행:
```bash
java -jar build/libs/core-0.0.1-SNAPSHOT.war
```

---

## 문제 해결

### "java: command not found"
- JAVA_HOME이 설정되지 않음
- 해결: 위의 Java 환경 설정 참조

### "Could not resolve ..."
- Gradle 캐시에 의존성이 없음
- 해결: 온라인 환경에서 `./gradlew build --refresh-dependencies` 재실행 후 캐시 재백업

### "Plugin ... was not found"
- Gradle 플러그인이 캐시에 없음
- 해결: `~/.gradle/caches` 전체를 백업했는지 확인

---

## 요약

### ✅ 올바른 오프라인 빌드 방법
1. 온라인에서 `./gradlew build --refresh-dependencies` 실행
2. `~/.gradle/` 디렉토리 전체 백업
3. 오프라인 환경에서 복원
4. `./gradlew build --offline` 실행

### ❌ 작동하지 않는 방법
- `gradle-repo` 디렉토리를 Maven 저장소로 사용
- 의존성 JAR만 복사 (플러그인 필요)
- Gradle 캐시 일부만 복사
