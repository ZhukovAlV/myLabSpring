package com.edu.ulab.app.validation;

import com.edu.ulab.app.dto.UserDto;

public class UserValidator {

    public static boolean isValidUser(UserDto userDto){

        return userDto.getFullName() != null
                && !userDto.getFullName().isBlank()
                && userDto.getAge() > 0;
    }
}