package com.idolcraft.mail.service;

import com.idolcraft.mail.common.MailException;
import com.idolcraft.mail.common.User;
import com.idolcraft.mail.config.MailProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Locale;

@Service
@Slf4j
public class MailSendService {
    private static final String MESSAGE_ENCODING = "UTF-8";
    private final JavaMailSender mailSender;
    private final MailProperties prop;
    private final MessageSource messageSource;

    @Autowired
    public MailSendService(JavaMailSender mailSender, MailProperties properties, MessageSource messageSource) {
        this.mailSender = mailSender;
        this.prop = properties;
        this.messageSource = messageSource;
    }

    public void send(User user, byte[] attache) {
        String[] to = user.getEmail().split(",");
        String[] bcc = null;
        if (prop.getBcc() != null && !prop.getBcc().isEmpty()) {
            bcc = prop.getBcc().split(",");
        }

        String mailHTMLBody = getMessage(
                "email.body",
                user.getGender().equalsIgnoreCase("M") ? prop.getGenderParamMale() : prop.getGenderParamFemale(),
                user.getFirstName(),
                user.getLastName());
        send(to, bcc, prop.getFrom(), prop.getSubject(), mailHTMLBody, prop.getAttacheName(), attache);
    }

    private void send(String[] to, String[] bcc, String from, String subject, String message, String attacheName, byte[] attache) {
        log.info("Trying to send email to {}, from {}, subject {}, message {}, attacheName {}, size {} byte",
                Arrays.toString(to), from, subject, message, attacheName, attache.length);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, MESSAGE_ENCODING);
            messageHelper.setTo(to);
            messageHelper.setSubject(subject);
            messageHelper.setText(message, true);
            messageHelper.setFrom(from);

            InputStreamSource inputStreamSource = () -> new ByteArrayInputStream(attache);
            messageHelper.addAttachment(attacheName, inputStreamSource);

            if (bcc != null) {
                messageHelper.setBcc(bcc);
            }

            mailSender.send(mimeMessage);

        } catch (Exception e) {
            throw new MailException(String.format("Sending email to %s filed", Arrays.toString(to)), e);
        }
    }

    private String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, Locale.getDefault());
    }
}
