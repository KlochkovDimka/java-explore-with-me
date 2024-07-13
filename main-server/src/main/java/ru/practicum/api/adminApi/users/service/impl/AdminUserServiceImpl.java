package ru.practicum.api.adminApi.users.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.api.adminApi.users.service.AdminUserService;
import ru.practicum.dto.users.NewUserRequest;
import ru.practicum.dto.users.UserDto;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAllUsers(List<Long> usersId, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from, size);

        if (usersId == null) {
            return userRepository.findAll(pageRequest).stream()
                    .map(UserMapper::convertToUserDto)
                    .collect(Collectors.toList());
        }

        Page<User> users = userRepository.findAllByIdIn(usersId, pageRequest);

        if (users.isEmpty()) {
            return List.of();
        }
        return UserMapper.convertListUserDto(users.getContent());
    }

    @Override
    @Transactional
    public UserDto saveNewUser(NewUserRequest newUserRequest) {
        User user = UserMapper.convertToUser(newUserRequest);
        return UserMapper.convertToUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
    }
}
