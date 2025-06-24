package org.example.v1.mailauth;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final StringRedisTemplate redisTemplate;
    private static final long EXPIRE_TIME = 3 * 60; // 3분

    public void sendAuthCode(String email) {
//        if (!isUniversityEmail(email)) {
//            throw new IllegalArgumentException("대학교 이메일 주소만 인증할 수 있습니다.");
//        }

        String code = generateCode();
        saveCodeToRedis(email, code);
        sendEmail(email, code);
    }

    private boolean isUniversityEmail(String email) {
        return email.toLowerCase().endsWith(".ac.kr");
    }

    private void saveCodeToRedis(String email, String code) {
        redisTemplate.opsForValue().set(email, code, EXPIRE_TIME, TimeUnit.SECONDS);
    }

    private String generateCode() {
        return String.valueOf(new Random().nextInt(899999) + 100000); // 6자리 숫자
    }

    private void sendEmail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("캠북스 - 이메일 인증 코드입니다.");
        message.setText("인증 코드: " + code);
        mailSender.send(message);
    }

    public boolean verifyCode(String email, String code) {
        String savedCode = redisTemplate.opsForValue().get(email);
        boolean success = code.equals(savedCode);

        if (success) {
            redisTemplate.opsForValue().set("verified:" + email, "true", 10, TimeUnit.MINUTES);
            redisTemplate.delete(email);
        }

        return success;
    }
    public boolean isVerified(String email) {
        return "true".equals(redisTemplate.opsForValue().get("verified:" + email));
    }
}
