package com.sky.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class MyTask {
    /*
    @Scheduled(cron = "0/5 * * * * ?")
    public void task1() {
        log.info("执行了task1任务,{}",new Date());
    }

     */
}
