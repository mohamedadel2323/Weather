package com.example.weather.home.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.databinding.NextForecastListItemBinding
import com.example.weather.model.pojo.Daily

class DailyAdapter : ListAdapter<Daily, DailyViewHolder>(DailyDiffUtil()) {
    lateinit var nextForecastListItemBinding: NextForecastListItemBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        nextForecastListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.next_forecast_list_item,
            parent,
            false
        )
        return DailyViewHolder(nextForecastListItemBinding)
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        nextForecastListItemBinding.daily = getItem(position)
    }
}

class DailyViewHolder(private val binding: NextForecastListItemBinding) :
    RecyclerView.ViewHolder(binding.root)

class DailyDiffUtil : DiffUtil.ItemCallback<Daily>() {
    override fun areItemsTheSame(oldItem: Daily, newItem: Daily): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Daily, newItem: Daily): Boolean {
        return oldItem == newItem
    }

}