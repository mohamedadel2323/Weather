package com.example.weather.model

import com.example.weather.data.database.FakeLocalSource
import com.example.weather.data.network.FakeRemoteSource
import com.example.weather.data.shared_preferences.FakeSettingsSharedPreferences
import com.example.weather.model.pojo.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.core.IsEqual
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.*

@Suppress("DEPRECATION")
class RepositoryTest {

    private val weatherResponseEntity = WeatherResponseEntity(
        listOf(), Current(
            0, 0.0, 0, 0.0, 0, 0, 0, 0, 0.0, 0.0, 0,
            listOf(), 0, 0.0, 0.0
        ), listOf(), listOf(), 0.0, 0.0, "", 0, 0
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
        ), listOf(), listOf(), listOf(), 0.0, 0.0, "", 1
    )
    private val favoritePlace1 = FavoritePlace(0, "cairo", 0.0, 0.0)
    private val favoritePlace2 = FavoritePlace(1, "Alex", 0.0, 0.0)
    private val favoritePlace3 = FavoritePlace(2, "Giza", 0.0, 0.0)

    private val alertEntity1 =
        AlertEntity(UUID.fromString("c9a646d7-ff7a-4d1e-8b3c-3f1a22e6e3ef"), 0, 0, 0.0, 0.0, false)
    private val alertEntity2 =
        AlertEntity(UUID.fromString("c9a646d8-ff7a-4d1e-8b3c-3f1a22e6e3ef"), 0, 0, 0.0, 0.0, true)
    private val alertEntity3 =
        AlertEntity(UUID.fromString("c9a646d9-ff7a-4d1e-8b3c-3f1a22e6e3ef"), 0, 0, 0.0, 0.0, false)


    private lateinit var fakeLocalSource: FakeLocalSource
    private lateinit var fakeRemoteSource: FakeRemoteSource
    private lateinit var fakeSettingsSharedPreferences: FakeSettingsSharedPreferences

    private lateinit var repository: Repository

    @Before
    fun setUpRepo() {
        fakeLocalSource = FakeLocalSource(
            mutableListOf(weatherResponseEntity),
            mutableListOf(favoritePlace1, favoritePlace2, favoritePlace3),
            mutableListOf(alertEntity1, alertEntity2, alertEntity3)
        )
        fakeRemoteSource = FakeRemoteSource(weatherResponse)
        fakeSettingsSharedPreferences = FakeSettingsSharedPreferences(31.0f, true)

        repository = Repository(
            fakeSettingsSharedPreferences,
            fakeRemoteSource,
            fakeLocalSource
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getWeather_weatherRetrievedFromRemoteSource() {
        runTest {
            // When getWeather called and repo have one weather entity
            var currentWeather: WeatherResponse? = null
            repository.getWeather(Location(0.0, 0.0), "metric", "en").collectLatest {
                currentWeather = it.body()
            }

            // Then the returned weather is the one in the remote source.
            currentWeather?.let {
                assertThat(currentWeather, `is`(weatherResponse))
            }
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun insertWeatherToDatabase_weatherInserted() {
        runTest {
            // When weather inserted to the database using repo
            repository.insertWeatherToDatabase(weatherResponseEntityToInsert)

            // Then current weather in database equal to the inserted one.
            var insertedWeather: WeatherResponseEntity? = null
            repository.getWeatherFromDatabase().collectLatest {
                insertedWeather = it
            }
            insertedWeather?.let {
                assertThat(it.timezone, IsEqual("cairo"))
            }
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getWeatherFromDatabase_weatherRetrievedFromLocalSource() {
        runTest {
            // When getWeatherFromDatabase called and repo have one weather entity
            var currentWeather: WeatherResponseEntity? = null
            repository.getWeatherFromDatabase().collectLatest {
                currentWeather = it
            }

            // Then the returned weather is the one in the database.
            currentWeather?.let {
                assertThat(currentWeather, `is`(weatherResponseEntity))
            }
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun insertFavorite_sizeOfList_sizeOfListPlusOne() {
        //Given favorite place to be inserted
        val favoritePlace = FavoritePlace(3, "Helwan", 0.0, 0.0)


        //When insertFavorite called with the given favoritePlace
        var sizeBeforeInsert = 0
        var sizeAfterInsert = 0
        runTest {
            repository.getAllFavorites().collectLatest { sizeBeforeInsert = it.size }
            repository.insertFavorite(favoritePlace)
            repository.getAllFavorites().collectLatest { sizeAfterInsert = it.size }
        }

        //Then the size after insertion is equal the size before plus one
        assertThat(sizeAfterInsert, `is`(sizeBeforeInsert.plus(1)))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getAllFavorites_favoriteListRetrieved() {
        //When getAllFavorites called
        var favoriteList: List<FavoritePlace> = mutableListOf()
        runTest {
            repository.getAllFavorites().collectLatest {
                favoriteList = it
            }
        }
        //Then assert all elements on the retrieved list with the provided list to the local source
        assertThat(favoriteList[0], `is`(favoritePlace1))
        assertThat(favoriteList[1], `is`(favoritePlace2))
        assertThat(favoriteList[2], `is`(favoritePlace3))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getAllFavorites_emptyFavoriteList_sizeEqualToZero() {
        //Given no favorites
        val localRepository = Repository(
            fakeSettingsSharedPreferences,
            fakeRemoteSource,
            FakeLocalSource(mutableListOf(), mutableListOf(), mutableListOf())
        )
        //When getAllFavorites called
        var favoriteList: List<FavoritePlace> = mutableListOf()
        runTest {
            localRepository.getAllFavorites().collectLatest {
                favoriteList = it
            }
        }
        //Then assert size of the retrieved list equal to 0
        assertThat(favoriteList.size, `is`(0))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteFavorite_sizeOfList_sizeOfListMinusOne() {
        //When deleteFavorite called with the given favoritePlace1
        var sizeBeforeInsert = 0
        var sizeAfterInsert = 0
        runTest {
            repository.getAllFavorites().collectLatest { sizeBeforeInsert = it.size }
            repository.deleteFavorite(favoritePlace1)
            repository.getAllFavorites().collectLatest { sizeAfterInsert = it.size }
        }

        //Then the size after insertion is equal the size before minus one
        assertThat(sizeAfterInsert, `is`(sizeBeforeInsert.minus(1)))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun insertAlert_sizeOfList_sizeOfListPlusOne() {
        //Given alert to be inserted
        val alertEntity = AlertEntity(
            UUID.fromString("c9a646d6-ff7a-4d1e-8b3c-3f1a22e6e3ef"),
            0,
            0,
            0.0,
            0.0,
            false
        )


        //When insertAlert called with the given alert
        var sizeBeforeInsert = 0
        var sizeAfterInsert = 0
        runTest {
            repository.getAllAlerts().collectLatest { sizeBeforeInsert = it.size }
            repository.insertAlert(alertEntity)
            repository.getAllAlerts().collectLatest { sizeAfterInsert = it.size }
        }

        //Then the size after insertion is equal the size before plus one
        assertThat(sizeAfterInsert, `is`(sizeBeforeInsert.plus(1)))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getAllAlerts_alertListRetrieved() {
        //When getAllAlerts called
        var alertList: List<AlertEntity> = mutableListOf()
        runTest {
            repository.getAllAlerts().collectLatest {
                alertList = it
            }
        }
        //Then assert all elements on the retrieved list with the provided list to the local source
        assertThat(alertList[0], `is`(alertEntity1))
        assertThat(alertList[1], `is`(alertEntity2))
        assertThat(alertList[2], `is`(alertEntity3))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteAlert_sizeOfList_sizeOfListMinusOne() {
        //When deleteAlert called with the given alertEntity1
        var sizeBeforeInsert = 0
        var sizeAfterInsert = 0
        runTest {
            repository.getAllAlerts().collectLatest { sizeBeforeInsert = it.size }
            repository.deleteAlert(alertEntity1)
            repository.getAllAlerts().collectLatest { sizeAfterInsert = it.size }
        }

        //Then the size after insertion is equal the size before minus one
        assertThat(sizeAfterInsert, `is`(sizeBeforeInsert.minus(1)))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteAlertByUuid_retrievedListOfAlertNotContainTheDeletedOne() {
        //When deleteAlert called with the given alertEntity1 uuid
        var alertList: List<AlertEntity> = mutableListOf()
        runTest {
            repository.deleteAlertByUuid(alertEntity1.id)
            repository.getAllAlerts().collectLatest { alertList = it }
        }
        //Then check if the deleted alert still exist on the list or not
        assertThat(alertList.map { it.id }, not(hasItem(alertEntity1.id)))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getFirstTime_False_False() {
        //Given firstTime value set to false
        repository.setFirstTime(false)

        //When getFirstTime called
        val value = repository.getFirstTime()

        //Then value should be false
        assertThat(value, `is`(false))

    }

    @ExperimentalCoroutinesApi
    @Test
    fun getLongitude() {
        //When getLongitude called
        val value = repository.getLongitude()

        //Then value should be the provided longitude
        assertThat(value, `is`(31.0))
    }
}