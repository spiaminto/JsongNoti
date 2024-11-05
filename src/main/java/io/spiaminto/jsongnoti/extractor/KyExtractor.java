package io.spiaminto.jsongnoti.extractor;

import io.spiaminto.jsongnoti.domain.Brand;
import io.spiaminto.jsongnoti.domain.Song;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class KyExtractor {

    private String numberSelector = ".search_chart_num";
    private String titleSelector = ".search_chart_tit>span:nth-child(1)";
    private String singerSelector = ".search_chart_sng";
    private String composerSelector = ".search_chart_cmp";
    private String writerSelector = ".search_chart_wrt";
    private String regDateSelector = ".search_chart_rel"; // 출시월, 사용x

    public List<Song> extract(List<Element> songElements) {
        List<Song> songs = new ArrayList<>();

        for (Element element : songElements) {
            String number = element.select(numberSelector).text();
            String title = element.select(titleSelector).text();
            String singer = element.select(singerSelector).text();
            String composer = element.select(composerSelector).text();
            String writer = element.select(writerSelector).text();

//            log.info("number = {}", number);
//            log.info("title = {}", title);
//            log.info("singer = {}", singer);
//            log.info("composer= {}", composer);
//            log.info("writer = {}", writer);

            String info;
            String songTitleWithoutInfo;
            if (title.contains("(")) {
                // 곡 정보 있음
                if (title.contains(")")) { // 곡 정보 닫힘 괄호 여부
                    info = title.substring(title.indexOf("(") + 1, title.indexOf(")"));
                } else {
                    info = title.substring(title.indexOf("(") + 1);
                }
                songTitleWithoutInfo = title.substring(0, title.indexOf("(")).stripTrailing();
            } else {
                // 곡 정보 없음
                info = "";
                songTitleWithoutInfo = title;
            }

//            log.info("info = {}", info);
//            log.info("songTitleWithoutInfo = {}", songTitleWithoutInfo);

            Song song = Song.builder().brand(Brand.KY)
                    .number(Integer.parseInt(number))
                    .title(songTitleWithoutInfo)
                    .info(info)
                    .singer(singer)
                    .writer(writer)
                    .composer(composer)
                    .regDate(LocalDate.now())
                    .build();

            songs.add(song);
        }

        return songs;
    }



}
