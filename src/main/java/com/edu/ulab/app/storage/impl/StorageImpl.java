package com.edu.ulab.app.storage.impl;

import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.User;
import com.edu.ulab.app.storage.Storage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Log4j2
public class StorageImpl implements Storage {

    /**
     * Хранилище пользователей
     */
    private final Map<Long, User> userRepo = new LinkedHashMap<>();

    /**
     * Хранилище книг
     */
    private final Map<Long, Book> bookRepo = new LinkedHashMap<>();

    /**
     * Формирование нового ID
     * @param map Map с Long ключами
     * @return максимальное значение ключа + 1
     */
    Long getUuid(Map<Long, ?> map) {
        if (map.isEmpty()) return 1L;
        else return (Long) map.keySet().toArray()[map.size() - 1] + 1;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        Optional<User> userOpt = Optional.ofNullable(userRepo.get(id));

        if (userOpt.isEmpty()) log.warn("Пользователь с ID: " + id + " не найден");

        return userOpt;
    }

    /**
     * Создание пользователя
     * @param fullName полное имя
     * @param title заголовок
     * @param age возраст
     * @param bookList список книг
     */
    public User createUser(String fullName, String title, int age, List<Book> bookList) {
        return new User(getUuid(userRepo), fullName, title, age, bookList);
    }

    /**
     * Создание пользователя
     * @param fullName полное имя
     * @param title заголовок
     * @param age возраст
     */
    public User createUser(String fullName, String title, int age) {
        return this.createUser(fullName, title, age, new ArrayList<>());
    }

    @Override
    public Optional<User> updateUser(User user) {
        Long userId = user.getId();
        Optional<User> userOpt = getUserById(userId);

        if (userOpt.isPresent()) {
            userRepo.put(userId, user);
            log.info("Пользователь с ID: " + userId + " обновлен");
            return Optional.of(user);
        }

        log.warn("Пользователь с ID: " + userId + " не найден");
        return Optional.empty();
    }

    @Override
    public Optional<User> saveUser(User user) {
        if (user.getId() == null) {
            Optional<User> userNew = Optional.of(createUser(user.getFullName(), user.getTitle(), user.getAge(), user.getBookList()));

            userRepo.put(userNew.get().getId(), userNew.get());
            log.info("Пользователь: " + userNew.get().getFullName() + " сохранен c ID:" + userNew.get().getId());

            return userNew;
        } else {
            log.info("Пользователь с ID: " + user.getId() + " имеется, он будет обновлен");
            return updateUser(user);
        }
    }

    @Override
    public boolean deleteUser(Long id) {
        User user = userRepo.get(id);

        if (user != null) {
            userRepo.remove(id);
            log.info("Пользователь с ID: " + id + " был удален");
            return true;
        }

        log.warn("Пользователь с ID: " + id + " отсутствует в БД");
        return false;
    }

    @Override
    public Optional<Book> getBookById(Long id) {
        Optional<Book> bookOpt = Optional.ofNullable(bookRepo.get(id));

        if (bookOpt.isEmpty()) log.warn("Книга с ID: " + id + " не найдена");

        return bookOpt;
    }

    /**
     * Создание книги
     * @param userId ID пользователя
     * @param title название
     * @param author автор
     * @param pageCount количество страниц
     * @return книга
     */
    public Book createBook(Long userId, String title, String author, long pageCount) {
        return new Book(getUuid(bookRepo), userId, title, author, pageCount);
    }

    @Override
    public Optional<Book> updateBook(Book book) {
        Long bookId = book.getId();
        Optional<Book> bookOpt = getBookById(bookId);

        if (bookOpt.isPresent()) {
            bookRepo.put(bookId, book);
            log.info("Книга с ID: " + bookId + " обновлена");
            return Optional.of(book);
        }

        log.warn("Книга с ID: " + bookId + " не найдена");
        return Optional.empty();
    }

    @Override
    public Optional<Book> saveBook(Book book) {
        if (book.getId() == null) {
            Optional<Book> bookNew = Optional.of(createBook(book.getUserId(), book.getTitle(), book.getAuthor(), book.getPageCount()));

            bookRepo.put(bookNew.get().getId(), bookNew.get());
            log.info("Книга: " + bookNew.get().getTitle() + " сохранена c ID:" + bookNew.get().getId());

            return bookNew;
        } else {
            log.info("Книга с ID: " + book.getId() + " имеется, она будет обновлена");
            return updateBook(book);
        }
    }

    @Override
    public boolean deleteBook(Long id) {
        Book book = bookRepo.get(id);

        if (book != null) {
            bookRepo.remove(id);
            log.info("Книга с ID: " + id + " была удалена");
            return true;
        }

        log.error("Книга с ID: " + id + " отсутствует в БД");
        return false;
    }
}
