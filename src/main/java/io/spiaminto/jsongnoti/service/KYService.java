package io.spiaminto.jsongnoti.service;

import io.spiaminto.jsongnoti.domain.Brand;
import io.spiaminto.jsongnoti.domain.Song;
import io.spiaminto.jsongnoti.domain.User;
import io.spiaminto.jsongnoti.extractor.KyExtractor;
import io.spiaminto.jsongnoti.filter.JapaneseNewSongFilter;
import io.spiaminto.jsongnoti.mail.GmailSender;
import io.spiaminto.jsongnoti.repository.SongRepository;
import io.spiaminto.jsongnoti.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class KYService {

    private final KyExtractor kyExtractor;
    private final JapaneseNewSongFilter japaneseNewSongFilter;
    private final SongRepository songRepository;
    private final GmailSender gmailSender;
    private final UserRepository userRepository;

    private String songSelector = ".search_chart_list";
    // 금영은 페이지단위로 가져와야함. 곡 하나의 선택자. 한페이지에 헤더(1) + 콘텐츠(10) 총 11개

    public void start() {
        // 접속후 페이지 파싱
        List<Element> songElements = connectAndParse();

        // html 에서 추출
        List<Song> extractedSongs = kyExtractor.extract(songElements);

        if (extractedSongs.isEmpty()) {
            log.info("[KYService] extractedSongs is empty");
            return;
        }

        // 이번달 일본 신곡 필터링
        List<Song> newSongs = japaneseNewSongFilter.filter(extractedSongs);
        newSongs.forEach(song -> log.info("new song = {}", song));

        // 신곡 없음
        if (newSongs.isEmpty()) {
            log.info("[KYService] newSongs is empty");
            return;
        }

        // DB 저장
        songRepository.saveAll(newSongs);

        // 메일 전송
        List<User> users = userRepository.findAllByVerifiedIsTrue();
        gmailSender.sendHtml(users, newSongs);
    }

    public List<Element> connectAndParse() {
        // 접속후 페이지 파싱
        List<Element> kySongElements = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            try {
                log.info("connecting page {}... url = {}", i, "https://kysing.kr/latest/?s_page=" + i);
                Element kyHtml = Jsoup.connect("https://kysing.kr/latest/?s_page=" + i).get();
                Elements songElements = kyHtml.select(songSelector); // 곡이 잇는지 선택
                if (songElements.size() <= 1) break; // 더이상 곡이 안나오면 종료

                songElements.remove(0); // 헤더 (곡번호, 곡제목, ...) 제거
                kySongElements.addAll(songElements);
//                Thread.sleep(200); // 0.2초 대기 // 얻어오는 속도가 생각보다 느려서 대기 해제

//                if (i > 10)break; // 테스트용 컷

            } catch (IOException e) {
                log.error("Jsoup.get() e = {} e.getMessage() = {}", e.getClass().getName(), e.getMessage());
            } catch (Exception e) {
                log.error("Jsoup.get() Unknown Error");
                e.printStackTrace();
            }
        }// connect for
        return kySongElements;
    }
}

/*
<ul class="search_chart_list clear">
    <li class="search_chart_chk">
        <input type="checkbox" name="dummy" id="songAll">
    </li>
    <li class="search_chart_num">곡번호</li>
    <li class="search_chart_tit">곡명<span class="mo-sng">/아티스트</span></li>
    <li class="search_chart_sng">아티스트</li>
    <li class="search_chart_cmp">작곡가</li>
    <li class="search_chart_wrt">작사가</li>
    <li class="search_chart_rel">출시월</li>
    <li class="search_chart_ytb"><span>유튜브</span></li>
    <li class="search_chart_lts"><span>LTS</span><span class="mo-svc">서비스</span></li>
    <li class="search_chart_fav"><span>애창곡</span></li>
    <li class="search_chart_pck"><span>PICK</span></li>
</ul>
<ul class="search_chart_list clear">

  <li class="search_chart_chk">
      <input type="checkbox" name="songseq[]" id="song_53698" class="ab" value="53698">
  </li>
  <li class="search_chart_num">53698</li>
      <li class="search_chart_tit clear">
         <span title="Touch" class="tit">Touch</span>
         <span title="KATSEYE" class="tit mo-art">KATSEYE</span>
         <img class="LyricsView" src="/wp-content/uploads/2021/01/ly.png">
         <!-- Lyrics Popup -->
         <div id="LyricsView" class="LyricsWrap clear">
             <p class="LyricsClose">닫기</p>
             <div class="LyricsCont">
                 <p class="LyricsTit">Touch</p>
                 Touch touch<br>
touch touch touch<br>
Thought about you way<br>
too<br>...
                </div>
            </div>
        </li>
    <li class="search_chart_sng" title="KATSEYE">KATSEYE</li>
    <li class="search_chart_cmp" title="Blake Slatkin,Omer F..">Blake Slatkin,Omer F..</li>
    <li class="search_chart_wrt" title="Words &amp; Music by">Words &amp; Music by</li>
        <li class="search_chart_rel">2024.11</li>
        <li class="search_chart_ytb"><img src="/wp-content/uploads/2021/01/you_gray.png"></li>
        <li class="search_chart_lts"><img src="/wp-content/uploads/2021/01/lts.png"><a href="#" class="mo-fav" onclick="javascript: document.getElementById('song_45717').checked = true; document.getElementById('submit_target').value = 'favorite'; document.search_chart_frm.submit();"><img src="/wp-content/uploads/2021/01/chart_mic.png"></a></li>
    <li class="search_chart_fav">
        <a href="#" onclick="javascript: document.getElementById('song_53698').checked = true; document.getElementById('submit_target').value = 'favorite'; document.search_chart_frm.submit();"><img src="/wp-content/uploads/2021/01/chart_mic.png"></a>
    </li>
    <li class="search_chart_pck">
        <a class="pick_click" href="#" onclick="javascript: document.getElementById('song_53698').checked = true; document.getElementById('submit_target').value = 'pick'; document.search_chart_frm.submit();"><img src="/wp-content/uploads/2021/01/plus.png"></a>
    </li>
</ul>
 */
