package com.example.weather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.weather.databinding.ActivityAlertDetailsAcitivityBinding
import com.example.weather.model.pojo.Alert

class AlertDetailsActivity : AppCompatActivity() {
    private lateinit var alertDetailsActivityBinding: ActivityAlertDetailsAcitivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        alertDetailsActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_alert_details_acitivity)
        val alert = Alert(
            intent.getStringExtra(Constants.DESCRIPTION) ?: getString(R.string.weather_is_fine),
            intent.getIntExtra(Constants.END_TIME, 0),
            intent.getStringExtra(Constants.EVENT) ?: getString(R.string.no_event),
            intent.getStringExtra(Constants.SENDER_NAME) ?: "",
            intent.getIntExtra(Constants.START_TIME, 0),
            listOf()
        )
        if (alert.sender_name.isEmpty()) {
            alertDetailsActivityBinding.startTimeTv.visibility = View.GONE
            alertDetailsActivityBinding.endTimeTv.visibility = View.GONE
            alertDetailsActivityBinding.startTimeValueTv.visibility = View.GONE
            alertDetailsActivityBinding.endTimeValueTv.visibility = View.GONE
            alertDetailsActivityBinding.senderTv.visibility = View.GONE
            alertDetailsActivityBinding.senderValueTv.visibility = View.GONE
        }
        alertDetailsActivityBinding.alert = alert

    }
}