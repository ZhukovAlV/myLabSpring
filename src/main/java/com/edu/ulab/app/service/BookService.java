package com.edu.ulab.app.service;


import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;

import java.util.List;

public interface BookService {
    BookDto createBook(BookDto userDto);

    void updateBook(BookDto userDto);

    BookDto getBookById(Long id);

    List<Book> getBooksByUserId(Long userId);

    void deleteBookById(Long id);

    void deleteBook(Book book);
}
