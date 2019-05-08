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

import cn.hutool.core.exceptions.DependencyException;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.thread.ThreadUtil;
import link.ttiot.common.core.constant.enums.ThreadPoolEnum;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Constructor;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

/**
 * @author: shijun
 * @date: 2019-04-16
 * @description:
 */

@UtilityClass
public class ThreadPoolProvider {

    private static final ConcurrentMap<ThreadPoolEnum, ThreadPool> map = new ConcurrentHashMap<>();

    private static ThreadPoolEnum defaultThreadPoolEnum;

    public ThreadPool getDefault() {
        return Optional.ofNullable(defaultThreadPoolEnum).map(u -> map.get(u)).orElseThrow(() -> new UtilException("Component does not exist"));
    }

    public ThreadPool get(ThreadPoolEnum type) {
        ThreadPool ob = map.get(type);
        if (ob == null) {
            ob = register(type, null, false,null);
        }
        return ob;
    }

    public ThreadPool register(ThreadPoolEnum type, Integer corePoolSize, boolean isDefault,Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        ThreadPool ob = map.get(type);
        if (ob == null) {
            synchronized (map) {
                try {
                    Constructor<?> cons = ThreadPool.class.getConstructor(ThreadPoolEnum.class, Integer.class,Thread.UncaughtExceptionHandler.class);
                    ob = (ThreadPool) cons.newInstance(type, corePoolSize,uncaughtExceptionHandler);
                    if (isDefault) {
                        defaultThreadPoolEnum = type;
                    }
                    map.put(type, ob);
                } catch (Throwable e) {
                    throw ExceptionUtil.wrap(e, DependencyException.class);
                }
            }
        }
        return ob;
    }
}
