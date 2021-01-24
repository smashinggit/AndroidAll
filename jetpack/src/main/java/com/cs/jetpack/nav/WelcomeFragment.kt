package com.cs.jetpack.nav

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.cmcc.jetpack.R
import kotlinx.android.synthetic.main.fragment_welcome.*

class WelcomeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btnLogin.setOnClickListener {

            // 方式一：利用ID导航
            val navOptions = NavOptions.Builder()
                .build()

            val bundle = Bundle().apply {
                putString("name", "chensen")
            }

            findNavController().navigate(R.id.loginFragment, bundle, navOptions)
        }

        btnRegister.setOnClickListener {
            findNavController().navigate(R.id.registerFragment)
        }
    }
}