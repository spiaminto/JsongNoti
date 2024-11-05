package io.spiaminto.jsongnoti.extractor;

import io.spiaminto.jsongnoti.domain.Brand;
import io.spiaminto.jsongnoti.domain.Song;
import io.spiaminto.jsongnoti.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Slf4j
public class TjExtractor {

    private final SongRepository repository;

    public List<Song> extract(Element html) {
        Elements songTable = html.select(songTableSelector);
        Elements songRows = songTable.select(songRowSelector);
        songRows.remove(0); // 헤더 (th) 제거

        List<Song> songs = new ArrayList<>();
        for (Element songRow : songRows) {
            String songNumber = songRow.select(songNumberSelector).text();
            String songTitle = songRow.select(songTitleSelector).text();
            String songSinger = songRow.select(songSingerSelector).text();
            String songWriter = songRow.select(songWriterSelector).text();
            String songComposer = songRow.select(songComposerSelector).text();

            String info = songTitle.contains("(") ?
                    songTitle.substring(songTitle.indexOf("(") + 1, songTitle.indexOf(")")) :
                    "";
            String songTitleWithoutInfo = songTitle.contains("(") ?
                    songTitle.substring(0, songTitle.indexOf("(")) :
                    songTitle;

            Song song = Song.builder().brand(Brand.TJ)
                    .number(Integer.parseInt(songNumber))
                    .title(songTitleWithoutInfo)
                    .info(info)
                    .singer(songSinger)
                    .writer(songWriter)
                    .composer(songComposer)
                    .regDate(LocalDate.now())
                    .build();

//            log.info("song = {}", song);

            songs.add(song);
        }

        return songs;

    }


    private final String songTableSelector = ".board_type1";
    private final String songRowSelector = "tbody > tr";

    private final String songNumberSelector = "td:nth-child(1)";
    private final String songTitleSelector = "td:nth-child(2)";
    private final String songSingerSelector = "td:nth-child(3)";
    private final String songWriterSelector = "td:nth-child(4)";
    private final String songComposerSelector = "td:nth-child(5)";



}

/*
<tbody>
  <tr>
    <th>곡 번호</th>
    <th>곡 제목</th>
    <th>가수</th>
    <th>작사</th>
    <th>작곡</th>
  </tr>
  <tr>
    <td>52702</td>
    <td class="left">誰我為</td>
    <td>TK from 凛として時雨</td>
    <td>TK</td>
    <td>TK</td>
  </tr>
  <tr>
    <td>68498</td>
    <td class="left">運命(TVアニメ 'ダンジョン飯' ost)</td>
    <td>sumika</td>
    <td>片岡健太</td>
    <td>片岡健太</td>
  </tr>
</tbody>
 */


