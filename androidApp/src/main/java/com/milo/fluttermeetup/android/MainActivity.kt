package com.milo.fluttermeetup.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.milo.fluttermeetup.entity.RocketLaunch
import com.milo.fluttermeetup.DualViewModel
import com.milo.fluttermeetup.Resource

class MainActivity : ComponentActivity() {

    private val viewModel = DualViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val state = viewModel.launchesResource.collectAsState()

            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = Color(0xFFf2f2f7)),
                        contentAlignment = Alignment.Center
                    ) {
                        when (state.value) {
                            Resource.Uninitialized -> StartButton(viewModel::retrieveLaunches)
                            is Resource.Content -> LaunchesUI(launches = (state.value as Resource.Content).launches)
                            Resource.Error -> {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Failed to load launches...")
                                    Spacer(modifier = Modifier.height(12.dp))
                                    StartButton(viewModel::retrieveLaunches)
                                }
                            }
                            Resource.Loading -> CircularProgressIndicator(
                                modifier = Modifier.size(64.dp),
                                strokeWidth = 6.dp,
                                color = Color(0xFF5507CC)
                            )
                        }
                    }
                }
            }
        }
    }
}

//region UI
@Composable
fun LaunchesUI(launches: List<RocketLaunch>) {
    Column {
        Text(
            modifier = Modifier.padding(start = 12.dp, top = 36.dp),
            text = "SpaceX Launches",
            fontSize = 34.sp,
            color = Color.Black,
            fontWeight = FontWeight.W700
        )
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier =
            Modifier
                .padding(horizontal = 12.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(32.dp)
                )
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                for (launch in launches) {
                    LaunchRow(launch)
                }
            }
        }
    }
}

@Composable
fun LaunchRow(launch: RocketLaunch) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        LaunchText(text = "Launch name: ${launch.missionName}")
        Spacer(modifier = Modifier.height(8.dp))
        val success = launch.launchSuccess ?: false
        LaunchText(
            text = if (success) "Successful" else "Unsuccessful",
            color = if (success) Color.Green else Color.Red
        )
        Spacer(modifier = Modifier.height(8.dp))
        LaunchText(text = "Launch year: ${launch.launchYear}")
        Spacer(modifier = Modifier.height(8.dp))
        LaunchText(text = "Launch details: ${launch.details ?: "No Details"}")
        Spacer(modifier = Modifier.height(16.dp))
        Divider(thickness = 1.5.dp, color = Color(0xFFf2f2f7))
    }
}

@Composable
fun StartButton(onClick: () -> Unit) {
    Button(
        colors = ButtonDefaults.buttonColors(
            contentColor = Color(0xFF5507CC),
            containerColor = Color(0xFF5507CC),
            disabledContainerColor = Color(0xFF5507CC),
            disabledContentColor = Color(0xFF5507CC)
        ),
        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp),
        onClick = onClick
    ) {
        Text(
            text = "Get Launches", color = Color.White, fontSize = 20.sp
        )
    }
}

@Composable
fun LaunchText(text: String, color: Color = Color.Black) {
    Text(
        text = text,
        color = color,
        fontSize = 19.sp
    )
}
//endregion


