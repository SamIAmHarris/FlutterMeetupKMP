package com.milo.fluttermeetup

import com.milo.fluttermeetup.entity.RocketLaunch
import com.milo.fluttermeetup.network.SpaceXApi

class SpaceXSDK {
    private val api = SpaceXApi()

    @Throws(Exception::class)
    suspend fun getLaunches(): List<RocketLaunch> {
        return api.getAllLaunches()
    }
}