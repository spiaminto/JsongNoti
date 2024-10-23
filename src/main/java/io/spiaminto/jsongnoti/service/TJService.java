package io.spiaminto.jsongnoti.service;

import io.spiaminto.jsongnoti.domain.Brand;
import io.spiaminto.jsongnoti.domain.Song;
import io.spiaminto.jsongnoti.domain.User;
import io.spiaminto.jsongnoti.extractor.TjExtractor;
import io.spiaminto.jsongnoti.filter.JapaneseNewSongFilter;
import io.spiaminto.jsongnoti.mail.GmailSender;
import io.spiaminto.jsongnoti.repository.SongRepository;
import io.spiaminto.jsongnoti.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TJService {

    private final TjExtractor tjExtractor;
    private final JapaneseNewSongFilter japaneseNewSongFilter;
    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final GmailSender gmailSender;

    public void start() {
        // 접속후 페이지 파싱
        Element tjHtml = new Element("div");
        try {
            tjHtml = Jsoup.connect("https://www.tjmedia.com/tjsong/song_monthNew.asp").get();
        } catch (IOException e) {
            log.error("Jsoup.get() e = {} e.getMessage() = {}", e.getClass().getName(), e.getMessage());
        } catch (Exception e) {
            log.error("Jsoup.get() Unknown Error");
            e.printStackTrace();
        }

        // html 에서 곡 추출
        List<Song> extractedSongs = tjExtractor.extract(tjHtml);

        if (extractedSongs.isEmpty()) {
            log.info("[TJService] extractedSongs is empty");
            return;
        }

        // 이번달 일본 신곡 필터링
        List<Song> newSongs = japaneseNewSongFilter.filter(extractedSongs);
        newSongs.forEach(song -> log.info("new song = {}", song));

        // 신곡 없음
        if (newSongs.isEmpty()) {
            log.info("[TJService] newSongs is empty");
            return;
        }

        // DB 저장
        songRepository.saveAll(newSongs);

        // 메일 전송
        List<User> users = userRepository.findAllByVerifiedIsTrue();
        gmailSender.sendHtml(users, newSongs);
    }



}
