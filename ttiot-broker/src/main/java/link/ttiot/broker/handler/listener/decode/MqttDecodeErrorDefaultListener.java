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

package link.ttiot.broker.handler.listener.decode;

import io.netty.handler.codec.mqtt.MqttUnacceptableProtocolVersionException;
import link.ttiot.broker.eventor.connect.MqttIdentifierRejectedExceptionEvent;
import link.ttiot.broker.eventor.connect.MqttUnacceptableProtocolVersionExceptionEvent;
import link.ttiot.broker.eventor.decode.MqttDecoderFailEvent;
import link.ttiot.common.context.protocal.mqtt.MqttApplicationListener;
import link.ttiot.common.ioc.annotation.DefaultListener;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: shijun
 * @date: 2019-04-15
 * @description:
 */
@Slf4j
@DefaultListener
public class MqttDecodeErrorDefaultListener extends MqttApplicationListener<MqttDecoderFailEvent> {

    public MqttDecodeErrorDefaultListener() {
        super();
    }

    @Override
    public void onApplicationEvent(MqttDecoderFailEvent failEvent) {
        publishEvent(failEvent.getSource().decoderResult().cause() instanceof MqttUnacceptableProtocolVersionException ? new MqttUnacceptableProtocolVersionExceptionEvent(failEvent.getSource(),failEvent.getChannelHandlerContext()):new MqttIdentifierRejectedExceptionEvent(failEvent.getSource(),failEvent.getChannelHandlerContext()));
    }

}
