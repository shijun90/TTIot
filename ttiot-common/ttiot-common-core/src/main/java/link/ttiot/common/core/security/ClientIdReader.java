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

package link.ttiot.common.core.security;

import link.ttiot.common.core.constant.ProtocalConstant;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

/**
 * @author: shijun
 * @date: 2019-04-23
 * @description:
 */
@AllArgsConstructor
public class ClientIdReader {

    @Getter
    private String tenantId;

    @Getter
    private String devName;

    @Getter
    private String password;

    public static ClientIdReader transverter(String clientId, byte[] passwordBytes) {
        ClientIdReader clientIdTransverter = null;
        try {
            clientIdTransverter = Optional.ofNullable(clientId).
                    map(u -> {
                        String[] var = u.split(ProtocalConstant.SEPARATOR);
                        return new ClientIdReader(var[0], var[1], new String(passwordBytes));
                    }).get();
        } catch (RuntimeException e) {
            return clientIdTransverter;
        }
        return clientIdTransverter;
    }

    public static String tenantId(String clientId) {
        String clientIdTransverter = null;
        try {
            clientIdTransverter = Optional.ofNullable(clientId).
                    map(u -> {
                        String[] var = u.split(ProtocalConstant.SEPARATOR);
                        return var[0];
                    }).get();
        } catch (RuntimeException e) {
            return clientIdTransverter;
        }
        return clientIdTransverter;
    }



    public static String devName(String clientId) {
        String clientIdTransverter = null;
        try {
            clientIdTransverter = Optional.ofNullable(clientId).
                    map(u -> {
                        String[] var = u.split(ProtocalConstant.SEPARATOR);
                        return var[1];
                    }).get();
        } catch (RuntimeException e) {
            return clientIdTransverter;
        }
        return clientIdTransverter;
    }


    public static String realClientId(String clientId) {
        String clientIdTransverter = null;
        try {
            clientIdTransverter = Optional.ofNullable(clientId).
                    map(u -> {
                        String[] var = u.split(ProtocalConstant.SEPARATOR);
                        return var[0]+ProtocalConstant.SEPARATOR+var[1];
                    }).get();
        } catch (RuntimeException e) {
            return clientIdTransverter;
        }
        return clientIdTransverter;
    }
}
