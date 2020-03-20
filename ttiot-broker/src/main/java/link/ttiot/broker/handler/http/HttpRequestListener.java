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

import cn.hutool.json.JSONUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.util.CharsetUtil;
import link.ttiot.broker.eventor.http.HttpRequestEvent;
import link.ttiot.broker.eventor.publish.MqttPublishTopicEvent;
import link.ttiot.common.context.protocal.ProtocolApplicationListener;
import link.ttiot.common.context.service.DeviceService;
import link.ttiot.common.core.constant.CommonConstant;
import link.ttiot.common.core.function.FunctionApi;
import link.ttiot.common.ioc.annotation.DefaultListener;
import link.ttiot.common.ioc.annotation.Inject;

/**
 * @author: shijun
 * @date: 2020-03-17
 * @description:
 */
@DefaultListener
public class HttpRequestListener  extends ProtocolApplicationListener<HttpRequestEvent> implements FunctionApi {

    @Inject
    private DeviceService devService;

    private static String INVALID_REQUEST="invalid request";

    private static String RUNTIME_EXCEPTION="unknown exception";

    private static String AUTHENTICATION_FAILURE="authentication failure";

    private static String INVALID_MQTTQOS="mqttqos must be >0 and <1";

    private static String USER_NAME="userName";

    private static String PASSWORD="password";

    private static String TENANT_ID="tenantId";

    public HttpRequestListener() {
        super();
    }

    @Override
    public void onApplicationEvent(HttpRequestEvent httpRequestEvent) {

        try{
            String path=httpRequestEvent.getSource().uri();
            HttpHeaders httpHeaders=httpRequestEvent.getSource().headers();
            String body = getBody(httpRequestEvent.getSource());
            HttpMethod method=httpRequestEvent.getSource().method();
            //只接受/mqtt和post请求
            if (!CommonConstant.WEBSOCKET_PATH.equalsIgnoreCase(path)||!HttpMethod.POST.equals(method)){
                send(httpRequestEvent.getChannelHandlerContext(),Ret.error(INVALID_REQUEST).toJson(), HttpResponseStatus.BAD_REQUEST);
                return;
            }else {
                boolean login = devService.login(httpHeaders.get(TENANT_ID), httpHeaders.get(USER_NAME),httpHeaders.get(PASSWORD));
                if (login){
                    HttpMqttRequestVo vo= JSONUtil.toBean(body,HttpMqttRequestVo.class);
                    if (MqttQoS.AT_MOST_ONCE.value()<=vo.getMqttQos()&&vo.getMqttQos()<=MqttQoS.AT_LEAST_ONCE.value()){
                        byte[] bytes = JSONUtil.toJsonPrettyStr(vo.getPayload()).getBytes();
                        publishEvent(new MqttPublishTopicEvent(vo.getTopic(), MqttQoS.valueOf(vo.getMqttQos()) ,bytes,
                                httpHeaders.get(TENANT_ID),vo.getRetain()));

                        send(httpRequestEvent.getChannelHandlerContext(),Ret.success(null).toJson(),HttpResponseStatus.OK);

                    }else {
                        send(httpRequestEvent.getChannelHandlerContext(),Ret.error(INVALID_MQTTQOS).toJson(), HttpResponseStatus.BAD_REQUEST);
                    }
                }else {
                    send(httpRequestEvent.getChannelHandlerContext(),Ret.error(AUTHENTICATION_FAILURE).toJson(), HttpResponseStatus.BAD_REQUEST);
                }

            }
        }catch(Exception e){
            send(httpRequestEvent.getChannelHandlerContext(),Ret.error(RUNTIME_EXCEPTION).toJson(),HttpResponseStatus.BAD_REQUEST);
            throw e;
        }finally{
            httpRequestEvent.getSource().release();
        }
    }


    private String getBody(FullHttpRequest request){
        ByteBuf buf = request.content();
        return buf.toString(CharsetUtil.UTF_8);
    }

    private void send(ChannelHandlerContext ctx, String context, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer(context, CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
