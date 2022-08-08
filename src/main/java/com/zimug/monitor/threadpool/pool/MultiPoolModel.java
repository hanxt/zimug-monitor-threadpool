package com.zimug.monitor.threadpool.pool;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @创建人 韩晓彤
 * @创建时间 2022/8/8
 * @描述 线程池配置集合
 **/
@ConfigurationProperties(prefix = "thread-pool")
public class MultiPoolModel {

  private Boolean enable;

  private List<PoolModel> poolLists;

  public Boolean getEnable() {
    return enable;
  }

  public void setEnable(Boolean enable) {
    this.enable = enable;
  }

  public List<PoolModel> getPoolLists() {
    return poolLists;
  }

  public void setPoolLists(List<PoolModel> poolLists) {
    this.poolLists = poolLists;
  }
}
