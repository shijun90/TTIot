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

import link.ttiot.broker.context.protocol.mqtt.MqttApplicationListener;
import link.ttiot.broker.eventor.connect.MqttIdentifierRejectedExceptionEvent;
import link.ttiot.common.ioc.annotation.DefaultListener;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: shijun
 * @date: 2019-04-15
 * @description:
 */
@Slf4j
@DefaultListener
public class MqttIdentifierRejectedExceptionListener extends MqttApplicationListener<MqttIdentifierRejectedExceptionEvent> {

    public MqttIdentifierRejectedExceptionListener() {
        super();
    }

    @Override
    public void onApplicationEvent(MqttIdentifierRejectedExceptionEvent rejectedExceptionEvent) {
        rejectedExceptionEvent.getChannelHandlerContext().channel().close();
    }

}
