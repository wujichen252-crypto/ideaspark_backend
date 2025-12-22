package com.example.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 定时任务配置类
 * 配置定时任务线程池和调度器
 */
@Configuration
@EnableScheduling
public class ScheduleConfig {

    /**
     * 配置定时任务线程池
     *
     * @return ThreadPoolTaskScheduler
     */
    @Bean(name = "taskScheduler")
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        
        // 设置线程池大小
        scheduler.setPoolSize(10);
        
        // 设置线程名称前缀
        scheduler.setThreadNamePrefix("schedule-pool-");
        
        // 设置线程池关闭时等待所有任务完成
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        
        // 设置线程池关闭时等待的最大时间
        scheduler.setAwaitTerminationSeconds(60);
        
        return scheduler;
    }
}
