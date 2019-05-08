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

package link.ttiot.common.core.provider.bean;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.exceptions.UtilException;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author: shijun
 * @date: 2019-04-14
 * @description:
 */
@UtilityClass
public class SingtonBeanProvider{

    private static final ConcurrentMap<Class, Object> map = new ConcurrentHashMap<Class, Object>();

    public <T> T produce(Class<T> type) {
        Object ob = map.get(type);
        try {
            if (ob == null) {
                synchronized (map) {
                    ob = type.newInstance();
                    map.put(type, ob);
                }
            }
        } catch (Throwable e){
            throw ExceptionUtil.wrap(e, UtilException.class);
        }
        return (T) ob;
    }


    public <T> T protocalProduce(Class<T> type,Boolean b) {
        Object ob = map.get(type);
        try {
            if (ob == null) {
                synchronized (map) {
                    Constructor<?> cons = type.getConstructor(boolean.class);
                    ob = cons.newInstance(b);
                    map.put(type, ob);
                }
            }
        } catch (Throwable e){
            throw ExceptionUtil.wrap(e, UtilException.class);
        }
        return (T) ob;
    }

    public <T> void remove(Class<T> type) {
        map.remove(type);
    }

    public <T> void display(T t) { }

}
