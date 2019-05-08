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

package link.ttiot.broker.handler.listener.ack;

import io.netty.handler.codec.mqtt.*;
import link.ttiot.broker.context.protocol.mqtt.MqttApplicationListener;
import link.ttiot.broker.eventor.ack.MqttAckEvent;
import link.ttiot.common.core.function.FunctionApi;
import link.ttiot.common.core.provider.message.MessageProvider;
import link.ttiot.common.ioc.annotation.DefaultListener;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;


/**
 * @author: shijun
 * @date: 2019-04-12
 * @description:
 */
@Slf4j
@DefaultListener
public class MqttAckListener extends MqttApplicationListener<MqttAckEvent> implements FunctionApi {

    public MqttAckListener() {
        super();
    }

    @Override
    public void onApplicationEvent(MqttAckEvent mqttAckEvent) {
        MqttMessage mqttMessage = mqttAckEvent.getSource();
        Object msg=null;
        if (mqttMessage instanceof MqttSubscribeMessage){
            msg= MessageProvider.subAckProduce((MqttSubscribeMessage) mqttMessage);
        }else if (mqttMessage instanceof MqttUnsubscribeMessage){
            msg= MessageProvider.unSubAckProduce((MqttUnsubscribeMessage) mqttMessage);
        }else if (mqttMessage instanceof MqttPublishMessage){
            msg= MessageProvider.pubAckProduce(mqttMessage.fixedHeader().messageType(),((MqttPublishMessage) mqttMessage).variableHeader().packetId(),0x02);
        }else if (mqttMessage instanceof MqttMessage){
            msg=mqttMessage;
        }
        Optional.ofNullable(msg).map(u->mqttAckEvent.getChannel().writeAndFlush(u));
    }

}
