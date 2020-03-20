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

package link.ttiot.broker.handler.rule;

import cn.hutool.json.JSONUtil;
import link.ttiot.broker.eventor.publish.MqttPublishTopicEvent;
import link.ttiot.common.context.Context;
import link.ttiot.common.ioc.annotation.Listener;
import link.ttiot.common.ioc.core.AbstractApplicationListener;
import link.ttiot.common.ioc.vo.MqttPayloadVo;

/**
 * @author: shijun
 * @date: 2020-03-19
 * @description:
 */
@Listener(asynchronous = true)
public class RuleDispatcher extends AbstractApplicationListener<MqttPublishTopicEvent> {

    @Override
    public void onApplicationEvent(MqttPublishTopicEvent mqttConnectEvent) {
        try {
            MqttPayloadVo vo = JSONUtil.toBean(new String(mqttConnectEvent.getBytes()), MqttPayloadVo.class);
            Context.me().multicastRuleHandler(vo.getRule(),vo);
        }catch (Exception e){
            return;
        }
    }

}