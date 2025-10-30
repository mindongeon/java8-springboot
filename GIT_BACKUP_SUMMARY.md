# Git을 통한 Gradle 캐시 배포 - 완전 가이드

## ✅ 완료된 작업

Git에 올릴 수 있도록 Gradle 캐시를 **95MB 이하**로 분할 압축했습니다!

---

## 📦 생성된 파일

### 1. 분할된 백업 파일 (Git에 커밋할 파일)
```
gradle-backup.tar.gz.part00  (95MB)  ✅ Git 업로드 가능
gradle-backup.tar.gz.part01  (95MB)  ✅ Git 업로드 가능  
gradle-backup.tar.gz.part02  (89MB)  ✅ Git 업로드 가능
─────────────────────────────────────────────────────
총 3개 파일, 279MB
```

### 2. 복원 스크립트
- **`restore-backup.sh`** - 자동 복원 스크립트 (3.0KB)

### 3. 문서
- **`README_GIT_BACKUP.md`** - 상세 가이드 (6.1KB)
- **`GIT_BACKUP_SUMMARY.md`** - 이 파일 (요약)

---

## 🚀 사용 방법

### 온라인 컴퓨터 (현재):

#### 1단계: Git에 커밋
```bash
git add gradle-backup.tar.gz.part*
git add restore-backup.sh
git add README_GIT_BACKUP.md
git add GIT_BACKUP_SUMMARY.md

git commit -m "Add Gradle cache backup for offline build (3 parts, 279MB)"

git push
```

#### ✅ GitHub 제한 확인
- 개별 파일 크기: **95MB 이하** ✅ 통과
- 총 커밋 크기: 약 280MB
- GitHub 권장: 1GB 이하 ✅ 통과

---

### 오프라인 컴퓨터:

#### 1단계: 저장소 클론
```bash
git clone <your-repository-url>
cd <repository-name>
```

#### 2단계: 백업 복원 (자동)
```bash
chmod +x restore-backup.sh
./restore-backup.sh
```

**스크립트가 자동으로:**
1. 3개 part 파일 확인 ✅
2. 파일 합치기 (279MB) ✅
3. ~/.gradle/ 압축 해제 ✅
4. 완료! ✅

#### 3단계: 오프라인 빌드
```bash
# Java 환경 설정
export JAVA_HOME="/c/Program Files/Java/jdk-17"
export PATH="$JAVA_HOME/bin:$PATH"

# 오프라인 빌드
./gradlew clean build --offline
```

**예상 결과:**
```
BUILD SUCCESSFUL in 12s
9 actionable tasks: 6 executed, 3 from cache
```

---

## 📊 크기 비교

| 방법 | 크기 | Git 업로드 |
|------|------|-----------|
| 원본 `.gradle/` | 1.1GB | ❌ 불가능 |
| 최소 백업 (단일) | 279MB | ❌ 불가능 (100MB 제한) |
| **분할 백업 (권장)** | **95MB × 3** | **✅ 가능** |

---

## 🔍 백업 내용

### ✅ 포함된 항목
- `caches/modules-2/` - 의존성 JAR 파일
- `caches/jars-9/` - 변환된 JAR
- `caches/8.14.3/` - Gradle 메타데이터 (transforms 제외)
- `wrapper/` - Gradle 8.14.3 배포본

### ❌ 제외된 항목 (자동 재생성)
- `transforms/` (~505MB) - 첫 빌드 시 자동 재생성
- `daemon/` - Gradle 데몬 로그
- `*.lock` - 락 파일
- `native/`, `notifications/`, `workers/` - 재생성 가능

---

## ⚠️ 주의사항

### 1. 모든 part 파일 필요
3개 파일 모두 있어야 복원 가능합니다.

### 2. Java 필수
오프라인 컴퓨터에도 **Java 17 이상** 필요

### 3. Git LFS (선택사항)
더 효율적인 관리를 원하면:
```bash
git lfs install
git lfs track "*.part*"
git add .gitattributes
```

---

## 🎯 검증 완료

### 테스트 환경
- 완전히 깨끗한 Gradle 캐시에서 테스트
- 파일 합치기: ✅ 성공
- 압축 해제: ✅ 성공  
- 오프라인 빌드: ✅ BUILD SUCCESSFUL

### 파일 무결성
```bash
# 테스트 결과
cat gradle-backup.tar.gz.part* > test.tar.gz
# 크기: 279M ✅
tar -tzf test.tar.gz
# 정상 압축 해제 ✅
```

---

## 💡 팁

### 새로운 의존성 추가 시
```bash
# 1. 온라인에서
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

# 4. Git 업데이트
git add gradle-backup.tar.gz.part*
git commit -m "Update Gradle cache"
git push
```

---

## 📁 프로젝트 파일 구조

```
프로젝트/
├── gradle-backup.tar.gz.part00    (95MB) ← Git에 커밋
├── gradle-backup.tar.gz.part01    (95MB) ← Git에 커밋
├── gradle-backup.tar.gz.part02    (89MB) ← Git에 커밋
├── restore-backup.sh               (3KB)  ← Git에 커밋
├── README_GIT_BACKUP.md           (6KB)  ← Git에 커밋
├── GIT_BACKUP_SUMMARY.md          (이 파일)
├── .gitignore                     (업데이트됨)
└── ... (프로젝트 파일들)
```

---

## ✨ 최종 요약

### 준비된 것 ✅
- [x] 279MB 백업을 3개 파일로 분할 (각 95MB 이하)
- [x] 자동 복원 스크립트 생성
- [x] 상세 문서 작성
- [x] .gitignore 업데이트
- [x] 파일 무결성 검증 완료

### 다음 단계 📝
1. Git에 커밋 및 푸시
2. 오프라인 컴퓨터에서 클론
3. `./restore-backup.sh` 실행
4. 오프라인 빌드 완료!

---

## 📞 문제 해결

### Q: part 파일이 손상되었다고 나옵니다
```bash
# 파일 크기 확인
ls -lh gradle-backup.tar.gz.part*
# part00, part01은 정확히 95MB, part02는 89MB여야 함

# Git에서 다시 받기
git fetch origin
git checkout HEAD -- gradle-backup.tar.gz.part*
```

### Q: restore-backup.sh가 실행되지 않습니다
```bash
# 실행 권한 부여
chmod +x restore-backup.sh

# 또는 직접 실행
bash restore-backup.sh
```

### Q: 오프라인 빌드가 실패합니다
```bash
# Java 환경 확인
java -version  # 17 이상이어야 함

# Gradle 캐시 확인
ls -la ~/.gradle/caches/

# 자세한 로그
./gradlew build --offline --info
```

---

**준비 완료! Git에 푸시하세요!** 🚀
