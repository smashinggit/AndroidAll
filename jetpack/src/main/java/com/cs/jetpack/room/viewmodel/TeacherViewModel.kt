package com.cs.jetpack.room.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs.jetpack.room.database.StudentDataBase
import com.cs.jetpack.room.eneity.Teacher
import com.cs.jetpack.room.repository.TeacherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TeacherViewModel : ViewModel() {

    private val repository = TeacherRepository(StudentDataBase.INSTANCE!!.teacherDao())

    var loading = MutableLiveData(false)

    var addTeacherResult = MutableLiveData<Long>()        //插入单个数据，返回值为rowId
    var addTeachersResult = MutableLiveData<Boolean>()   //插入多个数据，没有返回值
    var deleteTeachersResult = MutableLiveData<Int>()     //查询数据的结果集合
    var teachersResult = MutableLiveData<List<Teacher>>()     //查询数据的结果集合


    fun addTeacher(teacher: Teacher) {
        viewModelScope.launch(context = Dispatchers.IO) {
            loading.postValue(true)

            val rowId = repository.addTeacher(teacher)
            addTeacherResult.postValue(rowId)

            loading.postValue(false)

        }
    }

    fun addTeachers(vararg teacher: Teacher) {
        viewModelScope.launch(context = Dispatchers.IO) {
            loading.postValue(true)

            repository.addTeachers(*teacher)
            addTeachersResult.postValue(true)

            loading.postValue(false)

        }
    }


    fun deleteTeachers(vararg teacher: Teacher) {
        viewModelScope.launch(context = Dispatchers.IO) {
            loading.postValue(true)

            val lines = repository.deleteTeachers(*teacher)
            deleteTeachersResult.postValue(lines)

            loading.postValue(false)
        }
    }

    fun queryAll() {
        viewModelScope.launch(context = Dispatchers.IO) {
            loading.postValue(true)

            val teachers = repository.queryAll()
            teachersResult.postValue(teachers)

            loading.postValue(false)
        }
    }
}