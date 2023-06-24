package com.example.weather.alerts.view

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.weather.R
import com.example.weather.databinding.CustomAlertDialogBinding
import com.example.weather.databinding.FragmentAlertBinding
import java.text.SimpleDateFormat
import java.util.*


class AlertFragment : Fragment() {
    lateinit var fragmentAlertBinding: FragmentAlertBinding
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
                showPickers(customAlertDialogBinding.toDateTv, customAlertDialogBinding.toTimeTv)
            }
            customAlertDialogBinding.saveButton.setOnClickListener {
                dismiss()
            }
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
    }

    private fun updateTimeLabels(calendar: Calendar, timeTextView: TextView) {
        timeTextView.text =
            SimpleDateFormat("hh:mm aa", Locale.UK).format(calendar.time)
    }

    private fun updateDateLabels(calendar: Calendar, dateTextView: TextView) {
        dateTextView.text =
            SimpleDateFormat("dd MMM yyyy", Locale.UK).format(calendar.time)
    }
}