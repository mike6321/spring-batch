package com.choi.springbatch.part4;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


public interface UserRepository extends JpaRepository<User, Long> {

    @Transactional(readOnly = true)
    List<User> findAllByUpdatedDate(LocalDate updatedDate);

    @Transactional(readOnly = true)
    @Query(value = "select min(u.id) from User u")
    long findMinId();

    @Transactional(readOnly = true)
    @Query(value = "select max(u.id) from User u")
    long findMaxId();

}
