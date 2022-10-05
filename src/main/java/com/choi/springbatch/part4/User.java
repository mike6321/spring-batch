package com.choi.springbatch.part4;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Enumerated(EnumType.STRING)
    private Level level = Level.NORMAL;

    private int totalAmount;

    private LocalDateTime updatedDate;

    @Builder
    private User(String username, int totalAmount) {
        this.username = username;
        this.totalAmount = totalAmount;
    }

}
