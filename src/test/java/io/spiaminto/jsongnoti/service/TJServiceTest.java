package io.spiaminto.jsongnoti.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Slf4j
public class TJServiceTest {
    @Autowired TJService tjService;

    @Test
    @Transactional
    void start() {
        tjService.start();
    }
}
