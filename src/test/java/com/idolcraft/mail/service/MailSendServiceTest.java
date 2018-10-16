package com.idolcraft.mail.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;
import com.idolcraft.mail.PayloadFile;
import com.idolcraft.mail.common.MailException;
import com.idolcraft.mail.common.MailParameters;
import com.idolcraft.mail.config.MailProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;

import java.io.File;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Slf4j
public class MailSendServiceTest {
    @Autowired
    private MailProperties properties;
    @Autowired
    private MailSendService mailSendService;
    @Autowired
    private ObjectMapper objectMapper;
    @Value("${classpath:src/main/resources/pdf/report.pdf}")
    private File filePdf;
    @Value("${classpath:src/test/resources/files/mail-request.json}")
    private File mailRequest;

    private GreenMail greenMail;

    @Before
    public void startMailServer() throws Exception {
        ServerSetup setup = new ServerSetup(3025, "localhost", "smtp");
        greenMail = new GreenMail(setup);
        greenMail.start();
    }

    @Test
    public void testEmailOk() throws Exception {
        greenMail.setUser("idcmaildev@gmail.com", "testpwd");

        String to = "test@sender.com";
        byte[] byteArr = new byte[1];
        try {
            byteArr = Files.readAllBytes(filePdf.toPath());
        } catch(Exception e) {
            throw new MailException("could't read file", e);
        }

        MailParameters mailParameters = objectMapper.readValue(
                new PayloadFile(mailRequest).contentAsString(),
                MailParameters.class
        );

        mailSendService.send(mailParameters.getUser(), byteArr);

        Message[] messages = greenMail.getReceivedMessages();
        assertEquals(2, messages.length);
        //to
        assertEquals(1, messages[0].getRecipients(Message.RecipientType.TO).length);
        InternetAddress toAddress = (InternetAddress) messages[0].getRecipients(Message.RecipientType.TO)[0];
        assertEquals(to, toAddress.getAddress());
        //bcc
        assertEquals(1, messages[1].getRecipients(Message.RecipientType.TO).length);
        InternetAddress bccAddress = (InternetAddress) messages[1].getRecipients(Message.RecipientType.TO)[0];
        assertEquals(to, toAddress.getAddress());
        //from
        assertEquals(1, messages[0].getFrom().length);
        assertEquals(properties.getFrom(), messages[0].getFrom()[0].toString());
        //subject
        assertEquals(properties.getSubject(), messages[0].getSubject());
        //message & attacheName
        String body = GreenMailUtil.getBody(messages[0]).replaceAll("=\r?\n", "");
        assertTrue(body.contains("firstName"));
        assertTrue(body.contains("lastName"));
        assertTrue(body.contains(properties.getAttacheName()));
    }

    @Test(expected = MailException.class)
    public void testEmailFailed() throws Exception {
        assertEquals(0, greenMail.getReceivedMessages().length);

        String to = "test@sender.com";
        byte[] byteArr = new byte[1];

        MailParameters mailParameters = objectMapper.readValue(
                new PayloadFile(mailRequest).contentAsString(),
                MailParameters.class
        );

        mailSendService.send(mailParameters.getUser(), byteArr);
    }

    @After
    public void cleanup(){
        greenMail.stop();
    }
}