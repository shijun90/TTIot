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

package link.ttiot.common.redis;

import cn.hutool.core.io.IoUtil;
import link.ttiot.common.config.ContextConfig;
import link.ttiot.common.redis.serializer.FstSerializer;
import link.ttiot.common.redis.serializer.Serializer;
import redis.clients.jedis.*;
import java.io.IOException;
import java.util.*;


/**
 * @author: shijun
 * @date: 2019-04-18
 * @description:
 */
public class RedisSourceProvider {

    private ContextConfig.TTiotStarterContextConfigAttribut.TTiotRedisConfigAttribut redisConfigAttribut;

    private JedisPool pool;

    private KeyNamePolicy keyNamingPolicy;

    private Serializer serializer;

    public RedisSourceProvider() {
        redisConfigAttribut = ContextConfig.getConfig().TTiot.redis;
        serializer = new FstSerializer();
        keyNamingPolicy = KeyNamePolicy.defaultKeyNamePolicy;
        init();
    }

    private RedisSourceProvider init() {

        final JedisPoolConfig config = new JedisPoolConfig();

        configJedisPool(config);

        this.pool = new JedisPool(config,
                redisConfigAttribut.getHost(),
                redisConfigAttribut.getPort(),
                redisConfigAttribut.getConnectionTimeout(),
                redisConfigAttribut.getSoTimeout(),
                redisConfigAttribut.getPassword(),
                redisConfigAttribut.getDatabase(),
                "TTIot",
                redisConfigAttribut.isSsl(),
                null, null, null
        );


        return this;

    }

    private void configJedisPool(JedisPoolConfig config) {
        config.setMaxIdle(redisConfigAttribut.getMaxIdle());
        config.setMinIdle(redisConfigAttribut.getMinIdle());
        config.setMaxTotal(redisConfigAttribut.getMaxTotal());
        config.setMaxWaitMillis(redisConfigAttribut.getMaxWaitMillis());
    }


    /**
     * 从资源池中获取{@link Jedis}
     *
     * @return {@link Jedis}
     */
    public Jedis getJedis() {
        return this.pool.getResource();
    }


    public void close() throws IOException {
        IoUtil.close(pool);
    }

    protected byte[] valueToBytes(Object value) {
        return this.serializer.valueToBytes(value);
    }

    protected Object valueFromBytes(byte[] bytes) {
        return this.serializer.valueFromBytes(bytes);
    }

    protected byte[][] valuesToBytesArray(Object... valuesArray) {
        byte[][] data = new byte[valuesArray.length][];
        for (int i = 0; i < data.length; ++i) {
            data[i] = this.valueToBytes(valuesArray[i]);
        }
        return data;
    }

    protected byte[] keyToBytes(Object key) {
        String keyStr = this.keyNamingPolicy.getKeyName(key);
        return this.serializer.keyToBytes(keyStr);
    }

    protected byte[][] keysToBytesArray(Object... keys) {
        byte[][] result = new byte[keys.length][];
        for (int i = 0; i < result.length; ++i) {
            result[i] = this.keyToBytes(keys[i]);
        }
        return result;
    }

    protected List valueListFromBytesList(List<byte[]> data) {
        List<Object> result = new ArrayList();
        Iterator var3 = data.iterator();
        while (var3.hasNext()) {
            byte[] d = (byte[]) var3.next();
            result.add(this.valueFromBytes(d));
        }
        return result;
    }

    protected byte[] fieldToBytes(Object field) {
        return this.serializer.fieldToBytes(field);
    }

    protected byte[][] fieldsToBytesArray(Object... fieldsArray) {
        byte[][] data = new byte[fieldsArray.length][];

        for (int i = 0; i < data.length; ++i) {
            data[i] = this.fieldToBytes(fieldsArray[i]);
        }

        return data;
    }

    protected Object fieldFromBytes(byte[] bytes) {
        return this.serializer.fieldFromBytes(bytes);
    }


    protected void fieldSetFromBytesSet(Set<byte[]> data, Set<Object> result) {
        Iterator var3 = data.iterator();

        while (var3.hasNext()) {
            byte[] fieldBytes = (byte[]) var3.next();
            result.add(this.fieldFromBytes(fieldBytes));
        }

    }

