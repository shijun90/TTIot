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

package link.ttiot.broker.handler.listener.ping;

import link.ttiot.broker.context.protocol.mqtt.MqttApplicationListener;
import link.ttiot.broker.eventor.ping.MqttPingEvent;
import link.ttiot.broker.service.SubscribeService;
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
public class MqttPingListener extends MqttApplicationListener<MqttPingEvent> implements FunctionApi {

    @Inject
    private SubscribeService subscribeService;

    public MqttPingListener() {
        super();
    }

    @Override
    public void onApplicationEvent(MqttPingEvent mqttPingEvent) {

        log.info("receive：【{}】 heartbeat ",mqttPingEvent.getChannel().remoteAddress().toString());

        predicateDo(mqttPingEvent.getChannel(), u -> u.isWritable(), u -> u.writeAndFlush(MessageProvider.pingMessageProduce()));

    }


}
