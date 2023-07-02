package com.example.weather.home.view

import android.Manifest
import android.content.Context
import android.content.Intent
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
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather.*
import com.example.weather.database.ConcreteLocalSource
import com.example.weather.databinding.FragmentHomeBinding
import com.example.weather.home.viewmodel.HomeFragmentViewModel
import com.example.weather.home.viewmodel.HomeFragmentViewModelFactory
import com.example.weather.model.Repository
import com.example.weather.model.pojo.Location
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
    private lateinit var fragmentHomeBinding: FragmentHomeBinding
    private lateinit var fusedClient: FusedLocationProviderClient
    private lateinit var homeFragmentViewModel: HomeFragmentViewModel
    private lateinit var homeFragmentViewModelFactory: HomeFragmentViewModelFactory
    private lateinit var dailyAdapter: DailyAdapter
    private lateinit var hourlyAdapter: HourlyAdapter
    private var locationOption = Constants.GPS
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        setHourlyRecycler()
        setDailyRecycler()

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
                Snackbar.make(
                    requireView(),
                    "No Connection",
                    Snackbar.LENGTH_SHORT
                ).show()
                getWeatherFromDatabase()
            }
        } else if (locationOption == Constants.MAP) {


            if (checkConnection(requireContext())) {
                if (homeFragmentViewModel.getMapFirstTime()) {
                    homeFragmentViewModel.setMapFirstTime()
                    Navigation.findNavController(fragmentHomeBinding.root)
                        .navigate(HomeFragmentDirections.actionHomeFragmentToMapsFragment())
                }
                homeFragmentViewModel.getWeather(
                    Location(
                        homeFragmentViewModel.getLongitude(),
                        homeFragmentViewModel.getLatitude()
                    ),
                    homeFragmentViewModel.getTemperatureOption()!!,
                    homeFragmentViewModel.getLanguageOption()!!
                )
                getWeather()
            } else {
                Toast.makeText(requireContext(), "No Connection", Toast.LENGTH_SHORT).show()
                getWeatherFromDatabase()
            }
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
            }
        }
    }


    private fun getLastLocation() {
        if (checkPermissions(requireContext())) {
            if (isLocationEnabled(requireContext())) {
                lifecycleScope.launch(Dispatchers.IO) {
                    homeFragmentViewModel.requestNewLocationData(fusedClient).collectLatest {
                        when (it) {
                            is ApiState.SuccessLocation -> it.location?.let { it1 ->
                                homeFragmentViewModel.getWeather(
                                    it1,
                                    homeFragmentViewModel.getTemperatureOption()!!,
                                    homeFragmentViewModel.getLanguageOption()!!
                                )
                                withContext(Dispatchers.Main) {
                                    fragmentHomeBinding.homeProgressBar.visibility =
                                        View.GONE
                                }
                            }
                            else -> {
                                withContext(Dispatchers.Main) {
                                    fragmentHomeBinding.homeProgressBar.visibility =
                                        View.VISIBLE
                                }
                            }
                        }

                    }
                }
            } else {
                Snackbar.make(
                    requireView(),
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
        getWeather()

    }

    private fun getWeather() {
        lifecycleScope.launch {
            homeFragmentViewModel.weatherResponseStateFlow.collectLatest {
                when (it) {
                    is ApiState.Success -> {
                        fragmentHomeBinding.weather = it.data?.toWeatherResponseEntity()
                        hourlyAdapter.submitList(it.data?.hourly)
                        dailyAdapter.submitList(it.data?.daily)
                        fragmentHomeBinding.placeTv.text =
                            it.data?.timezone
                        Timber.e(it.data?.timezone.toString())
                        fragmentHomeBinding.homeProgressBar.visibility = View.GONE
                    }
                    is ApiState.Failure -> {
                        Timber.e(it.msg)
                        fragmentHomeBinding.placeTv.text = resources.getText(R.string.unknown)
                        fragmentHomeBinding.homeProgressBar.visibility = View.GONE
                    }
                    else -> {
                        fragmentHomeBinding.homeProgressBar.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun getWeatherFromDatabase() {
        homeFragmentViewModel.getWeatherFromDatabase()
        lifecycleScope.launch {
            homeFragmentViewModel.offlineWeatherStateFlow.collectLatest { apiState ->
                when (apiState) {
                    is ApiState.SuccessOffline -> {
                        fragmentHomeBinding.weather = apiState.data
                        hourlyAdapter.submitList(apiState.data?.hourly)
                        dailyAdapter.submitList(apiState.data?.daily)
                        fragmentHomeBinding.placeTv.text =
                            apiState.data?.timezone ?: "Unknown"
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

    private fun setDailyRecycler() {
        dailyAdapter = DailyAdapter()
        val dailyLayoutManager = LinearLayoutManager(requireContext())
        dailyLayoutManager.orientation = LinearLayoutManager.VERTICAL
        fragmentHomeBinding.nextForecastRv.apply {
            adapter = dailyAdapter
            layoutManager = dailyLayoutManager
        }
    }

    private fun setHourlyRecycler() {
        hourlyAdapter = HourlyAdapter()
        val hourlyLayoutManager = LinearLayoutManager(requireContext())
        hourlyLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        fragmentHomeBinding.hourlyDegreeRv.apply {
            adapter = hourlyAdapter
            layoutManager = hourlyLayoutManager
        }
    }
}