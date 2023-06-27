package com.example.weather.home.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.weather.Constants
import com.example.weather.R
import com.example.weather.checkPermissions
import com.example.weather.database.ConcreteLocalSource
import com.example.weather.databinding.FragmentHomeBinding
import com.example.weather.home.viewmodel.HomeFragmentViewModel
import com.example.weather.home.viewmodel.HomeFragmentViewModelFactory
import com.example.weather.isLocationEnabled
import com.example.weather.model.Repository
import com.example.weather.network.ApiClient
import com.example.weather.network.ApiState
import com.example.weather.shared_preferences.SettingsSharedPreferences
import com.example.weather.view.My_LOCATION_PERMISSION_ID
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber


class HomeFragment : Fragment() {
    lateinit var fragmentHomeBinding: FragmentHomeBinding
    private lateinit var fusedClient: FusedLocationProviderClient
    lateinit var homeFragmentViewModel: HomeFragmentViewModel
    lateinit var homeFragmentViewModelFactory: HomeFragmentViewModelFactory
    private var locationOption = Constants.GPS
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentHomeBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return fragmentHomeBinding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeFragmentViewModelFactory = HomeFragmentViewModelFactory(
            Repository.getInstance(
                SettingsSharedPreferences.getInstance(requireContext()),
                ApiClient,
                ConcreteLocalSource(requireContext())
            )
        )
        homeFragmentViewModel = ViewModelProvider(
            this,
            homeFragmentViewModelFactory
        ).get(HomeFragmentViewModel::class.java)
        locationOption = homeFragmentViewModel.getLocationOption()

        if (locationOption == Constants.GPS) {
            if (checkConnection(requireContext())) {
                fusedClient = LocationServices.getFusedLocationProviderClient(requireContext())
                if (checkPermissions(requireContext())) {
                    fragmentHomeBinding.permissionCv.visibility = View.GONE
                    fragmentHomeBinding.homeScrollView.visibility = View.VISIBLE

                    getLocationAndWeather()

                } else {
                    fragmentHomeBinding.permissionBtn.setOnClickListener {
                        requestPermissions()
                    }
                    fragmentHomeBinding.permissionCv.visibility = View.VISIBLE
                    fragmentHomeBinding.homeScrollView.visibility = View.GONE
                }
            } else {
                homeFragmentViewModel.getWeatherFromDatabase()
                lifecycleScope.launch {
                    homeFragmentViewModel.offlineWeatherStateFlow.collectLatest { apiState ->
                        when (apiState) {
                            is ApiState.SuccessOffline -> {
                                fragmentHomeBinding.placeTv.text =
                                    apiState.data?.timezone?.let {
                                        it.toString().split("/").last()
                                    } ?: "Unknown"
                                fragmentHomeBinding.homeProgressBar.visibility = View.GONE

                            }
                            else -> {
                                fragmentHomeBinding.homeProgressBar.visibility = View.VISIBLE
                            }
                        }
                    }
                }

            }
        } else if (locationOption == Constants.MAP) {
            Toast.makeText(requireContext(), "Map", Toast.LENGTH_SHORT).show()

        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        if (checkConnection(requireContext())) {
            if (locationOption == Constants.GPS) {
                if (checkPermissions(requireContext())) {
                    fragmentHomeBinding.permissionCv.visibility = View.GONE
                    fragmentHomeBinding.homeScrollView.visibility = View.VISIBLE
                    getLocationAndWeather()
                }
            } else if (locationOption == Constants.MAP) {
                Toast.makeText(requireContext(), "Map", Toast.LENGTH_SHORT).show()
            }
        }
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        if (requestCode == My_LOCATION_PERMISSION_ID) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                getLastLocation()
//            }
//        }
//    }

    private fun getLastLocation() {
        if (checkPermissions(requireContext())) {
            if (isLocationEnabled(requireContext())) {
                lifecycleScope.launch {
                    homeFragmentViewModel.requestNewLocationData(fusedClient).collectLatest {
                        withContext(Dispatchers.IO) {
                            Timber.e(it.toString())
                            homeFragmentViewModel.getWeather(it)
                        }
                    }
                }
            } else {
                Snackbar.make(
                    fragmentHomeBinding.root,
                    "Turn on location please",
                    Toast.LENGTH_SHORT
                ).apply {
                    setAction("Enable") {
                        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                            startActivity(this)
                        }
                    }
                    show()
                }
            }
        } else {
            requestPermissions()
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            My_LOCATION_PERMISSION_ID
        )
    }

    private fun getLocationAndWeather() {
        getLastLocation()

        lifecycleScope.launch {
            homeFragmentViewModel.weatherResponseStateFlow.collectLatest {
                when (it) {
                    is ApiState.Success -> {
                        fragmentHomeBinding.placeTv.text =
                            it.data?.timezone.toString().split("/").last()
                        Timber.e(it.data?.timezone.toString())
                        fragmentHomeBinding.homeProgressBar.visibility = View.GONE
                    }
                    is ApiState.Failure -> {
                        Timber.e(it.msg )
                        fragmentHomeBinding.placeTv.text = "Unknown"
                        fragmentHomeBinding.homeProgressBar.visibility = View.GONE
                    }
                    else -> {
                        fragmentHomeBinding.homeProgressBar.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkConnection(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return networkCapabilities != null && networkCapabilities.hasCapability(
            NetworkCapabilities.NET_CAPABILITY_INTERNET
        )
    }
}