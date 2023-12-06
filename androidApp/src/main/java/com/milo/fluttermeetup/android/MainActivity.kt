package com.milo.fluttermeetup.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.milo.fluttermeetup.SpaceXSDK
import com.milo.fluttermeetup.cache.DatabaseDriverFactory
import com.milo.fluttermeetup.entity.RocketLaunch
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val mainScope = MainScope()

    private val sdk = SpaceXSDK(DatabaseDriverFactory(this))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val launchesContent = remember { mutableStateOf<Resource>(Resource.Uninitialized) }

            fun displayLaunches(needReload: Boolean) {
                mainScope.launch {
                    kotlin.runCatching {
                        launchesContent.value = Resource.Loading
                        delay(3000)
                        sdk.getLaunches(needReload)
                    }.onSuccess {
                        launchesContent.value = Resource.Content(it)
                    }.onFailure {
                        launchesContent.value = Resource.Error
                    }
                }
            }

            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        when (launchesContent.value) {
                            Resource.Uninitialized ->
                                Button(onClick = { displayLaunches(false) }) {
                                    Text(text = "Get Launches", color = Color.White)
                                }

                            is Resource.Content -> {
                                val launches = (launchesContent.value as Resource.Content).launches
                                Column {
                                    Text(
                                        text = "SpaceX Launches",
                                        fontSize = 44.sp,
                                        color = Color.Black
                                    )
                                    LazyColumn {
                                        items(launches.size) { index ->
                                            val launch = launches[index]
                                            LaunchRow(launch)
                                        }
                                    }
                                }
                            }

                            Resource.Error -> Text("Failed to load launches")
                            Resource.Loading -> CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainScope.cancel()
    }
}

sealed class Resource {
    data object Uninitialized : Resource()
    data object Loading : Resource()
    data object Error : Resource()
    data class Content(val launches: List<RocketLaunch>) : Resource()
}

@Composable
fun LaunchRow(launch: RocketLaunch) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize()
                .background(color = Color.White)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Launch name ${launch.missionName}",
                fontSize = 20.sp,
                color = Color.Black
            )
            val success = launch.launchSuccess ?: false

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (success) "Unsuccessful" else "Successful",
                color = if (success) Color.Green else Color.Red,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Launch year: ${launch.launchYear}",
                color = Color.Black,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Launch details: ${launch.details}",
                color = Color.Black,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Divider(color = Color.Black)
        }
    }
}


