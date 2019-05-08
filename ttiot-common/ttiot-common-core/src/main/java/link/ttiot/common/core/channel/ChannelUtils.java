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

package link.ttiot.common.core.channel;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import link.ttiot.common.core.constant.ProtocalConstant;
import lombok.experimental.UtilityClass;

import java.util.Set;


/**
 * @author: shijun
 * @date: 2019-04-26
 * @description:
 */
@UtilityClass
public class ChannelUtils {

    public Boolean login() {
        return false;
    }

    public <T> Attribute<T> getAttributeKey(Channel channel, AttributeKey<T> attributeKey) {
        return channel.attr(attributeKey);
    }

    public boolean ifLogin(Channel channel) {
        //活跃状态并且通过认证
        return channel != null && channel.isActive() && getAttributeKey(channel, ProtocalConstant.LOGIN).get();
    }

    public String clientId(Channel channel) {
        return getAttributeKey(channel, ProtocalConstant.CLIENTID).get();
    }


    public void addReceive(Channel channel, int receiveId) {
        Set<Integer> set = getAttributeKey(channel, ProtocalConstant.RECEIVE).get();
        set.add(receiveId);
    }

    public void removeReceive(Channel channel, int receiveId) {
        Set<Integer> set = getAttributeKey(channel, ProtocalConstant.RECEIVE).get();
        set.remove(receiveId);
    }

    public boolean containReceive(Channel channel, int receiveId) {
        Set<Integer> set = getAttributeKey(channel, ProtocalConstant.RECEIVE).get();
        return set.contains(receiveId);
    }

}
