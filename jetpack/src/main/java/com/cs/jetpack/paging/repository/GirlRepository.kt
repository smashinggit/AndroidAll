package com.cs.jetpack.paging.repository

import android.util.Log
import com.alibaba.fastjson.JSONArray
import com.cs.common.http.BusinessException
import com.cs.jetpack.paging.api.GirlApi
import com.cs.jetpack.paging.bean.Girl
import com.cs.jetpack.paging.dao.GirlDao
import com.cs.jetpack.paging.http.RetrofitWrapper
import kotlinx.coroutines.withContext

class GirlRepository(
    private val dao: GirlDao,
    private val api: GirlApi = RetrofitWrapper.create(GirlApi::class.java)
) {

    suspend fun getGirl(): List<Girl> {
        api.getGirl(1, 30).run {
            if (100 == getIntValue("status")) {
                val girls =
                    JSONArray.parseArray(getJSONArray("data").toJSONString(), Girl::class.java)

                saveToDataBase(girls)
                return girls
            } else {
                throw  BusinessException(getIntValue("status"), "")
            }
        }
    }

    suspend fun getGirlFromLocal(): List<Girl> {
        var localGirls = dao.queryAll()
        if (localGirls.isEmpty()) {
            Log.e("tag", "数据库为空，从网络加载")
            localGirls = getGirl()
        }
        return localGirls
    }


    private suspend fun saveToDataBase(girls: MutableList<Girl>) {
        Log.e("tag", "saveToDataBase ${Thread.currentThread().name}")
        val start = System.currentTimeMillis()
        dao.insert(*girls.toTypedArray())

        Log.e("tag", "saveToDataBase 耗时 ${System.currentTimeMillis() - start}")
    }
}
