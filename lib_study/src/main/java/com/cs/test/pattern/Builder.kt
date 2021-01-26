package com.cs.test.pattern

/**
 *
 * @author  ChenSen
 * @date  2021/1/26
 * @desc
 **/


class Student(val name: String, var age: String, var sex: String) {
    private var mName = ""
    private var mAge = ""
    private var mSex = ""

    init {
        this.mName = name
        this.mAge = age
        this.mSex = sex
    }


    class Builder {
        private var mName = ""
        private var mAge = ""
        private var mSex = ""

        fun setName(name: String): Builder {
            this.mName = name
            return this
        }

        fun setAge(age: String): Builder {
            this.mAge = age
            return this
        }

        fun setSex(sex: String): Builder {
            this.mSex = sex
            return this
        }

        fun build(): Student {
            return Student(this.mName, this.mAge, this.mSex)
        }
    }

}

object Test {

    @JvmStatic
    fun main(args: Array<String>) {
        val zhangsan = Student.Builder()
            .setName("张三")
            .setAge("18")
            .setSex("男")
            .build()
    }
}
