package com.cs.jetpack.paging.bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alibaba.fastjson.annotation.JSONCreator
import com.alibaba.fastjson.annotation.JSONField

@Entity(tableName = "girls")
data class Girl @JSONCreator constructor(
    @PrimaryKey(autoGenerate = true)
    var _id: Int = 0,
    var author: String = "",
    var createdAt: String = "",
    var publishedAt: String = "",
    var desc: String = "",
    var images: String = "",
    var likeCounts: String = "",
    var stars: String = "",
    var title: String = "",
    var url: String = "",
    var views: String = ""
)
