package com.example.weather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.weather.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

lateinit var activityMainBinding: ActivityMainBinding
lateinit var navController: NavController
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        navController = Navigation.findNavController(this , R.id.nav_host)
        NavigationUI.setupWithNavController(activityMainBinding.bottomNavigation , navController)
    }
}