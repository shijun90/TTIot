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

/**
 * @author: shijun
 * @date: 2019-04-11
 * @description:
 */
public interface CommonConstant {
    String BASE_CONTEXT_CONFIG_FILE = "TTIotBootstrap.yml";
    String BASE_VERSION = "1.0.0";
    String BASE_BANNER_CONFIG_FILE = "banner.txt";
    String MQTT_DECODER="mqttDecoder";
    String MQTT_ENCODE="mqttEncode";
    String PROTOCOL_ADAPTIVE_HANDLER="protocolAdaptiveHandler";
    String MQTT_HANDLER="mqttHander";
    String MQTT_WEBSOCKET_CODEC="mqttWebSocketCodec";
    String HTTP_CHUNKED_WRITER="httpChunkedWriter";
    String HTTP_CODER="httpCoder";
    String HTTP_COMPRESSOR="compressor";
    String HTTP_AGGREGATOR="httpAggregator";
    String HTTP_REQUEST_HANDLER="httpRequestHandler";
    String MQTT_WEBSOCKET_SUBPROTOCOLS="server, mqttv3.1, mqttv3.1.1";
    String MQTT_WEBSOCKET_PROTOCOL="protocol";
    String HEARTBEAT_HANDLER="heartbeatHander";
    String FIELD_DEFAULT="isDefault";
    String FIELD_ASYNCHRONOUS="isAsynchronous";
    String EVENTLOOP_KEY="io.netty.eventLoopThreads";
    String WEBSOCKET_PATH="/mqtt";
    int HTTP_MAXCONTENT_LENGTH=1048576000;
    int MAX_PAYLOAD_SIZE=6553600;



}
