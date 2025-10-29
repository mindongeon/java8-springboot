# Spring Boot 오프라인 프로젝트

## 실행 방법

```bash
# 빌드
./gradlew clean build -x test

# 실행
./gradlew bootRun -x test
```

## 기술 스택
- Spring Boot 2.7.18
- MyBatis 2.3.2
- JSP + JSTL
- Tomcat 9.0.83
- Java 8

## 설정
- DataSource: Tomcat JNDI (application.yml에서 설정)
- 로깅: logback-spring.xml
- MyBatis Mapper: src/main/resources/mapper/

## 예시 코드
- Model: com.core.model.User
- Mapper: com.core.mapper.UserMapper
- Service: com.core.service.UserService
- Controller: com.core.controller.UserController
