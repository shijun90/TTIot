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

package link.ttiot.broker.handler.listener.connect;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleStateHandler;
import link.ttiot.broker.context.Session;
import link.ttiot.broker.context.protocol.mqtt.MqttApplicationListener;
import link.ttiot.broker.eventor.connect.MqttConnectBackEvent;
import link.ttiot.broker.eventor.connect.MqttConnectEvent;
import link.ttiot.broker.eventor.publish.MqttUnconfirmedPublishEvent;
import link.ttiot.broker.service.DeviceService;
import link.ttiot.broker.service.MessageStoreService;
import link.ttiot.broker.service.SessionService;
import link.ttiot.broker.service.SubscribeService;
import link.ttiot.common.core.constant.CommonConstant;
import link.ttiot.common.core.exception.ConnectException;
import link.ttiot.common.core.function.FunctionApi;
import link.ttiot.common.core.provider.message.MessageProvider;
import link.ttiot.common.core.security.ClientIdReader;
import link.ttiot.common.ioc.annotation.DefaultListener;
import link.ttiot.common.ioc.annotation.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;


/**
 * @author: shijun
 * @date: 2019-04-12
 * @description:
 */
@Slf4j
@DefaultListener
public class MqttConnectListener extends MqttApplicationListener<MqttConnectEvent> implements FunctionApi {

    @Inject
    private DeviceService devService;

    @Inject
    private SessionService sessionService;

    @Inject
    private SubscribeService subscribeService;

    @Inject
    private MessageStoreService messageStoreService;

    public MqttConnectListener() {
        super();
    }

    @Override
    public void onApplicationEvent(MqttConnectEvent mqttConnectEvent) {
        MqttConnectPayload payload = mqttConnectEvent.getSource().payload();
        predicateDoElseThrowException(mqttConnectEvent, u -> doLogin(mqttConnectEvent), u -> {
            Channel channel = u.getChannelHandlerContext().channel();
            String clientId= ClientIdReader.realClientId(payload.clientIdentifier());
            //连续两次发送登陆指令
            if (getTTIotContext().containsChannelByContext(channel)) {
                getTTIotContext().removeChannelByContext(clientId,sessionService,()->{
                    subscribeService.delete(clientId);
                    messageStoreService.deleteByClientId(clientId);
                });
            }
            getTTIotContext().setChannelByContext(clientId, channel);
            //存储session
            Session ttIotSession = new Session(clientId, mqttConnectEvent.getSource().variableHeader().isCleanSession(), null);
            if (mqttConnectEvent.getSource().variableHeader().isWillFlag()) {
                MqttPublishMessage willMessage = MessageProvider.publishMessageProduce(mqttConnectEvent.getSource().payload().willTopic(),
                        mqttConnectEvent.getSource().payload().willMessageInBytes(),
                        0,
                        MqttQoS.valueOf(mqttConnectEvent.getSource().variableHeader().willQos()),
                        mqttConnectEvent.getSource().variableHeader().isWillRetain());
                ttIotSession.setWillMessage(willMessage);
            }
            sessionService.save(ttIotSession);
            //确认登陆
            publishEvent(new MqttConnectBackEvent(mqttConnectEvent.getSource(), mqttConnectEvent.getChannelHandlerContext(), MqttConnectReturnCode.CONNECTION_ACCEPTED));
            if (!ttIotSession.isCleanSession()){
                publishEvent(new MqttUnconfirmedPublishEvent(payload.clientIdentifier()));
            }
            predicateDo(mqttConnectEvent.getSource(),h->h.variableHeader().keepAliveTimeSeconds()>0,var->{
                predicateDo(channel.pipeline(),var1->var1.names().contains(CommonConstant.HEARTBEAT_HANDLER),var2->var2.remove(CommonConstant.HEARTBEAT_HANDLER));
                channel.pipeline().addFirst(CommonConstant.HEARTBEAT_HANDLER, new IdleStateHandler(0, 0, Math.round(mqttConnectEvent.getSource().variableHeader().keepAliveTimeSeconds() * 1.2f)));
            });
        }, new ConnectException(MqttConnectReturnCode.CONNECTION_REFUSED_NOT_AUTHORIZED,mqttConnectEvent.getChannel()));
    }

    private boolean doLogin(MqttConnectEvent mqttConnectEvent) {
        MqttConnectPayload payload = mqttConnectEvent.getSource().payload();
        ClientIdReader transverter = ClientIdReader.transverter(payload.clientIdentifier(), payload.passwordInBytes());
        return Optional.ofNullable(transverter).map(u -> {
            boolean login = devService.login(u.getTenantId(), u.getDevName(), u.getPassword());
            if (!login) {
                publishEvent(new MqttConnectBackEvent(mqttConnectEvent.getSource(), mqttConnectEvent.getChannelHandlerContext(), MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED));
                return false;
            } else {
                return true;
            }
        }).orElseGet(() -> {
            publishEvent(new MqttConnectBackEvent(mqttConnectEvent.getSource(), mqttConnectEvent.getChannelHandlerContext(), MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD));
            return false;
        });
    }

}
