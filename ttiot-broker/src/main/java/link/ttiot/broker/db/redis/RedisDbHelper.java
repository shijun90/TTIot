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

package link.ttiot.broker.db.redis;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import link.ttiot.common.context.Session;
import link.ttiot.common.context.db.DbHelper;
import link.ttiot.common.context.entity.Device;
import link.ttiot.common.context.entity.MessageStore;
import link.ttiot.common.context.entity.Subscribe;
import link.ttiot.common.context.entity.Tenant;
import link.ttiot.common.core.bean.KeyFiledBean;
import link.ttiot.common.core.constant.ProtocalConstant;
import link.ttiot.common.core.constant.RedisDbHelperHeaderConstant;
import link.ttiot.common.core.security.ClientIdReader;
import link.ttiot.common.redis.RedisSourceProvider;
import lombok.NonNull;

import java.util.*;

/**
 * @author: shijun
 * @date: 2019-04-19
 * @description:
 */
public class RedisDbHelper implements DbHelper, RedisDbHelperHeaderConstant {

    public RedisDbHelper(RedisSourceProvider ttIotRedis) {
        this.ttIotRedis = ttIotRedis;
    }

    private RedisSourceProvider ttIotRedis;

    @Override
    public boolean saveSession(Session ttIotSession) {
        return Optional.ofNullable(RedisDbHelperHeaderConstant.getSessionKeyFiled(ttIotSession.getTenantId(), ttIotSession.getClientId()))
                .map(u -> ttIotRedis.hset(u.getKey(), u.getFiled(), ttIotSession))
                .equals(OKL);
    }

    @Override
    public Session getSession(String clientId) {
        KeyFiledBean keyFiled = RedisDbHelperHeaderConstant.getSessionKeyFiled(ClientIdReader.tenantId(clientId), clientId);
        return Optional.ofNullable((Session) ttIotRedis.hget(keyFiled.getKey(), keyFiled.getFiled())).orElse(null);
    }

    @Override
    public boolean deleteSession(String clientId) {
        return Optional.ofNullable(RedisDbHelperHeaderConstant.getSessionKeyFiled(ClientIdReader.tenantId(clientId), clientId))
                .map(u -> ttIotRedis.hdel(u.getKey(), u.getFiled())).equals(OKL);
    }

    @Override
    public Map<String, Session> listSession(String tenantId) {
        return ttIotRedis.hgetAll(RedisDbHelperHeaderConstant.getSessionKey(tenantId));
    }

    @Override
    public boolean containSession(String clientId) {
        KeyFiledBean keyFiled = RedisDbHelperHeaderConstant.getSessionKeyFiled(ClientIdReader.tenantId(clientId), clientId);
        return ttIotRedis.hexists(keyFiled.getKey(), keyFiled.getFiled());
    }

    @Override
    public Map<String, Tenant> listTenant() {
        return ttIotRedis.hgetAll(RedisDbHelperHeaderConstant.getTenantKey());
    }

    @Override
    public boolean saveTenant(Tenant tenant) {
        return Optional.ofNullable(RedisDbHelperHeaderConstant.getTenantKeyFiled(tenant.getTenantId()))
                .map(u -> ttIotRedis.hset(u.getKey(), u.getFiled(), tenant))
                .equals(OKL);
    }

    @Override
    public Tenant getTenant(String tenantId) {
        KeyFiledBean keyFiled = RedisDbHelperHeaderConstant.getTenantKeyFiled(tenantId);
        return Optional.ofNullable((Tenant) ttIotRedis.hget(keyFiled.getKey(), keyFiled.getFiled())).orElse(null);

    }

    @Override
    public boolean deleteTenant(String tenantId) {
        return Optional.ofNullable(RedisDbHelperHeaderConstant.getTenantKeyFiled(tenantId))
                .map(u -> ttIotRedis.hdel(u.getKey(), u.getFiled())).equals(OKL);
    }

    @Override
    public boolean saveDev(Device dev) {

        return Optional.ofNullable(RedisDbHelperHeaderConstant.getDevKeyFiled(dev.getTenantId(), dev.getClientId()))
                .map(u -> ttIotRedis.hset(u.getKey(), u.getFiled(), dev))
                .equals(OKL);
    }

    @Override
    public Device getDev(@NonNull String tenantId, @NonNull String devName) {
        return Optional.ofNullable(RedisDbHelperHeaderConstant.getDevKeyFiled(tenantId, devName)).map(u -> (Device) ttIotRedis.hget(u.getKey(), u.getFiled())).orElse(null);
    }

    @Override
    public boolean isOnline(String clientId) {
        return containSession(clientId);
    }

    @Override
    public Map<String, Device> listDevByTenantAndState(String tenantId) {
        return Optional.of(RedisDbHelperHeaderConstant.getDevKey(tenantId)).map(u -> ttIotRedis.hgetAll(u)).orElse(null);
    }

    @Override
    public boolean saveSubscribe(String tenantId, Subscribe subscribe) {
        return Optional.ofNullable(RedisDbHelperHeaderConstant.getTopicKeyFiled(tenantId, subscribe.getTopicFilter(), subscribe.getDevName())).map(u -> ttIotRedis.hset(u.getKey(), u.getFiled(), subscribe)).equals(OKL);
    }


    @Override
    public boolean deleteDevTopicByClientId(String clientId) {
        return Optional.ofNullable(clientId).map(u -> ttIotRedis.del(RedisDbHelperHeaderConstant.getDevTopicKey(ClientIdReader.tenantId(clientId), ClientIdReader.devName(clientId)))).equals(OKL);
    }

