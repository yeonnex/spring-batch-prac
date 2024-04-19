package org.ming.mingbatch.chunkPolicy;

import lombok.extern.slf4j.Slf4j;
import org.ming.mingbatch.dto.response.TransactionCountByAccountDto;
import org.ming.mingbatch.respository.AccountRepository;
import org.springframework.batch.repeat.CompletionPolicy;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

/**
 * 랜덤하게 청크 크기를 지정하도록 작성한 CompletionPolicy 구현체.
 * 매 청크 시작마다 랜던ㅁ하게 20 미만의 수를 지정하고 해당 개수만큼
 * 아이템이 처리되면 청크를 완료하는 CompletionPolicy 의 구현체.
 */

@Slf4j
@Component
public class RandomChunkPolicy implements CompletionPolicy {
    private int chunkSize;
    private int totalProcessed;
    private Random random = new Random();

    @Override
    public boolean isComplete(RepeatContext context, RepeatStatus result) {
        if (RepeatStatus.FINISHED == result) {
            return true;
        } else {
            return isComplete(context);
        }
    }

    @Override
    public boolean isComplete(RepeatContext context) {
        return this.totalProcessed >= chunkSize;
    }

    /**
     * 제일 먼저 호출되는 메서드. 청크의 시작을 알 수 있도록 정책을 초기화.
     * @param parent the current context if one is already in progress.
     * @return
     */
    @Override
    public RepeatContext start(RepeatContext parent) {
        this.chunkSize = random.nextInt(10);
        this.totalProcessed = 0;
        log.info("청크 사이즈가 {} 개로 설정되었습니다.", this.chunkSize);
        return parent;
    }

    @Override
    public void update(RepeatContext context) {
        this.totalProcessed++;
    }
}
