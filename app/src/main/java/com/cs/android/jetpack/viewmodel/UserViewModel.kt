package com.cs.android.jetpack.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * @author ChenSen
 * @since 2021/6/23 11:20
 * @desc
 */
class UserViewModel(api: Int) : ViewModel() {
    val data1 = MutableLiveData(0)

    fun getUser() {

    }
}


class UserViewModelFactory(private val api: Int) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return UserViewModel(api) as T
    }
}