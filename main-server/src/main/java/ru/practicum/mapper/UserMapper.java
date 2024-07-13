package ru.practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.users.NewUserRequest;
import ru.practicum.dto.users.UserDto;
import ru.practicum.dto.users.UserShortDto;
import ru.practicum.model.User;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static UserDto convertToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public static List<UserDto> convertListUserDto(List<User> users) {
        return users.stream()
                .map(UserMapper::convertToUserDto)
                .collect(Collectors.toList());
    }

    public static User convertToUser(NewUserRequest userRequest) {
        return User.builder()
                .email(userRequest.getEmail())
                .name(userRequest.getName())
                .build();
    }

    public static UserShortDto convertToUserShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}
