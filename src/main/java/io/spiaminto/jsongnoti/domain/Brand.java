package io.spiaminto.jsongnoti.domain;

import lombok.Getter;

@Getter
public enum Brand {
    TJ("TJ"),
    KY("금영"),
    TEST("테스트");

    private final String name;

    Brand(String name) {
        this.name = name;
    }
}
