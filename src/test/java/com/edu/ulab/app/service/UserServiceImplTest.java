package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.User;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Тестирование функционала {@link com.edu.ulab.app.service.impl.UserServiceImpl}.
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing user functionality.")
public class UserServiceImplTest {
    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @Test
    @DisplayName("Создание пользователя. Должно пройти успешно.")
    void saveUser_Test() {
        //given

        UserDto userDto = new UserDto();
        userDto.setAge(11);
        userDto.setFullName("test name");
        userDto.setTitle("test title");

        User user  = new User();
        user.setFullName("test name");
        user.setAge(11);
        user.setTitle("test title");

        User savedPerson  = new User();
        savedPerson.setId(1L);
        savedPerson.setFullName("test name");
        savedPerson.setAge(11);
        savedPerson.setTitle("test title");

        UserDto result = new UserDto();
        result.setId(1L);
        result.setAge(11);
        result.setFullName("test name");
        result.setTitle("test title");


        //when

        when(userMapper.userDtoToUser(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(savedPerson);
        when(userMapper.userToUserDto(savedPerson)).thenReturn(result);


        //then

        UserDto userDtoResult = userService.createUser(userDto);
        assertEquals(1L, userDtoResult.getId());
    }

    // update
    @Test
    @DisplayName("Обновление пользователя.")
    void updatePerson_Test() {
        // given

        UserDto userDto = new UserDto();
        userDto.setAge(11);
        userDto.setFullName("test name");
        userDto.setTitle("test title");
        userDto.setId(1L);

        User user  = new User();
        user.setFullName("test name");
        user.setAge(11);
        user.setTitle("test title");
        user.setId(1L);

        User savedUser  = new User();
        savedUser.setId(1L);
        savedUser.setFullName("test name");
        savedUser.setAge(11);
        savedUser.setTitle("test title");

        UserDto result = new UserDto();
        result.setId(1L);
        result.setAge(11);
        result.setFullName("test name");
        result.setTitle("test title");


        // when

        when(userMapper.userDtoToUser(userDto)).thenReturn(user);
        when(userRepository.findByIdForUpdate(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.userToUserDto(savedUser)).thenReturn(result);

        // then

        UserDto userDtoResult = userService.updateUser(userDto);
        assertThat(userDtoResult.getId()).isEqualTo(1L);
    }

    // get
    @Test
    @DisplayName("Получение пользователя.")
    void getPersonById_Test() {
        // given

        UserDto userDto = new UserDto();
        userDto.setAge(11);
        userDto.setFullName("test name");
        userDto.setTitle("test title");
        userDto.setId(1L);

        User user  = new User();
        user.setFullName("test name");
        user.setAge(11);
        user.setTitle("test title");
        user.setId(1L);

        User savedUser  = new User();
        savedUser.setId(1L);
        savedUser.setFullName("test name");
        savedUser.setAge(11);
        savedUser.setTitle("test title");

        UserDto result = new UserDto();
        result.setId(1L);
        result.setAge(11);
        result.setFullName("test name");
        result.setTitle("test title");

        // when

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.userToUserDto(user)).thenReturn(result);

        // then

        UserDto userById = userService.getUserById(1L);
        assertThat(userById.getId()).isEqualTo(1L);
    }

    // get all

    // delete
    // * failed
    @Test
    @ExceptionHandler(NotFoundException.class)
    @DisplayName("Удаление пользователя.")
    void deletePerson_Test() {

        // when

        catchThrowable(() -> userService.deleteUserById(1L));
    }

    // * failed
    //         doThrow(dataInvalidException).when(testRepository)
    //                .save(same(test));
    // example failed
    //  assertThatThrownBy(() -> testeService.createTest(testRequest))
    //                .isInstanceOf(DataInvalidException.class)
    //                .hasMessage("Invalid data set");
}
