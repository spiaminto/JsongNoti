package io.spiaminto.jsongnoti.mail;

import io.spiaminto.jsongnoti.domain.Song;
import io.spiaminto.jsongnoti.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
@Slf4j
public class GmailSender {

    private final JavaMailSender javaMailSender; // autowire 못하는거 버그인듯
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String from;

    /**
     * html 형식으로 메일 전송
     *
     * @param users
     * @param songs
     */
    public void sendHtml(List<User> users, List<Song> songs) {
        Context context = new Context(); // thymeleaf context

        List<mailSongDto> mailSongDtos = songs.stream().map(mailSongDto::fromSong).toList();
        context.setVariable("songs", mailSongDtos);

        context.setVariable("headerText", getHeaderText());
        context.setVariable("brandName", songs.get(0).getBrand().getName());

        log.info("mailSongDtos = {}", mailSongDtos);

        // 보낼 메일 생성
        List<MimeMessagePreparator> preparatories = new ArrayList<>();
        for (User user : users) {
            MimeMessagePreparator preparatory = mimeMessage -> { // callback 이므로 테스트는 메일을 보내거나 따로 떼어서 실행
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

                helper.setFrom(from);
                helper.setTo(user.getEmail());
                helper.setSubject(getSubject());

                String content = templateEngine.process("inline-email", context);
                helper.setText(content, true);
            };
            preparatories.add(preparatory);
        }

        // 메일 전송
        javaMailSender.send(preparatories.toArray(new MimeMessagePreparator[0]));
    }

    /**
     * 이메일 맨 위에 표시될 텍스트 생성
     *
     * @return
     */
    public String getHeaderText() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        int date = now.getDayOfMonth();
        String day = now.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);
        StringBuilder sb = new StringBuilder();
        sb.append(year).append("년 ").append(month).append("월 ").append(date).append("일 (").append(day).append(") ");
        sb.append("신곡 알림이 도착했습니다. ");
        sb.append("곡 제목을 클릭하면 구글 검색으로 이동합니다.");
        return sb.toString();
    }

    /**
     * 메일 제목 생성
     *
     * @return
     */
    public String getSubject() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now()) + " 신곡 알림 도착";
    }

    // 개발용 에러 메일 전송 ===========================================================

    /**
     * 에러메일 나한테 전송
     *
     * @param message 에러메시지
     */
    public void sendError(String message) {
        String subject = getErrorSubject();
        String content = getErrorContent(message);
        String to = "jsongnoti+error@gmail.com";

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(to);
        mail.setFrom(from);
        mail.setSubject(subject);
        mail.setText(content);

        try { // 여기는 에러나면 잡아야됨
            javaMailSender.send(mail);
        } catch (Exception e) {
            log.error("에러 메일 전송 실패 e = {}, message = {}", e.getClass().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 에러메일 제목 생성
     * @return
     */
    public String getErrorSubject() {
        return "JsongNoti lambda 에러 발생 " + LocalDateTime.now();
    }

    /**
     * 메일 내용 생성
     * @param message 에러메시지
     * @return
     */
    public String getErrorContent(String message) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n")
                .append(message);
        return sb.toString();
    }


    // 아래 일반 메일 (사용 x) ===========================================================

    /**
     * 일반 텍스트 형식으로 메일 전송
     *
     * @param users
     * @param songs
     */
    public void send(List<User> users, List<Song> songs) {

        String subject = getSubject();
        String content = getContent(songs);
        List<String> to = getTo(users);

        List<SimpleMailMessage> messages = new ArrayList<>();
        for (String email : to) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setFrom(from);
            message.setSubject(subject);
            message.setText(content);
            messages.add(message);
        }

        try {
            javaMailSender.send(messages.toArray(new SimpleMailMessage[0]));
        } catch (Exception e) {
            log.error("메일 전송 실패 e = {}, message = {}", e.getClass().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 메일 내용 생성
     *
     * @param songs
     * @return
     */
    public String getContent(List<Song> songs) {
        StringBuilder sb = new StringBuilder();

        String brandName = songs.get(0).getBrand().getName();
        sb.append("\n")
                .append(brandName).append("신곡 알림\n\n");

        for (Song song : songs) {
            sb.append(song.getSinger()).append(" - ").append(song.getTitle()).append("\n");
            if (!song.getInfo().isBlank()) {
                sb.append("(").append(song.getInfo()).append(")").append("\n");
            }
            sb.append("\n");
        }

        /*
        \n
        신곡알림

        가수이름1 - 노래제목1
        (노래정보1)

        가수이름2 - 노래제목2
        (노래정보2)

        ...
         */

        return sb.toString();

    }

    /**
     * 받는 사람 이메일 목록 생성
     *
     * @param users
     * @return
     */
    public List<String> getTo(List<User> users) {
        return users.stream()
                .map(User::getEmail)
                .toList();
    }

}
