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

package link.ttiot.broker.handler.listener.publish;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import link.ttiot.broker.context.Context;
import link.ttiot.broker.context.Session;
import link.ttiot.common.ioc.core.AbstractApplicationListener;
import link.ttiot.broker.entity.MessageStore;
import link.ttiot.broker.eventor.publish.MqttPublishDevEvent;
import link.ttiot.broker.service.MessageStoreService;
import link.ttiot.broker.service.SessionService;
import link.ttiot.common.core.channel.ChannelUtils;
import link.ttiot.common.core.constant.enums.MessageState;
import link.ttiot.common.core.function.FunctionApi;
import link.ttiot.common.core.provider.message.MessageProvider;
import link.ttiot.common.ioc.annotation.DefaultListener;
import link.ttiot.common.ioc.annotation.Inject;
import link.ttiot.common.ioc.core.ApplicationEvent;
import lombok.extern.slf4j.Slf4j;


/**
 * @author: shijun
 * @date: 2019-04-12
 * @description:
 */
@Slf4j
@DefaultListener
public class MqttPublishDevListener extends AbstractApplicationListener<MqttPublishDevEvent> implements FunctionApi {

    @Inject
    private MessageStoreService messageStoreService;

    @Inject
    private SessionService sessionService;

    public MqttPublishDevListener() {
        super();
    }

    @Override
    public void onApplicationEvent(MqttPublishDevEvent mqttPublishDevEvent) {
        int packetId = messageStoreService.incrPacketId();
        MqttPublishMessage mqttPublishMessage = MessageProvider.publishMessageProduce(mqttPublishDevEvent.getTopic(), mqttPublishDevEvent.getByteBuf(), packetId,
                mqttPublishDevEvent.getQoS(), false);
        Channel channel = getTTIotContext().getChannelByContext(mqttPublishDevEvent.getSource());
        if (ChannelUtils.ifLogin(channel)) {
            channel.writeAndFlush(mqttPublishMessage);
        } else {//已经下线或者刚刚被踢下线
            Session ttIotSession = sessionService.get(mqttPublishDevEvent.getSource());
            if (!ttIotSession.isCleanSession() && mqttPublishDevEvent.getQoS().value() < 1) {
                saveMessage(mqttPublishDevEvent.getSource(), mqttPublishDevEvent.getTopic(), mqttPublishDevEvent.getQoS(), mqttPublishDevEvent.getByteBuf(),packetId);
            }
        }
        // 存储未确认的消息
        if (mqttPublishDevEvent.getQoS().value() > 1) {
            saveMessage(mqttPublishDevEvent.getSource(), mqttPublishDevEvent.getTopic(), mqttPublishDevEvent.getQoS(), mqttPublishDevEvent.getByteBuf(),packetId);
        }
    }

    public boolean saveMessage(String clientId, String topic, MqttQoS mqttQoS, byte[] byteBuf,int packetId) {
        MessageStore messageStore = new MessageStore(MessageState.PUBLISH, clientId, topic, mqttQoS, packetId, byteBuf);
        return messageStoreService.save(clientId, messageStore);
    }

    public void publishEvent(ApplicationEvent event) {
        getTTIotContext().publishEvent(event);
    }

    public Context getTTIotContext() {
        return Context.me();
    }


}
