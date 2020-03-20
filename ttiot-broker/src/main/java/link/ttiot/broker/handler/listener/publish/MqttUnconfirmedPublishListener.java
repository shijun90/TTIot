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


import io.netty.handler.codec.mqtt.*;
import link.ttiot.common.context.Context;
import link.ttiot.common.context.service.MessageStoreService;
import link.ttiot.common.ioc.core.AbstractApplicationListener;
import link.ttiot.broker.eventor.publish.MqttUnconfirmedPublishEvent;
import link.ttiot.common.core.channel.ChannelUtils;
import link.ttiot.common.core.constant.enums.MessageState;
import link.ttiot.common.core.function.FunctionApi;
import link.ttiot.common.core.provider.message.MessageProvider;
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
public class MqttUnconfirmedPublishListener extends AbstractApplicationListener<MqttUnconfirmedPublishEvent> implements FunctionApi {

    @Inject
    private MessageStoreService messageStoreService;

    public MqttUnconfirmedPublishListener() {
        super();
    }

    @Override
    public void onApplicationEvent(MqttUnconfirmedPublishEvent mqttPublishEvent) {

        String clientId=mqttPublishEvent.getSource();

        Optional.ofNullable(getTTIotContext().getChannelByContext(clientId)).map(u->{
            if (ChannelUtils.ifLogin(u)){
                messageStoreService.getByClientId(clientId).forEach((k,v)->{
                    MqttMessage mqttMessage=null;
                    if (v.getMessageState()== MessageState.PUBLISH){
                        mqttMessage= MessageProvider.publishMessageProduce(v.getTopic(),v.getMessageBytes(),Integer.parseInt(k),v.getMqttQoS(),false);
                    }else if (v.getMessageState()== MessageState.PUBREL){
                        mqttMessage= MessageProvider.pubAckProduce(MqttMessageType.PUBREL,Integer.parseInt(k),0);
                    }
                    u.writeAndFlush(mqttMessage);
                });
            }
            return true;
        });

    }

    public Context getTTIotContext() {
        return Context.me();
    }
}
