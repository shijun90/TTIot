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

package link.ttiot.broker.handler.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import link.ttiot.broker.eventor.decode.MqttDecoderFailEvent;
import link.ttiot.broker.eventor.decode.MqttDecoderSuccessEvent;
import link.ttiot.broker.eventor.http.HttpRequestEvent;
import link.ttiot.common.context.Context;
import link.ttiot.common.context.service.DeviceService;
import link.ttiot.common.core.constant.CommonConstant;
import link.ttiot.common.core.function.FunctionApi;
import link.ttiot.common.ioc.annotation.DefaultListener;
import link.ttiot.common.ioc.annotation.Inject;
import lombok.extern.slf4j.Slf4j;


/**
 * @author: shijun
 * @date: 2020-02-28
 * @description:
 */
@ChannelHandler.Sharable
@Slf4j
public class HttpRequestHandler extends ChannelInboundHandlerAdapter implements FunctionApi {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        predicateDoElse(msg,u->!(u instanceof FullHttpRequest),u->ctx.close(),u->{
            Context.me().publishEvent(new HttpRequestEvent(u,ctx));
        });

    }



}