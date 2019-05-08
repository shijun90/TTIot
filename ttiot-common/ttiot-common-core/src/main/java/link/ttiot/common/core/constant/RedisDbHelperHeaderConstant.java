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

package link.ttiot.common.core.constant;

import link.ttiot.common.core.bean.KeyFiledBean;

/**
 * @author: shijun
 * @date: 2019-04-17
 * @description:存储结构
 * 1、租户存储
 * keyName [TTIot:tenant]
 * filed[tenantId]
 * value[Tenant]
 *
 * 2、设备存储
 * keyName [TTIot:dev:tenant:(x)]
 * filed[devId]
 * value[Dev]
 *
 * 3、topic存储
 * keyName [TTIot:topic:tenant:(x):topic:(t)]
 * filed[devName]
 * value[Subscribe]
 *
 * 4、dev-topic存储
 * keyName [TTIot:dev-topic:tenant:(x):dev:(d)]
 * value[topic]
 *
 * 5、session存储
 * keyName [TTIot:session:tenant:(x)]
 * filed[clientId]
 * value[Session]
 *
 * 6、retain_message存储
 * keyName [TTIot:retain_message:tenant:(x):topic:(x)]
 * value[MessageStore]
 *
 * 7、message存储
 * keyName [TTIot:message:clientId:(x)]
 * filed[packetId]
 * value[MessageStore]
 *
 */
public interface RedisDbHelperHeaderConstant {

    Long OKL = 1L;
    String SEPARATOR = ":";
    String ID = "TTIot";
    String TENANT = "tenant";
    String DEV = "dev";
    String TOPIC = "topic";
    String DEV_TOPIC = "dev_topic";
    String SESSION = "session";
    String MESSAGE = "message";
    String CLIENTID = "clientId";
    String INCR_PACKETID = "incr_packetId";
    String RETAIN_MESSAGE = "retain_message";
    /**
     * 1、租户结构
     */
    public static KeyFiledBean getTenantKeyFiled(String tenantId) {
        return new KeyFiledBean(getTenantKey(), tenantId);
    }
    public static String getTenantKey() {
        return new StringBuilder(ID).append(SEPARATOR).append(TENANT).toString();
    }
    /**
     * 2、设备结构
     */
    public static KeyFiledBean getDevKeyFiled(String tenantId, String devId) {
        return new KeyFiledBean(getDevKey(tenantId), devId);
    }

    public static String getDevKey(String tenantId) {
        return  new StringBuilder(ID).append(SEPARATOR).append(DEV).append(SEPARATOR).append(TENANT).append(SEPARATOR).append(tenantId).toString();
    }
    /**
     * 3、topic结构
     */
    public static KeyFiledBean getTopicKeyFiled(String tenantId, String topic, String devName) {
        return new KeyFiledBean(new StringBuilder(getTenantTopicKey(tenantId)).append(TOPIC).append(SEPARATOR).append(topic).toString(), devName);
    }

    public static String getTenantTopicKey(String tenantId) {
        return new StringBuilder(ID).append(SEPARATOR).append(TOPIC).append(SEPARATOR).append(TENANT).append(SEPARATOR).append(tenantId).append(SEPARATOR).toString();
    }
    /**
     * 4、dev-topic结构
     */
    public static String getDevTopicKey(String tenantId, String devName) {
        return new StringBuffer(ID).append(SEPARATOR).append(DEV_TOPIC).append(SEPARATOR).append(TENANT).append(SEPARATOR).append(tenantId).append(SEPARATOR).append(DEV).append(devName).toString();
    }

    /**
     * 5、 session结构
     */
    public static KeyFiledBean getSessionKeyFiled(String tenantId, String clientId) {
        return new KeyFiledBean(getSessionKey(tenantId), clientId);
    }

    public static String getSessionKey(String tenantId) {
        return new StringBuilder(ID).append(SEPARATOR).append(SESSION).append(SEPARATOR).append(TENANT).append(SEPARATOR).append(tenantId).toString();
    }
    /**
     *
     * 6、RetainMessage 结构
     */
    public static String getRetainMessageKey(String tenant, String topic) {
        return new StringBuilder(ID).append(SEPARATOR).append(RETAIN_MESSAGE).append(SEPARATOR).append(TENANT).append(SEPARATOR).append(tenant).append(TOPIC).append(SEPARATOR).append(topic).toString();
    }
    /**
     * 7、Message结构
     */
    public static KeyFiledBean getMessageKeyFiled(String clientId, Integer packetId) {
        return new KeyFiledBean(getMessageKey(clientId), packetId.toString());
    }

    public static String getMessageKey(String clientId) {
        return new StringBuilder(ID).append(SEPARATOR).append(MESSAGE).append(SEPARATOR).append(CLIENTID).append(SEPARATOR).append(clientId).toString();
    }

    public static String getIncrPacketKey() {
        return new StringBuilder(ID).append(SEPARATOR).append(INCR_PACKETID).toString();
    }
}
