package ru.practicum.api.adminApi.users.service;

import ru.practicum.dto.users.NewUserRequest;
import ru.practicum.dto.users.UserDto;

import java.util.List;

public interface AdminUserService {

    List<UserDto> findAllUsers(List<Long> usersId, int from, int size);

    UserDto saveNewUser(NewUserRequest newUserRequest);

    void deleteUserById(Long userId);
}
