# Windows ZIP 백업 가이드

## ✅ 완료된 작업

Windows 기본 압축 프로그램으로 풀 수 있는 **ZIP 형식**으로 백업했습니다!
Git에 올릴 수 있도록 **95MB 이하**로 분할했습니다.

---

## 📦 생성된 파일

### 분할된 ZIP 파일 (Git에 커밋)
```
gradle-backup.zip.part00  (95MB)  ✅ Git 업로드 가능
gradle-backup.zip.part01  (95MB)  ✅ Git 업로드 가능
gradle-backup.zip.part02  (87MB)  ✅ Git 업로드 가능
───────────────────────────────────────────────
총 3개 파일, 277MB (압축됨)
압축 해제 시: 약 586MB
```

---

## 🚀 온라인 컴퓨터: Git에 올리기

### Git 커밋
```bash
git add gradle-backup.zip.part*
git add create-zip-backup.py
git add split-zip.py
git add README_WINDOWS_ZIP.md

git commit -m "Add Gradle cache backup (Windows ZIP, 3 parts, 277MB)"

git push
```

---

## 📥 오프라인 컴퓨터: 복원하기

### 1단계: 저장소 클론
```bash
git clone <repository-url>
cd <repository-name>
```

### 2단계: 분할 파일 합치기

#### 방법 A: Windows 명령 프롬프트 (CMD)
```cmd
copy /b gradle-backup.zip.part* gradle-backup.zip
```

#### 방법 B: Windows PowerShell (권장)
```powershell
# PowerShell 5.0 이상
cmd /c copy /b gradle-backup.zip.part* gradle-backup.zip
```

#### 방법 C: Git Bash
```bash
cat gradle-backup.zip.part* > gradle-backup.zip
```

### 3단계: ZIP 압축 해제

#### 방법 A: Windows 탐색기 (가장 쉬움) ✨
1. `gradle-backup.zip` 파일 **우클릭**
2. **"압축 풀기"** 또는 **"Extract All"** 선택
3. 대상 폴더: **C:\Users\<사용자이름>\**
4. 확인 클릭
5. 자동으로 `.gradle` 폴더가 생성됨 ✅

#### 방법 B: PowerShell
```powershell
Expand-Archive -Path gradle-backup.zip -DestinationPath $env:USERPROFILE
```

#### 방법 C: 명령 프롬프트 (tar 사용)
```cmd
tar -xf gradle-backup.zip -C %USERPROFILE%
```

### 4단계: 오프라인 빌드

```cmd
REM Java 환경 설정 (예시)
set JAVA_HOME=C:\Program Files\Java\jdk-17
set PATH=%JAVA_HOME%\bin;%PATH%

REM Java 버전 확인
java -version

REM 오프라인 빌드
gradlew clean build --offline
```

**예상 출력:**
```
BUILD SUCCESSFUL in 12s
9 actionable tasks: 6 executed, 3 from cache
```

---

## 📊 백업 정보

### 크기 비교
| 항목 | 크기 |
|------|------|
| 원본 `.gradle/` | 1.1GB |
| ZIP 압축 | 277MB |
| 압축 해제 시 | 586MB |
| **절약** | **52.8% 압축률** |

### 포함된 내용 ✅
- `caches/modules-2/` - 의존성 JAR 파일
- `caches/jars-9/` - 변환된 JAR
- `caches/8.14.3/` - Gradle 메타데이터 (transforms 제외)
- `wrapper/` - Gradle 8.14.3 배포본

### 제외된 내용 (자동 재생성) ❌
- `transforms/` (~505MB) - 첫 빌드 시 자동 재생성
- `*.lock` - 락 파일
- `daemon/` - Gradle 데몬 로그
- `native/`, `notifications/`, `workers/`

---

## 🔍 검증

### ZIP 파일 무결성 확인

**Windows PowerShell:**
```powershell
# 파일 합치기
cmd /c copy /b gradle-backup.zip.part* test.zip

# ZIP 내용 확인
tar -tzf test.zip | Select-Object -First 20

# 정리
Remove-Item test.zip
```

**Git Bash:**
```bash
# 파일 합치기
cat gradle-backup.zip.part* > test.zip

# ZIP 내용 확인
unzip -l test.zip | head -20

# 정리
rm test.zip
```

---

## ⚠️ 주의사항

### 1. 모든 part 파일 필요
3개 파일 모두 있어야 복원 가능합니다.

### 2. Java 필수
오프라인 컴퓨터에도 **Java 17 이상** 필요:
- JDK 17 다운로드: https://adoptium.net/ (Temurin)
- Gradle은 백업에 포함됨 (별도 설치 불필요)

### 3. Windows 버전
- Windows 10/11: 기본 압축 해제 지원 ✅
- Windows 7/8: 7-Zip 또는 WinRAR 필요

---

## 💡 새로운 의존성 추가 시

온라인 환경에서 백업 재생성:

