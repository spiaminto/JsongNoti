package io.spiaminto.jsongnoti.mail;

import io.spiaminto.jsongnoti.domain.Song;
import lombok.Data;

/**
 * 메일에 담길 노래 정보
 */
@Data
public class mailSongDto {
    private int number;
    private String title;
    private String singer;
    private String info;
    private String searchUrl; // google 검색 파라미터에 노래제목 붙인 검색용 url

    public static mailSongDto fromSong(Song song) {
        mailSongDto mailSongDto = new mailSongDto();
        mailSongDto.setNumber(song.getNumber());
        mailSongDto.setTitle(song.getTitle());
        mailSongDto.setSinger(song.getSinger());
        mailSongDto.setInfo(song.getInfo());
        mailSongDto.setSearchUrl("https://www.google.com/search?q=" + song.getSinger() + " - " + song.getTitle());
        return mailSongDto;
    }
}
