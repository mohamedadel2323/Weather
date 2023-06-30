package com.example.weather.view

import android.Manifest
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.weather.Constants
import com.example.weather.R
import com.example.weather.database.ConcreteLocalSource
import com.example.weather.databinding.ActivityMainBinding
import com.example.weather.databinding.CustomSettingsDialogBinding
import com.example.weather.model.Repository
import com.example.weather.network.ApiClient
import com.example.weather.shared_preferences.SettingsSharedPreferences
import com.example.weather.viewmodel.MainActivityViewModel
import com.example.weather.viewmodel.MainActivityViewModelFactory

const val My_LOCATION_PERMISSION_ID = 5005

class MainActivity : AppCompatActivity() {
    lateinit var navController: NavController
    lateinit var activityMainBinding: ActivityMainBinding
    lateinit var mainActivityViewModel: MainActivityViewModel
    lateinit var mainActivityViewModelFactory: MainActivityViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        mainActivityViewModelFactory = MainActivityViewModelFactory(
            (Repository.getInstance(
                SettingsSharedPreferences.getInstance(this), ApiClient, ConcreteLocalSource(this)
            ))
        )
        mainActivityViewModel = ViewModelProvider(
            this,
            mainActivityViewModelFactory
        ).get(MainActivityViewModel::class.java)

        if (mainActivityViewModel.getFirstTime()) {
            showDialog()
        } else {
            activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
            navController = Navigation.findNavController(this, R.id.nav_host)
            NavigationUI.setupWithNavController(activityMainBinding.bottomNavigation, navController)
            if (navController.currentDestination == navController.findDestination(R.id.mapsFragment)
            ) {
                activityMainBinding.bottomNavigation.visibility = View.GONE
            }
        }
    }


    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            My_LOCATION_PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Pass the result to the fragment
        val fragment = supportFragmentManager.findFragmentById(R.id.nav_host)
        fragment?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    private fun showDialog() {

        val settingsDialogBinding = CustomSettingsDialogBinding.inflate(layoutInflater)

        AlertDialog.Builder(this).create().apply {
            setView(settingsDialogBinding.root)

            settingsDialogBinding.settingsRg.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.settingsGpsRb -> mainActivityViewModel.setLocationOption(Constants.GPS)
                    R.id.settingsLocationRb -> mainActivityViewModel.setLocationOption(Constants.MAP)
                }
            }
            settingsDialogBinding.notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    mainActivityViewModel.setNotificationOption(true)
                } else {
                    mainActivityViewModel.setNotificationOption(false)
                }
            }

            settingsDialogBinding.settingsOkBtn.setOnClickListener {
                activityMainBinding =
                    DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)
                navController = Navigation.findNavController(this@MainActivity, R.id.nav_host)
                NavigationUI.setupWithNavController(
                    activityMainBinding.bottomNavigation,
                    navController
                )
                if (navController.currentDestination == navController.findDestination(R.id.mapsFragment)
                ) {
                    activityMainBinding.bottomNavigation.visibility = View.GONE
                }
                mainActivityViewModel.setFirstTime()
                dismiss()
            }
            setCancelable(false)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
        }
    }
}