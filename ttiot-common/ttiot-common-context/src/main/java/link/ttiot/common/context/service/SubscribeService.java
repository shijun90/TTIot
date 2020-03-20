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
import link.ttiot.common.context.entity.Subscribe;
import link.ttiot.common.core.security.ClientIdReader;
import link.ttiot.common.ioc.annotation.Inject;
import lombok.Getter;

import java.util.List;

/**
 * @author: shijun
 * @date: 2019-04-28
 * @description:
 */
public class SubscribeService {

    @Getter
    @Inject
    private DbHelper dbHelper;

    public void put(String tenantId, Subscribe subscribe) {
        dbHelper.saveSubscribe(tenantId, subscribe);
        dbHelper.saveDevTopic(tenantId, subscribe);
    }

    public List<Subscribe> search(String tenantId, String topic) {
        return dbHelper.searchTopic(tenantId, topic);
    }


    public void delete(String tenantId, String topic, String devName) {
        dbHelper.deleteSubscribe(tenantId, topic, devName);
        dbHelper.deleteDevTopic(tenantId, topic, devName);
    }


    public void delete(String clientId) {
        //先删除topic里面的dev关系
        dbHelper.listSubscribeByDev(ClientIdReader.tenantId(clientId), ClientIdReader.devName(clientId)).stream().forEach(u -> {
            dbHelper.deleteSubscribe(ClientIdReader.tenantId(clientId), u, ClientIdReader.devName(clientId));
        });
        //再删除dev里面的topic关系
        dbHelper.deleteDevTopicByClientId(clientId);
    }
}
