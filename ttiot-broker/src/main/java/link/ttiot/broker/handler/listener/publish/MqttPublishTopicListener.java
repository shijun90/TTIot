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

import io.netty.handler.codec.mqtt.MqttQoS;
import link.ttiot.broker.context.Context;
import link.ttiot.common.ioc.core.AbstractApplicationListener;
import link.ttiot.broker.entity.MessageStore;
import link.ttiot.broker.eventor.publish.MqttPublishDevEvent;
import link.ttiot.broker.eventor.publish.MqttPublishTopicEvent;
import link.ttiot.broker.service.MessageStoreService;
import link.ttiot.broker.service.SubscribeService;
import link.ttiot.common.core.function.FunctionApi;
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
public class MqttPublishTopicListener extends AbstractApplicationListener<MqttPublishTopicEvent> implements FunctionApi {

    @Inject
    private SubscribeService subscribeService;

    @Inject
    private MessageStoreService messageStoreService;

    public MqttPublishTopicListener() {
        super();
    }

    @Override
    public void onApplicationEvent(MqttPublishTopicEvent mqttPublishTopicEvent) {

        predicateDoElse(mqttPublishTopicEvent,u->u.isRetain(),u->{
            if (u.getBytes().length == 0) {
                messageStoreService.deleteRetain(u.getTenantId(),u.getSource());
            } else {
                MessageStore messageStore=new MessageStore( u.getSource(),  u.getMqttQoS(), u.getBytes());
                messageStoreService.saveAndFlushRetain(u.getTenantId(),messageStore);
            }
        },u->{
            subscribeService.search(u.getTenantId(), u.getSource()).forEach(d->{
                //消息降级
                MqttQoS qoS = u.getMqttQoS().value() > d.getMqttQoS() ? MqttQoS.valueOf(d.getMqttQoS()) : u.getMqttQoS();
                //传递到dev
                publishEvent(new MqttPublishDevEvent(d.getClientId(),u.getSource(),
                        u.getBytes(),qoS,u.getTenantId()));
            });
        });

    }

    public void publishEvent(ApplicationEvent event) {
        getTTIotContext().publishEvent(event);
    }

    public Context getTTIotContext() {
        return Context.me();
    }


}
