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

import cn.hutool.core.io.resource.ClassPathResource;
import link.ttiot.common.core.constant.CommonConstant;
import lombok.Cleanup;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author: shijun
 * @date: 2019-04-11
 * @description:
 */
@Slf4j
public class ConfigurationFileReader {


    @SneakyThrows({FileNotFoundException.class,IOException.class})
    public static <T> T loaderYml(@NonNull File configFileSrc, Class<T> c){
        @Cleanup FileInputStream in = null;
        Yaml yaml = new Yaml();
        in = new FileInputStream(configFileSrc);
        return yaml.loadAs(in, c);
    }

    @SneakyThrows
    public static <T> T loaderBaseYml(Class<T> c) {
        ClassPathResource resource = new ClassPathResource(CommonConstant.BASE_CONTEXT_CONFIG_FILE);
        return loaderYml(resource.getFile(), c);
    }


}
