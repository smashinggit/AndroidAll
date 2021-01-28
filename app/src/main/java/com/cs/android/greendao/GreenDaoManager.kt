package com.cs.android.greendao

import com.cs.android.App

/**
 *
 * @author  ChenSen
 * @date  2021/1/28
 * @desc
 **/
object GreenDaoManager {
    var dao = App.mSession?.goodDao

    fun insert(vararg goods: Good) {

        // 如果不想因为重复添加数据而导致崩溃,可以使用insertOrReplaceInTx API
        dao?.insertOrReplaceInTx(*goods)
    }


    fun query(): List<Good> {
        return dao?.loadAll() ?: emptyList()
    }

    fun queryFruit(): List<Good> {
        return dao?.run {
            queryBuilder()
                .where(GoodDao.Properties.Type.eq("fruit"))
                .orderDesc()
                .list()
        } ?: emptyList()
    }

    fun queryCake(): List<Good> {
        return dao?.run {
            queryBuilder()
                .where(GoodDao.Properties.Type.eq("cake"))
                .orderDesc()
                .list()
        } ?: emptyList()
    }

}