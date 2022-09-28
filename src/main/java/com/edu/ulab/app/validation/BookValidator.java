package com.edu.ulab.app.validation;

import com.edu.ulab.app.dto.BookDto;

public class BookValidator {

    public static boolean isValidBook(BookDto bookDto){

        return !bookDto.getTitle().isBlank()
                && !bookDto.getAuthor().isBlank()
                && bookDto.getPageCount() > 0;
    }
}