    @Override
    public boolean saveDevTopic(String tenantId, Subscribe subscribe) {
        return Optional.ofNullable(RedisDbHelperHeaderConstant.getDevTopicKey(tenantId, subscribe.getDevName())).map(u -> ttIotRedis.sadd(u, subscribe.getTopicFilter())).equals(OKL);
    }

    @Override
    public Set<String> listSubscribeByDev(String tenantId, String devName) {
        return ttIotRedis.smembers(RedisDbHelperHeaderConstant.getDevTopicKey(tenantId, devName));
    }

    @Override
    public boolean deleteSubscribe(String tenantId, String topic, String devName) {
        return Optional.ofNullable(RedisDbHelperHeaderConstant.getTopicKeyFiled(tenantId, topic, devName)).map(u -> ttIotRedis.hdel(u.getKey(), u.getFiled())).equals(OKL);
    }

    @Override
    public boolean deleteDevTopic(String tenantId, String topic, String devName) {
        return Optional.ofNullable(RedisDbHelperHeaderConstant.getDevTopicKey(tenantId, devName)).map(u -> ttIotRedis.srem(u, topic)).equals(OKL);
    }

    @Override
    public List<Subscribe> searchTopic(String tenantId, String topic) {

        Map<String, Map<String, Subscribe>> map = listTopic(tenantId);
        List<Subscribe> subscribes = new ArrayList<>();
        map.forEach((filter, devs) -> {
            List<String> topics = StrUtil.split(topic, ProtocalConstant.SEPARATORTOPIC_CHAR);
            List<String> filters = StrUtil.split(filter, ProtocalConstant.SEPARATORTOPIC_CHAR);
            if (topics.size() >= filters.size()) {
                StringBuilder var = new StringBuilder();
                for (int i = 0; i < filters.size(); i++) {
                    String value = filters.get(i);
                    if (value.equals(ProtocalConstant.SEPARATORTOPIC_SINGLE)) {
                        var.append(ProtocalConstant.SEPARATORTOPIC_SINGLE + ProtocalConstant.SEPARATORTOPIC_STRING);
                    } else if (value.equals(ProtocalConstant.SEPARATORTOPIC_GLOB)) {
                        var.append(ProtocalConstant.SEPARATORTOPIC_GLOB + ProtocalConstant.SEPARATORTOPIC_STRING);
                        break;
                    } else {
                        var.append(topics.get(i) + ProtocalConstant.SEPARATORTOPIC_CHAR);
                    }
                }
                String var1 = StrUtil.removeSuffix(var.toString(),ProtocalConstant.SEPARATORTOPIC_STRING);
                if (filter.equals(var1)) {
                    Collection<Subscribe> collection = devs.values();
                    subscribes.addAll(collection);
                }
            }
        });
        return subscribes;
    }

    @Override
    public Map<String, Map<String, Subscribe>> listTopic(String tenantId) {

        Map<String, Map<String, Subscribe>> map = new HashMap<>();
        //查找所有keys
        Set<String> topicAll = ttIotRedis.keys(RedisDbHelperHeaderConstant.getTenantTopicKey(tenantId) + "*");
        //遍历
        CollectionUtil.newHashSet(topicAll).forEach(u ->
                map.put(u.substring(u.lastIndexOf(SEPARATOR) + 1), ttIotRedis.hgetAll(u)));
        return map;
    }

    @Override
    public boolean saveRetainMessage(String tenantId, String topic, MessageStore messageStore) {
        return Optional.ofNullable(RedisDbHelperHeaderConstant.getRetainMessageKey(tenantId, topic)).map(u -> ttIotRedis.set(u, messageStore)).equals(OKL);
    }

    @Override
    public MessageStore getRetainMessage(String tenantId, String topic) {
        return (MessageStore) ttIotRedis.get(RedisDbHelperHeaderConstant.getRetainMessageKey(tenantId, topic));
    }

    @Override
    public boolean deleteRetainMessage(String tenantId, String topic) {
        return Optional.ofNullable(RedisDbHelperHeaderConstant.getRetainMessageKey(tenantId, topic)).map(u -> ttIotRedis.del(u)).equals(OKL);
    }

    @Override
    public boolean saveMessage(int packetId, String clientId, MessageStore messageStore) {
        return Optional.ofNullable(RedisDbHelperHeaderConstant.getMessageKeyFiled(clientId, packetId)).map(u -> ttIotRedis.hset(u.getKey(), u.getFiled(), messageStore)).equals(OKL);
    }

    @Override
    public MessageStore getMessage(int packetId, String clientId) {
        return (MessageStore) Optional.ofNullable(RedisDbHelperHeaderConstant.getMessageKeyFiled(clientId, packetId)).map(u -> ttIotRedis.hget(u.getKey(), u.getFiled())).get();
    }

    @Override
    public boolean deleteMessage(int packetId, String clientId) {
        return Optional.ofNullable(RedisDbHelperHeaderConstant.getMessageKeyFiled(clientId, packetId)).map(u -> ttIotRedis.hdel(u.getKey(), u.getFiled())).equals(OKL);
    }

    @Override
    public boolean deleteMessageByClient(String clientId) {
        return Optional.ofNullable(RedisDbHelperHeaderConstant.getMessageKey(clientId)).map(u -> ttIotRedis.del(u)).equals(OKL);
    }

    @Override
    public Map<String, MessageStore> listMessageByClient(String clientId) {

        return Optional.ofNullable(RedisDbHelperHeaderConstant.getMessageKey(clientId)).map(u -> ttIotRedis.hgetAll(u)).get();
    }

    @Override
    public long incrPacketId() {
        return ttIotRedis.incr(RedisDbHelperHeaderConstant.getIncrPacketKey());
    }
}
