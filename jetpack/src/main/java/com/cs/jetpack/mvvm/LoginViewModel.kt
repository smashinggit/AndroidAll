package com.cs.jetpack.mvvm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    val account = MutableLiveData<String>("")
    val password = MutableLiveData<String>("")


    fun test() {
        account.value
    }

}