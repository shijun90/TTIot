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

import cn.hutool.core.lang.Console;
import link.ttiot.broker.db.redis.RedisDbHelper;
import link.ttiot.common.context.db.DbHelper;
import link.ttiot.common.context.entity.Device;
import link.ttiot.common.context.entity.Tenant;
import link.ttiot.common.core.security.IdGenerator;
import link.ttiot.common.redis.RedisSourceProvider;
import org.junit.Before;
import org.junit.Test;


/**
 * @author: shijun
 * @date: 2019-04-23
 * @description:
 */
public class IdGenerateTest {

    DbHelper dbHelper;

    private static String tenantId="TT";

    /**
     * 数据源
     */
    @Before
    public void data() {
        dbHelper = new RedisDbHelper(new RedisSourceProvider());
    }

    @Test
    public void tenant(){
     Tenant tenant = new Tenant(tenantId, "shijun'tt");
     dbHelper.saveTenant(tenant);
    }

    @Test
    public void dev() {
        String devName= IdGenerator.me().productDevNameId(null);
        String passwordId=IdGenerator.me().productDevPasswordId(null);
        String clientId=IdGenerator.me().productClientId(tenantId,devName);
        Console.print("devname:"+devName);
        Console.print("devpassword:"+passwordId);
        Console.print("clientId:"+clientId);
        Device dev2 = new Device(devName, passwordId,tenantId );
        dbHelper.saveDev(dev2);
    }


}
