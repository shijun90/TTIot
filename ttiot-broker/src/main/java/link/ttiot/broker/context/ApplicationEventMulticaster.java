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

package link.ttiot.broker.context;


import cn.hutool.core.exceptions.DependencyException;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.ClassUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttMessage;
import link.ttiot.broker.context.protocol.ProtocolApplicationEvent;
import link.ttiot.broker.context.protocol.mqtt.MqttApplicationEvent;
import link.ttiot.broker.handler.ExceptionHandlerAdapter;
import link.ttiot.common.core.channel.ChannelUtils;
import link.ttiot.common.core.exception.ConnectException;
import link.ttiot.common.core.thread.ThreadPoolProvider;
import link.ttiot.common.ioc.annotation.Eventor;
import link.ttiot.common.ioc.core.ApplicationEvent;
import link.ttiot.common.ioc.core.ApplicationListener;
import link.ttiot.common.ioc.multicaster.DefaultAbstractMulticaster;
import link.ttiot.common.ioc.multicaster.Multicaster;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Set;

/**
 * @author: shijun
 * @date: 2019-04-13
 * @description:
 */
@Slf4j
public class ApplicationEventMulticaster extends DefaultAbstractMulticaster implements Multicaster {

    private ExceptionHandlerAdapter exceptionHandler;

    public ApplicationEventMulticaster(ExceptionHandlerAdapter exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        registerListeners();
    }

    public void registerListeners() {
        super.registerListeners(Context.me().getDbHelper());
    }

    /**
     * 事件广播，处理全局异常
     */
    @Override
    public void multicastEventProcess(ApplicationEvent event) {
        List<ApplicationListener<ApplicationEvent>> listeners = getApplicationListeners(event);
        if (listeners != null && listeners.size() > 0) {
            for(ApplicationListener<ApplicationEvent> applicationListener:listeners){
                try {
                    if (applicationListener.isDefault()) {
                        invokeListener(applicationListener, event);
                    } else {
                        if (applicationListener.isAsynchronous()) {
                            ThreadPoolProvider.getDefault().execute(() -> invokeListener(applicationListener, event));
                        } else {
                            invokeListener(applicationListener, event);
                        }
                    }
                } catch (Throwable e) {
                    exceptionHandler.uncaughtException(Thread.currentThread(), e);
                    break;
                }
            }
        } else {
            log.error("event:{}, There is no listener processor", event.getClass());
        }
    }

    @Override
    public void multicastEvent(final ApplicationEvent event) {
        Eventor eventor = event.getClass().getAnnotation(Eventor.class);
        if (eventor != null) {
            //对每一个发布事件检查登陆状态
            if (eventor.auth()) {
                if (event instanceof ProtocolApplicationEvent) {
                    ChannelHandlerContext context = ((ProtocolApplicationEvent) event).getChannelHandlerContext();
                    if (!ChannelUtils.ifLogin(context.channel())) {
                        exceptionHandler.uncaughtException(Thread.currentThread(), new ConnectException(MqttConnectReturnCode.CONNECTION_REFUSED_NOT_AUTHORIZED, context.channel()));
                        context.channel().close();
                        return;
                    }
                }
            }
            multicastEventProcess(event);
        }
    }

    public MqttApplicationEvent mqttEventorProduce(@NonNull String type, ChannelHandlerContext channelHandlerContext, MqttMessage mqttMessage) {
        Set<Class<?>> classes = ClassUtil.scanPackageByAnnotation("", Eventor.class);
        for (Class<?> c : classes) {
            if (MqttApplicationEvent.class.isAssignableFrom(c)) {
                Eventor eventor = c.getAnnotation(Eventor.class);
                if (type.equals(eventor.value())) {
                    try {
                        Constructor<?> cons = c.getConstructor(MqttMessage.class, ChannelHandlerContext.class);
                        return (MqttApplicationEvent) cons.newInstance(mqttMessage, channelHandlerContext);
                    } catch (Throwable e) {
                        throw ExceptionUtil.wrap(e, DependencyException.class);
                    }
                }
            }
        }
        throw new DependencyException("class type :" + type + " not found");
    }

}
