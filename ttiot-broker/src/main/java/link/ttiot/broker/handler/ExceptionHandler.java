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

package link.ttiot.broker.handler;


import link.ttiot.broker.eventor.lwt.MqttLwtEvent;
import link.ttiot.common.context.Context;
import link.ttiot.common.context.exception.ExceptionHandlerAdapter;
import link.ttiot.common.core.channel.ChannelUtils;
import link.ttiot.common.core.exception.ConnectException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: shijun
 * @date: 2019-04-24
 * @description:
 */
@Slf4j
public class ExceptionHandler extends ExceptionHandlerAdapter {

    /**
     * 连接中异常
     * @param t
     * @param e
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (e instanceof ConnectException){
            log.error("connect error the reason is {}",e.getMessage());
            String clientId=ChannelUtils.clientId(((ConnectException) e).getChannel());
            if (clientId!=null){
                Context.me().publishEvent(new MqttLwtEvent(clientId));
            }
            ((ConnectException) e).getChannel().close();
        }
        e.printStackTrace();
    }

}
