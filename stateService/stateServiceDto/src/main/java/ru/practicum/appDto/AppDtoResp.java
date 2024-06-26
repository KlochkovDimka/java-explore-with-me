package ru.practicum.appDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppDtoResp {
    private String app;
    private String uri;
    private Long hits;
}
