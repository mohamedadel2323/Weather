package com.example.weather.alerts.view

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.weather.Constants
import com.example.weather.R
import com.example.weather.alerts.AlertWorker
import com.example.weather.alerts.viewmodel.AlertsViewModel
import com.example.weather.alerts.viewmodel.AlertsViewModelFactory
import com.example.weather.database.ConcreteLocalSource
import com.example.weather.databinding.CustomAlertDialogBinding
import com.example.weather.databinding.FragmentAlertBinding
import com.example.weather.model.Repository
import com.example.weather.model.pojo.AlertEntity
import com.example.weather.network.ApiClient
import com.example.weather.shared_preferences.SettingsSharedPreferences
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


class AlertFragment : Fragment() {
    private var startCalender: Calendar? = null
    private var endCalender: Calendar? = null
    lateinit var fragmentAlertBinding: FragmentAlertBinding
    lateinit var alertsViewModel: AlertsViewModel
    lateinit var alertsViewModelFactory: AlertsViewModelFactory
    lateinit var alertAdapter: AlertAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        fragmentAlertBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_alert, container, false)
        return fragmentAlertBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        alertAdapter = AlertAdapter { deleteOnLongClick(it) }
        setRecycler()
        alertsViewModelFactory = AlertsViewModelFactory(
            Repository.getInstance(
                SettingsSharedPreferences.getInstance(requireContext()),
                ApiClient,
                ConcreteLocalSource(requireContext())
            )
        )
        alertsViewModel =
            ViewModelProvider(this, alertsViewModelFactory).get(AlertsViewModel::class.java)

        alertsViewModel.getAllAlerts()
        lifecycleScope.launch {
            alertsViewModel.alertsStateFlow.collectLatest { alertList ->
                if (alertList.isNotEmpty()) {
                    fragmentAlertBinding.noAlertsLa.apply {
                        pauseAnimation()
                        visibility = View.GONE
                    }

                }
                alertAdapter.submitList(alertList.sortedBy { it.id }.reversed())
            }
        }
        fragmentAlertBinding.addAlertFab.setOnClickListener {
            showDialog()
        }
    }

    private fun showDialog() {

        val customAlertDialogBinding = CustomAlertDialogBinding.inflate(layoutInflater)

        AlertDialog.Builder(requireContext()).create().apply {
            setView(customAlertDialogBinding.root)
            customAlertDialogBinding.fromTimeCv.setOnClickListener {
                showPickers(
                    customAlertDialogBinding.fromDateTv,
                    customAlertDialogBinding.fromTimeTv
                )
            }
            customAlertDialogBinding.toTimeCv.setOnClickListener {
                showPickers(
                    customAlertDialogBinding.toDateTv,
                    customAlertDialogBinding.toTimeTv
                )
            }
            customAlertDialogBinding.saveButton.setOnClickListener {
                if (startCalender != null && endCalender != null) {
                    if (alertsViewModel.getLatitude() != 0.0 && alertsViewModel.getLongitude() != 0.0) {
                        val startTime = startCalender!!.timeInMillis
                        val endTime = endCalender!!.timeInMillis
                        alertsViewModel.insertAlert(
                            AlertEntity(
                                startTime = startTime,
                                endTime = endTime,
                                latitude = alertsViewModel.getLatitude(),
                                longitude = alertsViewModel.getLongitude()
                            )
                        )
                        startCalender = null
                        endCalender = null
//                    val workManager = WorkManager.getInstance(requireContext())
//                    val inputDate = Data.Builder().apply {
//                        putLong(Constants.START_TIME, startCalender!!.timeInMillis)
//                        putLong(Constants.END_TIME, endCalender!!.timeInMillis)
//                    }
//                    val constraints = Constraints.Builder()
//                        .setRequiredNetworkType(NetworkType.CONNECTED)
//                        .build()
//                    val request = OneTimeWorkRequestBuilder<AlertWorker>()
//                        .setConstraints(constraints)
//                        .build()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "You need to specify location",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                } else {
                    Toast.makeText(requireContext(), "Please specify you time", Toast.LENGTH_SHORT)
                        .show()
                }

                dismiss()
            }
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
        }
    }

    private fun showPickers(dateTextView: TextView, timeTextView: TextView) {
        val calender = Calendar.getInstance()

        val timePickerListener =
            TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                calender.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calender.set(Calendar.MINUTE, minute)
                updateTimeLabels(calender, timeTextView)
            }

        val datePickerListener =
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                calender.set(Calendar.YEAR, year)
                calender.set(Calendar.MONTH, month)
                calender.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateLabels(calender, dateTextView)

                TimePickerDialog(
                    requireContext(),
                    timePickerListener,
                    calender.get(Calendar.HOUR_OF_DAY),
                    calender.get(Calendar.MINUTE),
                    false
                ).apply {

                    show()
                }

            }

        DatePickerDialog(
            requireContext(),
            datePickerListener,
            calender.get(Calendar.YEAR),
            calender.get(Calendar.MONTH),
            calender.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis() - 1000
            show()
        }

        Timber.e("calender ${calender.timeInMillis}")

        if (timeTextView.id == R.id.fromTimeTv) {
            startCalender = calender
            Timber.e("start calender ${startCalender!!.timeInMillis}")
        }

        if (timeTextView.id == R.id.toTimeTv) {
            endCalender = calender
            Timber.e("end calender ${endCalender!!.timeInMillis}")
        }
    }

    private fun updateTimeLabels(calendar: Calendar, timeTextView: TextView) {
        timeTextView.text =
            SimpleDateFormat("hh:mm aa", Locale.UK).format(calendar.time)
    }

    private fun updateDateLabels(calendar: Calendar, dateTextView: TextView) {
        dateTextView.text =
            SimpleDateFormat("dd MMM yyyy", Locale.UK).format(calendar.time)
    }

    private fun deleteOnLongClick(alertEntity: AlertEntity) {
        AlertDialog.Builder(requireContext()).apply {
            setMessage("Confirm Deletion")
            setPositiveButton(
                "Delete"
            ) { dialog, _ ->
                alertsViewModel.deleteAlert(alertEntity)
                //todo dismiss the alert
                dialog.cancel()
            }
            setNegativeButton(
                "Back"
            ) { dialog, _ ->
                dialog.cancel()
            }
            create().show()
        }
    }

    private fun setRecycler() {
        val alertsLayoutManager = LinearLayoutManager(requireContext())
        alertsLayoutManager.orientation = LinearLayoutManager.VERTICAL
        fragmentAlertBinding.alertsRv.apply {
            adapter = alertAdapter
            layoutManager = alertsLayoutManager
        }
    }
}