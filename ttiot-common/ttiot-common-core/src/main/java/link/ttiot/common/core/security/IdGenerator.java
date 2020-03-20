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

import cn.hutool.core.lang.ObjectId;
import link.ttiot.common.core.constant.ProtocalConstant;
import link.ttiot.common.core.function.FunctionApi;

import java.util.Optional;

/**
 * @author: shijun
 * @date: 2019-04-22
 * @description:
 */
public class IdGenerator implements FunctionApi, ProtocalConstant {

    private static IdGenerator ttIotIdGenerator;

    private IdGenerator() {
    }

    public static IdGenerator me() {
        if (ttIotIdGenerator == null) {
            ttIotIdGenerator = new IdGenerator();
        }
        return ttIotIdGenerator;
    }

    public String productClientId(String tenantId, String devName) {
        return predicateThrowException(tenantId + SEPARATOR + devName, u -> u.length() < CLIENT_IDENTIFY_LENGTH, new RuntimeException());

    }

    public String productTenantId( ) {
        return ObjectId.next();
    }

    public String productDevNameId(String devName) {
        return Optional.ofNullable(devName).orElseGet(() -> ObjectId.next().substring(15,20));
    }

    public String productDevPasswordId(String devPassword) {
        return Optional.ofNullable(devPassword).orElseGet(() -> ObjectId.next().substring(15,20));
    }

}
