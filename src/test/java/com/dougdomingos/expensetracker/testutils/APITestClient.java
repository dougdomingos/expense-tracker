package com.dougdomingos.expensetracker.testutils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Helper class for simulating API requests.
 */
@RequiredArgsConstructor
public class APITestClient {

    private final String BASE_URI;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Setter
    private String route = "";

    @Setter
    private MockMvc driver;

    /**
     * Make a GET request to the specified route and returns the response.
     * 
     * @param content       The content of the request
     * @param expectMatcher The expected status of the response
     * @return The contents of the response, as a string
     * @throws Exception
     */
    public String makeGetRequest(
            Object content,
            ResultMatcher expectMatcher) throws Exception {

        return makeRequest(get(BASE_URI + route), content, expectMatcher);
    }

    /**
     * Make a POST request to the specified route and returns the response.
     * 
     * @param content       The content of the request
     * @param expectMatcher The expected status of the response
     * @return The contents of the response, as a string
     * @throws Exception
     */
    public String makePostRequest(
            Object content,
            ResultMatcher expectMatcher) throws Exception {

        return makeRequest(post(BASE_URI + route), content, expectMatcher);
    }

    /**
     * Make a PUT request to the specified route and returns the response.
     * 
     * @param content       The content of the request
     * @param expectMatcher The expected status of the response
     * @return The contents of the response, as a string
     * @throws Exception
     */
    public String makePutRequest(
            Object content,
            ResultMatcher expectMatcher) throws Exception {

        return makeRequest(put(BASE_URI + route), content, expectMatcher);
    }

    /**
     * Make a DELETE request to the specified route and returns the response.
     * 
     * @param content       The content of the request
     * @param expectMatcher The expected status of the response
     * @return The contents of the response, as a string
     * @throws Exception
     */
    public String makeDeleteRequest(
            Object content,
            ResultMatcher expectMatcher) throws Exception {

        return makeRequest(delete(BASE_URI + route), content, expectMatcher);
    }

    private String makeRequest(
            MockHttpServletRequestBuilder method,
            Object content,
            ResultMatcher expectMatcher) throws Exception {

        return driver.perform(method
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(content)))
                .andExpect(expectMatcher)
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
    }

}
