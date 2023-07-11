package com.example.weather.ui.favorites.view

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.example.weather.R
import com.example.weather.data.database.ConcreteLocalSource
import com.example.weather.databinding.FragmentFavoritesBinding
import com.example.weather.model.Repository
import com.example.weather.model.pojo.FavoritePlace
import com.example.weather.data.network.ApiClient
import com.example.weather.data.shared_preferences.SettingsSharedPreferences
import com.example.weather.ui.favorites.viewmodel.FavoritesFragmentViewModel
import com.example.weather.ui.favorites.viewmodel.FavoritesFragmentViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class FavoritesFragment : Fragment(), OnFavoriteLongClick {
    private lateinit var fragmentFavoritesBinding: FragmentFavoritesBinding
    private lateinit var favoritesFragmentViewModel: FavoritesFragmentViewModel
    private lateinit var favoritesFragmentViewModelFactory: FavoritesFragmentViewModelFactory
    private lateinit var favoritesAdapter: FavoritesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        fragmentFavoritesBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_favorites, container, false)
        return fragmentFavoritesBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        favoritesAdapter = FavoritesAdapter(this) { showDetails(it) }
        setRecycler()
        favoritesFragmentViewModelFactory = FavoritesFragmentViewModelFactory(
            (Repository.getInstance(
                SettingsSharedPreferences.getInstance(requireContext()),
                ApiClient,
                ConcreteLocalSource(requireContext())
            ))
        )

        favoritesFragmentViewModel = ViewModelProvider(
            this,
            favoritesFragmentViewModelFactory
        )[FavoritesFragmentViewModel::class.java]
        favoritesFragmentViewModel.getAllFavorites()
        lifecycleScope.launch {
            favoritesFragmentViewModel.favoritesStateFlow.collectLatest {
                if (it.isNotEmpty()) {
                    fragmentFavoritesBinding.noFavoritesLa.apply {
                        pauseAnimation()
                        visibility = View.GONE
                    }
                }
                favoritesAdapter.submitList(it)
            }
        }


        fragmentFavoritesBinding.favoriteInstructionIb.setOnClickListener {
            AlertDialog.Builder(requireContext()).apply {
                setTitle(getString(R.string.tip))
                setMessage(getString(R.string.favorites_instruction))
                setPositiveButton(
                    getString(R.string.ok)
                ) { dialog, _ ->
                    dialog.cancel()
                }
                create().show()
            }
        }

        fragmentFavoritesBinding.addFavFab.setOnClickListener {
            favoritesFragmentViewModel.setMapFavorite(true)
            Navigation.findNavController(requireView())
                .navigate(FavoritesFragmentDirections.actionFavoritesFragmentToMapsFragment())
        }
    }

    private fun setRecycler() {
        val favoritesLayoutManager = LinearLayoutManager(requireContext())
        favoritesLayoutManager.orientation = VERTICAL
        fragmentFavoritesBinding.favoritesRv.apply {
            adapter = favoritesAdapter
            layoutManager = favoritesLayoutManager
        }

    }

    private fun showDetails(favoritePlace: FavoritePlace) {
        favoritesFragmentViewModel.setDetails(true)
        Navigation.findNavController(requireView()).navigate(
            FavoritesFragmentDirections.actionFavoritesFragmentToHomeFragment(favoritePlace)
        )
    }

    override fun onFavoriteLongClick(favoritePlace: FavoritePlace) {
        AlertDialog.Builder(requireContext()).apply {
            setMessage(getString(R.string.confirm_deletion))
            setPositiveButton(
                getString(R.string.delete)
            ) { dialog, _ ->
                favoritesFragmentViewModel.deleteFavorite(favoritePlace)
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

}