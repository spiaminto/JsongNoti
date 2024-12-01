package io.spiaminto.jsongnoti.config;

import io.spiaminto.jsongnoti.service.KYService;
import io.spiaminto.jsongnoti.service.TJService;
import io.spiaminto.jsongnoti.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.function.Consumer;

// spring cloud function aws adaptor (for lambda) 의 함수 핸들러 등록 부분
// 레퍼런스 : https://docs.spring.io/spring-cloud-function/reference/adapters/aws-intro.html
@Configuration
@RequiredArgsConstructor
public class LambdaConfiguration {
    private final TJService tjService;
    private final KYService kyService;
    private final UserService userService;

    @Bean // 반환타입 void 의 Json 입력 ( Map<String, String> ) 을 받아서 처리하는 핸들러 등록
    public Consumer<Map<String, String>> processRequest() { // class B (ByteArray) 는 아마 InputStream
        return input -> {
            tjService.start();
            kyService.start();
            userService.clearAuthentications();
        };
    }
    // SPRING_CLOUD_FUNCTION_DEFINITION 환경변수에 processRequest 라는 이름으로 핸들러 별도 등록
}
