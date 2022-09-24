package com.edu.ulab.app.facade;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.mapper.UserMapper;

import com.edu.ulab.app.service.impl.BookServiceImpl;
import com.edu.ulab.app.service.impl.BookServiceImplTemplate;
import com.edu.ulab.app.service.impl.UserServiceImpl;
import com.edu.ulab.app.service.impl.UserServiceImplTemplate;
import com.edu.ulab.app.web.request.UserBookRequest;
import com.edu.ulab.app.web.response.UserBookResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class UserDataFacade {
    private final UserServiceImpl userService;
    private final BookServiceImpl bookService;
    private final UserMapper userMapper;
    private final BookMapper bookMapper;

    public UserDataFacade(UserServiceImpl userService,
                          BookServiceImpl bookService,
                          UserMapper userMapper,
                          BookMapper bookMapper) {
        this.userService = userService;
        this.bookService = bookService;
        this.userMapper = userMapper;
        this.bookMapper = bookMapper;
    }

    public UserBookResponse createUserWithBooks(UserBookRequest userBookRequest) {
        log.info("Got user book create request: {}", userBookRequest);
        UserDto userDto = userMapper.userRequestToUserDto(userBookRequest.getUserRequest());
        log.info("Mapped user request: {}", userDto);

        UserDto createdUser = userService.createUser(userDto);
        log.info("Created user: {}", createdUser);

        List<Long> bookIdList = userBookRequest.getBookRequests()
                .stream()
                .filter(Objects::nonNull)
                .map(bookMapper::bookRequestToBookDto)
                .peek(bookDto -> bookDto.setUserId(createdUser.getId()))
                .peek(mappedBookDto -> log.info("mapped book: {}", mappedBookDto))
                .map(bookService::createBook)
                .peek(createdBook -> log.info("Created book: {}", createdBook))
                .map(BookDto::getId)
                .toList();
        log.info("Collected book ids: {}", bookIdList);

        return UserBookResponse.builder()
                .userId(createdUser.getId())
                .booksIdList(bookIdList)
                .build();
    }

    public UserBookResponse updateUserWithBooks(UserBookRequest userBookRequest) {
        UserDto userForUpdate = userMapper.userRequestToUserDto(userBookRequest.getUserRequest());
        log.info("Got update for user: {}", userForUpdate);

        // Поиск пользователя осуществляется по fullName
        UserDto userFromDao = userService.getUserByFullName(userForUpdate);
        log.info("Got user for update: {}", userForUpdate);

        // Удаляем у пользователя книги из базы
        log.info("Delete book for user: {}", userFromDao);
        bookService.getBooksByUserId(userFromDao.getId()).forEach(bookService::deleteBook);

        userForUpdate.setId(userFromDao.getId());
        userService.updateUser(userForUpdate);
        log.info("User is update: {}", userForUpdate);

        // Заполняем пользователю новые книги
        log.info("Add new book to user: {}", userFromDao);
        List<Long> bookIdList = userBookRequest.getBookRequests()
                .stream()
                .filter(Objects::nonNull)
                .map(bookMapper::bookRequestToBookDto)
                .peek(bookDto -> bookDto.setUserId(userFromDao.getId()))
                .peek(mappedBookDto -> log.info("mapped book: {}", mappedBookDto))
                .map(bookService::createBook)
                .peek(createdBook -> log.info("Created book: {}", createdBook))
                .map(BookDto::getId)
                .toList();
        log.info("Collected book ids: {}", bookIdList);

        return UserBookResponse.builder()
                .userId(userFromDao.getId())
                .booksIdList(bookIdList)
                .build();
    }

    public UserBookResponse getUserWithBooks(Long userId) {
        log.info("Get User by ID: {}", userId);
        UserDto userDto = userService.getUserById(userId);
        log.info("Got User: {}", userDto);

        log.info("Get Books User with ID: {}", userId);
        List<Long> listBook = bookService.getBooksByUserId(userId)
                .stream()
                .filter(Objects::nonNull)
                .map(Book::getId)
                .toList();

        return UserBookResponse.builder().
                userId(userDto.getId()).
                booksIdList(listBook).
                build();
    }

    public void deleteUserWithBooks(Long userId) {
        log.info("Будет удален пользователь с ID: {}", userId);
        userService.deleteUserById(userId);
    }
}
