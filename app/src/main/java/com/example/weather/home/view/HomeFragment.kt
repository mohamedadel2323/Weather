package com.example.weather.home.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.example.weather.R
import com.example.weather.checkPermissions
import com.example.weather.databinding.FragmentHomeBinding
import com.example.weather.view.My_LOCATION_PERMISSION_ID
import timber.log.Timber

lateinit var fragmentHomeBinding: FragmentHomeBinding

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentHomeBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return fragmentHomeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (checkPermissions(requireContext())) {
            fragmentHomeBinding.permissionCv.visibility = View.GONE
            fragmentHomeBinding.homeScrollView.visibility = View.VISIBLE
        } else {
            fragmentHomeBinding.permissionBtn.setOnClickListener {
                requestPermissions()
            }
            fragmentHomeBinding.permissionCv.visibility = View.VISIBLE
            fragmentHomeBinding.homeScrollView.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        if (checkPermissions(requireContext())) {
            fragmentHomeBinding.permissionCv.visibility = View.GONE
            fragmentHomeBinding.homeScrollView.visibility = View.VISIBLE
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


}