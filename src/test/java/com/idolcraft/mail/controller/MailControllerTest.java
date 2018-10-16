package com.idolcraft.mail.controller;

import com.idolcraft.mail.ExpectedResponse;
import com.idolcraft.mail.service.MailSendService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class MailControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private MailSendService mailSendService;
    @Value("${classpath:src/test/resources/files/mail-request.json}")
    private File mailRequest;
    private final String mailUrl = "/idc/mail";

    @Test
    public void mailSendOk() throws Exception {

        doNothing().when(mailSendService)
                .send(any(), any());

        new ExpectedResponse(
                mockMvc,
                mailUrl,
                mailRequest
        ).post(
                status().isOk()
        );
    }

}
