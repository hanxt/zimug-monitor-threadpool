![](https://cdn.zimug.com/wx-zimug.png)

在开发spring boot应用服务的时候，难免会使用到异步任务及线程池。spring boot的线程池是可以自定义的，所以我们经常会在项目里面看到类似于下面这样的代码
~~~
@Bean
public Executor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(config.getCorePoolSize());
    executor.setMaxPoolSize(config.getMaxPoolSize());
    executor.setQueueCapacity(config.getQueueCapacity());
    executor.setThreadNamePrefix("TaskExecutePool-");
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.initialize();
    return executor;
}

~~~
使用起来很方便，但是这样做有几个问题：
* 开发人员在代码里面随意定义线程池，开发人员A自定义一个线程池，开发人员B自定义一个线程池。线程池的资源的使用没有规划与合理的安排，后期维护的成本升高。
* 一旦发现线程池数量不足或资源满载，很难调整配置，只能调整代码，重新部署。
* 多人编写代码，很难统计那个服务里面存在线程池，有几个线程池。
* 如果不去跟踪代码，你很难知道它的使用情况。定义了50个线程，存不存在资源浪费？存不存在资源等待？

为了解决上述的问题，我开发了一个Spring Boot Starter（开源项目地址：），方便集成到Spring Boot项目里面去。目标是：**在不改变SpringBoot线程池的核心实现的基础上，使其可视化、易观测、易配置、易使用**。

> 需要说明的是：zimug-monitor-threadpool并未改变SpringBoot线程池的实现，只是在其基础上添加了初始化阶段的配置自动化加载，运行时的状态监控。所以任何有关Spring Boot线程池运行时性能的讨论，都与本文及其实现无关。


## 一、易集成、易配置
该项目release jar包我已经上传到中央仓库，直接通过下面的maven坐标引入
~~~
<dependency>
    <groupId>com.zimug</groupId>
    <artifactId>zimug-monitor-threadpool</artifactId>
    <version>1.0</version>
</dependency>
~~~
如下配置spring boot YAML(application.yml)所示，配置了两个线程池，分别是test、test2。当`thread-pool.enable=true`的时候线程池配置生效。
~~~
thread-pool:
  enable: true
  poolLists:
    - poolId: test    #线程池唯一标识
      poolName: 测试1   #线程池的中文描述，比如线程池给谁用？
      coreSize: 5  #线程池初始化核心线程数量
      maxSize: 10  #线程池最大线程容量
      queueCapacity: 10  #线程池等待队列的容量
    - poolId: test2
      poolName: 测试2
      coreSize: 5
      maxSize: 10
      queueCapacity: 10
~~~
通过下面的这张图理解上面的配置信息
![](http://cdn.zimug.com/9f3e0592898b7476c607504df6d8a97f)
* 当线程任务数量core_size被活跃任务线程占满之后，线程任务会被放入等待队列(queueCapacity=10)
* 当等待队列queueCapacity也被占满之后，才会扩大线程池的容量
* 线程池的容量最大扩展到maxSize。如果maxSize和queueCapacity都满了，任务就阻塞了。


## 二、易使用
使用方式和SpringBoot 代码方式自定义线程池的使用方式是一样的。使用`@Async`注解的值是test，调用该注解标识的函数就会放入上文中配置的test线程池里面去执行。
~~~java
@Component
public class TestTask {
    @Async("test")   //注意这里，test是线程池配置的poolId
    public Future<String> test() throws Exception {
        System.out.println("当前线程：" + Thread.currentThread().getName());
        return new AsyncResult<>("测试任务");
    }
}
~~~


## 三、可视化易观测
在项目中引入zimug-monitor-threadpool之后，进行线程池配置，使用线程池。访问服务的`/pool.html`即可获取当前SpringBoot服务的线程池配置信息，以及运行时状态信息。
![](http://cdn.zimug.com/fb18ea484800ad1c22fbba21b3f78722)
* 线程池ID、描述、初始化线程数、最大线程数、任务等待队列的容量是上文中yaml静态配置
* 当前线程池的容量，即：线程池当前的线程数量（活跃+非活跃线程数总和）
* 当前活跃线程数，即：正在运行程序任务的线程数量
* 线程池活跃线程的最大峰值，如果该值等于初始化线程数，说明曾经出现了任务等待，即：任务放入等待队列，效率较低。如果该值大于初始化线程数，说明任务等待队列曾经满载，需要扩容。如果该值接近等于最大线程数，就需要扩大最大线程数的值。
* 当前任务等待队列剩余的容量，剩的越少，说明正在等待执行的任务就越多。

## 四、实现原理
zimug-monitor-threadpool的实现原理也非常简单，简单说一下原理，具体实现参考源码。

* 首先通过SpringBoot加载yaml配置信息，配置加载完成之后自定义实现配置自动化加载。这个实现原理及实现方法网上到处都是，我就不写了。
* 将配置信息加载之后new 一个ThreadPoolTaskExecutor 对象，并通过Spring的ConfigurableBeanFactory将线程池对象的bean注册到Spring上下文环境中，bean的id是poolId配置。就可以提供给运行时任务使用了。
~~~
configurableBeanFactory.registerSingleton(pool.getPoolId(), taskExecutor);
~~~
* 待需要监测线程池运行时状态的时候，再把线程池对象通过getBean方法获取到，从而获取运行时信息返回给前台请求。
~~~
ThreadPoolTaskExecutor memThreadPool = (ThreadPoolTaskExecutor) applicationContext.getBean(poolModel.getPoolId());
~~~

![](https://cdn.zimug.com/wx-zimug.png)


