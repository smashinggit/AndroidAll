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
import com.cs.jetpack.room.eneity.Teacher
import com.cs.jetpack.room.viewmodel.TeacherViewModel
import kotlinx.android.synthetic.main.fragment_teacher.*
import kotlinx.android.synthetic.main.item_teacher.view.*
import java.util.*
import kotlin.random.Random

class TeacherFragment : BaseFragment() {
    private val viewModel by lazy {
        ViewModelProvider(this).get(TeacherViewModel::class.java)
    }

    private val mAdapter by lazy {
        object : BaseDataAdapter<Teacher>(requireContext(), itemRes = R.layout.item_teacher) {
            override fun onBind(holder: DataViewHolder, position: Int, data: Teacher) {
                holder.itemView.tvNo.text = data.no.toString()
                holder.itemView.tvName.text = data.name
                holder.itemView.tvSex.text = data.sex
                holder.itemView.tvBirthday.text = data.birthday.timeInMillis.toString()
                holder.itemView.tvPart.text = data.part

                holder.itemView.setOnClickListener {
                    query("您确定要删除${data.name}这条数据") {
                        viewModel.deleteTeachers(data)
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
        return inflater.inflate(R.layout.fragment_teacher, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        rvTeachers.adapter = mAdapter
        rvTeachers.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        //默认数据
        btnAddDefault.setOnClickListener {
            viewModel.addTeachers(*TEACHERS)
        }

        btnAdd.setOnClickListener {
            val random = Random(System.currentTimeMillis())
            val no = random.nextInt(100, 1000)
            val name = random.nextInt(0, 100)
            val age = random.nextInt(0, 30)
            val teacher = Teacher(
                no,
                "教师${name}号",
                age,
                "男",
                Calendar.getInstance().apply {
                    set(1990, 2, 1)
                },
                "教导处"
            )

            viewModel.addTeacher(teacher)
        }


        btnAddMulti.setOnClickListener {
            val random = Random(System.currentTimeMillis())
            val no = random.nextInt(100, 1000)
            val name = random.nextInt(0, 100)
            val age = random.nextInt(0, 30)

            val teacher = Teacher(
                no, "教师${name}号", age, "男",
                Calendar.getInstance().apply {
                    set(1980, 4, 5)
                },
                "教导处"
            )
            val teacher2 = Teacher(
                no + 1, "教师${name + 1}号", age + 1, "女",
                Calendar.getInstance().apply {
                    set(1990, 9, 1)
                }, "教导处"
            )
            viewModel.addTeachers(teacher, teacher2)
        }

        viewModel.addTeacherResult.observe(requireActivity(), Observer {
            viewModel.queryAll()
        })

        viewModel.addTeachersResult.observe(requireActivity(), Observer {
            viewModel.queryAll()
        })

        viewModel.deleteTeachersResult.observe(requireActivity(), Observer {
            log("删除数据 $it")
            if (it > 0) {
                toast("删除${it}行数据")
            }
            viewModel.queryAll()
        })

        viewModel.teachersResult.observe(requireActivity(), Observer {
            if (it.isEmpty()) {
                toast("数据库为空")
            }
            mAdapter.update(it)
        })

        viewModel.queryAll()
    }

    companion object {
        val TEACHERS = arrayOf(
            Teacher(
                1, "妲己", 30, "男",
                Calendar.getInstance().apply {
                    set(1977, 9, 1)
                }, "计算机系"
            ),
            Teacher(2, "鲁班大师", 30, "女",
                Calendar.getInstance().apply {
                    set(1969, 3, 12)
                }
                , "数学系"),
            Teacher(3, "老夫子", 62, "男",
                Calendar.getInstance().apply {
                    set(1979, 4, 2)
                }
                , "美术系")
        )
    }

}