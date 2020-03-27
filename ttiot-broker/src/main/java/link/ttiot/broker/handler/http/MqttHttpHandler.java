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

package link.ttiot.broker.handler.http;

import cn.hutool.json.JSONUtil;
import io.netty.handler.codec.mqtt.MqttQoS;
import link.ttiot.broker.eventor.publish.MqttPublishTopicEvent;
import link.ttiot.common.context.Context;
import link.ttiot.common.ioc.annotation.Http;
import link.ttiot.common.ioc.core.HttpHandler;
import link.ttiot.common.ioc.vo.HttpRequest;
import link.ttiot.common.ioc.vo.HttpRet;

/**
 * @author: shijun
 * @date: 2020-03-26
 * @description:
 */
@Http(uri = "/mqtt")
public class MqttHttpHandler implements HttpHandler {

    private static String INVALID_MQTTQOS = "mqttqos must be >0 and <1";

    @Override
    public HttpRet handler(HttpRequest param) {
        HttpMqttRequestVo vo = JSONUtil.toBean(param.getBody(), HttpMqttRequestVo.class);
        if (MqttQoS.AT_MOST_ONCE.value() <= vo.getMqttQos() && vo.getMqttQos() <= MqttQoS.AT_LEAST_ONCE.value()) {
            byte[] bytes = JSONUtil.toJsonPrettyStr(vo.getPayload()).getBytes();
            Context.me().publishEvent(new MqttPublishTopicEvent(vo.getTopic(), MqttQoS.valueOf(vo.getMqttQos()), bytes,
                    param.getTenantId(), vo.getRetain()));
            return HttpRet.success(null);
        } else {
            return HttpRet.error(INVALID_MQTTQOS);
        }
    }
}
