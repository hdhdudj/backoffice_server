package io.spring.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class BatchTestConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job testJob(){
        return jobBuilderFactory.get("testJob").start(testStep()).build();
    }

    @Bean
    public Step testStep(){
        return stepBuilderFactory.get("testStep").tasklet((contribution, chunkContext) -> {
            log.info("----- This is Step1");
            return RepeatStatus.FINISHED;
        }).build();
    }
}
