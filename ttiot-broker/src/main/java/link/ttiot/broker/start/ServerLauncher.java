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

package link.ttiot.broker.start;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.exceptions.NotInitedException;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import link.ttiot.broker.handler.core.ProtocolAdaptiveHandler;
import link.ttiot.common.config.ContextConfig;
import link.ttiot.common.config.banner.BannerReader;
import link.ttiot.common.context.Context;
import link.ttiot.common.context.db.DbHelper;
import link.ttiot.common.context.exception.ExceptionHandlerAdapter;
import link.ttiot.common.core.constant.CommonConstant;
import link.ttiot.common.core.ssl.SslBuilder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

/**
 * @author: shijun
 * @date: 2019-04-11
 * @description:
 */
@Slf4j
public class ServerLauncher {

    @Getter
    private Context tTiotContext;

    private ExceptionHandlerAdapter exceptionHandler;

    private DbHelper dbHelper;

    private SSLContext sslContext;

    public void launch() {
        tTiotContext = Context.builder().dbHelper(dbHelper).exceptionHandler(exceptionHandler).build();
        ContextConfig.TTiotStarterContextConfigAttribut ttiotConfig=tTiotContext.getTTiotStarterContextConfig().TTiot;
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        if (ttiotConfig.ssl.isEnabled()){
            sslContext = new SslBuilder().createSslContext(ttiotConfig.ssl.getCertificateType(),ttiotConfig.ssl.getCertificatePath(),ttiotConfig.ssl.getCertificatePassword());
        }
        serverBootstrap.group(tTiotContext.getBossGroup(), tTiotContext.getWorkerGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        if (ttiotConfig.ssl.isEnabled()) {
                            addSslHandler(pipeline);
                        }
                        //protocol adaptive
                        pipeline.addLast(CommonConstant.HEARTBEAT_HANDLER, new IdleStateHandler(0, 0, ttiotConfig.getHeartbeatTimeout()));
                        pipeline.addLast(CommonConstant.PROTOCOL_ADAPTIVE_HANDLER, new ProtocolAdaptiveHandler());
                    }
                });

        try {
            Channel channel = serverBootstrap.bind(ttiotConfig.getHost(), ttiotConfig.getPort()).sync().channel();
            tTiotContext.setServerChannel(channel);
        } catch (InterruptedException e) {
            log.error("TTIot boot failed！");
            throw ExceptionUtil.wrap(e, NotInitedException.class);
        }
        BannerReader.startBannerListener();
        log.info("TTIot starting success!");
    };


    public void addSslHandler(ChannelPipeline channelPipeline) {
        try {
            SSLEngine sslEngine = sslContext.createSSLEngine();
            sslEngine.setUseClientMode(false);
            sslEngine.setNeedClientAuth(false);
            sslEngine.setWantClientAuth(false);
            channelPipeline.addLast(new SslHandler(sslEngine));
        } catch (Exception e) {
            log.error("is not ssl request... and " + e.getMessage(), e);
        }
    }

    public ServerLauncher exceptionHandler(ExceptionHandlerAdapter exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    };

    public ServerLauncher dbHelper(DbHelper dbHelper) {
        this.dbHelper = dbHelper;
        return this;
    }
}
