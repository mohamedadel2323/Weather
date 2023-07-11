package com.example.weather.ui.settings.view


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.weather.uitils.Constants
import com.example.weather.R
import com.example.weather.data.database.ConcreteLocalSource
import com.example.weather.databinding.FragmentSettingsBinding
import com.example.weather.model.Repository
import com.example.weather.data.network.ApiClient
import com.example.weather.ui.settings.viewmodel.SettingsFragmentViewModel
import com.example.weather.ui.settings.viewmodel.SettingsFragmentViewModelFactory
import com.example.weather.data.shared_preferences.SettingsSharedPreferences
import java.util.*


class SettingsFragment : Fragment() {

    private lateinit var fragmentSettingsBinding: FragmentSettingsBinding
    private lateinit var settingsFragmentViewModel: SettingsFragmentViewModel
    private lateinit var settingsFragmentViewModelFactory: SettingsFragmentViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        fragmentSettingsBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        return fragmentSettingsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsFragmentViewModelFactory = SettingsFragmentViewModelFactory(
            Repository.getInstance(
                SettingsSharedPreferences.getInstance(requireContext()),
                ApiClient,
                ConcreteLocalSource(requireContext())
            )
        )
        settingsFragmentViewModel = ViewModelProvider(
            this,
            settingsFragmentViewModelFactory
        )[SettingsFragmentViewModel::class.java]

        setCurrentSettings()
        settingsClickListeners()
    }

    private fun setCurrentSettings() {
        when (settingsFragmentViewModel.getLocationOption()) {
            Constants.GPS -> fragmentSettingsBinding.settingsLocationRg.check(R.id.gpsRb)
            else -> fragmentSettingsBinding.settingsLocationRg.check(R.id.mapRb)
        }

        when (settingsFragmentViewModel.getLanguageOption()) {
            Constants.ENGLISH -> fragmentSettingsBinding.settingsLanguageRg.check(R.id.englishRb)
            else -> fragmentSettingsBinding.settingsLanguageRg.check(R.id.arabicRb)
        }

        when (settingsFragmentViewModel.getUnitOption()) {
            Constants.METRIC, Constants.STANDARD -> fragmentSettingsBinding.settingsWindRg.check(
                R.id.meterPerSecondRb
            )
            else -> fragmentSettingsBinding.settingsWindRg.check(R.id.milePerHourRb)
        }

        when (settingsFragmentViewModel.getTemperatureOption()) {
            Constants.METRIC -> fragmentSettingsBinding.settingsTemperatureRg.check(R.id.celsiusRb)
            Constants.IMPERIAL -> fragmentSettingsBinding.settingsTemperatureRg.check(R.id.fahrenheitRb)
            else -> fragmentSettingsBinding.settingsTemperatureRg.check(R.id.kelvinRb)
        }

        when (settingsFragmentViewModel.getNotificationOption()) {
            true -> fragmentSettingsBinding.settingsNotificationRg.check(R.id.enableRb)
            else -> fragmentSettingsBinding.settingsNotificationRg.check(R.id.disableRb)
        }
    }

    private fun settingsClickListeners() {
        fragmentSettingsBinding.settingsLocationRg.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.gpsRb -> settingsFragmentViewModel.setLocationOption(Constants.GPS)
                else -> {
                    settingsFragmentViewModel.setLocationOption(Constants.MAP)
                    Navigation.findNavController(requireView())
                        .navigate(SettingsFragmentDirections.actionSettingsFragmentToMapsFragment())
                }
            }
        }

        fragmentSettingsBinding.settingsLanguageRg.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.englishRb -> {
                    settingsFragmentViewModel.setLanguageOption(Constants.ENGLISH)
                    updateLocal(Locale("en"))
                }
                else -> {
                    updateLocal(Locale("ar"))
                    settingsFragmentViewModel.setLanguageOption(Constants.ARABIC)
                }
            }
        }
        fragmentSettingsBinding.settingsTemperatureRg.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.celsiusRb -> settingsFragmentViewModel.setTemperatureOption(Constants.METRIC)
                R.id.fahrenheitRb -> settingsFragmentViewModel.setTemperatureOption(Constants.IMPERIAL)
                else -> settingsFragmentViewModel.setTemperatureOption(Constants.STANDARD)
            }
        }
        fragmentSettingsBinding.settingsWindRg.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.meterPerSecondRb -> {
                    when (settingsFragmentViewModel.getTemperatureOption()) {
                        Constants.STANDARD -> settingsFragmentViewModel.setUnitOption(Constants.STANDARD)
                        Constants.IMPERIAL -> {
                            settingsFragmentViewModel.setUnitOption(Constants.METRIC)
                            settingsFragmentViewModel.setTemperatureOption(Constants.METRIC)
                            setCurrentSettings()
                        }
                        else -> settingsFragmentViewModel.setUnitOption(Constants.METRIC)
                    }
                }
                else -> {
                    settingsFragmentViewModel.setUnitOption(Constants.IMPERIAL)
                    settingsFragmentViewModel.setTemperatureOption(Constants.IMPERIAL)
                    setCurrentSettings()
                }
            }
        }
        fragmentSettingsBinding.settingsNotificationRg.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.enableRb -> settingsFragmentViewModel.setNotificationOption(true)
                else -> settingsFragmentViewModel.setNotificationOption(false)
            }
        }
    }

    private fun updateLocal(locale: Locale){
        Locale.setDefault(locale)
        requireActivity().recreate()
    }
}