package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.User;
import com.edu.ulab.app.exception.DAOException;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final UserRepository userRepository;

    private final BookMapper bookMapper;

    public BookServiceImpl(BookRepository bookRepository,
                           UserRepository userRepository,
                           BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.bookMapper = bookMapper;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        Book book = bookMapper.bookDtoToBook(bookDto);
        log.info("Mapped book: {}", book);

        Book savedBook = bookRepository.save(book);
        log.info("Saved book: {}", savedBook);

        return bookMapper.bookToBookDto(savedBook);
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        Book book = bookMapper.bookDtoToBook(bookDto);
        log.info("Mapped book: {}", book);

        Book savedBook = bookRepository.save(book);
        log.info("Saved book: {}", savedBook);

        return bookMapper.bookToBookDto(savedBook);
    }

    @Override
    public BookDto getBookById(Long id) {
        Optional<Book> bookOpt = bookRepository.findById(id);

        if (bookOpt.isPresent()) return bookMapper.bookToBookDto(bookOpt.get());
        else throw new NotFoundException("Book with ID: " + id + " not found");
    }

    @Override
    public List<Book> getBooksByUserId(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isPresent()) return userOpt.get().getBookList();
        else throw new NotFoundException("User with ID: " + userId + " not found");
    }

    @Override
    public void deleteBookById(Long id) {
        Optional<Book> bookOpt = bookRepository.findById(id);

        if (bookOpt.isPresent()) {
            log.info("Book for remove: {}", bookOpt.get());
            bookRepository.delete(bookOpt.get());
        } else throw new NotFoundException("Book with ID: " + id + " not found");
    }

    @Override
    public void deleteBook(Book book) {
        log.info("Book for remove: {}", book);
        bookRepository.delete(book);
    }
}
