package com.cs.test.android

import androidx.annotation.IntDef


// Declare the constants
const val ROTATION_0 = 0
const val ROTATION_90 = 90
const val ROTATION_180 = 180
const val ROTATION_270 = 270


//Define the list of accepted constants and declare the RotationDegree annotation
@Retention(AnnotationRetention.SOURCE)
@IntDef(ROTATION_0, ROTATION_90, ROTATION_180, ROTATION_270)
annotation class RotationDegree

object TypedefTest {

    private fun setRotation(@RotationDegree rotation: Int) {
        println("rotation $rotation")
    }

    @JvmStatic
    fun main(arg: Array<String>) {
        setRotation(12)
        setRotation(1)
    }

}
