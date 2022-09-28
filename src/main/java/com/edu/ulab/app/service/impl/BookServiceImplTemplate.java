package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.exception.ValidationException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.service.BookService;
import com.edu.ulab.app.validation.BookValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class BookServiceImplTemplate implements BookService {

    private final JdbcTemplate jdbcTemplate;

    private final BookMapper bookMapper;

    public BookServiceImplTemplate(JdbcTemplate jdbcTemplate,
                                   BookMapper bookMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.bookMapper = bookMapper;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        if (BookValidator.isValidBook(bookDto)){
            final String INSERT_SQL = "INSERT INTO BOOK(TITLE, AUTHOR, PAGE_COUNT, USER_ID) VALUES (?,?,?,?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(
                    new PreparedStatementCreator() {
                        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                            PreparedStatement ps =
                                    connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                            ps.setString(1, bookDto.getTitle());
                            ps.setString(2, bookDto.getAuthor());
                            ps.setLong(3, bookDto.getPageCount());
                            ps.setLong(4, bookDto.getUserId());
                            return ps;
                        }
                    },
                    keyHolder);

            bookDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
            return bookDto;
        } else throw new ValidationException("Not validation data: " + bookDto);
    }

    @Override
    public void updateBook(BookDto bookDto) {
        if (BookValidator.isValidBook(bookDto)){
            Book book = bookMapper.bookDtoToBook(bookDto);
            log.info("Book to update: {}", book);

            jdbcTemplate.update("UPDATE BOOK SET USER_ID=?, TITLE=?, AUTHOR=?, PAGE_COUNT=? WHERE ID=?",
                    book.getUserId(), book.getTitle(), book.getAuthor(), book.getPageCount(), book.getId());
        } else throw new ValidationException("Not validation data: " + bookDto);
    }

    @Override
    public BookDto getBookById(Long id) {
        log.info("Book id to find: {}", id);
        Optional<Book> findBook = jdbcTemplate.query("SELECT * FROM BOOK WHERE ID = ?",
                        new BeanPropertyRowMapper<>(Book.class), new Object[]{id})
                .stream().findAny();

        if (findBook.isEmpty()) {
            throw new NotFoundException("Book with id " + id + " is not found");
        }

        log.info("Found book: {}", findBook);
        return bookMapper.bookToBookDto(findBook.get());
    }

    @Override
    public List<Book> getBooksByUserId(Long userId) {
        List<Book> books = jdbcTemplate.query("SELECT * FROM BOOK WHERE USER_ID = ?",
                new BeanPropertyRowMapper<>(Book.class), new Object[]{userId});
        log.info("Found books: {}", books);

        return books;
    }

    @Override
    public void deleteBookById(Long id) {
        log.info("Book id to delete: {}", id);
        jdbcTemplate.update("DELETE FROM BOOK WHERE ID=?", id);
    }

    @Override
    public void deleteBook(Book book) {
        deleteBookById(book.getId());
    }
}
