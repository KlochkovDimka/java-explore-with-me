package ru.practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.appDto.AppDtoReq;
import ru.practicum.appDto.AppDtoResp;
import ru.practicum.model.AppInfo;

import java.util.Collection;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppMapper {

    public static AppInfo convertToAppInfo(AppDtoReq appDtoReq) {
        return AppInfo.builder()
                .app(appDtoReq.getApp())
                .uri(appDtoReq.getUri())
                .ip(appDtoReq.getIp())
                .timestamp(appDtoReq.getTimeStamp())
                .build();
    }

    public static AppDtoResp convertToAppDtoResp(AppInfo appInfo) {
        return AppDtoResp.builder()
                .app(appInfo.getApp())
                .uri(appInfo.getUri())
                .build();
    }

    public static Collection<AppDtoResp> convertToCollectionAppDtoResp(Collection<AppInfo> listAppInfo) {
        return listAppInfo.stream()
                .map(AppMapper::convertToAppDtoResp)
                .collect(Collectors.toList());
    }

}
