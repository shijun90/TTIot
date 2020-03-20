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

package link.ttiot.example.server.listener;

import cn.hutool.core.lang.Console;
import lombok.experimental.UtilityClass;

/**
 * @author: shijun
 * @date: 2019-05-06
 * @description:
 */
@UtilityClass
public class DbDemo {


    public void saveLogin(Long time,String devName,String tenant){
        Console.print("储存信息："+"租户:"+tenant+"设备编号:"+devName+"在"+time+"登陆");
    }

}
