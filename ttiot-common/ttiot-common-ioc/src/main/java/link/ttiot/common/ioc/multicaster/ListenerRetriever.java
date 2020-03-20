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

import link.ttiot.common.ioc.core.ApplicationListener;
import link.ttiot.common.ioc.core.RuleHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @author: shijun
 * @date: 2019-04-13
 * @description:
 */
@Slf4j
public class ListenerRetriever {

    @Getter
    public final Set<ApplicationListener<?>> applicationListeners;

    @Getter
    public final Set<ApplicationListener<?>> defaultApplicationListeners;

    @Getter
    public final HashMap<String,Class<RuleHandler>> ruleHanlders;

    public ListenerRetriever() {
        applicationListeners = new HashSet<>();
        defaultApplicationListeners = new HashSet<>();
        ruleHanlders=new HashMap<>();
    }

    public void addApplicationListener(boolean isdefault, ApplicationListener applicationListener) {
        log.info("Components {} are added in TTIOC, defualt value is : {}",applicationListener.getClass(),isdefault);
        if (applicationListener!=null){
            if (isdefault) {
                defaultApplicationListeners.add(applicationListener);
            } else {
                applicationListeners.add(applicationListener);
            }
        }
    }

    public void addRuleHanlder(String name, Class<RuleHandler> ruleHandlerClass) {
        Class<?> interfaces[] = ruleHandlerClass.getInterfaces();
        for (Class<?> inte : interfaces) {
            if (inte==RuleHandler.class){
                log.info("RuleHandler {} are added in TTIOC, name is : {}",ruleHandlerClass,name);
                ruleHanlders.put(name,ruleHandlerClass);
            }
        }
    }
}
