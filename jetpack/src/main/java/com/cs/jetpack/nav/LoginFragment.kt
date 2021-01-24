package com.cs.jetpack.nav

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cmcc.jetpack.R
import com.cs.common.utils.log
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment() {
    private var name: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log("LoginFragment onCreate")

        arguments?.let {
            name = it.getString("name") ?: ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        tvName.text = name

        btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

}