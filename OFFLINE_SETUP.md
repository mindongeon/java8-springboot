# 오프라인 환경 설정 가이드

## 준비물
이 zip 파일에는 오프라인 개발에 필요한 모든 것이 포함되어 있습니다:
- ✅ 프로젝트 소스 코드
- ✅ Gradle Wrapper (인터넷 없이 빌드 가능)
- ✅ 모든 의존성 라이브러리 (libs 폴더)
- ✅ Java 8 JDK (java8.zip)

## 설치 순서

### 1. Java 8 설치
```bash
# java8.zip 압축 해제
unzip java8.zip -d /opt/java8
# 또는 원하는 경로에 압축 해제

# 환경변수 설정 (Linux/Mac)
export JAVA_HOME=/opt/java8
export PATH=$JAVA_HOME/bin:$PATH

# 환경변수 설정 (Windows)
set JAVA_HOME=C:\java8
set PATH=%JAVA_HOME%\bin;%PATH%

# Java 버전 확인
java -version
```

### 2. 오프라인 모드 활성화
```bash
# build.gradle.offline을 build.gradle로 교체
mv build.gradle build.gradle.online
mv build.gradle.offline build.gradle
```

### 3. 프로젝트 빌드
```bash
# 빌드 및 테스트
./gradlew clean build

# 애플리케이션 실행
./gradlew bootRun
```

## 트러블슈팅

### 문제: Gradle이 인터넷에 접속하려고 함
**해결:** `gradle.properties` 파일에 다음 추가
```properties
org.gradle.offline=true
```

### 문제: 의존성을 찾을 수 없음
**해결:** libs 폴더가 프로젝트 루트에 있는지 확인

### 문제: Gradle Wrapper 실행 안됨
**해결:** 실행 권한 부여
```bash
chmod +x gradlew
```

## 개발 시작하기

### IDE 설정 (IntelliJ IDEA)
1. IntelliJ IDEA 실행
2. File > Open > core 폴더 선택
3. Gradle 프로젝트로 자동 인식됨
4. Project Structure (Ctrl+Alt+Shift+S) > Project > SDK를 java8로 설정

### IDE 설정 (Eclipse)
1. Eclipse 실행
2. File > Import > Gradle > Existing Gradle Project
3. core 폴더 선택
4. Window > Preferences > Java > Installed JREs에서 java8 추가

## 빌드 산출물
빌드 후 WAR 파일 위치:
```
build/libs/core-0.0.1-SNAPSHOT.war
```

Tomcat 등의 WAS에 배포하여 사용 가능합니다.

## 추가 정보
- Spring Boot 버전: 2.7.18
- Java 버전: 8
- 빌드 도구: Gradle 7.x (Wrapper 포함)
- 프레임워크: Spring Boot + MyBatis + JSP
