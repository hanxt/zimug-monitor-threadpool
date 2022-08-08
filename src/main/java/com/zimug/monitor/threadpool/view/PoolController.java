package com.zimug.monitor.threadpool.view;

import com.zimug.monitor.threadpool.pool.MultiPoolModel;
import com.zimug.monitor.threadpool.pool.PoolModel;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

/**
 * @创建人 韩晓彤
 * @创建时间 2022/8/8
 * @描述 查看运行时线程池的状态
 **/
@RestController
@RequestMapping("/pool")
public class PoolController{

  @Resource
  private GenericApplicationContext applicationContext;

  @Resource
  private MultiPoolModel multiPoolModel;

  /**
   * 查询微服务线程池管理列表
   */
  @GetMapping("/list")
  public List<PoolModel> list() {
    return multiPoolModel.getPoolLists().stream()
            .map(this::getRuntimeState)
            .collect(Collectors.toList());
  }


  //将数据库中的线程池配置，与内存中的线程池运行状态整合
  private PoolModel getRuntimeState(PoolModel poolModel) {
    //获取内存中运行的线程池对象
    ThreadPoolTaskExecutor memThreadPool = (ThreadPoolTaskExecutor) applicationContext.getBean(poolModel.getPoolId());
    //获取内存中线程池的运行时状态
    poolModel.setCoreSize(memThreadPool.getCorePoolSize());
    poolModel.setActiveCount(memThreadPool.getActiveCount());
    poolModel.setCompletedTaskCount(memThreadPool.getThreadPoolExecutor().getCompletedTaskCount());
    poolModel.setLargestPoolSize(memThreadPool.getThreadPoolExecutor().getLargestPoolSize());
    poolModel.setPoolSize(memThreadPool.getPoolSize());
    poolModel.setMaxSize(memThreadPool.getThreadPoolExecutor().getMaximumPoolSize());
    //线程池队列
    BlockingQueue<Runnable> queue = memThreadPool.getThreadPoolExecutor().getQueue();
    poolModel.setRemainingCapacity(queue.remainingCapacity());
    return poolModel;
  }


}
