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

package link.ttiot.common.core.provider.message;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.*;
import lombok.experimental.UtilityClass;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * @author: shijun
 * @date: 2019-04-24
 * @description:
 */
@UtilityClass
public class MessageProvider {

    public MqttConnAckMessage connectBackproduce(MqttConnectReturnCode connectReturnCode, Boolean sessionPresent, Boolean isDup, Boolean isRetain) {
        MqttConnAckVariableHeader mqttConnAckVariableHeader = new MqttConnAckVariableHeader(connectReturnCode, sessionPresent);
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(
                MqttMessageType.CONNACK, isDup, MqttQoS.AT_MOST_ONCE, isRetain, 0x02);
        MqttConnAckMessage connAck = new MqttConnAckMessage(mqttFixedHeader, mqttConnAckVariableHeader);
        return connAck;
    }

    public MqttSubAckMessage subAckProduce(MqttSubscribeMessage mqttSubscribeMessage) {
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.SUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0);
        MqttMessageIdVariableHeader variableHeader = MqttMessageIdVariableHeader.from(mqttSubscribeMessage.variableHeader().messageId());
        List<Integer> qoSs = new LinkedList<>();
        for (MqttTopicSubscription subscription : mqttSubscribeMessage.payload().topicSubscriptions()) {
            qoSs.add(subscription.qualityOfService().value());
        }
        return new MqttSubAckMessage(mqttFixedHeader, variableHeader, new MqttSubAckPayload(qoSs));
    }

    public MqttPublishMessage publishMessageProduce(String topic, byte[] byteBuf, int messageId, MqttQoS qoS, Boolean isRetain) {
        MqttFixedHeader mqttFixedHeader = Optional.ofNullable(qoS).map(u -> {
            switch (u) {
                case AT_LEAST_ONCE:
                    return new MqttFixedHeader(MqttMessageType.PUBLISH, true, MqttQoS.AT_LEAST_ONCE, isRetain, 0);
                case EXACTLY_ONCE:
                    return new MqttFixedHeader(MqttMessageType.PUBLISH, true, MqttQoS.EXACTLY_ONCE, isRetain, 0);
            }
            return new MqttFixedHeader(MqttMessageType.PUBLISH, false, MqttQoS.AT_MOST_ONCE, isRetain, 0);
        }).get();
        MqttPublishVariableHeader mqttPublishVariableHeader = new MqttPublishVariableHeader(topic, messageId);
        return new MqttPublishMessage(mqttFixedHeader, mqttPublishVariableHeader, Unpooled.wrappedBuffer(byteBuf));
    }

    public MqttMessage pingMessageProduce() {
        return MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PINGRESP, false, MqttQoS.AT_MOST_ONCE, false, 0), null, null);
    }

    public MqttUnsubAckMessage unSubAckProduce(MqttUnsubscribeMessage unsubAckMessage) {
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.UNSUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0x02);
        MqttMessageIdVariableHeader variableHeader = MqttMessageIdVariableHeader.from(unsubAckMessage.variableHeader().messageId());
        return new MqttUnsubAckMessage(mqttFixedHeader, variableHeader);
    }

    public MqttPubAckMessage pubAckProduce(MqttMessageType messageType, int packetId,int remainingLength) {
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(messageType, false, MqttQoS.AT_MOST_ONCE, false, remainingLength);
        MqttMessageIdVariableHeader from = MqttMessageIdVariableHeader.from(packetId);
        return new MqttPubAckMessage(mqttFixedHeader, from);
    }

}
