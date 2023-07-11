package com.example.weather.ui.alerts.viewmodel

import app.cash.turbine.test
import com.example.weather.MainDispatcherRule
import com.example.weather.model.FakeRepository
import com.example.weather.model.pojo.AlertEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.hasItem
import org.junit.Assert.*

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

class AlertsViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val alertEntity1 =
        AlertEntity(UUID.fromString("c9a646d7-ff7a-4d1e-8b3c-3f1a22e6e3ef"), 0, 0, 0.0, 0.0, false)
    private val alertEntity2 =
        AlertEntity(UUID.fromString("c9a646d8-ff7a-4d1e-8b3c-3f1a22e6e3ef"), 0, 0, 0.0, 0.0, true)
    private val alertEntity3 =
        AlertEntity(UUID.fromString("c9a646d9-ff7a-4d1e-8b3c-3f1a22e6e3ef"), 0, 0, 0.0, 0.0, false)
    private val alertEntityToInsertion =
        AlertEntity(UUID.fromString("c9a646d5-ff7a-4d1e-8b3c-3f1a22e6e3ef"), 0, 0, 0.0, 0.0, false)

    private lateinit var repository: FakeRepository
    private lateinit var alertsViewModel: AlertsViewModel

    @Before
    fun setUp() {
        repository = FakeRepository(
            mutableListOf(),
            mutableListOf(),
            mutableListOf(alertEntity1, alertEntity2, alertEntity3), null
        )
        alertsViewModel = AlertsViewModel(repository)
    }

    @Test
    fun insertAlert_alertInserted_sizeOfListIs4() = runTest {
        //Given: alert inserted to the database
        alertsViewModel.insertAlert(alertEntityToInsertion)

        //When: calling the getAllAlerts fun
        alertsViewModel.getAllAlerts()
        var resultAlertList: List<AlertEntity> = listOf()

        alertsViewModel.alertsStateFlow.test {
            resultAlertList = this.awaitItem()
        }
        //Then assert the size equals to 4, and the retrieved list contains the inserted alert
        assertThat(resultAlertList.size, `is`(4))
        assertThat(resultAlertList.map { it.id }, hasItem(alertEntityToInsertion.id))
    }

    @Test
    fun deleteAlert_alertDeleted_sizeOfListIs2() = runTest {
        //Given: alert deleted from the database
        alertsViewModel.deleteAlert(alertEntity1)

        //When: calling the getAllAlerts fun
        alertsViewModel.getAllAlerts()
        var resultAlertList: List<AlertEntity> = listOf()

        alertsViewModel.alertsStateFlow.test {
            resultAlertList = this.awaitItem()
        }

        //Then assert the size equals to 2, and the retrieved list doesn't contain the deleted alert
        assertThat(resultAlertList.size, `is`(2))
        assertThat(resultAlertList.map { it.id }, CoreMatchers.not(hasItem(alertEntity1.id)))
    }

    @Test
    fun getAllAlerts_sizeOfListIs3() = runTest {
        //Given: the database has 3 alerts

        //When: calling getAllAlerts
        alertsViewModel.getAllAlerts()
        var resultAlertList: List<AlertEntity> = listOf()
        alertsViewModel.alertsStateFlow.test {
            resultAlertList = this.awaitItem()
        }

        //Then: assert the retrieved list equals to 3
        assertThat(resultAlertList.size, `is`(3))
    }
}