package com.cs.common.http

import com.alibaba.fastjson.JSONObject

class BusinessException(message: String) : RuntimeException(message) {
    var code = -1

    constructor(code: Int, message: String) : this(message) {
        this.code = code
    }

    constructor(jsonObject: JSONObject) : this(
        jsonObject.getIntValue("returnCode"),
        jsonObject.getString("returnMessage")
    )
}