    protected void valueSetFromBytesSet(Set<byte[]> data, Set<Object> result) {
        Iterator var3 = data.iterator();

        while (var3.hasNext()) {
            byte[] valueBytes = (byte[]) var3.next();
            result.add(this.valueFromBytes(valueBytes));
        }

    }

    // 操作


    public String set(Object key, Object value) {
        try (Jedis jedis = getJedis()) {
            return jedis.set(this.keyToBytes(key), this.valueToBytes(value));
        }
    }

    public String setex(Object key, int seconds, Object value) {
        try (Jedis jedis = getJedis()) {
            return jedis.setex(this.keyToBytes(key), seconds, this.valueToBytes(value));
        }
    }

    public Object get(Object key) {
        try (Jedis jedis = getJedis()) {
            return this.valueFromBytes(jedis.get(this.keyToBytes(key)));
        }
    }

    public Long del(Object key) {
        try (Jedis jedis = getJedis()) {
            return jedis.del(this.keyToBytes(key));
        }
    }

    public Long del(Object... keys) {
        try (Jedis jedis = getJedis()) {
            return jedis.del(this.keysToBytesArray(keys));
        }
    }

    public Set<String> keys(String pattern) {
        try (Jedis jedis = getJedis()) {
            return jedis.keys(pattern);
        }
    }

    public String mset(Object... keysValues) {
        try (Jedis jedis = getJedis()) {
            if (keysValues.length % 2 != 0) {
                throw new IllegalArgumentException("wrong number of arguments for met, keysValues length can not be odd");
            } else {
                byte[][] kv = new byte[keysValues.length][];

                for (int i = 0; i < keysValues.length; ++i) {
                    if (i % 2 == 0) {
                        kv[i] = this.keyToBytes(keysValues[i]);
                    } else {
                        kv[i] = this.valueToBytes(keysValues[i]);
                    }
                }
                String var8 = jedis.mset(kv);
                return var8;
            }
        }
    }

    public List mget(Object... keys) {
        try (Jedis jedis = getJedis()) {
            byte[][] keysBytesArray = this.keysToBytesArray(keys);
            List<byte[]> data = jedis.mget(keysBytesArray);
            return this.valueListFromBytesList(data);
        }
    }

    public boolean exists(Object key) {
        try (Jedis jedis = getJedis()) {
            return jedis.exists(this.keyToBytes(key));
        }
    }


    public Long expire(Object key, int seconds) {
        try (Jedis jedis = getJedis()) {
            return jedis.expire(this.keyToBytes(key), seconds);
        }
    }


    public Long ttl(Object key) {
        try (Jedis jedis = getJedis()) {
            return jedis.ttl(this.keyToBytes(key));
        }
    }

    public Long pttl(Object key) {
        try (Jedis jedis = getJedis()) {
            return jedis.pttl(this.keyToBytes(key));
        }
    }

    public Long hset(Object key, Object field, Object value) {
        try (Jedis jedis = getJedis()) {
            return jedis.hset(this.keyToBytes(key), this.fieldToBytes(field), this.valueToBytes(value));
        }

    }

    public String hmset(Object key, Map<Object, Object> hash) {
        try (Jedis jedis = getJedis()) {
            Map<byte[], byte[]> para = new HashMap();
            Iterator var5 = hash.entrySet().iterator();
            while (var5.hasNext()) {
                Map.Entry<Object, Object> e = (Map.Entry) var5.next();
                para.put(this.fieldToBytes(e.getKey()), this.valueToBytes(e.getValue()));
            }
            return jedis.hmset(this.keyToBytes(key), para);
        }
    }

    public Object hget(Object key, Object field) {
        try (Jedis jedis = getJedis()) {
            return this.valueFromBytes(jedis.hget(this.keyToBytes(key), this.fieldToBytes(field)));
        }
    }

    public List hmget(Object key, Object... fields) {
        try (Jedis jedis = getJedis()) {
            List<byte[]> data = jedis.hmget(this.keyToBytes(key), this.fieldsToBytesArray(fields));
            return this.valueListFromBytesList(data);
        }
    }

