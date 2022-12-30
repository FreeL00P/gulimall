package com.atguigu.gulimall.search.thread;

import org.springframework.cache.annotation.Cacheable;

import java.util.concurrent.*;

/**
 * ThreadTest
 *
 * @author fj
 * @date 2022/12/24 18:46
 */
public class ThreadTest {
    public static ExecutorService executor=Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main Start========> ");
        /** 多线程实现方法
         * 1、继承Thread
         *         Thread01 thread01 = new Thread01();
         *         thread01.start();
         * 2、实现Runnable接口
         *         Runable01 runable01 = new Runable01();
         *         new Thread(runable01).start();
         * 3、实现Callable接口+FutureTask (可以拿到返回结果可以处理异常)
         *    Callable01 callable01 = new Callable01();
         *    FutureTask<Integer> futureTask = new FutureTask<>(callable01);
         *    new Thread(futureTask).start();
         *    //阻塞等待整个线程执行完成，获取返回结果
         *    Integer integer = futureTask.get();
         * 4、线程池
         *  给线程提交任务
         *  service.execute(new Runable01());
         *  1、2不能得到返回值 3、可以获取到返回值
         *  1、2、3 都不能控制资源
         *  4 可以控制资源，性能稳定
         */

        /**
         * 七大参数
         * corePoolSize:[5] 核心线程数[会一直存在除非(allowCoreThreadTimeout)];
         * maximumPoolSize:[200] 最大线程数，控制资源
         * keepAliveTime 存活时间，如果当前的线程数量大于core数量
         *      释放空闲的线程(核心线程之外的线程) 只要线程空闲时间大于指定的keepAliveTime
         * unit:时间单位
         * BlockingQueue<Runnable> workQueue 阻塞队列。如果任务过多，就会将目前过多的任务放在队列里面
         *              只要有线程空闲，就会去队列里面取出新的任务继续
         * threadFactory 线程创建工厂
         * RejectedExecutionHandler handler 如果队列满了，按照我们指定的拒绝策略拒绝执行任务
         * 工作顺序
         * 1)线程池创建、准备好core数量的核心线程，准备接受任务
         * 1.1)core满了，就将再进来的任务放入阻塞队列中，空闲的core会自己去阻塞队列获取任务执行
         * 1.2)阻塞队列满了，就直接开新线程执行，最大只能开到max指定的数量
         * 1.3)max就用RejectedExecutionHandler拒绝策略拒绝任务
         * 1.4)max都执行完成，有很多空闲，在keepAliveTime释放这些线程
         *  new LinkedBlockingDeque<>(),默认是Integer最大值，内存不够，必须传指定数量
         */
//        ThreadPoolExecutor executor = new ThreadPoolExecutor(5,
//                200,
//                10,
//                TimeUnit.SECONDS,
//                new LinkedBlockingDeque<>(100000),
//                Executors.defaultThreadFactory(),
//                new ThreadPoolExecutor.AbortPolicy());
        //Executors.newCachedThreadPool();//core是0，所有线程都可以回收
        //Executors.newFixedThreadPool(10);//固定大小，core=max都不能被回收
        //Executors.newSingleThreadExecutor();//单线程的线程池，从队列中获取任务，挨个执行
        //Executors.newScheduledThreadPool();//定时任务的线程池
        /**
         * 方法完成后的感知
         */
//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("Thread->" + Thread.currentThread().getId());
//            int i = 10 / 0;
//            System.out.println("i ==> " + i);
//            return i;
//        }, executor).whenComplete((res, exception) -> {
//            //虽然得到异常信息，但没法修改返回数据
//            System.out.println("异步任务成功完成结果是 = " + res + "异常是" + exception);
//        }).exceptionally(throwable -> {
//            //可以感知异常，同时返回默认值
//            return 10;
//        });
        /**
         * 方法执行完成后的处理
         */
//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("Thread->" + Thread.currentThread().getId());
//            int i = 2;
//            System.out.println("i ==> " + i);
//            return i;
//        }, executor).handle((res,thr) ->{
//            if(res != null){
//                return res*2;
//            }
//            if (thr != null){
//                return 0;
//            }
//            return 0;
//        });
        /**
         * 线程串行化
         * 1)thenRun 不能获取上一步的执行结果 无返回值
         *      .thenRunAsync(()->{
         *             System.out.println("任务二启动了");
         *         });
         * 2) thenAccept可以获取到上一步的执行结果
         *              thenAcceptAsync(res->{
         *             System.out.println("任务二启动了..."+res);})
         *  3)thenApplyAsync 能接受上一步结果 有返回值
         *  .thenApplyAsync(res -> {
         *             System.out.println("任务二启动了..." + res);
         *             return "hello" + res;
         *         });
         */

        CompletableFuture<Integer> future01 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务1开始");
            int i = 10 / 4;
            System.out.println("任务1结束");
            return i;
        }, executor);
        CompletableFuture<Integer> future02 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务2开始");
            int i = 10 / 4;
            System.out.println("任务2结束");
            return i;
        }, executor);
//        future01.runAfterBothAsync(future02,()->{
//            System.out.println("任务三开始");
//        },executor);
//        future01.thenAcceptBothAsync(future02,(f1,f2)->{
//            System.out.println("任务三开始 之前的结果f1="+f1+" f2= "+f2);
//        },executor);

        CompletableFuture<String> future03 = future01.thenCombineAsync(future02, (f1, f2) -> {
            System.out.println("任务三开始 之前的结果f1=" + f1 + " f2= " + f2);
            return f1 + ":" + f2 + "=> Hello";
        }, executor);
        System.out.println("main End========> "+future03.get());
    }
    public static class Thread01 extends Thread {
        @Override
        public void run() {
            System.out.println("Thread->" + Thread.currentThread().getId());
            int i=10/2;
            System.out.println("i ==> " + i);
        }
    }

    public static class Runnable01 implements Runnable {

        @Override
        public void run() {
            System.out.println("Thread->" + Thread.currentThread().getId());
            int i=10/2;
            System.out.println("i ==> " + i);
        }
    }

    public static class Callable01 implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            System.out.println("Thread->" + Thread.currentThread().getId());
            int i=10/2;
            System.out.println("i ==> " + i);
            return i;
        }
    }
}
