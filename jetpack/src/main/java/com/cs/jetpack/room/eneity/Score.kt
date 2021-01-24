package com.cs.jetpack.room.eneity

import androidx.room.Entity
import androidx.room.ForeignKey

/**
 * 成绩表
 */
@Entity(tableName = "scores")
data class Score(
    @ForeignKey(
        entity = Student::class,
        childColumns = ["studentNo"],
        parentColumns = ["no"],
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
    )
    var studentNo: String, //学号(外键)

    @ForeignKey(
        entity = Course::class,
        childColumns = ["courseNo"],
        parentColumns = ["no"],
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
    )
    var courseNo: String,  //课程号(外键)

    var degree: Int        //分数
) {
}