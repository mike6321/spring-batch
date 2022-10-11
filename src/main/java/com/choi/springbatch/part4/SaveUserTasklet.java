package com.choi.springbatch.part4;

import com.choi.springbatch.part5.Orders;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class SaveUserTasklet implements Tasklet {

    private final UserRepository userRepository;
    private static final int SIZE = 10_000;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        List<User> users = createUsers();
        Collections.shuffle(users);
        userRepository.saveAll(users);
        return RepeatStatus.FINISHED;
    }

    private List<User> createUsers() {
        List<User> users = new ArrayList<>();
        createUser(users, 1_000, 1);
        createUser(users, 200_000, 2);
        createUser(users, 300_000, 3);
        createUser(users, 500_000, 4);
        return users;
    }

    private void createUser(List<User> users, int amount, int dayOfMonth) {
        IntStream.range(0, SIZE)
                .forEach(index -> users.add(
                        User.builder()
                                .orders(Collections.singletonList(
                                        Orders.builder()
                                                .amount(amount)
                                                .createdDate(LocalDate.of(2022, 11 , dayOfMonth))
                                                .itemName("item" + index)
                                                .build()))
                                .username("test username " + index)
                                .build()
                ));
    }

}
