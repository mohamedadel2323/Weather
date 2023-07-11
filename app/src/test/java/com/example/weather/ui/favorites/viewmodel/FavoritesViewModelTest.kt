package com.example.weather.ui.favorites.viewmodel

import app.cash.turbine.test
import com.example.weather.MainDispatcherRule
import com.example.weather.model.FakeRepository
import com.example.weather.model.pojo.AlertEntity
import com.example.weather.model.pojo.FavoritePlace
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

import org.junit.Test

class FavoritesViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val favoritePlace1 = FavoritePlace(0, "cairo", 0.0, 0.0)
    private val favoritePlace2 = FavoritePlace(1, "Alex", 0.0, 0.0)
    private val favoritePlace3 = FavoritePlace(2, "Giza", 0.0, 0.0)

    private lateinit var repository: FakeRepository
    private lateinit var favoritesViewModel: FavoritesFragmentViewModel

    @Before
    fun setUp() {
        repository = FakeRepository(
            mutableListOf(),
            mutableListOf(favoritePlace1, favoritePlace2, favoritePlace3),
            mutableListOf(), null
        )
        favoritesViewModel = FavoritesFragmentViewModel(repository)
    }

    @Test
    fun getAllFavorites_listSize3() = runTest {
        //Given: the database has 3 favorite places

        //When: calling getAllAlerts
        favoritesViewModel.getAllFavorites()
        var resultAlertList: List<FavoritePlace> = listOf()
        favoritesViewModel.favoritesStateFlow.test {
            resultAlertList = this.awaitItem()
        }

        //Then: assert the retrieved list equals to 3
        assertThat(resultAlertList.size, `is`(3))
    }

    @Test
    fun deleteFavorite_deletedItem_listSize2() = runTest {
        //Given: favorite place deleted from the database
        favoritesViewModel.deleteFavorite(favoritePlace1)

        //When: calling the getAllFavorites fun
        favoritesViewModel.getAllFavorites()
        var resultAlertList: List<FavoritePlace> = listOf()
        favoritesViewModel.favoritesStateFlow.test {
            resultAlertList = this.awaitItem()
        }

        //Then assert the size equals to 2, and the retrieved list doesn't contain the deleted favorite place
        assertThat(resultAlertList.size, `is`(2))
        assertThat(
            resultAlertList.map { it.id }, not(hasItem(favoritePlace1))
        )
    }
}