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

package link.ttiot.common.ioc.multicaster;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.DependencyException;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.ClassUtil;
import link.ttiot.common.core.constant.CommonConstant;
import link.ttiot.common.ioc.annotation.DefaultListener;
import link.ttiot.common.ioc.annotation.Inject;
import link.ttiot.common.ioc.annotation.Listener;
import link.ttiot.common.ioc.core.ApplicationEvent;
import link.ttiot.common.ioc.core.ApplicationListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author: shijun
 * @date: 2019-05-05
 * @description:
 */
@Slf4j
public abstract class DefaultAbstractMulticaster {

    @Getter
    private ListenerRetriever listenerRetriever = new ListenerRetriever();

    /**
     * 扫描 listener
     */
    public void registerListeners(Object dbHelper) {
        Set<Class<?>> classes = ClassUtil.scanPackage();
        HashSet<Class<?>> customClasses = CollUtil.newHashSet();
        HashSet<Class<?>> classesDefault = CollUtil.newHashSet();
        //全局扫描Listener和DefaultListener
        classes.forEach(c -> {
            if (c.getAnnotation(Listener.class) != null) {
                customClasses.add(c);
            } else if (c.getAnnotation(DefaultListener.class) != null) {
                classesDefault.add(c);
            }
        });
        //替换注册Listener
        customClasses.forEach(c -> {
            Listener eventor = c.getAnnotation(Listener.class);
            if (eventor.replace()) {
                Class var1 = ClassUtil.getTypeArgument(c);
                classesDefault.removeIf(v -> ClassUtil.getTypeArgument(v) == var1);
                classesDefault.add(c);
            } else {
                listenerRetriever.addApplicationListener(false, getApplicationInstance(c,eventor.asynchronous(),false,dbHelper));
            }
        });
        //注册defaultListener
        classesDefault.forEach(c -> {
            listenerRetriever.addApplicationListener(true, getApplicationInstance(c, true,true,dbHelper));
        });
    }


    public Field[] listField(Object object){
        Class clazz = object.getClass();
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null){
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        Field[] fields = new Field[fieldList.size()];
        fieldList.toArray(fields);
        return fields;
    }

    /**
     * 装配
     */
    public ApplicationListener getApplicationInstance(Class c, boolean asynchronous,boolean def,Object dbHelper) {
        try {
            Constructor<?> cons = c.getConstructor();
            Object var = cons.newInstance();
            Field[] allFiles=listField(var);
            for (Field filed :allFiles) {
                filed.setAccessible(true);
                Inject inject = filed.getAnnotation(Inject.class);
                if (inject != null) {
                    filed.setAccessible(true);
                    Object service = filed.getType().newInstance();
                    for (Field db : filed.getType().getDeclaredFields()) {
                        Inject injectDb = filed.getAnnotation(Inject.class);
                        if (injectDb != null) {
                            db.setAccessible(true);
                            db.set(service,dbHelper);
                        }
                    }
                    filed.set(var, service);
                }else{
                    if (filed.getName().equals(CommonConstant.FIELD_DEFAULT)){
                        filed.set(var,def);
                    }else if(filed.getName().equals(CommonConstant.FIELD_ASYNCHRONOUS)){
                        filed.set(var,asynchronous);
                    }
                }
            }
            return (ApplicationListener) var;
        } catch (Throwable e) {
            throw ExceptionUtil.wrap(e, DependencyException.class);
        }
    }


    public List<ApplicationListener<ApplicationEvent>> getApplicationListeners(ApplicationEvent event) {
        List<ApplicationListener<ApplicationEvent>> listeners = new LinkedList<>();
        for (ApplicationListener applicationListener : getListenerRetriever().getDefaultApplicationListeners()) {
            if (event.getClass() == ClassUtil.getTypeArgument(applicationListener.getClass())) {
                listeners.add(applicationListener);
            }
        }
        for (ApplicationListener applicationListener : getListenerRetriever().getApplicationListeners()) {
            if (event.getClass() == ClassUtil.getTypeArgument(applicationListener.getClass())) {
                listeners.add(applicationListener);
            }
        }
        return listeners;
    }


    public void invokeListener(ApplicationListener<ApplicationEvent> listener, ApplicationEvent event) {
        listener.onApplicationEvent(event);
    }
}
