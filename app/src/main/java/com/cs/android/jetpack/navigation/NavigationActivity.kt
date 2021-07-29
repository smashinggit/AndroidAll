package com.cs.android.jetpack.navigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.cs.android.R
import com.cs.android.databinding.ActivityNavigationBinding

/**
 * @author ChenSen
 * @since 2021/7/23 10:59
 * @desc
 */
class NavigationActivity : AppCompatActivity() {

    lateinit var binding: ActivityNavigationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNavigationBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val navigationController = findNavController(R.id.navHostFragment)

        binding.navigationView.setupWithNavController(navigationController)
    }
}