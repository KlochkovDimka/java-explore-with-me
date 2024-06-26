package ru.practicum.service;

import ru.practicum.appDto.AppDtoReq;
import ru.practicum.appDto.AppDtoResp;

import java.time.LocalDateTime;
import java.util.Collection;

public interface ServiceHttp {

    Collection<AppDtoResp> save(AppDtoReq appDtoReq);

    Collection<AppDtoResp> getStets(LocalDateTime start,
                                    LocalDateTime end,
                                    Collection<String> uris,
                                    boolean unique);
}
