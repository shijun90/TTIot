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

package link.ttiot.common.core.constant;

import io.netty.util.AttributeKey;

import java.util.List;
import java.util.Set;

/**
 * @author: shijun
 * @date: 2019-04-22
 * @description:
 */
public interface ProtocalConstant {

    String SEPARATOR = "-";
    String TERMINATOR = ";";
    int CLIENT_IDENTIFY_LENGTH = 21;
    char SEPARATORTOPIC_CHAR = '/';
    String SEPARATORTOPIC_STRING = "/";
    String SEPARATORTOPIC_SINGLE = "+";
    String SEPARATORTOPIC_GLOB = "#";
    AttributeKey<Boolean> LOGIN = AttributeKey.valueOf("LOGIN");
    AttributeKey<String> CLIENTID = AttributeKey.valueOf("CLIENTID");
    AttributeKey<Set<Integer>> RECEIVE = AttributeKey.valueOf("RECEIVE");


}
