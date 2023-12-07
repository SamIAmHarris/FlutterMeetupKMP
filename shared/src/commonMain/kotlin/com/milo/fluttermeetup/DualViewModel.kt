package com.milo.fluttermeetup

import com.milo.fluttermeetup.entity.RocketLaunch
import com.rickclephas.kmm.viewmodel.KMMViewModel
import com.rickclephas.kmm.viewmodel.MutableStateFlow
import com.rickclephas.kmm.viewmodel.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DualViewModel() : KMMViewModel() {

    private val sdk: SpaceXSDK = SpaceXSDK()

    private val _launchesResource =
        MutableStateFlow<Resource>(viewModelScope, Resource.Uninitialized)
    val launchesResource = _launchesResource.asStateFlow()

    init {
        retrieveLaunches()
    }

    fun retrieveLaunches() {
        viewModelScope.coroutineScope.launch {
            kotlin.runCatching {
                _launchesResource.value = Resource.Loading
                delay(1000)
                sdk.getLaunches()
            }.onSuccess {
                _launchesResource.value = Resource.Content(it)
            }.onFailure {
                println(it.message)
                _launchesResource.value = Resource.Error
            }
        }
    }
}

sealed class Resource {
    data object Uninitialized : Resource()
    data object Loading : Resource()
    data object Error : Resource()
    data class Content(val launches: List<RocketLaunch>) : Resource()
}
