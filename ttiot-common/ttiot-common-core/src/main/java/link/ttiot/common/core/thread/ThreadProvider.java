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
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.ThreadFactory;

/**
 * @author: shijun
 * @date: 2019-05-07
 * @description:
 */
@Slf4j
public class ThreadProvider implements ThreadFactory {

    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    public ThreadProvider(Thread.UncaughtExceptionHandler uncaughtExceptionHandler){
        this.uncaughtExceptionHandler=uncaughtExceptionHandler;
    }

    @Override
    public Thread newThread(Runnable r) {
       return Optional.ofNullable(uncaughtExceptionHandler).map(u->{
           log.info("Task named {} begin",r.getClass());
            Thread thread=new Thread(r);
            thread.setUncaughtExceptionHandler(u);
            return thread;
        }).orElse(new Thread(r));
    }
}
