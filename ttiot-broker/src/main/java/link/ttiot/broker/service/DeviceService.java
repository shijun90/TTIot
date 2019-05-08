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

package link.ttiot.broker.service;

import link.ttiot.broker.db.DbHelper;
import link.ttiot.common.ioc.annotation.Inject;
import lombok.Getter;

import java.util.Optional;

/**
 * @author: shijun
 * @date: 2019-04-22
 * @description:
 */
public class DeviceService{

    @Getter
    @Inject
    private DbHelper dbHelper;

    public boolean login(String tenantId, String userName, String password) {
        return Optional.ofNullable(dbHelper.getDev(tenantId, userName)).
                map(u -> u.getClientPassword()
                        .equals(password))
                .orElse(false);
    }

}
