# Git을 통한 Gradle 캐시 배포 가이드

## 📦 분할 압축된 백업 파일

Gradle 캐시를 Git에 올릴 수 있도록 **95MB 이하**로 분할했습니다.

### 파일 목록

```
gradle-backup.tar.gz.part00  (95MB)
gradle-backup.tar.gz.part01  (95MB)
gradle-backup.tar.gz.part02  (89MB)
─────────────────────────────────────
총 3개 파일, 약 279MB
```

---

## 🚀 온라인 환경: Git에 올리기

### 1단계: 백업 생성 (이미 완료됨)

```bash
# 이미 생성된 파일들:
ls -lh gradle-backup.tar.gz.part*
# gradle-backup.tar.gz.part00  95M
# gradle-backup.tar.gz.part01  95M
# gradle-backup.tar.gz.part02  89M
```

### 2단계: Git에 커밋

```bash
# .gitignore에서 part 파일 제외 (있다면)
git add gradle-backup.tar.gz.part*
git add restore-backup.sh
git add README_GIT_BACKUP.md

git commit -m "Add Gradle cache backup for offline build

- Split into 3 parts (95MB each) for Git
- Total size: 279MB
- Includes all dependencies and Gradle wrapper
"

git push
```

---

## 📥 오프라인 환경: Git에서 받아서 복원

### 1단계: 저장소 클론

```bash
git clone <repository-url>
cd <repository-directory>
```

### 2단계: 자동 복원 (권장)

```bash
./restore-backup.sh
```

**스크립트가 자동으로:**
- ✅ 3개의 part 파일 확인
- ✅ 파일 합치기 (`cat part* > backup.tar.gz`)
- ✅ 기존 `~/.gradle` 백업 (있다면)
- ✅ 압축 해제 (`tar -xzf`)
- ✅ 복원 완료!

### 3단계: 수동 복원 (선택사항)

자동 스크립트 대신 수동으로:

```bash
# 1. 파일 합치기
cat gradle-backup.tar.gz.part* > gradle-backup.tar.gz

# 2. 압축 해제
tar -xzf gradle-backup.tar.gz -C ~/

# 3. 확인
ls -la ~/.gradle/
```

---

## 🔨 오프라인 빌드 실행

### Java 환경 설정

```bash
export JAVA_HOME="/c/Program Files/Java/jdk-17"
export PATH="$JAVA_HOME/bin:$PATH"

# 확인
java -version
# java version "17.0.12" ...
```

### 오프라인 빌드

```bash
./gradlew clean build --offline
```

**예상 출력:**
```
> Configure project :
? Repository: Maven Central

> Task :clean
> Task :compileJava FROM-CACHE
> Task :processResources
> Task :classes
> Task :bootWar
> Task :war
> Task :assemble
> Task :test FROM-CACHE
> Task :check UP-TO-DATE
> Task :build

BUILD SUCCESSFUL in 12s
9 actionable tasks: 6 executed, 3 from cache
```

**생성된 파일:**
```
build/libs/core-0.0.1-SNAPSHOT.war
```

---

## 📊 백업 내용

### 포함된 항목 ✅

| 디렉토리 | 크기 | 설명 |
|----------|------|------|
| `caches/modules-2/` | ~54MB | 의존성 JAR 파일 |
| `caches/jars-9/` | ~379MB | 변환된 JAR 파일 |
| `caches/8.14.3/` | ~16MB | Gradle 메타데이터 (transforms 제외) |
| `wrapper/` | ~146MB | Gradle 8.14.3 배포본 |

### 제외된 항목 ❌

| 항목 | 크기 | 이유 |
|------|------|------|
| `transforms/` | ~505MB | 첫 빌드 시 자동 재생성 |
| `daemon/` | ~1MB | Gradle 데몬 로그 (불필요) |
| `*.lock` | 작음 | 락 파일 (재생성됨) |
| `native/` | ~600KB | 네이티브 라이브러리 (재생성) |

---

## ⚠️ 주의사항

### 1. GitHub 파일 크기 제한
- **개별 파일:** 100MB 이하 ✅
- **저장소 권장:** 1GB 이하 ✅
- **분할 크기:** 95MB (안전 마진)

### 2. Java 필수
오프라인 컴퓨터에도 **Java 17 이상** 필요:
- Gradle은 백업에 포함됨 (설치 불필요)
- Java는 별도 설치 필요

### 3. Git LFS (선택사항)
대용량 파일 관리를 위해 Git LFS 사용 가능:

```bash
git lfs install
git lfs track "*.part*"
git add .gitattributes
git add gradle-backup.tar.gz.part*
git commit -m "Add Gradle cache with LFS"
git push
```

---

## 🔍 검증

### 백업 무결성 확인

```bash
# 1. part 파일 합치기
cat gradle-backup.tar.gz.part* > test-merged.tar.gz

# 2. 압축 테스트
tar -tzf test-merged.tar.gz | head -20

# 3. 크기 확인
du -sh test-merged.tar.gz
# 279M test-merged.tar.gz

# 4. 정리
rm test-merged.tar.gz
```

---

## 📝 파일 재생성

새로운 의존성 추가 후 백업 재생성:

```bash
# 1. 온라인에서 의존성 다운로드
./gradlew build --refresh-dependencies

# 2. 백업 재생성
cd ~/.gradle
tar -czf gradle-backup.tar.gz \
    --exclude='*.lock' \
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

# 3. 분할
cd /프로젝트경로
split -b 95M -d ~/.gradle/gradle-backup.tar.gz gradle-backup.tar.gz.part

# 4. Git 커밋
git add gradle-backup.tar.gz.part*
git commit -m "Update Gradle cache backup"
git push
```

---

## 💡 자주 묻는 질문

### Q: part 파일을 모두 받아야 하나요?
A: **네**, 3개 파일 모두 필요합니다. 하나라도 빠지면 복원 불가능합니다.

### Q: Git LFS 없이도 가능한가요?
A: **네**, 일반 Git으로도 가능합니다. 각 파일이 95MB 이하이므로 GitHub 제한에 문제없습니다.

### Q: 용량이 너무 큰데 더 줄일 수 없나요?
A: `transforms/`를 이미 제외했습니다(505MB 절약). 더 줄이면 오프라인 빌드가 불가능합니다.

### Q: 다른 프로젝트에서도 사용 가능한가요?
A: **네**, 같은 Gradle 버전(8.14.3)을 사용하는 프로젝트라면 캐시를 공유할 수 있습니다.

---

## ✨ 요약

### 온라인 컴퓨터:
```bash
git add gradle-backup.tar.gz.part*
git commit -m "Add Gradle cache backup"
git push
```

### 오프라인 컴퓨터:
```bash
git clone <repo>
./restore-backup.sh
./gradlew clean build --offline
# BUILD SUCCESSFUL ✅
```

**총 배포 크기:** 279MB (3개 파일)
**Git 제한:** ✅ 통과 (각 95MB 이하)
