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
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import link.ttiot.broker.eventor.connect.MqttConnectBackEvent;
import link.ttiot.common.context.protocal.mqtt.MqttApplicationListener;
import link.ttiot.common.core.provider.message.MessageProvider;
import link.ttiot.common.ioc.annotation.DefaultListener;
import lombok.extern.slf4j.Slf4j;


/**
 * @author: shijun
 * @date: 2019-04-12
 * @description:
 */
@Slf4j
@DefaultListener
public class MqttConnectBackListener extends MqttApplicationListener<MqttConnectBackEvent> {

    public MqttConnectBackListener() {
        super();
    }

    @Override
    public void onApplicationEvent(MqttConnectBackEvent mqttConnectBackEvent) {
        MqttConnectMessage mqttConnectMessage= mqttConnectBackEvent.getSource();
        Channel channel=mqttConnectBackEvent.getChannelHandlerContext().channel();
        MqttConnAckMessage mqttConnAckMessage=null;
        switch (mqttConnectBackEvent.getMqttConnectReturnCode()){
            case CONNECTION_ACCEPTED:
                mqttConnAckMessage= MessageProvider.connectBackproduce( mqttConnectBackEvent.getMqttConnectReturnCode(), !mqttConnectMessage.variableHeader().isCleanSession(),mqttConnectMessage.fixedHeader().isDup() , mqttConnectMessage.fixedHeader().isRetain());
                break;
            case CONNECTION_REFUSED_IDENTIFIER_REJECTED:
                mqttConnAckMessage= MessageProvider.connectBackproduce( mqttConnectBackEvent.getMqttConnectReturnCode(), true,false ,false);
                break;
            case CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD:
                mqttConnAckMessage= MessageProvider.connectBackproduce( mqttConnectBackEvent.getMqttConnectReturnCode(), true,false ,false);
                return;
        }
        channel.writeAndFlush(mqttConnAckMessage);

    }

}
