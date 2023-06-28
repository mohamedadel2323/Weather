package com.example.weather.home.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.databinding.HourlyDegreeListItemBinding
import com.example.weather.model.pojo.Hourly

class HourlyAdapter : ListAdapter<Hourly, HourlyViewHolder>(HourlyDiffUtil()) {
    lateinit var hourlyDegreeListItemBinding: HourlyDegreeListItemBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        hourlyDegreeListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.hourly_degree_list_item,
            parent,
            false
        )
        return HourlyViewHolder(hourlyDegreeListItemBinding)
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        hourlyDegreeListItemBinding.hourly = getItem(position)
    }
}

class HourlyViewHolder(private val binding: HourlyDegreeListItemBinding) :
    RecyclerView.ViewHolder(binding.root)

class HourlyDiffUtil : DiffUtil.ItemCallback<Hourly>() {
    override fun areItemsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
        return oldItem == newItem
    }
}