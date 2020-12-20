package com.cs.android.view.taglayout

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class TagAdapter {


    abstract fun getCount(): Int

    abstract fun getView(inflater: LayoutInflater, position: Int, parent: ViewGroup): View

    fun notifyChanged() {

    }

}