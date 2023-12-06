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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.milo.fluttermeetup.SpaceXSDK
import com.milo.fluttermeetup.cache.DatabaseDriverFactory
import com.milo.fluttermeetup.entity.RocketLaunch
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope

class MainActivity : ComponentActivity() {

    private val sdk = SpaceXSDK(DatabaseDriverFactory(this))
    private val viewModel = LaunchesViewModel(sdk)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val state = viewModel.state

            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        when (state) {
                            Resource.Uninitialized -> StartButton(viewModel::retrieveLaunches)
                            is Resource.Content -> LaunchesUI(launches = state.launches)
                            Resource.Error -> Text("Failed to load launches...")
                            Resource.Loading -> CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}

class LaunchesViewModel(private val sdk: SpaceXSDK) : ViewModel() {
    var state by mutableStateOf<Resource>(Resource.Uninitialized)
        private set

    fun retrieveLaunches() {
        viewModelScope.launch {
            kotlin.runCatching {
                state = Resource.Loading
                delay(3000)
                sdk.getLaunches(true)
            }.onSuccess {
                state = Resource.Content(it)
            }.onFailure {
                state = Resource.Error
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


//region UI
@Composable
fun LaunchesUI(launches: List<RocketLaunch>) {
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

@Composable
fun LaunchRow(launch: RocketLaunch) {
    Box {
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
            LaunchText(text = "Launch name ${launch.missionName}")
            val success = launch.launchSuccess ?: false
            LaunchText(text = if (success) "Unsuccessful" else "Successful")
            LaunchText(text = "Launch year: ${launch.launchYear}")
            LaunchText(text = "Launch details: ${launch.details}")
            Divider(color = Color.Black)
        }
    }
}

@Composable
fun StartButton(onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text(text = "Get Launches", color = Color.White)
    }
}

@Composable
fun LaunchText(text: String) {
    Text(
        text = text,
        color = Color.Black,
        fontSize = 18.sp
    )
}
//endregion