    public Long hdel(Object key, Object... fields) {
        try (Jedis jedis = getJedis()) {
            return jedis.hdel(this.keyToBytes(key), this.fieldsToBytesArray(fields));
        }
    }

    public boolean hexists(Object key, Object field) {
        try (Jedis jedis = getJedis()) {
            return jedis.hexists(this.keyToBytes(key), this.fieldToBytes(field));
        }
    }

    public Map hgetAll(Object key) {
        try (Jedis jedis = getJedis()) {
            Map<byte[], byte[]> data = jedis.hgetAll(this.keyToBytes(key));
            Map<Object, Object> result = new HashMap();
            Iterator var5 = data.entrySet().iterator();
            while (var5.hasNext()) {
                Map.Entry<byte[], byte[]> e = (Map.Entry) var5.next();
                result.put(this.fieldFromBytes((byte[]) e.getKey()), this.valueFromBytes((byte[]) e.getValue()));
            }
            Map var10 = result;
            return var10;
        }
    }

    public List hvals(Object key) {
        try (Jedis jedis = this.getJedis()) {
            List<byte[]> data = jedis.hvals(this.keyToBytes(key));
            return this.valueListFromBytesList(data);
        }
    }

    public Set<Object> hkeys(Object key) {
        try (Jedis jedis = this.getJedis()) {
            Set<byte[]> fieldSet = jedis.hkeys(this.keyToBytes(key));
            Set<Object> result = new HashSet();
            this.fieldSetFromBytesSet(fieldSet, result);
            return result;
        }
    }

    public Long hlen(Object key) {
        try (Jedis jedis = this.getJedis()) {
            return jedis.hlen(this.keyToBytes(key));
        }
    }

    public List<Object> getList(String pattern) {
        try (Jedis jedis = this.getJedis()) {
            Set<String> r = jedis.keys(pattern);
            return Optional.ofNullable(r).map(u -> {
                List<Object> ro = new ArrayList<>();
                for (String v : r) {
                    ro.add(jedis.get(v));
                }
                return ro;
            }).get();
        }
    }


    public Long sadd(Object key, Object... members) {
        try (Jedis jedis = this.getJedis()) {
            return jedis.sadd(this.keyToBytes(key), this.valuesToBytesArray(members));
        }
    }

    public Long scard(Object key) {
        try (Jedis jedis = this.getJedis()) {
            return jedis.scard(this.keyToBytes(key));
        }

    }

    public Object spop(Object key) {
        try (Jedis jedis = this.getJedis()) {
            return this.valueFromBytes(jedis.spop(this.keyToBytes(key)));
        }
    }

    public Set smembers(Object key) {
        try (Jedis jedis = this.getJedis()) {
            Set<byte[]> data = jedis.smembers(this.keyToBytes(key));
            Set<Object> result = new HashSet();
            this.valueSetFromBytesSet(data, result);
            return result;
        }
    }

    public boolean sismember(Object key, Object member) {
        try (Jedis jedis = this.getJedis()) {
            return jedis.sismember(this.keyToBytes(key), this.valueToBytes(member));
        }
    }

    public Long srem(Object key, Object... members) {
        try (Jedis jedis = this.getJedis()) {
            return jedis.srem(this.keyToBytes(key), this.valuesToBytesArray(members));
        }
    }

    public List<Object> scan(Object key) {
        try (Jedis jedis = this.getJedis()) {
            String cursor = ScanParams.SCAN_POINTER_START;
            ScanParams scanParams = new ScanParams();
            scanParams.match(this.keyToBytes(key));
            List<Object> rlist = new ArrayList<>();
            while (true) {
                ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
                cursor = scanResult.getStringCursor();
                List<String> list = scanResult.getResult();
                for (String var : list) {
                    rlist.add(jedis.get(var));
                }
                if (Optional.ofNullable(cursor).equals(0)) {
                    break;
                }
            }
            return rlist;
        }
    }

    public Long incr(String key) {
        try (Jedis jedis = this.getJedis()) {
            return jedis.incr(key);
        }

    }

}
