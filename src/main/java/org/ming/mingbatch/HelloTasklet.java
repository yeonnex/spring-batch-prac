package org.ming.mingbatch;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

public class HelloTasklet implements Tasklet {
    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        String name = (String) chunkContext.getStepContext()
                .getJobParameters()
                .get("name");
        ExecutionContext jobContext = stepContribution.getStepExecution()
                .getJobExecution().getExecutionContext();
        jobContext.put("user.name", name);
        return RepeatStatus.FINISHED;
    }
}
