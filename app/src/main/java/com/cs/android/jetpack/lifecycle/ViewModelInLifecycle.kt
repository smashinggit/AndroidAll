package com.cs.android.jetpack.lifecycle

import androidx.lifecycle.*

/**
 * @author ChenSen
 * @since 2021/6/22 20:47
 * @desc
 */
class ViewModelInLifecycle : ViewModel() {
    val data1 = MutableLiveData(1)

    val data2 = Transformations.map(data1) { input ->
        input + 2
    }


    val data3 = Transformations.switchMap(data1) { input ->
        queryData(input)
    }

    fun queryData(flag: Int): LiveData<String> {
        return MutableLiveData<String>(System.currentTimeMillis().toString())
    }


    val source1 = MutableLiveData(false)
    val source2 = MutableLiveData(false)

    val mediatorLiveData = MediatorLiveData<Boolean>().apply {
        addSource(source1) {

        }

        addSource(source2) {

        }
    }

}