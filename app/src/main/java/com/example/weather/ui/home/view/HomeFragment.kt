package com.example.weather.ui.home.view

import android.Manifest
import android.content.Intent
import android.location.Geocoder
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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather.*
import com.example.weather.data.database.ConcreteLocalSource
import com.example.weather.databinding.FragmentHomeBinding
import com.example.weather.model.Repository
import com.example.weather.model.pojo.FavoritePlace
import com.example.weather.model.pojo.Location
import com.example.weather.data.network.ApiClient
import com.example.weather.data.network.ApiState
import com.example.weather.data.shared_preferences.SettingsSharedPreferences
import com.example.weather.ui.home.viewmodel.HomeFragmentViewModel
import com.example.weather.ui.home.viewmodel.HomeFragmentViewModelFactory
import com.example.weather.ui.view.My_LOCATION_PERMISSION_ID
import com.example.weather.uitils.Constants
import com.example.weather.uitils.checkConnection
import com.example.weather.uitils.checkPermissions
import com.example.weather.uitils.isLocationEnabled
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
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
    private var favoritePlace: FavoritePlace? = null
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
        )[HomeFragmentViewModel::class.java]

        setHourlyRecycler()
        setDailyRecycler()

        fragmentHomeBinding.swipeRefresh.setOnRefreshListener {
            if (checkConnection(requireContext())) {
                if (locationOption == Constants.GPS) {
                    if (checkPermissions(requireContext())) {
                        if (isLocationEnabled(requireContext())) {
                            fragmentHomeBinding.permissionCv.visibility = View.GONE
                            fragmentHomeBinding.swipeRefresh.visibility = View.VISIBLE
                            if (favoritePlace != null) {
                                homeFragmentViewModel.getWeather(
                                    Location(favoritePlace!!.longitude, favoritePlace!!.latitude),
                                    homeFragmentViewModel.getTemperatureOption()!!,
                                    homeFragmentViewModel.getLanguageOption()!!
                                )
                                getWeatherFromDatabase()
                            } else {
                                getLocationAndWeather()
                            }
                        } else {
                            Snackbar.make(
                                requireActivity().findViewById(android.R.id.content),
                                resources.getString(R.string.turn_on_location),
                                Snackbar.LENGTH_SHORT
                            ).apply {
                                setAction(resources.getString(R.string.enable)) {
                                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                                        startActivity(this)
                                    }
                                }
                                show()
                            }
                        }
                    } else {
                        if (favoritePlace != null) {
                            homeFragmentViewModel.getWeather(
                                Location(favoritePlace!!.longitude, favoritePlace!!.latitude),
                                homeFragmentViewModel.getTemperatureOption()!!,
                                homeFragmentViewModel.getLanguageOption()!!
                            )
                            getWeatherFromDatabase()
                        }
                    }
                } else {
                    homeFragmentViewModel.getWeather(
                        Location(
                            homeFragmentViewModel.getLongitude(),
                            homeFragmentViewModel.getLatitude()
                        ),
                        homeFragmentViewModel.getTemperatureOption()!!,
                        homeFragmentViewModel.getLanguageOption()!!
                    )
                    getWeatherFromDatabase()
                }

            }
            fragmentHomeBinding.swipeRefresh.isRefreshing = false
        }

        favoritePlace = if (homeFragmentViewModel.getDetails()) {
            homeFragmentViewModel.setDetails(false)
            HomeFragmentArgs.fromBundle(requireArguments()).favorite
        } else {
            null
        }

        if (favoritePlace != null) {
            Timber.e(favoritePlace.toString())
            fragmentHomeBinding.homeTitleTv.text = resources.getText(R.string.favorites)
            val bottomNavigationView =
                requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
            if (bottomNavigationView != null) {
                bottomNavigationView.visibility = View.GONE
            }
        }

        locationOption = homeFragmentViewModel.getLocationOption()

        if (locationOption == Constants.GPS) {
            if (checkConnection(requireContext())) {
                fusedClient = LocationServices.getFusedLocationProviderClient(requireContext())
                if (checkPermissions(requireContext())) {
                    fragmentHomeBinding.permissionCv.visibility = View.GONE
                    fragmentHomeBinding.swipeRefresh.visibility = View.VISIBLE
                    if (favoritePlace != null) {
                        homeFragmentViewModel.getWeather(
                            Location(favoritePlace!!.longitude, favoritePlace!!.latitude),
                            homeFragmentViewModel.getTemperatureOption()!!,
                            homeFragmentViewModel.getLanguageOption()!!
                        )
                        getWeatherFromDatabase()
                    } else {
                        getLocationAndWeather()
                    }
                } else {
                    fragmentHomeBinding.permissionBtn.setOnClickListener {
                        requestPermissions()
                    }
                    fragmentHomeBinding.permissionCv.visibility = View.VISIBLE
                    fragmentHomeBinding.swipeRefresh.visibility = View.GONE
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.no_connection),
                    Toast.LENGTH_SHORT
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
                if (favoritePlace != null) {
                    homeFragmentViewModel.getWeather(
                        Location(favoritePlace!!.longitude, favoritePlace!!.latitude),
                        homeFragmentViewModel.getTemperatureOption()!!,
                        homeFragmentViewModel.getLanguageOption()!!
                    )
                } else {
                    homeFragmentViewModel.getWeather(
                        Location(
                            homeFragmentViewModel.getLongitude(),
                            homeFragmentViewModel.getLatitude()
                        ),
                        homeFragmentViewModel.getTemperatureOption()!!,
                        homeFragmentViewModel.getLanguageOption()!!
                    )
                }
                getWeatherFromDatabase()
            } else {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.no_connection),
                    Toast.LENGTH_SHORT
                ).show()
                getWeatherFromDatabase()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        if (checkPermissions(requireContext())) {
            fragmentHomeBinding.permissionCv.visibility = View.GONE
            fragmentHomeBinding.swipeRefresh.visibility = View.VISIBLE
        }
        val navController = findNavController()
        if (navController.currentDestination == navController.findDestination(R.id.mapsFragment)
        ) {
            navController.navigateUp()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
        if (bottomNavigationView != null) {
            bottomNavigationView.visibility = View.VISIBLE
        }
    }


    private fun getLastLocation() {
        if (checkPermissions(requireContext())) {
            if (isLocationEnabled(requireContext())) {
                lifecycleScope.launch(Dispatchers.IO) {
                    fusedClient = LocationServices.getFusedLocationProviderClient(requireContext())
                    homeFragmentViewModel.requestNewLocationData(fusedClient).collectLatest {
                        when (it) {
                            is ApiState.SuccessLocation -> it.location?.let { it1 ->
                                homeFragmentViewModel.setLatitude(it1.latitude)
                                homeFragmentViewModel.setLongitude(it1.longitude)
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
                    requireActivity().findViewById(android.R.id.content),
                    getString(R.string.turn_on_location),
                    Snackbar.LENGTH_SHORT
                ).apply {
                    setAction(getString(R.string.enable)) {
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
        getWeatherFromDatabase()
    }

    private fun getWeatherFromDatabase() {
        homeFragmentViewModel.getWeatherFromDatabase()
        lifecycleScope.launch {
            homeFragmentViewModel.offlineWeatherStateFlow.collectLatest { apiState ->
                when (apiState) {
                    is ApiState.SuccessOffline -> {
                        fragmentHomeBinding.weather = apiState.data
                        val geocoderArr =
                            Geocoder(requireContext()).getFromLocation(
                                apiState.data?.lat ?: 0.0, apiState.data?.lon ?: 0.0, 5
                            )

                        var place = if (geocoderArr.isNullOrEmpty()) {
                            apiState.data?.timezone
                        } else {
                            ("${geocoderArr[0]?.locality ?: ""}-" +
                                    "${geocoderArr[0]?.adminArea ?: ""}-" +
                                    (geocoderArr[0]?.countryName ?: ""))
                        }
                        if (place != null && place.startsWith('-')) {
                            place = place.substring(1)
                        }
                        if (place != null && place.endsWith('-')) {
                            place = place.substring(0, place.length - 1)
                        }
                        hourlyAdapter.submitList(apiState.data?.hourly)
                        dailyAdapter.submitList(apiState.data?.daily)
                        fragmentHomeBinding.placeTv.text = place
                        fragmentHomeBinding.homeProgressBar.visibility = View.GONE

                    }
                    else -> {
                        fragmentHomeBinding.homeProgressBar.visibility = View.VISIBLE
                    }
                }
            }
        }
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