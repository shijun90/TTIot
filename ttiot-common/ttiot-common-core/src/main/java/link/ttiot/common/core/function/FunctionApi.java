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

package link.ttiot.common.core.function;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author: shijun
 * @date: 2019-04-22
 * @description:
 */
public interface FunctionApi {


    default <T> void predicateDoElse(T t, Predicate<T> predicate, Consumer<T> consumer, Consumer<T> consumer2) {
        if (t != null) {
            if (predicate.test(t)) {
                consumer.accept(t);
            } else {
                consumer2.accept(t);
            }
        }
    }

    default <T> void predicateDo(T t, Predicate<T> predicate, Consumer<T> consumer) {
        if (t != null) {
            if (predicate.test(t)) {
                consumer.accept(t);
            }
        }
    }

    default <T> T predicateThrowException(T t, Predicate<T> predicate,RuntimeException e){
        if (t != null) {
            if (!predicate.test(t)) {
               throw e;
            }else{
                return t;
            }
        }
        return null;
    }


    default <T> void predicateDoElseThrowException(T t, Predicate<T> predicate, Consumer<T> consumer,RuntimeException e){
        if (t != null) {
            if (!predicate.test(t)) {
                throw e;
            }else{
                consumer.accept(t);
            }
        }
    }
}
