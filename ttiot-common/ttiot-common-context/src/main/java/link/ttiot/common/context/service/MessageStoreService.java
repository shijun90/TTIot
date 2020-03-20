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

package link.ttiot.common.context.service;


import link.ttiot.common.context.db.DbHelper;
import link.ttiot.common.context.entity.MessageStore;
import link.ttiot.common.ioc.annotation.Inject;
import lombok.Getter;

import java.util.Map;

/**
 * @author: shijun
 * @date: 2019-05-03
 * @description:
 */
public class MessageStoreService {

    @Getter
    @Inject
    private DbHelper dbHelper;

    public boolean saveAndFlushRetain(String tenantId, MessageStore messageStore) {
        return dbHelper.saveRetainMessage(tenantId, messageStore.getTopic(), messageStore);
    }

    public MessageStore getRetain(String tenantId, String topic) {
        return dbHelper.getRetainMessage(tenantId, topic);
    }

    public boolean deleteRetain(String tenantId, String topic) {
        return dbHelper.deleteRetainMessage(tenantId, topic);
    }

    public boolean save(String clientId, MessageStore messageStore) {
        return dbHelper.saveMessage(messageStore.getPacketId(), clientId, messageStore);
    }

    public MessageStore get(int packetId, String clientId) {
        return dbHelper.getMessage(packetId, clientId);
    }

    public boolean delete(int packetId, String clientId) {
        return dbHelper.deleteMessage(packetId, clientId);
    }

    public boolean deleteByClientId(String clientId) {
        return dbHelper.deleteMessageByClient(clientId);
    }

    public int incrPacketId() {
        return (int) dbHelper.incrPacketId();
    }

    public Map<String, MessageStore> getByClientId(String clientId) {

        return dbHelper.listMessageByClient(clientId);

    }
}
