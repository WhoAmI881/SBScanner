package com.example.sbscanner.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.sbscanner.App
import com.example.sbscanner.R
import com.example.sbscanner.databinding.ActivityMainBinding
import com.example.sbscanner.presentation.navigation.Screens
import com.github.terrakok.cicerone.Command
import com.github.terrakok.cicerone.Replace
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.github.terrakok.cicerone.androidx.FragmentScreen

class MainActivity : AppCompatActivity() {

    private val navigatorHolder = App.INSTANCE.navigatorHolder
    private val navigator: AppNavigator = object : AppNavigator(this, R.id.main_container) {
        override fun setupFragmentTransaction(
            screen: FragmentScreen,
            fragmentTransaction: FragmentTransaction,
            currentFragment: Fragment?,
            nextFragment: Fragment
        ) {
        }
    }
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        if (savedInstanceState == null) {
            navigator.applyCommands(arrayOf<Command>(Replace(Screens.Start())))
        }
    }


    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }
}
