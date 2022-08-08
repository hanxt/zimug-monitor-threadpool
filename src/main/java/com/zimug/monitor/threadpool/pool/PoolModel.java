package com.zimug.monitor.threadpool.pool;

/**
 * @创建人 韩晓彤
 * @创建时间 2022/8/8
 * @描述 线程池静态参数及运行时状态实体
 **/
public class PoolModel {

  //静态参数配置
  /** 线程池唯一标识(英文) */
  private String poolId;

  /** 线程池名称(中文描述) */
  private String poolName;

  /** 初始线程数 */
  private Integer coreSize;

  /** 最大线程数 */
  private Integer maxSize;

  /** 等待队列容量 */
  private Integer queueCapacity;


  //运行时状态
  private Integer poolSize;  //当前线程池线程数
  private Integer activeCount; //当前线程池活跃线程数
  private Long completedTaskCount; //当前线程池完成任务数
  private Integer largestPoolSize;  //当前线程池峰值线程数
  private Integer remainingCapacity; //任务等待队列剩余容量

  public String getPoolId() {
    return poolId;
  }

  public void setPoolId(String poolId) {
    this.poolId = poolId;
  }

  public String getPoolName() {
    return poolName;
  }

  public void setPoolName(String poolName) {
    this.poolName = poolName;
  }

  public Integer getCoreSize() {
    return coreSize;
  }

  public void setCoreSize(Integer coreSize) {
    this.coreSize = coreSize;
  }

  public Integer getMaxSize() {
    return maxSize;
  }

  public void setMaxSize(Integer maxSize) {
    this.maxSize = maxSize;
  }

  public Integer getQueueCapacity() {
    return queueCapacity;
  }

  public void setQueueCapacity(Integer queueCapacity) {
    this.queueCapacity = queueCapacity;
  }

  public Integer getPoolSize() {
    return poolSize;
  }

  public void setPoolSize(Integer poolSize) {
    this.poolSize = poolSize;
  }

  public Integer getActiveCount() {
    return activeCount;
  }

  public void setActiveCount(Integer activeCount) {
    this.activeCount = activeCount;
  }

  public Long getCompletedTaskCount() {
    return completedTaskCount;
  }

  public void setCompletedTaskCount(Long completedTaskCount) {
    this.completedTaskCount = completedTaskCount;
  }

  public Integer getLargestPoolSize() {
    return largestPoolSize;
  }

  public void setLargestPoolSize(Integer largestPoolSize) {
    this.largestPoolSize = largestPoolSize;
  }

  public Integer getRemainingCapacity() {
    return remainingCapacity;
  }

  public void setRemainingCapacity(Integer remainingCapacity) {
    this.remainingCapacity = remainingCapacity;
  }
}
