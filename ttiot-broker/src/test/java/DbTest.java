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

import link.ttiot.broker.db.DbHelper;
import link.ttiot.broker.db.redis.RedisDbHelper;
import link.ttiot.broker.entity.Device;
import link.ttiot.broker.entity.Tenant;
import link.ttiot.common.redis.RedisSourceProvider;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

/**
 * @author: shijun
 * @date: 2019-04-19
 * @description:
 */
public class DbTest {

    DbHelper dbHelper;
    Tenant tenant;
    Device dev;
    Device dev2;

    @Before
    public void before() {
        /**
         * 数据源插件
         */
        dbHelper = new RedisDbHelper(new RedisSourceProvider());

        tenant = new Tenant("TTIot", "TTIot");
        dev = new Device("TTIot_client", "TTIot_client_password", "");
        dev2 = new Device("TTIot_client1", "TTIot_client_password", "");
    }

    @Test
    public void save() {
        /**
         * 保存租户
         */
        Boolean saveTenant1 = dbHelper.saveTenant(tenant);
        Boolean saveTenant2 = dbHelper.saveTenant(new Tenant("TTIot1", "TTIot"));
        Tenant getTenant1 = dbHelper.getTenant("TTIotx");
        Tenant getTenant2 = dbHelper.getTenant("TTIot");
        Tenant getTenant3 = dbHelper.getTenant("TTIot");
        Map<String, Tenant> map = dbHelper.listTenant();
        /**
         * 保存设备
         */
        Boolean saveDev1 = dbHelper.saveDev(dev);
        Boolean saveDev2 = dbHelper.saveDev(dev2);
        Map<String, Device> listDev = dbHelper.listDevByTenantAndState("TTIot");

    }

    @Test
    public void delete() {
        dbHelper.deleteTenant("TTIot");
        Boolean deleteTenant = dbHelper.deleteTenant("TTIot");
    }



}
