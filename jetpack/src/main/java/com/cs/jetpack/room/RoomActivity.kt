package com.cs.jetpack.room

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.cmcc.jetpack.R
import com.cs.common.base.BaseActivity
import com.cs.jetpack.room.viewmodel.StudentViewModel
import kotlinx.android.synthetic.main.activity_room.*

class RoomActivity : BaseActivity() {
    private val viewModel by lazy {
        ViewModelProvider(this).get(StudentViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)

        setSupportActionBar(toolBar)
        toolBar.title = "学生表"

        viewModel.loading.observe(this, Observer {
            if (it) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        })
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.room_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.menuStudent -> {
                findNavController(R.id.nav_host_fragment_container).navigate(R.id.studentFragment)
                toolBar.title = "学生表"
            }

            R.id.menuTeacher -> {
                findNavController(R.id.nav_host_fragment_container).navigate(R.id.teacherFragment)
                toolBar.title = "教师表"
            }

            R.id.menuCourse -> {

            }

            R.id.menuScore -> {

            }
        }

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment_container).navigateUp()
    }

}