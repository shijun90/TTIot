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

package link.ttiot.common.ioc.vo;

import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author: shijun
 * @date: 2020-03-17
 * @description:
 */
@AllArgsConstructor
@Data
public class HttpRet {

    private static int ERROR=500;
    private static int SUCCESS=0;
    private static String SUCCESS_DF_MSG="success";
    private static String ERROR_MSG_NOTFUND="404 not fund";

    private Integer code;

    private Object data;

    private String msg;

    public static HttpRet error(String msg){
        return new HttpRet(ERROR,null,msg);
    }

    public static HttpRet errorForNotFund(){
        return new HttpRet(ERROR,null,ERROR_MSG_NOTFUND);
    }

    public boolean isSuccess(){
        return this.code==0;
    }

    public static HttpRet success(Object data){
        return new HttpRet(SUCCESS,data,SUCCESS_DF_MSG);
    }

    public String toJson(){
        return JSONUtil.toJsonPrettyStr(this);
    }
}
