package com.cs.jetpack.room.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs.jetpack.room.database.StudentDataBase
import com.cs.jetpack.room.eneity.Student
import com.cs.jetpack.room.repository.StudentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StudentViewModel : ViewModel() {
    private var repository = StudentRepository(StudentDataBase.INSTANCE!!.studentDao())

    var loading = MutableLiveData(false)

    var addStudentResult = MutableLiveData<Long>()       //插入单个数据，返回值为rowId
    var addStudentsResult = MutableLiveData<Boolean>() //插入多个数据，没有返回值
    var studentsResult = MutableLiveData<List<Student>>()     //查询数据的结果集合
    var deleteStudentsResult = MutableLiveData<Int>()     //查询数据的结果集合


    fun addStudent(student: Student) {
        viewModelScope.launch(context = Dispatchers.IO) {
            loading.postValue(true)

            val result = repository.insert(student)
            addStudentResult.postValue(result)

            loading.postValue(false)
        }
    }

    fun addMultiStudent(vararg student: Student) {
        viewModelScope.launch(context = Dispatchers.IO) {
            loading.postValue(true)

            repository.insertMulti(*student)

            loading.postValue(false)
            addStudentsResult.postValue(true)
        }
    }


    fun delete(student: Student) {
        viewModelScope.launch(context = Dispatchers.IO) {
            loading.postValue(true)

            val affected = repository.delete(student)
            deleteStudentsResult.postValue(affected)

            loading.postValue(false)
        }
    }


    fun queryAll() {
        viewModelScope.launch(context = Dispatchers.IO) {
            loading.postValue(true)

            val result = repository.queryAll()
            studentsResult.postValue(result)

            loading.postValue(false)
        }
    }

}