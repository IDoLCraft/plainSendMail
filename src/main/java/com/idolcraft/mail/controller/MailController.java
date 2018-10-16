package com.idolcraft.mail.controller;

import com.idolcraft.mail.common.MailException;
import com.idolcraft.mail.common.MailParameters;
import com.idolcraft.mail.service.MailSendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.Files;
import java.util.Collections;

import static com.idolcraft.mail.controller.MailController.URL_REGISTRATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@Slf4j
@RestController
@RequestMapping(URL_REGISTRATION)
public class MailController {
    public static final String URL_REGISTRATION = "/idc/mail";
    @Value("${classpath:src/main/resources/pdf/report.pdf}")
    private File filePdf;

    private final MailSendService mailSendService;

    @Autowired
    public MailController(MailSendService mailSendService) {
        this.mailSendService = mailSendService;
    }

    @PostMapping(consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity sendMail(@RequestBody MailParameters mailParameters) {

        log.info("sendMail, {}", mailParameters);
        byte[] file = new byte[1];
        try {
            file = Files.readAllBytes(filePdf.toPath());
        } catch(Exception e) {
            throw new MailException("could't read file", e);
        }
        mailSendService.send(mailParameters.getUser(), file);

        return ResponseEntity.ok(Collections.singletonMap("sendMail", mailParameters.getUser().getEmail()));
    }

}
