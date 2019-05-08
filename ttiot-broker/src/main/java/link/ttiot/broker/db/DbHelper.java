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

package link.ttiot.broker.db;

import link.ttiot.broker.context.Session;
import link.ttiot.broker.entity.Device;
import link.ttiot.broker.entity.MessageStore;
import link.ttiot.broker.entity.Subscribe;
import link.ttiot.broker.entity.Tenant;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author: shijun
 * @date: 2019-04-17
 * @description:
 */
public interface DbHelper {
    /**
     *增加session
     */
    public boolean saveSession(Session ttIotSession);
    /**
     *查询session
     */
    public Session getSession(String clientId);
    /**
     *移除session
     */
    public boolean deleteSession(String clientId);
    /**
     * 返回租户所有session
     */
    public Map<String, Session> listSession(String tenantId);
    /**
     * 查询session是否存在
     */
    public boolean containSession(String clientId);
    /**
     * 获取所有租户信息
     */
    public Map<String, Tenant> listTenant();
    /**
     * 增加租户
     */
    public boolean saveTenant(Tenant tenant);
    /**
     * 获取租户
     */
    public Tenant getTenant(String tenantId);
    /**
     * 删除租户
     */
    public boolean deleteTenant(String tenantId);
    /**
     * 增加设备
     */
    public boolean saveDev(Device dev);
    /**
     * 获取租户里的某个设备
     */
    public Device getDev(String tenantId, String devName);
    /**
     * 判断用户是否在线
     */
    public boolean isOnline(String clientId);
    /**
     * 获取所有租户的所有设备
     */
    public Map<String, Device> listDevByTenantAndState(String tenantId);
    /**
     *存入topic
     */
    public boolean saveSubscribe(String tenantId, Subscribe subscribe);
    /**
     * 删除devTopic
     */
    public boolean deleteDevTopicByClientId(String clientId);
    /**
     *存入dev-topic
     */
    public boolean saveDevTopic(String tenantId,Subscribe subscribe);
    /**
     *获取设备关联的所有topic
     */
    public Set<String> listSubscribeByDev(String tenantId,String devName);
    /**
     * 解绑topic
     */
    public boolean deleteSubscribe(String tenantId, String topic,String devName);
    /**
     * 移除设备里面的topic
     */
    public boolean deleteDevTopic(String tenantId, String topic,String devName);
    /**
     * 搜索topic 绑定的所有设备
     */
    public List<Subscribe> searchTopic(String tenantId, String topic);
    /**
     * 查询租户里的所有的topic
     */
    public Map<String,Map<String,Subscribe>> listTopic(String tenantId);
    /**
     * 增加 retainMessage
     */
    public boolean saveRetainMessage(String tenantId,String topic, MessageStore messageStore);
    /**
     * 获取 retainMessage
     */
    public MessageStore getRetainMessage(String tenantId,String topic);
    /**
     * 删除 retainMessage
     */
    public boolean deleteRetainMessage(String tenantId,String topic);
    /**
     * 增加 Message
     */
    public boolean saveMessage(int packetId,String clientId,MessageStore messageStore);
    /**
     * 获取 Message
     */
    public MessageStore getMessage(int packetId,String clientId);
    /**
     * 删除 Message
     */
    public boolean deleteMessage(int packetId,String clientId);
    /**
     * 删除 client下的所有Message
     */
    public boolean deleteMessageByClient(String clientId);
    /**
     * 获取client下的所有message
     */
    public Map<String, MessageStore> listMessageByClient(String clientId);
    /**
     * 获取全局唯一的packetId
     */
    public long incrPacketId();
}
