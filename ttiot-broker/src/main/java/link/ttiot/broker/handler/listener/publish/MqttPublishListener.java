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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.mqtt.*;
import link.ttiot.broker.context.protocol.mqtt.MqttApplicationListener;
import link.ttiot.broker.eventor.ack.MqttAckEvent;
import link.ttiot.broker.eventor.publish.MqttPublishEvent;
import link.ttiot.broker.eventor.publish.MqttPublishTopicEvent;
import link.ttiot.common.core.channel.ChannelUtils;
import link.ttiot.common.core.function.FunctionApi;
import link.ttiot.common.ioc.annotation.DefaultListener;
import lombok.extern.slf4j.Slf4j;


/**
 * @author: shijun
 * @date: 2019-04-12
 * @description:
 */
@Slf4j
@DefaultListener
public class MqttPublishListener extends MqttApplicationListener<MqttPublishEvent> implements FunctionApi {

    public MqttPublishListener() {
        super();
    }

    @Override
    public void onApplicationEvent(MqttPublishEvent mqttPublishEvent) {
        MqttFixedHeader mqttFixedHeader = mqttPublishEvent.getSource().fixedHeader();
        MqttPublishVariableHeader mqttPublishVariableHeader = mqttPublishEvent.getSource().variableHeader();
        ByteBuf payload = mqttPublishEvent.getSource().payload();
        byte[] bytes = ByteBufUtil.getBytes(payload);
        // 客户端每次发送一个新的这些类型的报文时都必须分配一个当前 未使用的PacketID
        int packetId = mqttPublishVariableHeader.packetId();
        log.info("publish message, the messageId is {}", packetId);
        // 发布通知topic时间
        predicateDo(packetId, u -> !ChannelUtils.containReceive(mqttPublishEvent.getChannel(), u), u -> {
            //推送给订阅者
            publishEvent(new MqttPublishTopicEvent(mqttPublishVariableHeader.topicName(), mqttFixedHeader.qosLevel(), bytes,
                    mqttPublishEvent.getTenantId(),mqttFixedHeader.isRetain()));
            //返回给客户端
            if (mqttFixedHeader.qosLevel().value() >= 1) {
                if (mqttFixedHeader.qosLevel().value() == 2) {
                    ChannelUtils.addReceive(mqttPublishEvent.getChannel(), u);
                }
                publishEvent(new MqttAckEvent(mqttPublishEvent.getSource(),mqttPublishEvent.getChannelHandlerContext()));
            }
        });
    }

}
