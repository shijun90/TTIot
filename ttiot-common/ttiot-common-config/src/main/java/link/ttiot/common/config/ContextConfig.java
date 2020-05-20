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

package link.ttiot.common.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: shijun
 * @date: 2019-04-11
 * @description:
 */

@Slf4j
public class ContextConfig {

    public static ContextConfig tTiotStarterContextConfig;

    public static ContextConfig getConfig() {
        if (tTiotStarterContextConfig == null) {
            tTiotStarterContextConfig = ConfigurationFileReader.loaderBaseYml(ContextConfig.class);
        }
        return tTiotStarterContextConfig;
    }

    @Getter
    @Setter
    public TTiotStarterContextConfigAttribut TTiot;

    public static class TTiotStarterContextConfigAttribut {
        @Getter
        @Setter
        private int port;
        @Getter
        @Setter
        private String host;
        @Getter
        @Setter
        private int heartbeatTimeout;

        public TTiotStarterContextNettyConfigAttribut netty;

        public TTiotRedisConfigAttribut redis;

        public Ssl ssl;

        public static class Ssl {
            @Getter
            @Setter
            private boolean enabled;
            @Getter
            @Setter
            private String certificateType;
            @Getter
            @Setter
            private String certificatePath;
            @Getter
            @Setter
            private String certificatePassword;
        }


        public static class TTiotStarterContextNettyConfigAttribut {
            @Getter
            @Setter
            private int bossGroupCount;
            @Getter
            @Setter
            private int workerGroupCount;
            @Getter
            @Setter
            private String leakDetectorLevel;
            @Getter
            @Setter
            private int maxPayloadSize;
        }

        public static class TTiotRedisConfigAttribut {
            @Getter
            @Setter
            private String host;
            @Getter
            @Setter
            private int port;
            @Getter
            @Setter
            private int timeout;
            @Getter
            @Setter
            private int connectionTimeout;
            @Getter
            @Setter
            private int soTimeout;
            @Getter
            @Setter
            private String password;
            @Getter
            @Setter
            private int database;
            @Getter
            @Setter
            private int maxIdle;
            @Getter
            @Setter
            private int minIdle;
            @Getter
            @Setter
            private int maxTotal;
            @Getter
            @Setter
            private int maxWaitMillis;
            @Getter
            @Setter
            private boolean ssl;
        }


    }

}
