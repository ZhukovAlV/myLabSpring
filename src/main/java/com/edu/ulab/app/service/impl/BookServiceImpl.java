package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.User;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.exception.ValidationException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.BookService;
import com.edu.ulab.app.validation.BookValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

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
        if (BookValidator.isValidBook(bookDto)){
            Book book = bookMapper.bookDtoToBook(bookDto);
            log.info("Mapped book: {}", book);

            Book savedBook = bookRepository.save(book);
            log.info("Saved book: {}", savedBook);

            return bookMapper.bookToBookDto(savedBook);
        } else throw new ValidationException("Not validation data: " + bookDto);
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        if (BookValidator.isValidBook(bookDto)){
            Book book = bookMapper.bookDtoToBook(bookDto);
            log.info("Mapped book: {}", book);

            Book savedBook = bookRepository.save(book);
            log.info("Saved book: {}", savedBook);

            return bookMapper.bookToBookDto(savedBook);
        } else throw new ValidationException("Not validation data: " + bookDto);
    }

    @Override
    public BookDto getBookById(Long id) {
        Optional<Book> bookOpt = bookRepository.findById(id);

        if (bookOpt.isPresent()) {
            log.info("Got book: {}", bookOpt.get());
            return bookMapper.bookToBookDto(bookOpt.get());
        } else throw new NotFoundException("Book with ID: " + id + " not found");
    }

    @Override
    public List<BookDto> getBooksByUserId(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isPresent()) {
            log.info("Got user: {}", userOpt.get());
            return userOpt.get().getBookList().stream().map(bookMapper::bookToBookDto).toList();
        } else throw new NotFoundException("User with ID: " + userId + " not found");
    }

    @Override
    public void deleteBookById(Long id) {
        Optional<Book> bookOpt = bookRepository.findById(id);

        if (bookOpt.isPresent()) {
            bookRepository.delete(bookOpt.get());
            log.info("Book remove: {}", bookOpt.get());
        } else throw new NotFoundException("Book with ID: " + id + " not found");
    }

    @Override
    public void deleteBook(BookDto bookDto) {
        log.info("Book for remove: {}", bookDto);
        bookRepository.delete(bookMapper.bookDtoToBook(bookDto));
    }

    @Override
    public List<BookDto> getAllBooks() {
        return StreamSupport.stream(bookRepository.findAll().spliterator(),false)
                .filter(Objects::nonNull)
                .map(bookMapper::bookToBookDto)
                .toList();
    }
}
