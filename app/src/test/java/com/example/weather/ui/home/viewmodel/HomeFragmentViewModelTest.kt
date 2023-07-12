package com.example.weather.ui.home.viewmodel

import app.cash.turbine.test
import com.example.weather.MainDispatcherRule
import com.example.weather.data.network.ApiState
import com.example.weather.model.FakeRepository
import com.example.weather.model.pojo.*
import com.example.weather.uitils.toWeatherResponseEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class HomeFragmentViewModelTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val weatherResponseEntity = WeatherResponseEntity(
        listOf(), Current(
            0, 0.0, 0, 0.0, 0, 0, 0, 0, 0.0, 0.0, 0,
            listOf(), 0, 0.0, 0.0
        ), listOf(), listOf(), 0.0, 0.0, "", 0, 1
    )
    private val weatherResponseEntityToInsert = WeatherResponseEntity(
        listOf(), Current(
            0, 0.0, 0, 0.0, 0, 0, 0, 0, 0.0, 0.0, 0,
            listOf(), 0, 0.0, 0.0
        ), listOf(), listOf(), 0.0, 0.0, "cairo", 0, 0
    )
    private val weatherResponse = WeatherResponse(
        listOf(), Current(
            0, 0.0, 0, 0.0, 0, 0, 0, 0, 0.0, 0.0, 0,
            listOf(), 0, 0.0, 0.0
        ), listOf(), listOf(), listOf(), 0.0, 0.0, "", 0
    )

    private lateinit var fakeRepository: FakeRepository
    private lateinit var homeFragmentViewModel: HomeFragmentViewModel

    @Before
    fun setUpViewModel() {
        fakeRepository = FakeRepository(
            mutableListOf(weatherResponseEntity),
            mutableListOf(),
            mutableListOf(),
            weatherResponse
        )
        homeFragmentViewModel = HomeFragmentViewModel(fakeRepository)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getWeather_weatherResponseNotNullOnTheRemoteSource_resultEqualProvidedWeatherResponse() =
        runTest {
            //Given: provided weatherResponse not null

            //When: getWeather called and get weather response from the remote and insert it to the database if it success
            var localResultWeatherResponse: WeatherResponseEntity? = null

            homeFragmentViewModel.getWeather(Location(0.0, 0.0), "metric", "en")
            homeFragmentViewModel.getWeatherFromDatabase()

            homeFragmentViewModel.offlineWeatherStateFlow.test {
                this.awaitItem().apply {
                    when (this) {
                        is ApiState.SuccessOffline -> {
                            localResultWeatherResponse = data
                        }

                        else -> {
                            //locationResultWeatherResponse still null
                        }
                    }
                }
            }
            val remoteResultWeatherResponse = weatherResponse.toWeatherResponseEntity()

            //Then: check equality of remote and retrieved local weatherResponse
            assertThat(localResultWeatherResponse, `is`(remoteResultWeatherResponse))
        }


    @ExperimentalCoroutinesApi
    @Test
    fun getWeather_nullWeatherResponseOnTheRemoteSource_resultTimeZoneFailed() = runTest {
        //Given: provided weatherResponse is null
        fakeRepository.weatherResponse = null

        //When: getWeather called
        var localResultWeatherResponse: WeatherResponseEntity? = null
        homeFragmentViewModel.getWeather(Location(0.0, 0.0), "metric", "en")

        fakeRepository.getWeather(Location(0.0, 0.0), "metric", "en").collectLatest {
            if (it.isSuccessful){
                localResultWeatherResponse = it.body()?.toWeatherResponseEntity()
            }
        }

        //Then: check the time zone value if it's "failed" and not inserted to the data base.
        assertThat(localResultWeatherResponse!!.timezone, `is`("failed"))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getWeatherFromDatabase_weatherEntityInserted_retrievedEqualsInserted() = runTest {

        //When: weather inserted and getWeatherFromDatabase called
        var localWeatherResponseEntity: WeatherResponseEntity? = null
        fakeRepository.insertWeatherToDatabase(weatherResponseEntityToInsert)
        homeFragmentViewModel.getWeatherFromDatabase()

        homeFragmentViewModel.offlineWeatherStateFlow.test {
            this.awaitItem().apply {
                when (this) {
                    is ApiState.SuccessOffline -> localWeatherResponseEntity = data
                    else -> {}
                }
            }

        }

        //Then: check equality of inserted and retrieved local weatherResponse
        assertThat(localWeatherResponseEntity, `is`(weatherResponseEntityToInsert))
    }
}