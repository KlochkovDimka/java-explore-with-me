package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.appDto.AppDtoReq;
import ru.practicum.appDto.AppDtoResp;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class AppClient {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String SERVER_URL = "http://stats-service:9090";
    private final RestTemplate template;


    public AppClient(RestTemplate template) {
        this.template = template;
    }

    public void createApp(AppDtoReq appDtoReq) {
        template.exchange(SERVER_URL + "/hit",
                HttpMethod.POST,
                new HttpEntity<Object>(appDtoReq),
                Object.class);
    }

    public List<AppDtoResp> getAllApp(LocalDateTime start, LocalDateTime end, Collection<String> uris, Boolean unique) {
        URI uri = UriComponentsBuilder.fromHttpUrl(SERVER_URL)
                .path("/stats")
                .queryParam("start", start.format(FORMATTER))
                .queryParam("end", end.format(FORMATTER))
                .queryParam("uris", uris)
                .queryParam("unique", unique)
                .build()
                .toUri();

        ResponseEntity<List<AppDtoResp>> response = template.exchange(uri,
                HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }
}
