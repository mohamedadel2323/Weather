package com.example.weather.ui.favorites.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.databinding.FavoritesListItemBinding
import com.example.weather.model.pojo.FavoritePlace

class FavoritesAdapter(private val listener : OnFavoriteLongClick,val sendPlace: (FavoritePlace) -> Unit) :
    ListAdapter<FavoritePlace, FavoriteViewHolder>(FavoritesDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = DataBindingUtil.inflate<FavoritesListItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.favorites_list_item,
            parent,
            false
        )
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.binding.favoritesCard.setOnClickListener {
            sendPlace(getItem(position))
        }
        holder.binding.favoritesCard.setOnLongClickListener {
            listener.onFavoriteLongClick(getItem(position))
            true
        }
    }
}

class FavoriteViewHolder(val binding: FavoritesListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(favoritePlace: FavoritePlace) {
        binding.favoritePlace = favoritePlace
        binding.executePendingBindings()
    }
}

class FavoritesDiffUtil : DiffUtil.ItemCallback<FavoritePlace>() {
    override fun areItemsTheSame(oldItem: FavoritePlace, newItem: FavoritePlace): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: FavoritePlace, newItem: FavoritePlace): Boolean {
        return oldItem == newItem
    }
}

interface OnFavoriteLongClick{
    fun onFavoriteLongClick(favoritePlace: FavoritePlace)
}