# Gradle 오프라인 환경 완벽 가이드

이 가이드는 인터넷 연결 없이 Gradle 프로젝트를 빌드하고 실행하는 방법을 설명합니다.

## 목차

1. [준비된 파일](#준비된-파일)
2. [빠른 시작](#빠른-시작)
3. [오프라인 패키지 생성](#오프라인-패키지-생성)
4. [오프라인 환경 설정](#오프라인-환경-설정)
5. [사용 가능한 Gradle Tasks](#사용-가능한-gradle-tasks)
6. [문제 해결](#문제-해결)

---

## 준비된 파일

오프라인 실행을 위해 다음 파일들이 준비되었습니다:

```
프로젝트/
├── gradle-offline/
│   └── gradle-8.14.3-all.zip       (214MB - Gradle 배포본)
├── libs/dependencies/
│   └── *.jar                        (49개 의존성 파일)
├── offline-package/
│   ├── OFFLINE_SETUP.md            (상세 설정 가이드)
│   └── setup-offline.sh            (자동 설정 스크립트)
├── gradle.properties               (Gradle 최적화 설정)
└── build.gradle                    (오프라인 Tasks 정의)
```

### 의존성 목록 (49개)

- **Spring Boot**: 2.7.18
- **Spring Framework**: 5.3.31
- **MyBatis**: 3.5.14
- **Tomcat Embed**: 9.0.83
- **Jackson**: 2.13.5
- **Hibernate Validator**: 6.2.5
- **Lombok**: 1.18.42
- 기타 필요한 라이브러리들

---

## 빠른 시작

### 온라인 환경 (의존성 다운로드)

```bash
# 1. 오프라인 패키지 생성
./gradlew createOfflinePackage

# 2. 모든 의존성 다운로드 (캐시 준비)
./gradlew build --refresh-dependencies

# 3. Gradle 캐시 백업
cd ~
tar -czf gradle-home.tar.gz .gradle/

# 4. 프로젝트 압축
cd /프로젝트경로
tar -czf project.tar.gz \\
    --exclude='build' \\
    --exclude='.gradle' \\
    --exclude='.idea' \\
    .
```

### 오프라인 환경 (빌드 실행)

```bash
# 1. Gradle 캐시 복원
tar -xzf gradle-home.tar.gz -C ~

# 2. 프로젝트 압축 해제
tar -xzf project.tar.gz

# 3. 오프라인 빌드
cd project
./gradlew build --offline

# 성공!
```

---

## 오프라인 패키지 생성

### 사용 가능한 Gradle Tasks

온라인 환경에서 다음 tasks를 실행하여 오프라인 준비:

#### 1. `downloadDependencies`
모든 의존성을 Gradle 캐시에 다운로드합니다.

```bash
./gradlew downloadDependencies
```

**출력:**
```
✓ 모든 의존성 다운로드 완료
```

#### 2. `copyDependencies`
의존성을 `libs/dependencies/` 디렉토리로 복사합니다.

```bash
./gradlew copyDependencies
```

**결과:** 49개의 JAR 파일이 복사됨

#### 3. `createOfflinePackage`
완전한 오프라인 패키지를 생성합니다.

```bash
./gradlew createOfflinePackage
```

**생성 파일:**
- `gradle-offline/gradle-8.14.3-all.zip`
- `libs/dependencies/*.jar`
- `offline-package/OFFLINE_SETUP.md`
- `offline-package/setup-offline.sh`

#### 4. `packageForOffline`
프로젝트 전체를 ZIP으로 압축합니다.

```bash
./gradlew packageForOffline
```

**결과:** `build/offline-package-0.0.1-SNAPSHOT.zip` 생성

---

## 오프라인 환경 설정

### 방법 1: --offline 플래그 사용 (가장 간단) ⭐

이 방법이 가장 간단하고 권장됩니다.

#### 온라인 환경에서:

```bash
# 1. 오프라인 패키지 생성
./gradlew createOfflinePackage

# 2. 의존성 다운로드
./gradlew build --refresh-dependencies

# 3. Gradle 홈 디렉토리 백업
cd ~
tar -czf gradle-home.tar.gz .gradle/

# 4. 복사할 파일들:
# - 프로젝트 전체 디렉토리
# - gradle-home.tar.gz
```

#### 오프라인 환경에서:

```bash
# 1. Gradle 캐시 복원
tar -xzf gradle-home.tar.gz -C ~

# 2. 프로젝트로 이동
cd /프로젝트경로

# 3. 오프라인 빌드
./gradlew build --offline

# 4. 실행
java -jar build/libs/core-0.0.1-SNAPSHOT.war
```

**장점:**
- 설정 변경 불필요
- Gradle 캐시를 그대로 사용
- 가장 안정적

### 방법 2: 자동 설정 스크립트 사용

```bash
# 온라인 환경에서
./offline-package/setup-offline.sh

# 다음 파일들이 생성됨:
# - gradle-home-backup.tar.gz
# - project-offline.tar.gz

# 오프라인 환경에서
tar -xzf gradle-home-backup.tar.gz -C $HOME
tar -xzf project-offline.tar.gz
./gradlew build --offline
```

### 방법 3: Gradle Wrapper 로컬 배포본 사용

`gradle/wrapper/gradle-wrapper.properties` 파일 수정:

```properties
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=file:///Users/mindongeon/dev/source/exb/core/gradle-offline/gradle-8.14.3-all.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
```

**주의:** 절대 경로를 사용해야 합니다!

---

## 사용 가능한 Gradle Tasks

### 오프라인 환경에서 사용 가능한 모든 Tasks

```bash
# 빌드 관련
./gradlew build --offline              # 전체 빌드
./gradlew clean --offline              # 빌드 결과 삭제
./gradlew compileJava --offline        # Java 컴파일만
./gradlew test --offline               # 테스트 실행
./gradlew bootWar --offline            # WAR 파일 생성

# 의존성 확인
./gradlew dependencies --offline       # 의존성 트리 출력
./gradlew buildEnvironment --offline   # 빌드 환경 정보

# 프로젝트 정보
./gradlew tasks --offline              # 사용 가능한 tasks 목록
./gradlew projects --offline           # 프로젝트 구조
./gradlew properties --offline         # 프로젝트 속성
```

---

## gradle.properties 설정

프로젝트에 이미 최적화된 설정이 포함되어 있습니다:

```properties
# JVM 메모리 설정
org.gradle.jvmargs=-Xmx2048m -XX:MaxMetaspaceSize=512m

# 성능 최적화
org.gradle.caching=true               # 빌드 캐시 활성화
org.gradle.daemon=true                # Gradle Daemon 사용
org.gradle.parallel=true              # 병렬 빌드

# 오프라인 모드 (필요시 주석 해제)
# org.gradle.offline=true

# 경고 표시
org.gradle.warning.mode=all
```

### 영구 오프라인 모드

항상 오프라인으로 실행하려면 주석을 해제:

```properties
org.gradle.offline=true
```

이렇게 설정하면 `--offline` 플래그 없이도 오프라인 모드로 실행됩니다.

---

## 검증

### 오프라인 빌드 테스트

```bash
# 1. 네트워크 연결 확인 (선택)
ping google.com  # 실패해야 정상

# 2. 오프라인 빌드
./gradlew clean build --offline

# 3. 성공 확인
ls -lh build/libs/
# core-0.0.1-SNAPSHOT.war 파일이 있어야 함
```

### 예상 출력

```
BUILD SUCCESSFUL in 2s
9 actionable tasks: 9 executed

생성된 파일:
- build/libs/core-0.0.1-SNAPSHOT.war (25MB)
```

---

## 문제 해결

### 1. "Could not resolve" 에러

**증상:**
```
Could not resolve com.google.guava:guava:30.1-jre
```

**원인:** 해당 의존성이 Gradle 캐시에 없음

**해결:**
```bash
# 온라인 환경에서
./gradlew build --refresh-dependencies

# ~/.gradle 디렉토리를 다시 백업하여 오프라인 환경으로 복사
```

### 2. "Gradle distribution ... not found"

**증상:**
```
Exception in thread "main" java.io.FileNotFoundException:
https://services.gradle.org/distributions/gradle-8.14.3-all.zip
```

**원인:** Gradle Wrapper가 배포본을 다운로드하려고 시도

**해결 방법 1:** gradle-wrapper.properties 수정
```properties
distributionUrl=file:///절대경로/gradle-offline/gradle-8.14.3-all.zip
```

**해결 방법 2:** Gradle 캐시에 배포본 복사
```bash
mkdir -p ~/.gradle/wrapper/dists/gradle-8.14.3-all/xxxxx/
cp gradle-offline/gradle-8.14.3-all.zip ~/.gradle/wrapper/dists/gradle-8.14.3-all/xxxxx/
```

### 3. 플러그인 다운로드 에러

**증상:**
```
Could not resolve org.springframework.boot:spring-boot-gradle-plugin:2.7.18
```

**원인:** Gradle 플러그인이 캐시에 없음

**해결:**
```bash
# 온라인 환경에서
./gradlew tasks --refresh-dependencies
./gradlew downloadDependencies

# Gradle 캐시 전체를 백업
tar -czf gradle-cache-full.tar.gz ~/.gradle/
```

### 4. Verification 에러

**증상:**
```
Dependency verification failed
```

**해결:** verification-metadata.xml 파일을 함께 복사

```bash
# 프로젝트의 gradle/verification-metadata.xml이 반드시 포함되어야 함
ls gradle/verification-metadata.xml
```

### 5. 메모리 부족 에러

**증상:**
```
OutOfMemoryError: Java heap space
```

**해결:** gradle.properties에서 메모리 증가
```properties
org.gradle.jvmargs=-Xmx4096m -XX:MaxMetaspaceSize=1024m
```

---

## 디버깅 팁

### 1. 상세 로그 확인

```bash
./gradlew build --offline --info
```

### 2. 디버그 모드

```bash
./gradlew build --offline --debug > build-debug.log 2>&1
```

### 3. 캐시 위치 확인

```bash
ls -la ~/.gradle/caches/modules-2/files-2.1/
```

### 4. 의존성 트리 출력

```bash
./gradlew dependencies --offline > dependencies.txt
```

---

## 자주 묻는 질문 (FAQ)

### Q1: 의존성을 추가했는데 오프라인에서 빌드가 안 돼요

A: 새로운 의존성은 온라인 환경에서 다운로드해야 합니다.

```bash
# 온라인 환경
./gradlew build --refresh-dependencies
tar -czf gradle-cache-updated.tar.gz ~/.gradle/

# 오프라인 환경으로 복사 후
tar -xzf gradle-cache-updated.tar.gz -C ~
```

### Q2: 전체 용량이 얼마나 되나요?

A:
- Gradle 배포본: ~214MB
- 프로젝트 의존성: ~27MB
- Gradle 캐시 (~/.gradle): ~500MB-1GB
- **총합:** ~750MB-1.2GB

### Q3: Docker 컨테이너에서도 사용할 수 있나요?

A: 네, Dockerfile에 추가:

```dockerfile
FROM openjdk:8-jdk

# Gradle 캐시 복사
COPY gradle-home.tar.gz /tmp/
RUN tar -xzf /tmp/gradle-home.tar.gz -C /root

# 프로젝트 복사
COPY . /app
WORKDIR /app

# 오프라인 빌드
RUN ./gradlew build --offline
```

### Q4: 여러 프로젝트에서 공유할 수 있나요?

A: 네, ~/.gradle 디렉토리를 공유하면 됩니다. 같은 Gradle 캐시를 여러 프로젝트가 사용할 수 있습니다.

---

## 최적화 팁

### 1. 빌드 속도 향상

gradle.properties:
```properties
org.gradle.caching=true
org.gradle.parallel=true
org.gradle.daemon=true
org.gradle.configureondemand=true
```

### 2. 캐시 크기 줄이기

불필요한 Gradle 버전 제거:
```bash
cd ~/.gradle/wrapper/dists
ls -la
# 사용하지 않는 버전 삭제
rm -rf gradle-7.* gradle-6.*
```

### 3. 프로젝트 압축 최적화

```bash
tar -czf project-minimal.tar.gz \\
    --exclude='build' \\
    --exclude='.gradle' \\
    --exclude='.idea' \\
    --exclude='*.iml' \\
    --exclude='out' \\
    --exclude='logs' \\
    --exclude='*.log' \\
    .
```

---

## 요약

### ✅ 온라인 환경에서 해야 할 일

1. `./gradlew createOfflinePackage`
2. `./gradlew build --refresh-dependencies`
3. `tar -czf gradle-home.tar.gz ~/.gradle/`
4. 파일들을 오프라인 환경으로 복사

### ✅ 오프라인 환경에서 해야 할 일

1. `tar -xzf gradle-home.tar.gz -C ~`
2. `./gradlew build --offline`
3. 성공!

---

## 지원

문제가 발생하면:
1. `offline-package/OFFLINE_SETUP.md` 참조
2. `./gradlew build --offline --info` 로그 확인
3. [문제 해결](#문제-해결) 섹션 참조

**오프라인 환경 구성 완료를 축하합니다! 🎉**
