package dev.yerokha.flightstatusapi.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.yerokha.flightstatusapi.infrastructure.dto.LoginRequest;
import dev.yerokha.flightstatusapi.infrastructure.dto.RegistrationRequest;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Order(1)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    public static String accessToken;
    public static String refreshToken;

    @Test
    @Order(1)
    void registerUser_ShouldReturn201AndSuccessMessage() throws Exception {
        RegistrationRequest request = new RegistrationRequest("testappuser", "password");
        String json = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/api/v1/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.message").value("Registration success! Please login")
                );
    }

    @Test
    @Order(1)
    void registerUser_ShouldReturn400PasswordIsTooShort() throws Exception {
        RegistrationRequest request = new RegistrationRequest("testuser", "passwor");
        String json = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/api/v1/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value("Password length must be between 8 and 30")
                );
    }

    @Test
    @Order(1)
    void register_ShouldReturn201AndSuccessMessage() throws Exception {
        RegistrationRequest request = new RegistrationRequest("testuser", "password");
        String json = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/api/v1/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.message").value("Registration success! Please login")
                );
    }

    @Test
    @Order(2)
    void login_ShouldReturn200AndSuccessMessage() throws Exception {
        LoginRequest request = new LoginRequest("testappuser", "password");
        String json = objectMapper.writeValueAsString(request);
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/auth/login")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.accessToken").isNotEmpty(),
                        jsonPath("$.refreshToken").isNotEmpty()
                )
                .andReturn();

        accessToken = extractToken(mvcResult.getResponse().getContentAsString(), "accessToken");
        refreshToken = extractToken(mvcResult.getResponse().getContentAsString(), "refreshToken");
    }

    @Test
    @Order(2)
    void login_ShouldReturn401AndFailureMessage() throws Exception {
        LoginRequest request = new LoginRequest("username", "invalid_password");
        String json = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/api/v1/auth/login")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isUnauthorized(),
                        jsonPath("$.message").value("Invalid username or password")
                );
    }

    @Test
    void refreshToken_ShouldReturn200AndNewAccessToken() throws Exception {
        Thread.sleep(1000);
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/auth/refresh-token")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("Bearer " + refreshToken))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.accessToken").isNotEmpty()
                )
                .andReturn();

        String newAccessToken = extractToken(mvcResult.getResponse().getContentAsString(),
                "accessToken");

        Assertions.assertNotEquals(accessToken, newAccessToken,
                "New access token should be different from the initial one");

        accessToken = newAccessToken;
    }

    static String extractToken(String responseContent, String tokenName) throws JSONException {
        JSONObject jsonResponse = new JSONObject(responseContent);
        return jsonResponse.getString(tokenName);
    }
}