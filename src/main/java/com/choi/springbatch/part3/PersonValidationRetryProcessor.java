package com.choi.springbatch.part3;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.retry.support.RetryTemplateBuilder;

@Slf4j
public class PersonValidationRetryProcessor implements ItemProcessor<Person, Person> {

    private final RetryTemplate retryTemplate;

    public PersonValidationRetryProcessor() {
        this.retryTemplate = new RetryTemplateBuilder()
                .maxAttempts(3)
                .retryOn(NotFoundNameException.class)
                .withListener(new SavePersonRetryListener())
                .build();
    }

    @Override
    public Person process(Person item) throws Exception {
        return this.retryTemplate.execute(
                context -> {
                    // retry callBack
                    if (item.isNotEmptyName()) {
                        return item;
                    }
                    throw new NotFoundNameException();
                },
                context -> {
                    // recovery callBack
                    return item.unknownName();
                }
        );
    }

    /**
     * RetryListener.open
     *  return false 인 경우 retry 를 시도하지 않는다.
     * RetryTemplate.RetryCallback
     * RetryListener.onError
     *  maxAttempts 설정 값 만큼 반복
     * RetryTemplate.RecoveryCallback
     *  maxAttempts 반복 후에도 에러가 발생한 경우 실행
     * RetryListener.close
     * */
    public static class SavePersonRetryListener implements RetryListener {

        @Override
        public <T, E extends Throwable> boolean open(RetryContext retryContext, RetryCallback<T, E> retryCallback) {
            return true;
        }

        @Override
        public <T, E extends Throwable> void close(RetryContext retryContext, RetryCallback<T, E> retryCallback, Throwable throwable) {
            log.info("close");
        }

        @Override
        public <T, E extends Throwable> void onError(RetryContext retryContext, RetryCallback<T, E> retryCallback, Throwable throwable) {
            log.info("onError");
        }

    }

}
