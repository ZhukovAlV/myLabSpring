package com.edu.ulab.app.repository;

import com.edu.ulab.app.entity.User;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    /*
    User has books - book - started - comited status - other logic
    User has books - book - in progress
    User has books - book - finished
     */

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from User p where p.id = :id")
    Optional<User> findByIdForUpdate(long id);

    Optional<User> findByFullName(String fullName);
}
