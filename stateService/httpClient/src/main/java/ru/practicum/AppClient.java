package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class AppClient extends BaseClient {



    public AppClient(@Value("${state-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory("http://localhost:9090"))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
        log.info("--------------------------------------------------------" + serverUrl);
    }

    public ResponseEntity<Object> createApp(Objects appDtoReq) {
        return post("/hit", appDtoReq);
    }

    public ResponseEntity<Object> getAllApp(LocalDateTime start, LocalDateTime end, Collection<String> uris, Boolean unique) {
        Map<String, Object> param = Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "unique", unique
        );
        return get("/stats", param);
    }
}
