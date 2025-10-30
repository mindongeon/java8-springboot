# 🚀 Gradle 오프라인 빌드 빠른 시작

인터넷 연결 없이 Gradle 프로젝트를 빌드하고 실행하는 방법

## 📦 준비된 파일

- ✅ **Gradle 배포본**: `gradle-offline/gradle-8.14.3-all.zip` (214MB)
- ✅ **프로젝트 의존성**: `libs/dependencies/` (49개 JAR 파일, 27MB)
- ✅ **자동화 스크립트**: `offline-package/setup-offline.sh`
- ✅ **상세 가이드**: `OFFLINE_GUIDE.md`, `offline-package/OFFLINE_SETUP.md`

## ⚡ 빠른 시작 (3단계)

### 온라인 환경 (준비)

```bash
# 1단계: 오프라인 패키지 생성
./gradlew createOfflinePackage

# 2단계: 의존성 캐시 준비
./gradlew build --refresh-dependencies

# 3단계: Gradle 캐시 백업
cd ~ && tar -czf gradle-home.tar.gz .gradle/
```

**복사할 파일:**
- 프로젝트 전체 디렉토리
- `~/gradle-home.tar.gz`

### 오프라인 환경 (실행)

```bash
# 1단계: 캐시 복원
tar -xzf gradle-home.tar.gz -C ~

# 2단계: 오프라인 빌드
cd /프로젝트경로
./gradlew build --offline

# 완료! 🎉
```

## 🛠 사용 가능한 Gradle Tasks

### 오프라인 준비 Tasks

| Task | 설명 |
|------|------|
| `downloadDependencies` | 모든 의존성 다운로드 |
| `copyDependencies` | 의존성을 libs/로 복사 |
| `copyGradleWrapper` | Gradle 배포본 다운로드 |
| `createOfflinePackage` | 완전한 오프라인 패키지 생성 ⭐ |
| `packageForOffline` | 전체를 ZIP으로 압축 |

### 사용 예시

```bash
# 완전한 오프라인 패키지 생성 (권장)
./gradlew createOfflinePackage

# ZIP 파일로 압축
./gradlew packageForOffline
# → build/offline-package-0.0.1-SNAPSHOT.zip
```

## 🔧 오프라인 빌드 명령어

```bash
# 기본 빌드
./gradlew build --offline

# 클린 빌드
./gradlew clean build --offline

# WAR 파일만 생성
./gradlew bootWar --offline

# 테스트 실행
./gradlew test --offline

# 의존성 확인
./gradlew dependencies --offline
```

## 📋 체크리스트

### ✅ 온라인 환경에서

- [ ] `./gradlew createOfflinePackage` 실행
- [ ] `./gradlew build --refresh-dependencies` 실행
- [ ] `~/.gradle` 디렉토리 백업
- [ ] 프로젝트 디렉토리 복사 준비

### ✅ 오프라인 환경에서

- [ ] Gradle 캐시 복원 (`~/.gradle`)
- [ ] 프로젝트 디렉토리 복사
- [ ] `./gradlew build --offline` 실행
- [ ] WAR 파일 확인 (`build/libs/`)

## 🎯 권장 방법

**가장 간단하고 안정적인 방법:**

```bash
# 온라인 환경
./gradlew createOfflinePackage
./gradlew build --refresh-dependencies
cd ~ && tar -czf gradle-home.tar.gz .gradle/
cd /프로젝트경로 && tar -czf project.tar.gz \\
    --exclude='build' --exclude='.gradle' .

# 복사: gradle-home.tar.gz + project.tar.gz

# 오프라인 환경
tar -xzf gradle-home.tar.gz -C ~
tar -xzf project.tar.gz
./gradlew build --offline
```

## 📊 용량 정보

| 항목 | 크기 |
|------|------|
| Gradle 배포본 | ~214MB |
| 프로젝트 의존성 | ~27MB |
| Gradle 캐시 | ~500MB-1GB |
| **총합** | **~750MB-1.2GB** |

## 🔍 검증

```bash
# 오프라인 빌드 테스트
./gradlew clean build --offline

# 성공 시 출력:
# BUILD SUCCESSFUL in 2s
# 9 actionable tasks: 9 executed

# 생성된 파일 확인
ls -lh build/libs/
# core-0.0.1-SNAPSHOT.war (25MB)
```

## ⚠️ 주의사항

- `--offline` 플래그는 네트워크 접근을 완전히 차단합니다
- 새로운 의존성 추가 시 온라인 환경에서 다시 다운로드 필요
- `~/.gradle` 디렉토리 전체를 반드시 복사해야 합니다
- `gradle/verification-metadata.xml` 파일도 함께 유지해야 합니다

## 🐛 문제 해결

### "Could not resolve" 에러

```bash
# 온라인 환경에서
./gradlew build --refresh-dependencies
tar -czf gradle-cache-updated.tar.gz ~/.gradle/

# 오프라인 환경으로 다시 복사
```

### Gradle Wrapper 다운로드 에러

`gradle/wrapper/gradle-wrapper.properties` 수정:
```properties
distributionUrl=file:///절대경로/gradle-offline/gradle-8.14.3-all.zip
```

### 상세 로그 확인

```bash
./gradlew build --offline --info
./gradlew build --offline --debug > build.log 2>&1
```

## 📚 상세 문서

자세한 내용은 다음 문서를 참조하세요:

- **완벽 가이드**: [OFFLINE_GUIDE.md](OFFLINE_GUIDE.md)
- **설정 가이드**: [offline-package/OFFLINE_SETUP.md](offline-package/OFFLINE_SETUP.md)
- **자동 스크립트**: [offline-package/setup-offline.sh](offline-package/setup-offline.sh)

## 🎉 성공 메시지

```
╔══════════════════════════════════════════════════════════╗
║     오프라인 빌드 성공!                                   ║
╚══════════════════════════════════════════════════════════╝

BUILD SUCCESSFUL in 2s

생성된 파일:
  build/libs/core-0.0.1-SNAPSHOT.war (25MB)

다음 단계:
  java -jar build/libs/core-0.0.1-SNAPSHOT.war
```

---

## 💡 팁

### gradle.properties 설정

```properties
# 영구 오프라인 모드 (필요시)
org.gradle.offline=true

# 성능 최적화
org.gradle.caching=true
org.gradle.daemon=true
org.gradle.parallel=true
```

### Docker에서 사용

```dockerfile
FROM openjdk:8-jdk
COPY gradle-home.tar.gz /tmp/
RUN tar -xzf /tmp/gradle-home.tar.gz -C /root
COPY . /app
WORKDIR /app
RUN ./gradlew build --offline
```

---

**오프라인 환경 구축 완료! 🚀**

문제가 있으면 [OFFLINE_GUIDE.md](OFFLINE_GUIDE.md)를 참조하세요.
