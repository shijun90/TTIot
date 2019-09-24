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

package link.ttiot.broker.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import link.ttiot.broker.context.Context;
import link.ttiot.broker.eventor.decode.MqttDecoderFailEvent;
import link.ttiot.broker.eventor.decode.MqttDecoderSuccessEvent;
import link.ttiot.broker.eventor.lwt.MqttLwtEvent;
import link.ttiot.common.core.channel.ChannelUtils;
import link.ttiot.common.core.function.FunctionApi;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;


/**
 * @author: shijun
 * @date: 2019-04-12
 * @description:
 */
@ChannelHandler.Sharable
@Slf4j
public class MqttDecoderHandler extends SimpleChannelInboundHandler<MqttMessage> implements FunctionApi {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MqttMessage msg) {
        Context.me().publishEvent(msg.decoderResult().isFailure() ? new MqttDecoderFailEvent(msg, ctx) : new MqttDecoderSuccessEvent(msg, ctx));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.debug("The current thread is raising an exception");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            predicateDo(ctx.channel(), u -> idleStateEvent.state() == IdleState.ALL_IDLE, u -> {
                Optional.ofNullable(ChannelUtils.clientId(u)).map(clientId -> {
                    Context.me().publishEvent(new MqttLwtEvent(clientId));
                    return true;
                }).orElse(false);
                ctx.close();
            });
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }


}
