package com.choi.springbatch.part4;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class SaveUserTasklet implements Tasklet {

    private final UserRepository userRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        List<User> users = createUsers();
        Collections.shuffle(users);
        userRepository.saveAll(users);
        return RepeatStatus.FINISHED;
    }

    private List<User> createUsers() {
        List<User> users = new ArrayList<>();
        createUser(users, 0, 100, 1_000);
        createUser(users, 100, 200, 200_000);
        createUser(users, 200, 300, 300_000);
        createUser(users, 300, 400, 500_000);
        return users;
    }

    private void createUser(List<User> users, int firstIndex, int lastIndex, int amount) {
        IntStream.range(firstIndex, lastIndex)
                .forEach(index -> users.add(
                        User.builder()
                                .totalAmount(amount)
                                .username("test username " + index)
                                .build()
                ));
    }

}
