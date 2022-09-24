package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.User;
import com.edu.ulab.app.exception.DAOException;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userMapper.userDtoToUser(userDto);
        log.info("Mapped user: {}", user);

        User savedUser = userRepository.save(user);
        log.info("Saved user: {}", savedUser);

        return userMapper.userToUserDto(savedUser);
    }


    @Override
    public UserDto updateUser(UserDto userDto) {
        User user = userMapper.userDtoToUser(userDto);
        log.info("Mapped user: {}", user);

        User savedUser = userRepository.save(user);
        log.info("Update user: {}", savedUser);

        return userMapper.userToUserDto(savedUser);
    }

    @Override
    public UserDto getUserById(Long id) {
        Optional<User> userOpt = userRepository.findById(id);

        if (userOpt.isPresent()) {
            log.info("Find user: {}", userOpt.get());
            return userMapper.userToUserDto(userOpt.get());
        } else throw new NotFoundException("User with ID: " + id + " not found");
    }

    @Override
    public UserDto getUserByFullName(UserDto userDto) {
        Optional<User> userOpt = userRepository.findByFullName(userDto.getFullName());

        if (userOpt.isPresent()) {
            log.info("Find user: {}", userOpt.get());
            return userMapper.userToUserDto(userOpt.get());
        } else throw new NotFoundException("User not found");
    }

    @Override
    public void deleteUserById(Long id) {
        Optional<User> userOpt = userRepository.findById(id);

        if (userOpt.isPresent()) {
            log.info("User for remove: {}", userOpt.get());
            userRepository.delete(userOpt.get());
        } else throw new NotFoundException("User with ID: " + id + " not found");
    }

    @Override
    public void setBooksForUser(Long userId, List<Book> bookList) {
        log.info("List book: {}", bookList);
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isPresent()) {
            log.info("User for set bookList: {}", userOpt.get());
            userOpt.get().setBookList(bookList);
            userRepository.save(userOpt.get());
        } else throw new NotFoundException("User with ID: " + userId + " not found");
    }
}
