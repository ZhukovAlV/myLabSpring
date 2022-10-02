package com.edu.ulab.app.repository;

import com.edu.ulab.app.config.SystemJpaTest;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.User;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

import static com.vladmihalcea.sql.SQLStatementCountValidator.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

/**
 * Тесты репозитория {@link BookRepository}.
 */
@SystemJpaTest
public class BookRepositoryTest {
    @Autowired
    BookRepository bookRepository;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        SQLStatementCountValidator.reset();
    }

    @DisplayName("Сохранить книгу и автора. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void findAllBadges_thenAssertDmlCount() {
        //Given

        User user = new User();
        user.setAge(111);
        user.setTitle("reader");
        user.setFullName("Test Test");

        User savedPerson = userRepository.save(user);

        Book book = new Book();
        book.setAuthor("Test Author");
        book.setTitle("test");
        book.setPageCount(1000);
        book.setUserId(savedPerson.getId());

        //When
        Book result = bookRepository.save(book);

        //Then
        assertThat(result.getPageCount()).isEqualTo(1000);
        assertThat(result.getTitle()).isEqualTo("test");
        assertSelectCount(1);
        assertInsertCount(2);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    // update
    @DisplayName("Обновить книгу. Число update должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void updateBook_thenAssertDmlCount() {
        // Given
        Book book = new Book();
        book.setAuthor("updated author");
        book.setTitle("updated title");
        book.setPageCount(1000);
        book.setId(2002L);

        // When
        Book updatedBook = bookRepository.save(book);

        // Then
        assertThat(updatedBook.getAuthor()).isEqualTo("updated author");
        assertThat(updatedBook.getTitle()).isEqualTo("updated title");
        assertThat(updatedBook.getPageCount()).isEqualTo(1000);

        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);

    }

    // get
    @DisplayName("Получить книгу. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void getBook_thenAssertDmlCount() {

        // When
        Book book = bookRepository.findById(2002L).orElseThrow();

        // Then
        assertThat(book.getAuthor()).isEqualTo("author");
        assertThat(book.getTitle()).isEqualTo("default book");
        assertThat(book.getPageCount()).isEqualTo(5500);

        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    // get all
    @DisplayName("Получить все книги. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void getAllBooks_thenAssertDmlCount() {

        // When
        List<Book> books = new ArrayList<>();
        bookRepository.findAll().forEach(books::add);

        // Then
        assertThat(books.size()).isEqualTo(2);

        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);

    }

    // delete
    @DisplayName("Удалить книгу. Число delete должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void deleteBook_thenAssertDmlCount() {

        // When
        bookRepository.deleteById(2002L);
        List<Book> books = new ArrayList<>();
        bookRepository.findAll().forEach(books::add);

        // Then
        assertThat(books.size()).isEqualTo(1);

        assertSelectCount(2);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(1);
    }

    // * failed
    // example failed test
    @DisplayName("Получить книгу с id null. Должно выбросить ошибку.")
    @Test
    @ExceptionHandler(IllegalArgumentException.class)
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void getBookByIdNull() {
        // when
        catchThrowable(() -> bookRepository.findById(null));
    }
}
