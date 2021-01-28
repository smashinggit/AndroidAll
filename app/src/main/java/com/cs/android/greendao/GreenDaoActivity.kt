package com.cs.android.greendao

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cs.android.R
import kotlinx.android.synthetic.main.activity_greendao.*

/**
 *
 * @author  ChenSen
 * @date  2021/1/28
 * @desc GreenDao的使用Demo
 **/
class GreenDaoActivity : AppCompatActivity() {

    var mAdapter = GoodAdapter()
   private var mData = arrayListOf<Good>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_greendao)

        recyclerView.adapter = mAdapter
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


        btnInsert.setOnClickListener {

        }

        btnQuery.setOnClickListener {

        }

        btnFruit.setOnClickListener {

        }

        btnCake.setOnClickListener {

        }

    }

    inner class GoodAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view =
                LayoutInflater.from(this@GreenDaoActivity)
                    .inflate(R.layout.item_greendao_good, parent, false)
            return object : RecyclerView.ViewHolder(view) {}
        }

        override fun getItemCount(): Int {
            return 10
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

//            holder.itemView.ivPic
        }

    }
}