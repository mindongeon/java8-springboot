package com.core;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class CoreApplicationTests {

    @Test
    void contextLoads() {
        // 기본 컨텍스트 로드 테스트
        // DataSource는 자동 설정에서 제외됨
    }

}
