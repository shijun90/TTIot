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

package link.ttiot.common.core.ssl;

import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

/**
 * @author: shijun
 * @date: 2020-05-19
 * @description:
 */
@Slf4j
public class SslBuilder {

    private static String sslContextType="ssl";

    public SSLContext createSslContext(String KeyStoreType,String path,String sslPassword) {
        try {
            //JKS \PKCS12-.pfx
            KeyStore ks=KeyStore.getInstance(KeyStoreType);
            // path
            InputStream ksInputStream=new FileInputStream(path);
            ks.load(ksInputStream,sslPassword.toCharArray());
            KeyManagerFactory kmf=KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks,sslPassword.toCharArray());
            SSLContext sslContext=SSLContext.getInstance(sslContextType);
            sslContext.init(kmf.getKeyManagers(),null,null);
            return sslContext;
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return null;
        }
    }


}
