package io.spring.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
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
        return jobBuilderFactory.get("testJob")
                .start(step1())
                .next(step2())
                .next(step3())
                .build();
    }

    @Bean
    @JobScope
    public Step step1(){
        return stepBuilderFactory.get("step1").tasklet((contribution, chunkContext) -> {
            log.info("----- This is Step1");
            log.info("----- requestDate = {}");
//            throw new IllegalAccessException("실패합니다.");
            return RepeatStatus.FINISHED;
        }).build();
    }

    @Bean
    @JobScope
    public Step step2(){
        return stepBuilderFactory.get("step2")
                .tasklet((contribution, chunkContext) -> {
                    log.info("----- This is Step2");
                    log.info("----- requestDate = {}");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    @JobScope
    public Step step3(){
        return stepBuilderFactory.get("step3")
                .tasklet((contribution, chunkContext) -> {
                    log.info("----- This is Step3");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}
