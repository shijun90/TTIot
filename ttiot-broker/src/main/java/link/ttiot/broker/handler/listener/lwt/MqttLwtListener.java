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

package link.ttiot.broker.handler.listener.lwt;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import link.ttiot.common.context.Context;
import link.ttiot.common.context.service.SessionService;
import link.ttiot.common.ioc.core.AbstractApplicationListener;
import link.ttiot.broker.eventor.lwt.MqttLwtEvent;
import link.ttiot.broker.eventor.publish.MqttPublishTopicEvent;
import link.ttiot.common.core.function.FunctionApi;
import link.ttiot.common.ioc.annotation.DefaultListener;
import link.ttiot.common.ioc.annotation.Inject;
import link.ttiot.common.ioc.core.ApplicationEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;


/**
 * @author: shijun
 * @date: 2019-04-12
 * @description:
 */
@Slf4j
@DefaultListener
public class MqttLwtListener extends AbstractApplicationListener<MqttLwtEvent> implements FunctionApi {

    @Inject
    private SessionService sessionService;

    public MqttLwtListener() {
        super();
    }

    @Override
    public void onApplicationEvent(MqttLwtEvent mqttLwtEvent) {
        Optional.ofNullable(sessionService.get(mqttLwtEvent.getSource())).map(session->{
          return Optional.ofNullable(session.getWillMessage()).map(willMessage->{
               MqttFixedHeader mqttFixedHeader = willMessage.fixedHeader();
               MqttPublishVariableHeader mqttPublishVariableHeader = willMessage.variableHeader();
               ByteBuf payload = willMessage.payload();
               byte[] bytes = ByteBufUtil.getBytes(payload);
               publishEvent(new MqttPublishTopicEvent(mqttPublishVariableHeader.topicName(), mqttFixedHeader.qosLevel(), bytes,
                       session.getTenantId(),mqttFixedHeader.isRetain()));
               return true;
            }).orElse(false);
        }).orElse(null);
    }

    public void publishEvent(ApplicationEvent event) {
        getTTIotContext().publishEvent(event);
    }

    public Context getTTIotContext() {
        return Context.me();
    }

}
