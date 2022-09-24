package com.edu.ulab.app.storage;

import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.User;

import java.util.Optional;

public interface Storage {

    /**
     * Получение пользователя
     * @param id ID пользователя
     * @return Optional<User> пользователь с данным id
     */
    Optional<User> getUserById(Long id);

    /**
     * Обновление пользователя
     * @param user пользователь
     * @return Optional<User> пользователь
     */
    Optional<User> updateUser(User user);

    /**
     * Сохранение пользователя
     * @param user пользователь
     * @return Optional<User> пользователь
     */
    Optional<User> saveUser(User user);

    /**
     * Удаление пользователя
     * @param id ID пользователя
     */
    boolean deleteUser(Long id);

    /**
     * Получение книги по id
     * @param id ID книги
     * @return Optional<Book> книга
     */
    Optional<Book> getBookById(Long id);

    /**
     * Сохранение книги
     * @param book книга
     * @return обновленная книга
     */
    Optional<Book> updateBook(Book book);

    /**
     * Сохранение книги
     * @param book книга
     * @return либо обновленная, либо новая книга
     */
    Optional<Book> saveBook(Book book);

    /**
     * Удаление книги
     * @param id ID книги
     */
    boolean deleteBook(Long id);

}
