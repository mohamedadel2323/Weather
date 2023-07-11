package com.example.weather.ui.alerts.view

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.example.weather.uitils.Constants
import com.example.weather.R
import com.example.weather.data.database.ConcreteLocalSource
import com.example.weather.databinding.CustomAlertDialogBinding
import com.example.weather.databinding.FragmentAlertBinding
import com.example.weather.model.Repository
import com.example.weather.model.pojo.AlertEntity
import com.example.weather.data.network.ApiClient
import com.example.weather.data.shared_preferences.SettingsSharedPreferences
import com.example.weather.ui.alerts.viewmodel.AlertsViewModel
import com.example.weather.ui.alerts.viewmodel.AlertsViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

private const val REQUEST_OVERLAY_PERMISSION = 5

class AlertFragment : Fragment() {
    private var startCalender: Calendar? = null
    private var endCalender: Calendar? = null
    private lateinit var fragmentAlertBinding: FragmentAlertBinding
    private lateinit var alertsViewModel: AlertsViewModel
    private lateinit var alertsViewModelFactory: AlertsViewModelFactory
    private lateinit var alertAdapter: AlertAdapter
    private lateinit var workManager: WorkManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        fragmentAlertBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_alert, container, false)
        return fragmentAlertBinding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        workManager = WorkManager.getInstance(requireContext())
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
            ViewModelProvider(this, alertsViewModelFactory)[AlertsViewModel::class.java]

        alertsViewModel.getAllAlerts()
        lifecycleScope.launch {
            alertsViewModel.alertsStateFlow.collectLatest { alertList ->
                if (alertList.isNotEmpty()) {
                    fragmentAlertBinding.noAlertsLa.apply {
                        pauseAnimation()
                        visibility = View.GONE
                    }

                }
                alertAdapter.submitList(alertList.sortedBy { it.startTime }.reversed())
            }
        }

        fragmentAlertBinding.alertsInstructionIb.setOnClickListener {
            AlertDialog.Builder(requireContext()).apply {
                setTitle(getString(R.string.tip))
                setMessage(getString(R.string.alert_instruction))
                setPositiveButton(
                    getString(R.string.ok)
                ) { dialog, _ ->
                    dialog.cancel()
                }
                create().show()
            }
        }

        fragmentAlertBinding.addAlertFab.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(context)) {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + requireContext().packageName)
                    )
                    startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION)
                } else {
                    showDialog()
                }
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
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
                        startCalender!!.set(Calendar.SECOND, 0)
                        startCalender!!.set(Calendar.MILLISECOND, 0)
                        endCalender!!.set(Calendar.SECOND, 0)
                        endCalender!!.set(Calendar.MILLISECOND, 0)
                        val startTime = startCalender!!.timeInMillis
                        val endTime = endCalender!!.timeInMillis
                        var isNotification = false
                        if (alertsViewModel.getNotificationOption()) {
                            isNotification = true
                        }
                        val alertEntity = AlertEntity(
                            startTime = startTime,
                            endTime = endTime,
                            latitude = alertsViewModel.getLatitude(),
                            longitude = alertsViewModel.getLongitude(),
                            isNotification = isNotification
                        )
                        startCalender = null
                        endCalender = null
                        val inputDate = Data.Builder().apply {
                            putLong(Constants.START_TIME, startTime.div(1000))
                            putLong(Constants.END_TIME, endTime.div(1000))
                            putDouble(Constants.LATITUDE, alertEntity.latitude)
                            putDouble(Constants.LONGITUDE, alertEntity.longitude)
                            putBoolean(Constants.NOTIFICATION_OPTION, alertEntity.isNotification)
                        }.build()
                        val constraints = Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build()
                        val request =
                            OneTimeWorkRequestBuilder<com.example.weather.ui.alerts.AlertWorker>().apply {
                                setConstraints(constraints)
                                setInputData(inputDate)
                                setInitialDelay(
                                    startTime - System.currentTimeMillis(),
                                    TimeUnit.MILLISECONDS
                                )
                            }.build()
                        workManager.enqueue(request)
                        alertEntity.id = request.id
                        alertsViewModel.insertAlert(alertEntity)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.need_location),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.specify_your_time),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

                dismiss()
            }
            customAlertDialogBinding.dialogRg.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.dialogNotificationRb -> alertsViewModel.setNotificationOption(true)
                    R.id.dialogAlertRb -> alertsViewModel.setNotificationOption(false)
                }
            }
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
        }
    }

    private fun showPickers(dateTextView: TextView, timeTextView: TextView) {
        val calender = Calendar.getInstance()

        val timePickerListener =
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                calender.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calender.set(Calendar.MINUTE, minute)
                updateTimeLabels(calender, timeTextView)
            }

        val datePickerListener =
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
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
            setMessage(getString(R.string.confirm_deletion))
            setPositiveButton(
                getString(R.string.delete)
            ) { dialog, _ ->
                alertsViewModel.deleteAlert(alertEntity)
                workManager.cancelWorkById(alertEntity.id)
                dialog.cancel()
            }
            setNegativeButton(
                getString(R.string.back)
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