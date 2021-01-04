package com.cs.jetpack.room.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.cmcc.jetpack.R
import com.cs.common.base.BaseFragment
import com.cs.common.utils.log
import com.cs.common.utils.query
import com.cs.common.utils.toast
import com.cs.jetpack.adapter.BaseDataAdapter
import com.cs.jetpack.room.eneity.Student
import com.cs.jetpack.room.viewmodel.StudentViewModel
import kotlinx.android.synthetic.main.fragment_student.*
import kotlinx.android.synthetic.main.item_student.view.*
import kotlin.random.Random

class StudentFragment : BaseFragment() {
    private val viewModel by lazy {
        ViewModelProvider(this).get(StudentViewModel::class.java)
    }

    private val mAdapter by lazy {
        object : BaseDataAdapter<Student>(requireContext(), itemRes = R.layout.item_student) {
            override fun onBind(holder: DataViewHolder, position: Int, data: Student) {
                holder.itemView.tvNo.text = data.no.toString()
                holder.itemView.tvName.text = data.name
                holder.itemView.tvAge.text = data.age.toString()
                holder.itemView.tvSex.text = data.sex

                holder.itemView.setOnClickListener {
                    query("您确定要删除${data.name}这条数据") {
                        viewModel.delete(data)
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_student, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        rvStudents.adapter = mAdapter
        rvStudents.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        //默认数据
        btnAddDefault.setOnClickListener {
            viewModel.addMultiStudent(*STUDENTS)
        }

        btnAdd.setOnClickListener {
            val random = Random(System.currentTimeMillis())
            val no = random.nextInt(100, 255)
            val name = random.nextInt(0, 100)
            val age = random.nextInt(0, 30)

            val student = Student(no, "鲁班${name}号", age, "男")
            viewModel.addStudent(student)
        }

        btnAddMulti.setOnClickListener {
            val random = Random(System.currentTimeMillis())
            val no = random.nextInt(100, 1000)
            val name = random.nextInt(0, 100)
            val age = random.nextInt(0, 30)

            val student = Student(no, "鲁班${name}号", age, "男")
            val student2 = Student(no + 1, "鲁班${name + 1}号", age + 1, "男")
            viewModel.addMultiStudent(student, student2)
        }

        viewModel.addStudentResult.observe(requireActivity(), Observer {
            viewModel.queryAll()
        })

        viewModel.addStudentsResult.observe(requireActivity(), Observer {
            viewModel.queryAll()
        })

        viewModel.deleteStudentsResult.observe(requireActivity(), Observer {
            log("删除数据 $it")
            if (it > 0) {
                toast("删除${it}行数据")
            }
            viewModel.queryAll()
        })

        viewModel.studentsResult.observe(requireActivity(), Observer {
            if (it.isEmpty()) {
                toast("数据库为空")
            }
            mAdapter.update(it)
        })

        viewModel.queryAll()
    }


    companion object {
        val STUDENTS = arrayOf(
            Student(1, "后裔", 30, "男"),
            Student(2, "嫦娥", 30, "女"),
            Student(3, "鲁班七号", 9, "男"),
            Student(4, "蔡文姬", 8, "女"),
            Student(5, "大乔", 19, "女"),
            Student(6, "小乔", 18, "女"),
            Student(7, "吕布", 32, "男"),
            Student(8, "貂蝉", 31, "女"),
            Student(9, "张飞", 35, "男"),
            Student(10, "关羽", 36, "男"),
        )
    }
}