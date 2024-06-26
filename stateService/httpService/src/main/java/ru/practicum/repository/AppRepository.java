package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.appDto.AppDtoResp;
import ru.practicum.model.AppInfo;

import java.time.LocalDateTime;
import java.util.Collection;

@Repository
public interface AppRepository extends JpaRepository<AppInfo, Long> {

    @Query("SELECT new ru.practicum.appDto.AppDtoResp(a.app, a.uri, COUNT(a.ip)) " +
            "FROM AppInfo a " +
            "WHERE a.uri IN :uris " +
            "AND a.timestamp BETWEEN :start AND :end " +
            "GROUP BY a.app, a.uri")
    Collection<AppDtoResp> findByUris(Collection<String> uris,
                                      LocalDateTime start,
                                      LocalDateTime end);

    @Query("SELECT new ru.practicum.appDto.AppDtoResp(a.app, a.uri, COUNT(a.ip)) " +
            "FROM ru.practicum.model.AppInfo a " +
            "WHERE a.timestamp BETWEEN :start AND :end " +
            "GROUP BY a.app, a.uri")
    Collection<AppDtoResp> findByTimestampBetween(LocalDateTime start,
                                                  LocalDateTime end);

    @Query("SELECT new ru.practicum.appDto.AppDtoResp(a.app, a.uri, COUNT(a.ip)) " +
            "FROM AppInfo a " +
            "WHERE a.uri IN :uris " +
            "AND a.timestamp BETWEEN :start AND :end " +
            "GROUP BY a.app, a.uri")
    Collection<AppDtoResp> findByFalseUniqueIpAndTimestampBetween(LocalDateTime start,
                                                                  LocalDateTime end);

    @Query("SELECT new ru.practicum.appDto.AppDtoResp(a.app, a.uri, COUNT(DISTINCT a.ip)) " +
            "FROM AppInfo a " +
            "WHERE a.uri IN :uris " +
            "AND a.timestamp BETWEEN :start AND :end " +
            "GROUP BY a.app, a.uri")
    Collection<AppDtoResp> findByTrueUniqueIpAndUrisAndTimestamp(Collection<String> uris,
                                                                 LocalDateTime start,
                                                                 LocalDateTime end);
}
