package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.User;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class UserServiceImplTemplate implements UserService {
    private final JdbcTemplate jdbcTemplate;
    private final UserMapper userMapper;

    public UserServiceImplTemplate(JdbcTemplate jdbcTemplate,
                                   UserMapper userMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto createUser(UserDto userDto) {

        final String INSERT_SQL = "INSERT INTO PERSON(FULL_NAME, TITLE, AGE) VALUES (?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                    ps.setString(1, userDto.getFullName());
                    ps.setString(2, userDto.getTitle());
                    ps.setLong(3, userDto.getAge());
                    return ps;
                }, keyHolder);

        userDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return userDto;
    }

    @Override
    public void updateUser(UserDto userDto) {
        User user = userMapper.userDtoToUser(userDto);
        log.info("User to update: {}", user);

        final String UPDATE_SQL = "UPDATE PERSON SET FULL_NAME=?, TITLE=?, AGE=? WHERE ID=?";
        jdbcTemplate.update(UPDATE_SQL,
                user.getFullName(), user.getTitle(), user.getAge(), user.getId());
    }

    @Override
    public UserDto getUserById(Long id) {
        Optional<User> findUser = jdbcTemplate.query("SELECT * FROM PERSON WHERE ID = ?",
                        new BeanPropertyRowMapper<>(User.class), new Object[]{id})
                .stream()
                .findAny();

        if (findUser.isEmpty()) {
            throw new NotFoundException("User with id " + id + " is not found");
        }
        return userMapper.userToUserDto(findUser.get());
    }

    @Override
    public void deleteUserById(Long id) {
        log.info("User id to delete: {}", id);
        jdbcTemplate.update("DELETE FROM PERSON WHERE ID=?", id);

        // Так как каскадное удаление без hibernate не работает, удаляем вручную
        log.info("Books user id to delete: {}", id);
        jdbcTemplate.update("DELETE FROM BOOK WHERE USER_ID=?", id);
    }

    @Override
    public UserDto getUserByFullName(UserDto userDto) {
        Optional<User> findUser = jdbcTemplate.query("SELECT * FROM PERSON WHERE FULL_NAME = ?",
                        new BeanPropertyRowMapper<>(User.class), new Object[]{userDto.getFullName()})
                .stream()
                .findAny();

        if (findUser.isEmpty()) {
            throw new NotFoundException("User with id " + userDto.getId() + " is not found");
        }
        return userMapper.userToUserDto(findUser.get());
    }
}
