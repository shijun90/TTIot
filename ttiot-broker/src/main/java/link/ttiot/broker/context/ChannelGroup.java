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

package link.ttiot.broker.context;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.EventExecutor;
import link.ttiot.broker.service.SessionService;
import link.ttiot.common.core.constant.ProtocalConstant;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: shijun
 * @date: 2019-05-03
 * @description:
 */
public class ChannelGroup extends DefaultChannelGroup {

    private Map<String, ChannelId> ChannelIdMap = new ConcurrentHashMap<>();

    /**
     * 上线
     *
     * @return
     */
    public boolean add(String clientId, Channel channel) {
        channel.attr(ProtocalConstant.LOGIN).set(true);
        channel.attr(ProtocalConstant.CLIENTID).set(clientId);
        channel.attr(ProtocalConstant.RECEIVE).set(new HashSet<>());
        this.ChannelIdMap.put(clientId, channel.id());
        return super.add(channel);
    }

    public Channel find(String clientId) {
        return Optional.ofNullable(ChannelIdMap.get(clientId)).map(u -> super.find(u)).orElse(null);
    }

    /**
     * 下线
     *
     * @return
     */
    public boolean remove(String clientId, SessionService sessionService, Runnable runnable) {

        return Optional.ofNullable(sessionService.get(clientId)).map(u -> {
            if (u.isCleanSession()) {
                runnable.run();
                sessionService.delete(clientId);
            }
            return super.remove(find(clientId));
        }).orElse(false);
    }

    public ChannelGroup(EventExecutor executor) {
        super(executor);
    }


}
