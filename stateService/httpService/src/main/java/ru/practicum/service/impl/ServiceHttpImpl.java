package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.appDto.AppDtoReq;
import ru.practicum.appDto.AppDtoResp;
import ru.practicum.mapper.AppMapper;
import ru.practicum.model.AppInfo;
import ru.practicum.repository.AppRepository;
import ru.practicum.service.ServiceHttp;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceHttpImpl implements ServiceHttp {

    private final AppRepository appRepository;

    @Override
    public Collection<AppDtoResp> save(AppDtoReq appDtoReq) {
        AppInfo appInfo = AppMapper.convertToAppInfo(appDtoReq);
        return List.of(AppMapper.convertToAppDtoResp(appInfo));
    }

    @Override
    public Collection<AppDtoResp> getStets(LocalDateTime start, LocalDateTime end, Collection<String> uris, boolean unique) {
        if (unique) {
            if (uris != null) {
                return appRepository.findByTrueUniqueIpAndUrisAndTimestamp(uris, start, end);
            } else {
                return appRepository.findByFalseUniqueIpAndTimestampBetween(start, end);
            }
        } else {
            if (uris != null) {
                return appRepository.findByUris(uris, start, end);
            } else {
                return appRepository.findByTimestampBetween(start, end);
            }
        }
    }
}
