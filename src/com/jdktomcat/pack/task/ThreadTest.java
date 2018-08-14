package com.jdktomcat.pack.task;

/**
 * 类描述：
 *
 * @author 汤旗
 * @date 2018-08-14
 */
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池测试
 *
 * @author Kai.Zhao
 * @date 2018/08/07
 */
public class ThreadTest {


    /**
     * 异步线程每个任务花费时间太长就会导致积压问题
     * <p>
     * 如果线程最大数量设置过大，队列设置长度过长，长期高负载最终会导致程序OOM。
     * <p>
     * 解决方案：
     * 1、添加业务熔断机制，当业务执行时间在10分钟内超过阈值时间3秒100次后，熔断业务，将邮件保存至失败列表；
     * 2、添加定时补发机制，将发送失败的邮件通过定时程序补发；
     * 3、根据邮件服务提供商发送质量，支持自动切换通道；
     * 4、采用邮件分级制度，优先发送优先级比较高的邮件，优先级低的可以选择不发或者延迟发放。
     * 5、记录异步进入时间、异步发送时间，当两者时间差大于设定阈值后，v消息报警，通知开发切换同步发送通道。
     *
     * @param args 参数
     * @throws InterruptedException 线程中断异常
     */
    public static void main(String[] args) throws InterruptedException {

        withPool();
    }

    /**
     * 使用线程池
     *
     * @throws InterruptedException 线程中断异常
     */
    private static void withPool() throws InterruptedException {

//        final ThreadPoolExecutor pool = newPool();
         final ThreadPoolExecutor pool = oldPool();

        int i = 1;

        // 执行业务次数
        int executeCount = 20000;

        // 系统允许容忍的最大响应时间 5秒
        final int responseTime = 4900;

        // 每个任务花费时间 0.1秒
        final int taskCost = 100;

        final Random random = new Random();

        // 每秒的任务数 TPS = 500
        final int tasks = 500;

        do {

            final int count = i;

            // 进入异步方法时间
            final Date now = DateTime.now().toDate();
            final long start = System.currentTimeMillis();
            pool.execute(new Runnable() {

                @Override
                public void run() {

                    Date date = DateTime.now().toDate();
                    try {
//                        // 模拟业务执行
                        Thread.sleep(random.nextInt(responseTime) + taskCost);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    System.out.println("count: " + count + ", 进入时间: " + simpleDateFormat.format(date) +
                            ", 执行时间: " + simpleDateFormat.format(DateTime.now().toDate()) + " " + getTreadPoolInfo(pool)
                            + ", cost =" + (System.currentTimeMillis() - start));
                }
            });

            Thread.sleep(1000 / tasks);

            i++;
        } while (i <= executeCount);
    }

    /**
     * 修改前线程池配置
     */
    public static ThreadPoolExecutor oldPool() {
        /*
         * 核心线程数
         */
        int corePoolSize = Runtime.getRuntime().availableProcessors();
        /*
         * 最大线程数
         * 活动线程数量 >= 核心线程数 && 排队线程数量满 === 线程池创建新线程处理任务
         * 活动线程数量 = 最大线程数 && 排队线程数量满 === 线程池拒绝处理任务
         */
        int maxPoolSize = Integer.MAX_VALUE;
        /*
         * 线程空闲时间
         */
        long keepAliveTime = 10L;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        /*
         * 当核心线程数达到最大时 新添加任务将在队列中排队等待
         */
        int capacity = 1000000;

        return new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, timeUnit,
                new ArrayBlockingQueue<Runnable>(capacity), new ThreadFactoryBuilder()
                .setNameFormat("thread-pool-2-").build(),
                new RejectedExecutionHandler() {

                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        System.out.println(String.format("======================================================== 异步线程池队列已经塞满 %s", getTreadPoolInfo(executor)));
                    }
                });
    }

    /**
     * 修改后线程池配置
     */
    private static ThreadPoolExecutor newPool() {
        /*
         * 核心线程数
         */
        int corePoolSize = 80;
        /*
         * 最大线程数
         * 活动线程数量 >= 核心线程数 && 排队线程数量满 === 线程池创建新线程处理任务
         * 活动线程数量 = 最大线程数 && 排队线程数量满 === 线程池拒绝处理任务
         */
        int maxPoolSize = 2500;
        /*
         * 线程空闲时间
         */
        long keepAliveTime = 0L;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        /*
         * 当核心线程数达到最大时 新添加任务将在队列中排队等待
         */
        int capacity = 1;

        return new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, timeUnit,
                new ArrayBlockingQueue<Runnable>(capacity), new ThreadFactoryBuilder()
                .setNameFormat("thread-pool-1-").build(),
                new RejectedExecutionHandler() {

                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        System.out.println(String.format("========================================================异步线程池队列已经塞满 %s", getTreadPoolInfo(executor)));
                    }
                });
    }

    /**
     * 线程池详情
     *
     * @return 线程池详情
     */
    private static String getTreadPoolInfo(ThreadPoolExecutor executor) {
        return String.format("当前排队线程数: %s, 当前活动线程数: %s, 执行完成线程数: %s, 总线程数: %s",
                executor.getQueue().size(), executor.getActiveCount(), executor.getCompletedTaskCount(), executor.getTaskCount());
    }
}