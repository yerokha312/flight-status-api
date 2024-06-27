package dev.yerokha.flightstatusapi.presentation.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import dev.yerokha.flightstatusapi.application.dto.CreateFlightRequest;
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

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static dev.yerokha.flightstatusapi.presentation.controller.AuthenticationControllerTest.accessTokenModerator;
import static dev.yerokha.flightstatusapi.presentation.controller.AuthenticationControllerTest.accessTokenUser;
import static java.time.LocalDateTime.now;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Order(2)
@SpringBootTest
@DirtiesContext
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FlightControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Order(1)
    void createFlight() throws Exception {
        LocalDateTime departure = now().plusDays(1);
        LocalDateTime arrival = now().plusDays(1).plusHours(8);
        CreateFlightRequest createFlightRequest = new CreateFlightRequest(
                "ALA",
                "LHR",
                departure,
                "+05:00",
                arrival,
                "+01:00",
                "in_time"
        );

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mmXXX");

        String requestBody = objectMapper.writeValueAsString(createFlightRequest);

        String formattedDepartureWithOffset = OffsetDateTime.of(departure, ZoneOffset.of("+05:00")).format(formatter);
        String formattedArrivalWithOffset = OffsetDateTime.of(arrival, ZoneOffset.of("+01:00")).format(formatter);
        mockMvc.perform(post("/api/v1/flights")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessTokenModerator))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.id").exists(),
                        jsonPath("$.origin").value("ALA"),
                        jsonPath("$.destination").value("LHR"),
                        jsonPath("$.departure").value(formattedDepartureWithOffset),
                        jsonPath("$.arrival").value(formattedArrivalWithOffset),
                        jsonPath("$.flightStatus").value("IN_TIME")
                );
    }

    @Test
    @Order(1)
    void createFlightWithInvalidOrigin_ShouldReturn400() throws Exception {
        LocalDateTime departure = now().plusDays(1);
        LocalDateTime arrival = now().plusDays(1).plusHours(8);
        CreateFlightRequest createFlightRequest = new CreateFlightRequest(
                "Almaty",
                "LHR",
                departure,
                "+05:00",
                arrival,
                "+01:00",
                "in_time"
        );

        String requestBody = objectMapper.writeValueAsString(createFlightRequest);

        mockMvc.perform(post("/api/v1/flights")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessTokenModerator))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value("Origin must be a valid airport code (3 or 4 uppercase letters)")
                );
    }

    @Test
    @Order(1)
    void createFlightWithUserTokenWithoutModeratorRole_ShouldReturn403() throws Exception {
        LocalDateTime departure = now().plusDays(1);
        LocalDateTime arrival = now().plusDays(1).plusHours(8);
        CreateFlightRequest createFlightRequest = new CreateFlightRequest(
                "ALA",
                "LHR",
                departure,
                "+05:00",
                arrival,
                "+01:00",
                "in_time"
        );

        String requestBody = objectMapper.writeValueAsString(createFlightRequest);

        mockMvc.perform(post("/api/v1/flights")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessTokenUser))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(2)
    void getFlightsWithoutAuthorizationToken_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/v1/flights"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(2)
    void getFlightsWithUserToken_ShouldReturn200() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/flights")
                        .header("Authorization", "Bearer " + accessTokenUser))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content").isArray(),
                        jsonPath("$.content", hasSize(10))
                )
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        List<String> publishedDates = JsonPath.read(content, "$.content[*].arrival");
        for (int i = 1; i < 10; i++) {
            assert publishedDates.get(i - 1).compareTo(publishedDates.get(i)) >= 0;
        }
    }

    @Test
    @Order(2)
    void getFlightsWithModeratorToken_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/flights")
                        .header("Authorization", "Bearer " + accessTokenModerator))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content").isArray(),
                        jsonPath("$.content", hasSize(10))
                );
    }

    @Test
    @Order(2)
    void getFlightsFilteredByOrigin_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/flights")
                        .header("Authorization", "Bearer " + accessTokenModerator)
                        .param("filter", "origin")
                        .param("origin", "ALA"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content").isArray(),
                        jsonPath("$.content", hasSize(1)),
                        jsonPath("$.content[0].origin").value("ALA"),
                        jsonPath("$.content[0].destination").value("LHR")
                );
    }

    @Test
    @Order(2)
    void getFlightsFilteredByDestination_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/flights")
                        .header("Authorization", "Bearer " + accessTokenModerator)
                        .param("filter", "destination")
                        .param("destination", "LHR"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content").isArray(),
                        jsonPath("$.content", hasSize(1)),
                        jsonPath("$.content[0].origin").value("ALA"),
                        jsonPath("$.content[0].destination").value("LHR")
                );
    }

    @Test
    @Order(2)
    void getFlightsFilteredByOriginAndDestination_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/flights")
                        .header("Authorization", "Bearer " + accessTokenModerator)
                        .param("filter", "origin_and_destination")
                        .param("origin", "ALA")
                        .param("destination", "LHR"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content").isArray(),
                        jsonPath("$.content", hasSize(1)),
                        jsonPath("$.content[0].origin").value("ALA"),
                        jsonPath("$.content[0].destination").value("LHR")
                );
    }

    @Test
    @Order(2)
    void getFlightsWithInvalidFilterType_ShouldReturn400() throws Exception {
        mockMvc.perform(get("/api/v1/flights")
                        .header("Authorization", "Bearer " + accessTokenModerator)
                        .param("filter", "invalid_filter_type")
                        .param("invalid_filter_type", "VAL"))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value("Invalid filter type: invalid_filter_type")
                );
    }

    @Test
    @Order(2)
    void getOneFlightById_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/flights/100000")
                        .header("Authorization", "Bearer " + accessTokenModerator))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    void getOneFlightWithInvalidId_ShouldReturn400() throws Exception {
        mockMvc.perform(get("/api/v1/flights/0")
                        .header("Authorization", "Bearer " + accessTokenModerator))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value("Invalid flight ID")
                );
    }

    @Test
    @Order(3)
    void updateFlightStatusWithModeratorToken_ShouldReturn200() throws Exception {
        MvcResult getResult = mockMvc.perform(get("/api/v1/flights/100000")
                        .header("Authorization", "Bearer " + accessTokenModerator))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = getResult.getResponse().getContentAsString();
        JsonNode flightNode = objectMapper.readTree(responseContent);
        String currentStatus = flightNode.get("flightStatus").asText();

        String newStatus = switch (currentStatus) {
            case "IN_TIME" -> "DELAYED";
            case "DELAYED" -> "CANCELLED";
            default -> "IN_TIME";
        };

        mockMvc.perform(put("/api/v1/flights/100000")
                        .header("Authorization", "Bearer " + accessTokenModerator)
                        .contentType(MediaType.TEXT_PLAIN_VALUE)
                        .content(newStatus))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/flights/100000")
                        .header("Authorization", "Bearer " + accessTokenModerator))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flightStatus").value(newStatus));
    }

    @Test
    @Order(3)
    void updateFlightStatusWithUserToken_ShouldReturn403() throws Exception {
        mockMvc.perform(put("/api/v1/flights/100000")
                        .header("Authorization", "Bearer " + accessTokenUser)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("IN_TIME"))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(3)
    void updateFlightStatusWithoutToken_ShouldReturn401() throws Exception {
        mockMvc.perform(put("/api/v1/flights/100000")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("IN_TIME"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(3)
    void updateFlightStatusWithInvalidStatus_ShouldReturn400() throws Exception {
        mockMvc.perform(put("/api/v1/flights/100000")
                        .header("Authorization", "Bearer " + accessTokenModerator)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("INVALID_STATUS"))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value("Invalid flight status. Try again")
                );
    }

    @Test
    @Order(3)
    void updateFlightStatusThatDoesNotExist_ShouldReturn404() throws Exception {
        mockMvc.perform(put("/api/v1/flights/99999")
                        .header("Authorization", "Bearer " + accessTokenModerator)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("CANCELLED"))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.message").value("Flight with ID 99999 not found")
                );
    }

    @Test
    @Order(3)
    void updateFlightStatusWithNegativeId_ShouldReturn400() throws Exception {
        mockMvc.perform(put("/api/v1/flights/-1")
                        .header("Authorization", "Bearer " + accessTokenModerator)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("CANCELLED"))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value("Invalid flight ID")
                );
    }
}