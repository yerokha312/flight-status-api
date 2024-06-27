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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Order(1)
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    public static String accessTokenModerator;
    public static String accessTokenUser;
    public static String refreshTokenModerator;
    public static String refreshTokenUser;

    @Test
    @Order(1)
    void registerUser_ShouldReturn201AndSuccessMessage() throws Exception {
        RegistrationRequest request = new RegistrationRequest("testappuser", "p@ssw0rD");
        String requestBody = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/api/v1/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.message").value("Registration success! Please login")
                );
    }

    @Test
    @Order(1)
    void registerUser_ShouldReturn400PasswordIsTooShort() throws Exception {
        RegistrationRequest request = new RegistrationRequest("testuser", "passwor");
        String requestBody = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/api/v1/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value("Password length must be between 8-30 characters, and should contain at least 1 upper, 1 lower and 1 special symbol")
                );
    }

    @Test
    @Order(1)
    void registerUser_ShouldReturn409AndIsAlreadyTakenException() throws Exception {
        RegistrationRequest request = new RegistrationRequest("testappuser", "p@ssw0rD");
        String requestBody = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/api/v1/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpectAll(
                        status().isConflict(),
                        jsonPath("$.message").value("Username is already taken. Try another one.")
                );
    }

    @Test
    @Order(2)
    void login_ShouldReturn200AndSuccessMessage() throws Exception {
        LoginRequest request = new LoginRequest("moderator", "password");
        String requestBody = objectMapper.writeValueAsString(request);
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/auth/login")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.accessToken").isNotEmpty(),
                        jsonPath("$.refreshToken").isNotEmpty()
                )
                .andReturn();

        accessTokenModerator = extractToken(mvcResult.getResponse().getContentAsString(), "accessToken");
        refreshTokenModerator = extractToken(mvcResult.getResponse().getContentAsString(), "refreshToken");
    }

    @Test
    @Order(2)
    void login_ShouldReturn200AndSuccessMessageForUser() throws Exception {
        Thread.sleep(1000);
        LoginRequest request = new LoginRequest("testappuser", "p@ssw0rD");
        String requestBody = objectMapper.writeValueAsString(request);
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/auth/login")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.accessToken").isNotEmpty(),
                        jsonPath("$.refreshToken").isNotEmpty()
                )
                .andReturn();

        accessTokenUser = extractToken(mvcResult.getResponse().getContentAsString(), "accessToken");
        refreshTokenUser = extractToken(mvcResult.getResponse().getContentAsString(), "refreshToken");
    }

    @Test
    @Order(2)
    void login_ShouldReturn401AndFailureMessage() throws Exception {
        LoginRequest request = new LoginRequest("username", "invalid_password");
        String requestBody = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/api/v1/auth/login")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isUnauthorized(),
                        jsonPath("$.message").value("Invalid username or password")
                );
    }

    @Test
    @Order(3)
    void refreshToken_ShouldReturn200AndNewAccessToken() throws Exception {
        Thread.sleep(1000);
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/auth/refresh-token")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("Bearer " + refreshTokenModerator))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.accessToken").isNotEmpty()
                )
                .andReturn();

        String newAccessToken = extractToken(mvcResult.getResponse().getContentAsString(),
                "accessToken");

        Assertions.assertNotEquals(accessTokenModerator, newAccessToken,
                "New access token should be different from the initial one");

        accessTokenModerator = newAccessToken;
    }

    @Test
    @Order(3)
    void refreshToken_ShouldReturn401AndWarnAboutMalformedToken() throws Exception {
        Thread.sleep(1000);
        mockMvc.perform(post("/api/v1/auth/refresh-token")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("Bearer " + "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJ1c2VybmFtZSIsInJvbGUiOiJVU0VSIiwiaXNzIjoic2VsZiIsImV4cCI6MTcyMDAzNjEwOCwidG9rZW5UeXBlIjoiUkVGUkVTSCIsImlhdCI6MTcxOTQzMTMwOCwidXNlcklkIjoyMDJ9.ZzSkdRD96tMXynWgLIj-OZ0r2ocoTyHFoEaZf_60ajoSMb_B23xDvRvyne8GS5C80Cpry_qSa0xjfWGIVWNsNq5T002PPCX70-IEuevGn6TiPahcAjrE9KSrEVrLYSJ2gw3Au4co2BgH7D4X2NCf1tYtkftcCFE6qg2Rly1iYN2KR9gvf9nT6fxfjU_yQjN8oDIeeb0dG3atFa3TBHOFDu6nJHWcRHa08aQXYd3kp09t1o2gMfVKJ72u4SjAjID68z7mBF6O_tmG_m1u-gS1FlzxuZjGWs1BbideDpYw5c5teqJ-Br5NwDGdgMEn5ui5a33ZNmtiDSf8iR-P8QWtsw"))
                .andExpectAll(
                        status().isUnauthorized(),
                        jsonPath("$.message").isNotEmpty()
                );

    }

    static String extractToken(String responseContent, String tokenName) throws JSONException {
        JSONObject jsonResponse = new JSONObject(responseContent);
        return jsonResponse.getString(tokenName);
    }
}