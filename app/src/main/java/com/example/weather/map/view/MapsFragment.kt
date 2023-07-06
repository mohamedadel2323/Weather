package com.example.weather.map.view

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.weather.BuildConfig
import com.example.weather.R
import com.example.weather.checkConnection
import com.example.weather.database.ConcreteLocalSource
import com.example.weather.databinding.CustomSaveDialogBinding
import com.example.weather.databinding.FragmentMapsBinding
import com.example.weather.map.viewmodel.MapsFragmentViewModelFactory
import com.example.weather.map.viewmodel.MapsFragmentsViewModel
import com.example.weather.model.Repository
import com.example.weather.model.pojo.FavoritePlace
import com.example.weather.network.ApiClient
import com.example.weather.shared_preferences.SettingsSharedPreferences
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.bottomnavigation.BottomNavigationView
import timber.log.Timber

class MapsFragment : Fragment() {
    private lateinit var fragmentMapsBinding: FragmentMapsBinding
    lateinit var mapsFragmentsViewModel: MapsFragmentsViewModel
    lateinit var mapsFragmentViewModelFactory: MapsFragmentViewModelFactory
    private var locationLatLng: LatLng? = null
    private lateinit var map: GoogleMap

    @RequiresApi(Build.VERSION_CODES.M)
    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        setMapClick(googleMap)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentMapsBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_maps, container, false)
        return fragmentMapsBinding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        mapsFragmentViewModelFactory = MapsFragmentViewModelFactory(
            (Repository.getInstance(
                SettingsSharedPreferences.getInstance(requireContext()),
                ApiClient,
                ConcreteLocalSource(requireContext())
            ))
        )

        mapsFragmentsViewModel = ViewModelProvider(
            this,
            mapsFragmentViewModelFactory
        ).get(MapsFragmentsViewModel::class.java)

        // Initialize the SDK
        if (!Places.isInitialized())
            Places.initialize(requireActivity().applicationContext, BuildConfig.API_KEY)
        Places.createClient(requireContext())

        Timber.e(Places.isInitialized().toString())

        val startAutocomplete =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data
                    if (intent != null) {
                        val place = Autocomplete.getPlaceFromIntent(intent)
                        Timber.e(
                            "Place: ${place.name}, ${place.id}, ${place.latLng}"
                        )
                        showGooglePlaceDialog(place)
                    }
                } else if (result.resultCode == Activity.RESULT_CANCELED) {
                    // The user canceled the operation.
                    Timber.e("User canceled autocomplete")
                } else {
                    Timber.e(result.resultCode.toString())
                }
            }

        fragmentMapsBinding.searchIcon.setOnClickListener {
            try {
                val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
                // Start the autocomplete intent.
                val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                    .setTypeFilter(TypeFilter.CITIES)
                    .build(requireContext())
                startAutocomplete.launch(intent)
            } catch (ex: Exception) {
                Timber.e(ex.localizedMessage)
            }

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()

        mapsFragmentsViewModel.setMapFavorite(false)

        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
        if (bottomNavigationView != null) {
            bottomNavigationView.visibility = View.VISIBLE
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setMapClick(map: GoogleMap) {
        map.apply {
            setOnMapClickListener { latLng ->
                map.clear()
                locationLatLng = latLng
                addMarker(MarkerOptions().position(latLng))
                Timber.e(latLng.toString())
                moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                if (checkConnection(requireContext())) {
                    val geocoderArr =
                        Geocoder(requireContext()).getFromLocation(
                            locationLatLng!!.latitude, locationLatLng!!.longitude, 5
                        )
                    geocoderArr?.let {
                        if (geocoderArr.isNotEmpty()) {
                            Timber.e(it[0].countryName)
                        } else {
                            Timber.e("empty")
                        }
                    }
                    showDialog(geocoderArr)
                } else {
                    Toast.makeText(requireContext(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show()
                    Navigation.findNavController(requireView()).navigateUp()
                }
            }
        }
    }

    private fun showGooglePlaceDialog(place: Place) {
        map.addMarker(MarkerOptions().position(place.latLng))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(place.latLng, 10f))
        val customSaveDialogBinding = CustomSaveDialogBinding.inflate(layoutInflater)

        AlertDialog.Builder(requireContext()).create().apply {
            setView(customSaveDialogBinding.root)

            customSaveDialogBinding.placeTv.text = place.name
            customSaveDialogBinding.saveBtn.setOnClickListener {
                //if user opened map fragment from favorites fragment
                if (mapsFragmentsViewModel.getMapFavorite()) {
                    mapsFragmentsViewModel.addFavoritePlace(
                        FavoritePlace(
                            placeName = place.name,
                            latitude = place.latLng.latitude,
                            longitude = place.latLng.longitude
                        )
                    )
                } else {
                    mapsFragmentsViewModel.setLatitude(locationLatLng!!.latitude)
                    mapsFragmentsViewModel.setLongitude(locationLatLng!!.longitude)
                }
                dismiss()
                findNavController().navigateUp()
            }
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            window?.setGravity(Gravity.BOTTOM)
            show()
        }
    }

    private fun showDialog(geocoderArr: MutableList<Address>?) {

        val customSaveDialogBinding = CustomSaveDialogBinding.inflate(layoutInflater)

        AlertDialog.Builder(requireContext()).create().apply {
            setView(customSaveDialogBinding.root)
            if (geocoderArr.isNullOrEmpty()) {
                customSaveDialogBinding.placeTv.text = getString(R.string.pick_vaild_place)
                customSaveDialogBinding.saveBtn.visibility = View.GONE
            } else {
                customSaveDialogBinding.placeTv.text = geocoderArr?.get(0)?.countryName ?: ""
                customSaveDialogBinding.detailsTv.text =
                    ("${geocoderArr?.get(0)?.adminArea ?: ""}-" +
                            "${geocoderArr?.get(0)?.subAdminArea ?: ""}-" +
                            (geocoderArr?.get(0)?.locality ?: ""))
                        ?: "Unknown"
            }

            customSaveDialogBinding.saveBtn.setOnClickListener {
                //if user opened map fragment from favorites fragment
                if (mapsFragmentsViewModel.getMapFavorite()) {
                    var placeName = geocoderArr?.get(0)?.locality ?: ""
                    if (placeName.isEmpty()) {
                        placeName = geocoderArr?.get(0)?.subAdminArea ?: ""
                    }
                    if (placeName.isEmpty()) {
                        placeName = geocoderArr?.get(0)?.countryName ?: ""
                    }

                    mapsFragmentsViewModel.addFavoritePlace(
                        FavoritePlace(
                            placeName = placeName,
                            latitude = locationLatLng!!.latitude,
                            longitude = locationLatLng!!.longitude
                        )
                    )


                } else {
                    mapsFragmentsViewModel.setLatitude(locationLatLng!!.latitude)
                    mapsFragmentsViewModel.setLongitude(locationLatLng!!.longitude)
                }
                dismiss()
                findNavController().navigateUp()
            }
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            window?.setGravity(Gravity.BOTTOM)
            show()
        }
    }
}