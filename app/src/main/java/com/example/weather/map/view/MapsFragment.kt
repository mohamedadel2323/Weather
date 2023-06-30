package com.example.weather.map.view

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.weather.R
import com.example.weather.database.ConcreteLocalSource
import com.example.weather.databinding.CustomSaveDialogBinding
import com.example.weather.databinding.FragmentMapsBinding
import com.example.weather.map.viewmodel.MapsFragmentViewModelFactory
import com.example.weather.map.viewmodel.MapsFragmentsViewModel
import com.example.weather.model.Repository
import com.example.weather.network.ApiClient
import com.example.weather.shared_preferences.SettingsSharedPreferences
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import timber.log.Timber

class MapsFragment : Fragment() {
    private lateinit var fragmentMapsBinding: FragmentMapsBinding
    lateinit var mapsFragmentsViewModel: MapsFragmentsViewModel
    lateinit var mapsFragmentViewModelFactory: MapsFragmentViewModelFactory
    private var locationLatLng: LatLng? = null
    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
//        val sydney = LatLng(-34.0, 151.0)
//        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        setMapClick(googleMap)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentMapsBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_maps, container, false)
        return fragmentMapsBinding.root
    }

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
    }


    override fun onDestroyView() {
        super.onDestroyView()
        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
        if (bottomNavigationView != null) {
            bottomNavigationView.visibility = View.VISIBLE
        }
    }

    private fun setMapClick(map: GoogleMap) {
        map.apply {
            setOnMapClickListener { latLng ->
                map.clear()
                locationLatLng = latLng
                addMarker(MarkerOptions().position(latLng))
                Timber.e(latLng.toString())
                moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
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
            }
        }

    }


    private fun showDialog(geocoderArr: MutableList<Address>?) {

        val customSaveDialogBinding = CustomSaveDialogBinding.inflate(layoutInflater)

        AlertDialog.Builder(requireContext()).create().apply {
            setView(customSaveDialogBinding.root)
            customSaveDialogBinding.placeTv.text = geocoderArr?.get(0)?.countryName ?: "Unknown"
            customSaveDialogBinding.detailsTv.text =
                "${geocoderArr?.get(0)?.adminArea}-${geocoderArr?.get(0)?.subAdminArea}-${geocoderArr?.get(0)?.locality}"
                    ?: "Unknown"

            customSaveDialogBinding.saveBtn.setOnClickListener {
                Timber.e(locationLatLng.toString())
                mapsFragmentsViewModel.setLatitude(locationLatLng!!.latitude)
                mapsFragmentsViewModel.setLongitude(locationLatLng!!.longitude)
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