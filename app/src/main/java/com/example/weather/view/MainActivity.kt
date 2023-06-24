package com.example.weather.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.weather.R
import com.example.weather.checkPermissions
import com.example.weather.databinding.ActivityMainBinding
import com.example.weather.isLocationEnabled
import com.example.weather.viewmodel.MainActivityViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

const val My_LOCATION_PERMISSION_ID = 5005

class MainActivity : AppCompatActivity() {

    lateinit var navController: NavController
    lateinit var activityMainBinding: ActivityMainBinding
    lateinit var mainActivityViewModel: MainActivityViewModel
    private lateinit var fusedClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        navController = Navigation.findNavController(this, R.id.nav_host)
        NavigationUI.setupWithNavController(activityMainBinding.bottomNavigation, navController)
        fusedClient = LocationServices.getFusedLocationProviderClient(this)
        mainActivityViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        getLastLocation()
    }

    private fun getLastLocation() {
        if (checkPermissions(this)) {
            if (isLocationEnabled(this)) {
                lifecycleScope.launch {
                    mainActivityViewModel.requestNewLocationData(fusedClient).collectLatest {
                        Timber.e(it.toString())
                    }
                }
            } else {
                Toast.makeText(this, "Turn on location please", Toast.LENGTH_SHORT).show()
                Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                    startActivity(this)
                }
            }

        } else {
            requestPermissions()
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
        if (requestCode == My_LOCATION_PERMISSION_ID) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            }
        }
    }
}