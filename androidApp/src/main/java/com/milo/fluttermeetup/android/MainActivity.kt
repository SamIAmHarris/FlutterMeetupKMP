package com.milo.fluttermeetup.android

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotApplyResult
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.milo.fluttermeetup.Greeting
import com.milo.fluttermeetup.SpaceXSDK
import com.milo.fluttermeetup.cache.DatabaseDriverFactory
import com.milo.fluttermeetup.entity.RocketLaunch
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
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
                        sdk.getLaunches(needReload)
                    }.onSuccess {
                        launchesContent.value = Resource.Content(it)
                    }.onFailure {
                        Log.i("Launch", "Failed to get launches: $it")
                        launchesContent.value = Resource.Error
                    }
                }
            }

            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (launchesContent.value) {
                        Resource.Uninitialized ->
                            Button(onClick = { displayLaunches(false) }) {
                                Text(text = "Get Launches")
                            }

                        is Resource.Content -> Text("Received data: ${(launchesContent.value as Resource.Content).launches}")
                        Resource.Error -> Text("Failed to load launches")
                        Resource.Loading -> Text("Loading")
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
fun GreetingView(text: String) {
    Text(text = text)
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        GreetingView("Hello, Android!")
    }
}


