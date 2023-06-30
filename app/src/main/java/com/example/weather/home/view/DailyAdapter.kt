package com.example.weather.home.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.databinding.NextForecastListItemBinding
import com.example.weather.model.pojo.Daily

class DailyAdapter : ListAdapter<Daily, DailyViewHolder>(DailyDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val binding = DataBindingUtil.inflate<NextForecastListItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.next_forecast_list_item,
            parent,
            false
        )
        return DailyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class DailyViewHolder(private val binding: NextForecastListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(daily: Daily) {
        binding.daily = daily
        binding.executePendingBindings()
    }
}

class DailyDiffUtil : DiffUtil.ItemCallback<Daily>() {
    override fun areItemsTheSame(oldItem: Daily, newItem: Daily): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Daily, newItem: Daily): Boolean {
        return oldItem == newItem
    }
}