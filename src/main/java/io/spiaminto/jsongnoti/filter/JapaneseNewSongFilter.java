package io.spiaminto.jsongnoti.filter;

import io.spiaminto.jsongnoti.domain.Brand;
import io.spiaminto.jsongnoti.domain.Song;
import io.spiaminto.jsongnoti.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Slf4j
public class JapaneseNewSongFilter {

    private final SongRepository songRepository;

    /**
     * 일본곡이면서 신곡인 곡을 필터링 하여 반환
     * @param songs
     * @return
     */
    public List<Song> filter(List<Song> songs) {
        // 일본곡 필터링
        List<Song> japaneseSongs = songs.stream()
//                .filter(song -> songTextFilter(song.getTitle(), song.getSinger())) // 비활성화
                .filter(song -> songNumberFilter(song.getNumber(), song.getBrand()))
                .toList();

        // 이번달 신곡 필터링
        List<Song> newSongs = newSongFilter(japaneseSongs);

        return newSongs;
    }

    /**
     * 노래제목, 가수 필터
     * 제목 or 가수 중 히라가나, 가타카나, 상용한자가 포함된 경우 true
     *
     * 현재 비활성화
     * 제목과 가수명이 모두 영어인 경우 문제발생
     *  ㄴ 문제내역 :  mrs.gree apple - familie 작사 작곡은 오모리모토키로 한자이나 제목과 가수명으로 필터링하면 제외됨
     *  일단 이 필터를 수정하지 않고 비활성화 해놓고 추이를 볼 예정
     *
     * @param title
     * @return
     */
    protected boolean songTextFilter(String title, String singer) {
        // 히라가나, 가타카나, 상용한자 (한중일)
        String japaneseRegex = "[\\p{InHiragana}\\p{InKatakana}\\p{InCJKUnifiedIdeographs}]";
        Pattern pattern = Pattern.compile(japaneseRegex);
        return pattern.matcher(title).find() ||
                pattern.matcher(singer).find();
    }

    /**
     * 번호 필터
     * TJ
     * 68XXX, 52XXX 인경우 true
     * 금영
     * 76XXX 인경우 true
     *
     * @param number
     * @return
     */
    protected boolean songNumberFilter(int number, Brand brand) {
        if (brand == Brand.TJ) {
            return (number > 68000 && number < 69000) ||
                    (number > 52000 && number < 53000);
        } else if (brand == Brand.KY) {
            return (number > 76000 && number < 77000);
        } else {
            throw new IllegalArgumentException("지원하지 않는 브랜드, brand = " + brand);
        }
    }

    /**
     * DB 에 있는 곡과 비교하여 신곡인지 필터링
     * @param songs
     * @return
     */
    protected List<Song> newSongFilter(List<Song> songs) {
        // DB 에서 이번달 + 저번달 곡 조회 (이번달만 조회하면 이번달 신곡이 없을시 저번달 신곡을 거를수 없음)
        LocalDate lastMonthStartTime = LocalDate.now().withDayOfMonth(1).minusMonths(1);
        List<Song> savedSongs = songRepository.findSongsByBrandAndTimeAfter(songs.get(0).getBrand(), lastMonthStartTime);
        // savedSongs 가 비어있을 경우 IndexOutOfBoundsException 발생 (문제상황이 맞기때문에 따로 잡진 않음)

        // 이미 DB 에 저장된 곡 제거
        List<Song> newSongs = songs.stream()
                .filter(song -> savedSongs.stream()
                        .noneMatch(thisMonthSong -> thisMonthSong.getNumber() == song.getNumber()))
                .toList();
        return newSongs;
    }
}