```bash
# 1. 의존성 다운로드
gradlew build --refresh-dependencies

# 2. ZIP 백업 생성
python create-zip-backup.py

# 3. ZIP 분할
python split-zip.py gradle-backup.zip

# 4. Git 업데이트
git add gradle-backup.zip.part*
git commit -m "Update Gradle cache"
git push
```

---

## 🎯 완전 자동화 스크립트

### Windows PowerShell 복원 스크립트

다음 내용을 `restore-gradle-windows.ps1`로 저장:

```powershell
# Gradle 캐시 복원 스크립트 (Windows)

Write-Host "=" -ForegroundColor Green -NoNewline
Write-Host "=" * 60 -ForegroundColor Green

Write-Host "  Gradle 캐시 복원 (Windows)" -ForegroundColor Cyan
Write-Host "=" * 60 -ForegroundColor Green
Write-Host ""

# 1. part 파일 확인
$parts = Get-ChildItem -Filter "gradle-backup.zip.part*" | Sort-Object Name

if ($parts.Count -eq 0) {
    Write-Host "ERROR: gradle-backup.zip.part* 파일을 찾을 수 없습니다." -ForegroundColor Red
    exit 1
}

Write-Host "발견된 part 파일: $($parts.Count)개" -ForegroundColor Yellow
foreach ($part in $parts) {
    $sizeMB = [math]::Round($part.Length / 1MB, 1)
    Write-Host "  $($part.Name) (${sizeMB}MB)" -ForegroundColor Gray
}
Write-Host ""

# 2. 파일 합치기
Write-Host "1. 분할 파일 합치는 중..." -ForegroundColor Yellow
cmd /c copy /b gradle-backup.zip.part* gradle-backup.zip

if ($LASTEXITCODE -eq 0) {
    $zipSize = [math]::Round((Get-Item gradle-backup.zip).Length / 1MB, 1)
    Write-Host "   [OK] 합치기 완료 (${zipSize}MB)" -ForegroundColor Green
} else {
    Write-Host "   [ERROR] 파일 합치기 실패" -ForegroundColor Red
    exit 1
}
Write-Host ""

# 3. 기존 .gradle 백업
Write-Host "2. 기존 .gradle 디렉토리 백업 중..." -ForegroundColor Yellow
$gradleDir = Join-Path $env:USERPROFILE ".gradle"

if (Test-Path $gradleDir) {
    $timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
    $backupDir = Join-Path $env:USERPROFILE ".gradle.backup.$timestamp"
    Move-Item $gradleDir $backupDir
    Write-Host "   [OK] 백업됨: $backupDir" -ForegroundColor Green
} else {
    Write-Host "   [INFO] 기존 디렉토리 없음 (건너뜀)" -ForegroundColor Gray
}
Write-Host ""

# 4. ZIP 압축 해제
Write-Host "3. Gradle 캐시 압축 해제 중..." -ForegroundColor Yellow
Expand-Archive -Path gradle-backup.zip -DestinationPath $env:USERPROFILE -Force

if ($?) {
    Write-Host "   [OK] $gradleDir 복원 완료" -ForegroundColor Green
} else {
    Write-Host "   [ERROR] 압축 해제 실패" -ForegroundColor Red
    exit 1
}
Write-Host ""

# 5. 결과
Write-Host "=" * 60 -ForegroundColor Green
Write-Host "  복원 완료!" -ForegroundColor Cyan
Write-Host "=" * 60 -ForegroundColor Green
Write-Host ""
Write-Host "복원된 위치: $gradleDir" -ForegroundColor White
Write-Host ""
Write-Host "이제 오프라인 빌드를 실행하세요:" -ForegroundColor Yellow
Write-Host "  set JAVA_HOME=C:\Program Files\Java\jdk-17" -ForegroundColor Gray
Write-Host "  set PATH=%JAVA_HOME%\bin;%PATH%" -ForegroundColor Gray
Write-Host "  gradlew clean build --offline" -ForegroundColor Gray
Write-Host ""
```

**실행:**
```powershell
PowerShell -ExecutionPolicy Bypass -File restore-gradle-windows.ps1
```

---

## ✨ 최종 요약

### 준비된 파일 ✅
- [x] ZIP 백업 생성 (277MB, 52.8% 압축)
- [x] 3개 파일로 분할 (각 95MB 이하)
- [x] Windows 호환 형식
- [x] 자동 복원 스크립트
- [x] 상세 문서

### Git에 올릴 파일 📤
```
gradle-backup.zip.part00
gradle-backup.zip.part01
gradle-backup.zip.part02
create-zip-backup.py
split-zip.py
README_WINDOWS_ZIP.md
```

### 오프라인에서 복원 📥
1. Git 클론
2. Part 파일 합치기: `copy /b gradle-backup.zip.part* gradle-backup.zip`
3. 우클릭 → "압축 풀기" → C:\Users\<사용자>\
4. `gradlew build --offline` ✅

**Windows 전용! 간단! 빠름!** 🚀
