package com.cs.jetpack.paging.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cs.common.http.BusinessException
import com.cs.common.http.Response
import com.cs.jetpack.paging.bean.Girl
import com.cs.jetpack.paging.repository.GirlRepository
import kotlinx.coroutines.launch

class GirlViewModel(private val repository: GirlRepository) : ViewModel() {

    var loading = MutableLiveData(false)
    var girls = MutableLiveData<Response<List<Girl>>>()

    fun getGirl() {

        viewModelScope.launch {
            try {
                girls.postValue(Response.loading())
                girls.postValue(Response.success(repository.getGirlFromLocal()))

            } catch (e: Exception) {
                e.printStackTrace()
                if (e is BusinessException) {
                    girls.postValue(Response.error("${e.message} ${e.code}"))
                } else {
                    girls.postValue(Response.error(e.message ?: ""))
                }
            }
        }
    }

}

class GirlViewModelFactory(private val repository: GirlRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return modelClass.getConstructor(GirlRepository::class.java)
            .newInstance(repository)
    }
}