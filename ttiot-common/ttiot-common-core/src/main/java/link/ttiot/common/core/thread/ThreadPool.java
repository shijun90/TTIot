/**
 * Copyright(c) link.ttiot & shijun All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Author: shijun (conttononline@outlook.com)
 */

package link.ttiot.common.core.thread;
import io.netty.util.NettyRuntime;
import io.netty.util.internal.SystemPropertyUtil;
import link.ttiot.common.core.constant.CommonConstant;
import link.ttiot.common.core.constant.enums.ThreadPoolEnum;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author: shijun
 * @date: 2019-05-07
 * @description:
 */
public class ThreadPool {

    private static final Integer DEFAULT_EVENT_LOOP_THREADS;

    static {
        DEFAULT_EVENT_LOOP_THREADS = Math.max(1, SystemPropertyUtil.getInt(
                CommonConstant.EVENTLOOP_KEY, NettyRuntime.availableProcessors() * 2));
    }

    private ExecutorService exec;
    private ScheduledExecutorService scheduleExec;

    public ThreadPool(final ThreadPoolEnum type, Integer corePoolSize, Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        if (corePoolSize==-1||corePoolSize==null){
            corePoolSize=DEFAULT_EVENT_LOOP_THREADS;
        }
        scheduleExec = Executors.newScheduledThreadPool(corePoolSize,new ThreadProvider(uncaughtExceptionHandler));
        switch (type) {
            case fixedThread:
                exec = Executors.newFixedThreadPool(corePoolSize,new ThreadProvider(uncaughtExceptionHandler));
                break;
            case singleThread:
                exec = Executors.newSingleThreadExecutor(new ThreadProvider(uncaughtExceptionHandler));
                break;
            case cachedThread:
                exec = Executors.newCachedThreadPool(new ThreadProvider(uncaughtExceptionHandler));
                break;
        }
    }

    public void execute(final Runnable command) {
        exec.execute(command);
    }

    public void execute(final List<Runnable> commands) {
        for (Runnable command : commands) {
            exec.execute(command);
        }
    }

    public void shutDown() {
        exec.shutdown();
    }

    public List<Runnable> shutDownNow() {
        return exec.shutdownNow();
    }

    public boolean isShutDown() {
        return exec.isShutdown();
    }

    public boolean isTerminated() {
        return exec.isTerminated();
    }

    public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
        return exec.awaitTermination(timeout, unit);
    }

    public <T> Future<T> submit(final Callable<T> task) {
        return exec.submit(task);
    }

    public <T> Future<T> submit(final Runnable task, final T result) {
        return exec.submit(task, result);
    }

    public Future<?> submit(final Runnable task) {
        return exec.submit(task);
    }

    public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return exec.invokeAll(tasks);
    }

    public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks, final long timeout, final TimeUnit unit) throws
            InterruptedException {
        return exec.invokeAll(tasks, timeout, unit);
    }

    public <T> T invokeAny(final Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return exec.invokeAny(tasks);
    }

    public <T> T invokeAny(final Collection<? extends Callable<T>> tasks, final long timeout, final TimeUnit unit) throws
            InterruptedException, ExecutionException, TimeoutException {
        return exec.invokeAny(tasks, timeout, unit);
    }

    public ScheduledFuture<?> schedule(final Runnable command, final long delay, final TimeUnit unit) {
        return scheduleExec.schedule(command, delay, unit);
    }

    public <V> ScheduledFuture<V> schedule(final Callable<V> callable, final long delay, final TimeUnit unit) {
        return scheduleExec.schedule(callable, delay, unit);
    }

    public ScheduledFuture<?> scheduleWithFixedRate(final Runnable command, final long initialDelay,
                                                    final long period, final TimeUnit unit) {
        return scheduleExec.scheduleAtFixedRate(command, initialDelay, period, unit);
    }

    public ScheduledFuture<?> scheduleWithFixedDelay(final Runnable command, final long initialDelay,
                                                     final long delay, final TimeUnit unit) {
        return scheduleExec.scheduleWithFixedDelay(command, initialDelay, delay, unit);
    }

}
