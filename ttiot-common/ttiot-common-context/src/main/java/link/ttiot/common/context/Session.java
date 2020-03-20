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

package link.ttiot.common.context;

import io.netty.handler.codec.mqtt.MqttPublishMessage;
import link.ttiot.common.core.security.ClientIdReader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: shijun
 * @date: 2019-05-02
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Session implements Serializable {

    private static final long serialVersionUID = 1L;

    private String clientId;

    private boolean cleanSession;

    private MqttPublishMessage willMessage;

    public String getTenantId() {
        return ClientIdReader.tenantId(this.clientId);
    }

}
