package ru.practicum.statistic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import stat.dto.StatCreationDto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EwmStatisticClient {
    private final ObjectMapper objectMapper;
    private final RestTemplate rest;
    private final String uri;

    @Autowired
    public EwmStatisticClient(@Value("${server.url}") String serverUri, RestTemplateBuilder builder) {
        objectMapper = JsonMapper.builder().build();
        uri = serverUri;
        rest = builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUri))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    public ResponseEntity<Object> saveStatistic(String ip, String requestURI) {
        StatCreationDto statCreationDto = StatCreationDto.builder()
                .ip(ip)
                .uri(requestURI)
                .timestamp(LocalDateTime.now())
                .app("${service.name}")
                .build();
        String path = uri + "/hit";
        return makeAndSendRequest(HttpMethod.POST, path, null, statCreationDto);
    }

    public long getStats(String endpoint) {
        String[] uris = {endpoint};
        Map<String, Object> params = new HashMap<>();
        params.put("start", "1199-12-31 23:59:59");
        params.put("end", "2199-12-31 23:59:59");
        params.put("unique", true);
        params.put("uris", uris);
        String path = uri + "/stats?start={start}&end={end}&unique={unique}&uris={uris}";
        Object response = makeAndSendRequest(HttpMethod.GET, path, params, null).getBody();
        StatisticDto statistics;
        try {
            statistics = convertToStatisticDto(response);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
        return statistics.getHits();
    }

    private StatisticDto convertToStatisticDto(Object response) throws JsonProcessingException {
        String jsonBody = objectMapper.writeValueAsString(response);
        String jsonStatisticDto = jsonBody.substring(1, jsonBody.length() - 1);
        return objectMapper.readValue(jsonStatisticDto, StatisticDto.class);
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path,
                                                          @Nullable Map<String, Object> parameters, @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());
        ResponseEntity<Object> statsServerResponse;
        try {
            if (parameters != null) {
                statsServerResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                statsServerResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(statsServerResponse);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }
        return responseBuilder.build();
    }
}
