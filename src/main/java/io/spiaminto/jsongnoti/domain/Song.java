package io.spiaminto.jsongnoti.domain;

/*
        id		    PK
        number      곡 번호
		title		이름
		info		정보 ( ~~ED 같은거, 추가텍스트 )
		brand   	노래방 브랜드
		singer		가수
		composer	작곡가
		writer		작사가
		created_at	등록 날짜
 */

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static lombok.AccessLevel.*;

@Entity
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PROTECTED)
@Builder @Getter  @EqualsAndHashCode(of = {"number", "title"}) @ToString // 노래 번호, 제목으로 중복판별
public class Song {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Brand brand;

    private int number;
    private String title;
    private String info;

    private String singer;
    private String composer;
    private String writer;

    private LocalDate regDate;

    @Builder.Default
    private boolean mailed = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

}



