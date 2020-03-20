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

package link.ttiot.common.context;

import cn.hutool.core.util.ReflectUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.concurrent.GlobalEventExecutor;
import link.ttiot.common.config.ContextConfig;
import link.ttiot.common.context.db.DbHelper;
import link.ttiot.common.context.exception.ExceptionHandlerAdapter;
import link.ttiot.common.context.protocal.mqtt.MqttApplicationEvent;
import link.ttiot.common.context.service.SessionService;
import link.ttiot.common.core.constant.enums.ThreadPoolEnum;
import link.ttiot.common.core.provider.bean.SingtonBeanProvider;
import link.ttiot.common.core.thread.ThreadPoolProvider;
import link.ttiot.common.ioc.core.ApplicationEvent;
import link.ttiot.common.ioc.core.RuleHandler;
import link.ttiot.common.ioc.vo.MqttPayloadVo;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


/**
 * @author: shijun
 * @date: 2019-04-11
 * @description:
 */
@Slf4j
public class Context {
    @Getter
    @Setter
    private ContextConfig tTiotStarterContextConfig;
    @Getter
    private ApplicationEventMulticaster applicationEventMulticaster;
    @Getter
    private NioEventLoopGroup bossGroup;
    @Getter
    private NioEventLoopGroup workerGroup;
    @Getter
    @Setter
    private Channel serverChannel;
    @Getter
    private DbHelper dbHelper;
    @Getter
    private ChannelGroup ttIotChannelGroup;
    @Getter
    private ExceptionHandlerAdapter exceptionHandler;

    public Context() {
    }

    public static Context me() {
        return SingtonBeanProvider.produce(Context.class);
    }

    public static Context builder() {

        Context ttIotContext = me();
        ttIotContext.tTiotStarterContextConfig = ContextConfig.getConfig();
        ttIotContext.ttIotChannelGroup = new ChannelGroup(GlobalEventExecutor.INSTANCE);
        ttIotContext.bossGroup = (ttIotContext.tTiotStarterContextConfig.TTiot.netty.getBossGroupCount() <= 0) ? new NioEventLoopGroup() : new NioEventLoopGroup(ttIotContext.tTiotStarterContextConfig.TTiot.netty.getBossGroupCount());
        ttIotContext.workerGroup = (ttIotContext.tTiotStarterContextConfig.TTiot.netty.getWorkerGroupCount() <= 0) ? new NioEventLoopGroup() : new NioEventLoopGroup(ttIotContext.tTiotStarterContextConfig.TTiot.netty.getWorkerGroupCount());
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.valueOf(ttIotContext.tTiotStarterContextConfig.TTiot.netty.getLeakDetectorLevel().toUpperCase()));
        return ttIotContext;
    }

    public Context exceptionHandler(ExceptionHandlerAdapter exceptionHandler) {
        Context ttIotContext = me();
        ttIotContext.exceptionHandler = exceptionHandler;
        return ttIotContext;
    }

    public Context dbHelper(DbHelper dbHelper) {
        Context ttIotContext = me();
        ttIotContext.dbHelper = dbHelper;
        return ttIotContext;
    }

    public  Context build(){
        Context ttIotContext = me();
        if (ttIotContext.getDbHelper()==null){
            throw  new RuntimeException();
        }
        if (ttIotContext.getExceptionHandler()==null){
            throw  new RuntimeException();
        }
        ttIotContext.applicationEventMulticaster = new ApplicationEventMulticaster(ttIotContext.exceptionHandler);
        ThreadPoolProvider.register(ThreadPoolEnum.cachedThread,ttIotContext.tTiotStarterContextConfig.TTiot.netty.getWorkerGroupCount(),true,ttIotContext.exceptionHandler);
        return ttIotContext;
    };

    public void publishEvent(ApplicationEvent event) {
        applicationEventMulticaster.multicastEvent(event);
    }

    public Channel getChannelByContext(String clientId) {
        return ttIotChannelGroup.find(clientId);
    }

    public boolean containsChannelByContext(Channel channel) {
        return ttIotChannelGroup.contains(channel);
    }

    public boolean setChannelByContext(String clientId, Channel channel) {
        return ttIotChannelGroup.add(clientId, channel);
    }

    public boolean removeChannelByContext(String clientId, SessionService sessionService, Runnable runnable) {
        return ttIotChannelGroup.remove(clientId, sessionService, runnable);
    }

    public MqttApplicationEvent mqttEventorProduce(String type, ChannelHandlerContext channelHandlerContext, MqttMessage mqttMessage) {
        return applicationEventMulticaster.mqttEventorProduce(type, channelHandlerContext, mqttMessage);
    }

    public void multicastRuleHandler(String ruleName, Object o) {
        applicationEventMulticaster.multicastRuleHandler(ruleName,o);
    }

}
