package com.idolcraft.mail;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.File;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ExpectedResponse {
    private final String requestPath;
    private final PayloadFile payload;
    private final MockMvc mockMvc;


    public ExpectedResponse(
        MockMvc mockMvc,
        String requestPath,
        File requestPayload) {
        this(mockMvc, requestPath, new PayloadFile(requestPayload));
    }

    public ExpectedResponse(
        MockMvc mockMvc,
        String requestPath,
        PayloadFile requestPayload) {
        this.mockMvc = mockMvc;
        this.requestPath = requestPath;
        this.payload = requestPayload;
    }

    public ExpectedResponse(
        MockMvc mockMvc,
        String requestPath) {
        this(mockMvc, requestPath, (PayloadFile) null);
    }

    private void perform(MockHttpServletRequestBuilder requestBuilder, ResultMatcher... expect) throws Exception {
        requestBuilder
            .contentType(MediaType.APPLICATION_JSON_UTF8);

        if (payload != null)
            requestBuilder.content(
                payload.bytes()
            );

        ResultActions action = mockMvc.perform(
            requestBuilder
        );

        for (ResultMatcher matcher : expect) {
            action.andExpect(matcher);
        }

        action.andDo(print())
            .andReturn();
    }

    public void post(File file) throws Exception {
        post(
            content().json(new PayloadFile(file).contentAsString()),
            status().is2xxSuccessful()
        );
    }

    public void post(ResultMatcher... expect) throws Exception {
        perform(
            MockMvcRequestBuilders.post(requestPath),
            expect
        );
    }

    public void patch(File file) throws Exception {
        patch(
            content().json(new PayloadFile(file).contentAsString()),
            status().is2xxSuccessful()
        );
    }

    public void patch(ResultMatcher... expect) throws Exception {
        perform(
            MockMvcRequestBuilders.patch(requestPath),
            expect
        );
    }

    public void get(ResultMatcher... expect) throws Exception {
        perform(
            MockMvcRequestBuilders.get(requestPath),
            expect
        );
    }

    public void get(File file) throws Exception {
        get(
            content().json(new PayloadFile(file).contentAsString()),
            status().is2xxSuccessful()
        );
    }
}
