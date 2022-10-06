package com.choi.springbatch.part4;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


public interface UserRepository extends JpaRepository<User, Long> {

    @Transactional(readOnly = true)
    List<User> findAllByUpdatedDate(LocalDate updatedDate);

}
