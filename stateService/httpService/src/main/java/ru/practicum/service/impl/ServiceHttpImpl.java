package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.appDto.AppDtoReq;
import ru.practicum.appDto.AppDtoResp;
import ru.practicum.mapper.AppMapper;
import ru.practicum.model.AppInfo;
import ru.practicum.repository.AppRepository;
import ru.practicum.service.ServiceHttp;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceHttpImpl implements ServiceHttp {

    private final AppRepository appRepository;

    @Override
    @Transactional
    public Collection<AppDtoResp> save(AppDtoReq appDtoReq) {
        AppInfo appInfo = AppMapper.convertToAppInfo(appDtoReq);
        appInfo.setTimestamp(LocalDateTime.now());
        appRepository.save(appInfo);
        return List.of(AppMapper.convertToAppDtoResp(appInfo));
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<AppDtoResp> getStets(LocalDateTime start,
                                           LocalDateTime end,
                                           Collection<String> uris,
                                           boolean unique) {
        if (start.isAfter(end)) {
            throw new ValidationException("Error time");
        }
        if (unique) {
            if (uris != null) {
                return appRepository.findByTrueUniqueIpAndUrisAndTimestamp(uris, start, end);
            }
            return appRepository.findByFalseUniqueIpAndTimestampBetween(start, end);
        } else if (uris != null) {
            return appRepository.findByUris(uris, start, end);
        }
        return appRepository.findByTimestampBetween(start, end);


    }
}
