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

package link.ttiot.broker.handler.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import link.ttiot.broker.handler.MqttDecoderHandler;
import link.ttiot.broker.handler.http.HttpHeaderDecoder;
import link.ttiot.broker.handler.http.HttpPacket;
import link.ttiot.broker.handler.http.HttpRequestHandler;
import link.ttiot.broker.handler.websocket.MqttWebSocketCodec;
import link.ttiot.common.context.Context;
import link.ttiot.common.core.constant.CommonConstant;
import link.ttiot.common.ioc.annotation.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.util.List;


/**
 * @author: shijun
 * @date: 2020-03-02
 * @description:
 */
@Slf4j
public class ProtocolAdaptiveHandler extends ByteToMessageDecoder {
    /**
     * WebSocket握手的协议前缀
     */
    String Sec_WebSocket_Key = "Sec-WebSocket-Key".toLowerCase();


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        byte[] content = new byte[in.readableBytes()];
        in.readBytes(content);
        if (in != null) {
            HttpPacket httpPacket = HttpHeaderDecoder.decode(ByteBuffer.wrap(content), false);
            if (httpPacket == null) {
                log.info("the channel's protocol is tcp");
                //tcp
                tcp(ctx);
            } else {
                if (httpPacket.getHeaders().get(Sec_WebSocket_Key) != null) {
                    log.info("the channel's protocol is wbsocket");
                    //wbsocket
                    webSocket(ctx);
                } else {
                    log.info("the channel's protocol is http");
                    //http
                    http(ctx);

                }
            }
        }
        in.resetReaderIndex();
        ctx.pipeline().remove(CommonConstant.PROTOCOL_ADAPTIVE_HANDLER);
    }


    public void http(ChannelHandlerContext ctx){
        httpCommon(ctx);
        ctx.pipeline().addLast(CommonConstant.HTTP_REQUEST_HANDLER,new HttpRequestHandler());
        ctx.pipeline().remove(CommonConstant.HEARTBEAT_HANDLER);
    }

    public void tcp(ChannelHandlerContext ctx){
        mqtt(ctx);
    }

    public void webSocket(ChannelHandlerContext ctx){
        httpCommon(ctx);
        ctx.pipeline().addLast(CommonConstant.MQTT_WEBSOCKET_PROTOCOL,new WebSocketServerProtocolHandler(CommonConstant.WEBSOCKET_PATH,CommonConstant.MQTT_WEBSOCKET_SUBPROTOCOLS));
        ctx.pipeline().addLast(CommonConstant.MQTT_WEBSOCKET_CODEC, new MqttWebSocketCodec());
        mqtt(ctx);
    }

    public void mqtt(ChannelHandlerContext ctx){
        ctx.pipeline().addLast(CommonConstant.MQTT_DECODER, new MqttDecoder(CommonConstant.MAX_PAYLOAD_SIZE));
        ctx.pipeline().addLast(CommonConstant.MQTT_ENCODE, MqttEncoder.INSTANCE);
        ctx.pipeline().addLast(CommonConstant.MQTT_HANDLER, new MqttDecoderHandler());
    }

    public void httpCommon(ChannelHandlerContext ctx){
        ctx.pipeline().addLast(CommonConstant.HTTP_CODER,new HttpServerCodec());
        ctx.pipeline().addLast(CommonConstant.HTTP_AGGREGATOR, new HttpObjectAggregator(CommonConstant.HTTP_MAXCONTENT_LENGTH));
        ctx.pipeline().addLast(CommonConstant.HTTP_COMPRESSOR, new HttpContentCompressor());
        ctx.pipeline().addLast(CommonConstant.HTTP_CHUNKED_WRITER,new ChunkedWriteHandler());
    }
}
