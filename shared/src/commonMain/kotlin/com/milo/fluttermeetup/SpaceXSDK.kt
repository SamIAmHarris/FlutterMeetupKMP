package com.milo.fluttermeetup

import com.milo.fluttermeetup.cache.Database
import com.milo.fluttermeetup.cache.DatabaseDriverFactory
import com.milo.fluttermeetup.entity.RocketLaunch
import com.milo.fluttermeetup.network.SpaceXApi


class SpaceXSDK (databaseDriverFactory: DatabaseDriverFactory) {
    private val database = Database(databaseDriverFactory)
    private val api = SpaceXApi()

    @Throws(Exception::class) suspend fun getLaunches(forceReload: Boolean): List<RocketLaunch> {
        val cachedLaunches = database.getAllLaunches()
        return if (cachedLaunches.isNotEmpty() && !forceReload) {
            cachedLaunches
        } else {
            api.getAllLaunches().also {
                database.clearDatabase()
                database.createLaunches(it)
            }
        }
    }
}