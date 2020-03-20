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

package link.ttiot.common.context.protocal;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import link.ttiot.common.core.channel.ChannelUtils;
import link.ttiot.common.core.security.ClientIdReader;
import link.ttiot.common.ioc.core.ApplicationEvent;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

/**
 * @author: shijun
 * @date: 2019-04-15
 * @description:
 */
public abstract class ProtocolApplicationEvent extends ApplicationEvent {

    @Getter
    public ChannelHandlerContext channelHandlerContext;
    @Getter
    @Setter
    private Channel channel;

    public ProtocolApplicationEvent(Object msg, ChannelHandlerContext context) {
        super(msg);
        this.channel = context.channel();
        channelHandlerContext = context;
    }

    public String getClientId() {
        return Optional.ofNullable(channel).map(u -> ChannelUtils.clientId(u)).orElse(null);
    }

    public String getTenantId() {
        return Optional.ofNullable(getClientId()).map(u -> ClientIdReader.tenantId(u)).orElse(null);
    }

    public String getDevName() {
        return Optional.ofNullable(getClientId()).map(u -> ClientIdReader.devName(u)).orElse(null);
    }


}
