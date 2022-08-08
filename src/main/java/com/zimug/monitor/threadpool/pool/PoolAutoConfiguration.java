package com.zimug.monitor.threadpool.pool;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @创建人 字母哥
 * @创建时间 2022/8/8
 * @描述 根据配置信息自动装配Spring Boot线程池
 **/
@Configuration
@EnableAsync
@EnableConfigurationProperties(MultiPoolModel.class)
@ConditionalOnProperty(name = "thread-pool.enable", havingValue = "true")
public class PoolAutoConfiguration {

  @Resource
  ConfigurableBeanFactory configurableBeanFactory;

  //线程池静态配置信息
  private MultiPoolModel multiPoolModel;
  public PoolAutoConfiguration(MultiPoolModel multiPoolModel) {
    this.multiPoolModel = multiPoolModel;
  }

  @PostConstruct
  public void init() {

    for(PoolModel pool : multiPoolModel.getPoolLists()){
      ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
      //设置线程池静态参数信息
      taskExecutor.setCorePoolSize(pool.getCoreSize());
      taskExecutor.setMaxPoolSize(pool.getMaxSize());
      taskExecutor.setQueueCapacity(pool.getQueueCapacity());
      taskExecutor.setThreadNamePrefix(pool.getPoolId() + "Thread"); //如：testThread-
      taskExecutor.setWaitForTasksToCompleteOnShutdown(true);

      //注册Spring Bean
      configurableBeanFactory.registerSingleton(pool.getPoolId(), taskExecutor);

      System.out.println(pool.getPoolId() + "线程池注册");
      //初始化线程池
      taskExecutor.initialize();
    }


  }

}
