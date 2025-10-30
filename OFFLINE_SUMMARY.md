# 오프라인 빌드 요약 가이드

## ✅ 답변: 가능합니다!

**필요한 것:**
1. Gradle 캐시 백업
2. 프로젝트 파일
3. Java 17+ (오프라인 컴퓨터에 설치되어 있어야 함)

---

## 📦 Gradle 캐시 크기 최적화

### 원본 크기
- `~/.gradle/` 전체: **1.1GB**

### 최소화 버전 (transforms 제외)
- 백업 파일 크기: **279MB** (압축됨)
- 압축 해제 시: 약 **600MB**
- **절약: 약 500MB** (transforms 디렉토리 제외)

---

## 🚀 빠른 시작 가이드

### 온라인 컴퓨터에서:

```bash
# 1. Java 환경 설정
export JAVA_HOME="/c/Program Files/Java/jdk-17"
export PATH="$JAVA_HOME/bin:$PATH"

# 2. 의존성 다운로드
./gradlew build --refresh-dependencies

# 3. 최소 백업 생성
./create-minimal-backup.sh
# 또는 수동:
cd ~/.gradle
tar -czf gradle-minimal-backup.tar.gz \
    --exclude='caches/8.14.3/transforms' \
    --exclude='daemon' \
    --exclude='native' \
    --exclude='notifications' \
    --exclude='workers' \
    caches/modules-2 \
    caches/jars-9 \
    caches/8.14.3 \
    caches/journal-1 \
    caches/build-cache-1 \
    wrapper

# 4. 프로젝트 백업
cd /프로젝트경로
tar -czf project.tar.gz \
    --exclude='build' \
    --exclude='.gradle' \
    --exclude='.idea' \
    .
```

**전달할 파일:**
- ✅ `gradle-minimal-backup.tar.gz` (279MB)
- ✅ `project.tar.gz` (약 50MB)
- 💡 총 약 **330MB** (vs 원본 1.1GB)

---

### 오프라인 컴퓨터에서:

```bash
# 0. Java 확인/설정 (필수!)
export JAVA_HOME="/path/to/jdk-17"
export PATH="$JAVA_HOME/bin:$PATH"
java -version  # 17 이상 확인

# 1. Gradle 캐시 복원
tar -xzf gradle-minimal-backup.tar.gz -C ~/

# 2. 프로젝트 압축 해제
mkdir my-project
tar -xzf project.tar.gz -C my-project
cd my-project

# 3. 오프라인 빌드
./gradlew clean build --offline
```

**성공 메시지:**
```
BUILD SUCCESSFUL in 12s
9 actionable tasks: 6 executed, 3 from cache
```

---

## 🔍 제외된 항목 (빌드 시 자동 재생성)

백업에서 제외되어 크기를 줄인 항목들:

| 항목 | 크기 | 설명 |
|------|------|------|
| `transforms/` | ~505MB | 변환된 JAR 캐시 (자동 재생성) |
| `daemon/` | ~1MB | Gradle 데몬 로그 (불필요) |
| `native/` | ~600KB | 네이티브 라이브러리 (자동 재생성) |
| `notifications/` | 작음 | 알림 설정 (불필요) |
| `workers/` | 작음 | 워커 캐시 (자동 재생성) |

**중요:** 이 항목들은 첫 빌드 시 자동으로 재생성되므로 백업에서 제외해도 문제없습니다.

---

## 📊 크기 비교

### 옵션 1: 전체 백업
- **크기:** 1.1GB
- **장점:** 즉시 사용 가능, transforms 재생성 불필요
- **단점:** 용량 큼

### 옵션 2: 최소 백업 (권장) ⭐
- **크기:** 279MB (압축), 600MB (압축 해제)
- **장점:** 용량 75% 절약
- **단점:** 첫 빌드 시 transforms 재생성 (약 2-3초 추가)

---

## ⚠️ 주의사항

### 1. Java는 필수!
오프라인 컴퓨터에도 **Java 17 이상**이 설치되어 있어야 합니다.
- Gradle은 백업에 포함됨 (설치 불필요)
- Java는 별도로 설치 필요

### 2. gradle-repo는 사용 불가
프로젝트에 `gradle-repo` 디렉토리가 있지만:
- Gradle 캐시 형식 (해시 기반)
- Maven 저장소로 사용 불가
- **반드시 `~/.gradle/caches` 백업 사용**

### 3. transforms는 선택사항
- 포함: 빌드 즉시 시작
- 제외: 첫 빌드 시 2-3초 추가 소요

---

## 🎯 검증된 방법

위 방법은 완전히 깨끗한 Gradle 환경(`~/.gradle-test3`)에서 테스트되어 **작동 확인됨**:

```bash
export GRADLE_USER_HOME="/c/Users/omen/.gradle-test3"
./gradlew clean build --offline

# 결과: BUILD SUCCESSFUL ✅
```

---

## 📁 생성된 파일

프로젝트에 다음 파일들이 생성되었습니다:

1. **`create-minimal-backup.sh`** - 최소 백업 생성 스크립트
2. **`OFFLINE_BUILD_GUIDE.md`** - 상세한 오프라인 빌드 가이드
3. **`OFFLINE_SUMMARY.md`** - 이 문서 (요약본)
4. **`gradle-minimal-backup.tar.gz`** - 생성된 백업 파일 (279MB)

---

## 💡 자주 묻는 질문

### Q: Java가 없는 오프라인 컴퓨터라면?
A: JDK 17 설치 파일도 함께 가져가야 합니다 (약 150MB 추가)

### Q: transforms를 포함하면 더 빠를까?
A: 첫 빌드만 2-3초 빠름. 이후 빌드는 동일

### Q: 새로운 의존성을 추가하면?
A: 온라인 환경에서 다시 `./gradlew build --refresh-dependencies` 후 백업 재생성

### Q: 여러 프로젝트에서 사용 가능?
A: 네! 같은 Gradle 버전을 사용하는 프로젝트라면 캐시 공유 가능

---

## ✨ 요약

**온라인 컴퓨터:**
```bash
./create-minimal-backup.sh  # 279MB 백업 생성
```

**오프라인 컴퓨터:**
```bash
tar -xzf gradle-minimal-backup.tar.gz -C ~/
./gradlew clean build --offline
# BUILD SUCCESSFUL ✅
```

**총 전송 용량:** 약 330MB (vs 원본 1.1GB, 75% 절약)
