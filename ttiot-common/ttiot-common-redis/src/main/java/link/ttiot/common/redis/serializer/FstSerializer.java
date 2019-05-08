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

package link.ttiot.common.redis.serializer;

import redis.clients.util.SafeEncoder;

import java.io.*;

/**
 * @author: shijun
 * @date: 2019-04-19
 * @description:
 */
public class FstSerializer implements Serializer {

    public static final Serializer me = new FstSerializer();

    public FstSerializer() {
    }

    @Override
    public byte[] keyToBytes(String key) {
        return SafeEncoder.encode(key);
    }

    @Override
    public String keyFromBytes(byte[] bytes) {
        return SafeEncoder.encode(bytes);
    }

    @Override
    public byte[] fieldToBytes(Object field) {
        return this.valueToBytes(field);
    }

    @Override
    public Object fieldFromBytes(byte[] bytes) {
        return this.valueFromBytes(bytes);
    }

    @Override
    public byte[] valueToBytes(Object value) {
        ObjectOutputStream fstOut = null;
        byte[] var4;
        try {
            //todo: exchange ByteArrayOutputStream to fast ByteArrayOutputStream
            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            fstOut = new ObjectOutputStream(bytesOut);
            fstOut.writeObject(value);
            fstOut.flush();
            var4 = bytesOut.toByteArray();
        } catch (Exception var13) {
            throw new RuntimeException(var13);
        } finally {
            if (fstOut != null) {
                try {
                    fstOut.close();
                } catch (IOException var12) {
                }
            }

        }
        return var4;
    }

    @Override
    public Object valueFromBytes(byte[] bytes) {
        if (bytes != null && bytes.length != 0) {
            ObjectInputStream fstInput = null;
            Object var3;
            try {
                fstInput = new ObjectInputStream(new ByteArrayInputStream(bytes));
                var3 = fstInput.readObject();
            } catch (Exception var12) {
                throw new RuntimeException(var12);
            } finally {
                if (fstInput != null) {
                    try {
                        fstInput.close();
                    } catch (IOException var11) {
                    }
                }
            }
            return var3;
        } else {
            return null;
        }
    }
}
