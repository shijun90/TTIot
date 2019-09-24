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

package link.ttiot.common.config.banner;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.lang.Console;
import cn.hutool.core.text.StrFormatter;
import io.netty.util.NettyRuntime;
import io.netty.util.internal.SystemPropertyUtil;
import link.ttiot.common.config.ConfigurationFileReader;
import link.ttiot.common.config.ContextConfig;
import link.ttiot.common.core.constant.CommonConstant;
import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * @author: shijun
 * @date: 2019-05-06
 * @description:
 */
public class BannerReader {

    private static final Integer DEFAULT_EVENT_LOOP_THREADS;

    static {
        DEFAULT_EVENT_LOOP_THREADS = Math.max(1, SystemPropertyUtil.getInt(
                CommonConstant.EVENTLOOP_KEY, NettyRuntime.availableProcessors() * 2));
    }

    @SneakyThrows
    private static void loaderBanner(Integer port,Integer heartbeatTimeout,String bossGroup,String workerGroup){
        URL resource= ConfigurationFileReader.class.getClassLoader().getResource(CommonConstant.BASE_BANNER_CONFIG_FILE);
        InputStream in=resource.openStream();
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        byte[] buffer=new byte[2048];
        int lenght=0;
        while ((lenght=in.read(buffer))!=-1){
            bos.write(buffer,0,lenght);
        }
        in.close();
        String result=StrFormatter.format(new String(bos.toByteArray(),"UTF-8"),CommonConstant.BASE_VERSION,port.toString(),bossGroup,workerGroup,heartbeatTimeout);
        Console.print(result);
    }

    public static void startBannerListener(){
        ContextConfig tTiotStarterContextConfig = ContextConfig.getConfig();
        Integer bossCount=tTiotStarterContextConfig.TTiot.netty.getBossGroupCount();
        Integer workerCount=tTiotStarterContextConfig.TTiot.netty.getWorkerGroupCount();
        loaderBanner(tTiotStarterContextConfig.TTiot.getPort(),tTiotStarterContextConfig.TTiot.getHeartbeatTimeout(),bossCount==-1?DEFAULT_EVENT_LOOP_THREADS.toString():bossCount.toString(),workerCount==-1?DEFAULT_EVENT_LOOP_THREADS.toString():workerCount.toString());
    }

}
