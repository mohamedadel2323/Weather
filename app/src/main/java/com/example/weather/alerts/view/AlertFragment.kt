package com.example.weather.alerts.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.weather.R
import com.example.weather.databinding.FragmentAlertBinding

lateinit var fragmentAlertBinding: FragmentAlertBinding

class AlertFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentAlertBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_alert, container, false)
        return fragmentAlertBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}