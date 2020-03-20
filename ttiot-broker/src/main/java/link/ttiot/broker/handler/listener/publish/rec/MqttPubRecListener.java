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

package link.ttiot.broker.handler.listener.publish.rec;

import io.netty.handler.codec.mqtt.*;

import link.ttiot.broker.eventor.ack.MqttAckEvent;
import link.ttiot.broker.eventor.publish.rec.MqttPubRecEvent;
import link.ttiot.common.context.entity.MessageStore;
import link.ttiot.common.context.protocal.mqtt.MqttApplicationListener;
import link.ttiot.common.context.service.MessageStoreService;
import link.ttiot.common.core.constant.enums.MessageState;
import link.ttiot.common.core.function.FunctionApi;
import link.ttiot.common.core.provider.message.MessageProvider;
import link.ttiot.common.ioc.annotation.DefaultListener;
import link.ttiot.common.ioc.annotation.Inject;
import lombok.extern.slf4j.Slf4j;


/**
 * @author: shijun
 * @date: 2019-04-12
 * @description:
 */
@Slf4j
@DefaultListener
public class MqttPubRecListener extends MqttApplicationListener<MqttPubRecEvent> implements FunctionApi {

    @Inject
    private MessageStoreService messageStoreService;

    public MqttPubRecListener() {
        super();
    }

    @Override
    public void onApplicationEvent(MqttPubRecEvent mqttPubRecEvent) {

        MqttMessageIdVariableHeader mqttMessageIdVariableHeader = (MqttMessageIdVariableHeader) mqttPubRecEvent.getSource().variableHeader();
        MessageStore messageStore = messageStoreService.get(mqttMessageIdVariableHeader.messageId(), mqttPubRecEvent.getClientId());
        messageStore.setMessageState(MessageState.PUBREL);
        messageStoreService.save(mqttPubRecEvent.getClientId(), messageStore);
        MqttMessage mqttMessage = MessageProvider.pubAckProduce(MqttMessageType.PUBREL, mqttMessageIdVariableHeader.messageId(), 0);
        publishEvent(new MqttAckEvent(mqttMessage, mqttPubRecEvent.getChannelHandlerContext()));
    }


}
