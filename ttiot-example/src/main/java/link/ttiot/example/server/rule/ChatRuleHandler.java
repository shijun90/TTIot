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

package link.ttiot.example.server.rule;

import link.ttiot.common.context.service.DeviceService;
import link.ttiot.common.ioc.annotation.Inject;
import link.ttiot.common.ioc.annotation.Ruler;
import link.ttiot.common.ioc.core.RuleHandler;
import link.ttiot.common.ioc.vo.MqttPayload;

/**
 * @author: shijun
 * @date: 2020-03-19
 * @description:
 */
@Ruler(name = "chat")
public class ChatRuleHandler implements RuleHandler {

    @Inject
    private DeviceService deviceService;

    @Override
    public void handler(MqttPayload mqttPayloadVo) {
        System.out.println(mqttPayloadVo);
    }
}
