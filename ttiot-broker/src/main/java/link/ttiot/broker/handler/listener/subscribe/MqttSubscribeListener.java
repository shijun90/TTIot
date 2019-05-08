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

package link.ttiot.broker.handler.listener.subscribe;

import link.ttiot.broker.context.protocol.mqtt.MqttApplicationListener;
import link.ttiot.broker.entity.Subscribe;
import link.ttiot.broker.eventor.ack.MqttAckEvent;
import link.ttiot.broker.eventor.publish.MqttPublishDevEvent;
import link.ttiot.broker.eventor.subscribe.MqttSubscribeEvent;
import link.ttiot.broker.service.MessageStoreService;
import link.ttiot.broker.service.SubscribeService;
import link.ttiot.common.core.function.FunctionApi;
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
public class MqttSubscribeListener extends MqttApplicationListener<MqttSubscribeEvent> implements FunctionApi {

    @Inject
    private SubscribeService subscribeService;

    private MessageStoreService messageStoreService;

    public MqttSubscribeListener() {
        super();
    }

    @Override
    public void onApplicationEvent(MqttSubscribeEvent mqttSubscribeEvent) {

        mqttSubscribeEvent.getSource().payload().topicSubscriptions().stream().forEach(u -> {
            log.info("The topic is {} called , and deviceName is {}", u.topicName(),mqttSubscribeEvent.getClientId());
            subscribeService.put(mqttSubscribeEvent.getTenantId(),new Subscribe(u.topicName(),mqttSubscribeEvent.getDevName(),u.qualityOfService().value(),mqttSubscribeEvent.getClientId()));
            //检查保留消息
            Optional.ofNullable(messageStoreService.getRetain(mqttSubscribeEvent.getTenantId(),u.topicName())).map(messageStore->{
                publishEvent(new MqttPublishDevEvent(mqttSubscribeEvent.getClientId(),messageStore.getTopic(),
                        messageStore.getMessageBytes(),messageStore.getMqttQoS(),mqttSubscribeEvent.getTenantId()));
                return true;
            }).orElse(false);
            //发布ack
            publishEvent(new MqttAckEvent(mqttSubscribeEvent.getSource(),mqttSubscribeEvent.getChannelHandlerContext()));

        });
    }


}
