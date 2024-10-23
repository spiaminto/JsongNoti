package io.spiaminto.jsongnoti.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.spiaminto.jsongnoti.extractor.TjExtractor;
import io.spiaminto.jsongnoti.service.KYService;
import io.spiaminto.jsongnoti.service.TJService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

//LEGACY
//@Slf4j
//@Component
//@NoArgsConstructor // 기본 생성자가 없으면 람다가 실행하지 못함
//public class LambdaHandler implements RequestHandler<Map<String, String>, Void> {
//
//    @Autowired private  TJService tjService;
//    @Autowired private  KYService kyService;
//
//    @Override
//    public Void handleRequest(Map<String, String> input, Context context) {
//
//        tjService.start();
//        kyService.start();
//        return null;
//    }
//}
