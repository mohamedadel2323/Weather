package com.example.weather.alerts.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.databinding.AlarmListItemBinding
import com.example.weather.model.pojo.AlertEntity

class AlertAdapter(private val sendAlert: (AlertEntity) -> Unit) :
    ListAdapter<AlertEntity, AlertViewHolder>(AlertsDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val binding = DataBindingUtil.inflate<AlarmListItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.alarm_list_item,
            parent,
            false
        )
        return AlertViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        holder.bind(getItem(position))

        holder.binding.alertCard.setOnLongClickListener {
            sendAlert(getItem(position))
            true
        }
    }
}

class AlertViewHolder(val binding: AlarmListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(alert: AlertEntity) {
        binding.alert = alert
        binding.executePendingBindings()
    }
}

class AlertsDiffUtil : DiffUtil.ItemCallback<AlertEntity>() {
    override fun areItemsTheSame(oldItem: AlertEntity, newItem: AlertEntity): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: AlertEntity, newItem: AlertEntity): Boolean {
        return oldItem == newItem
    }
}