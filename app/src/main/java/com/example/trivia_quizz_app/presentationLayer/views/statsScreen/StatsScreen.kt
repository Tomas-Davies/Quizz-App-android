package com.example.trivia_quizz_app.presentationLayer.views.statsScreen

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.example.trivia_quizz_app.R
import com.example.trivia_quizz_app.presentationLayer.components.ResultRow
import com.example.trivia_quizz_app.ui.theme.AppTheme

class StatsScreen: AppCompatActivity() {
    private val viewModel by viewModels<StatsViewModel> {
        StatsViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StatsScreenContent(viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatsScreenContent(viewModel: StatsViewModel){
    val ctx = LocalContext.current
    val activity = (ctx as Activity)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.statistics)) },
                navigationIcon = {
                    IconButton(onClick = { activity.finish() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) {innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            StatsView(viewModel = viewModel)
        }
    }
}

@Composable
fun StatsView(viewModel: StatsViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        val screenHeight = LocalConfiguration.current.screenHeightDp.dp
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp

        val trophyDrawable = when {
            viewModel.successRate < 33 -> R.drawable.bronze_trophy
            viewModel.successRate < 66 -> R.drawable.silver_trophy
            else -> R.drawable.gold_trophy
        }
        Log.w("SUUUUUUUUUUUUUUUUUUUUUUUUC", "${viewModel.successRate}")
        Image(
            painter = painterResource(id = trophyDrawable),
            contentDescription = "Trophy",
            modifier = Modifier.size(screenHeight / 3)
        )
        

        ElevatedCard {
            val width = 3 * (screenWidth / 4)

            Column(
                modifier = Modifier
                    .width(width)
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                val label0 = stringResource(id = R.string.statistics_completed_quizzes)
                ResultRow(name = label0, data = viewModel.completed.toString())
                val label1 = stringResource(id = R.string.quiz_result_correct)
                ResultRow(name = label1, data = viewModel.answeredCorrectly.toString())
                val label2 = stringResource(id = R.string.quiz_result_wrong)
                ResultRow(name = label2, data = viewModel.answeredWrong.toString())
                val label3 = stringResource(id = R.string.quiz_result_max_streak)
                ResultRow(name = label3, data = viewModel.maxStreak.toString())
                val label4 = stringResource(id = R.string.statistics_success_rate)
                ResultRow(name = label4, data = viewModel.successRate.toString() + "%")
            }
        }

        //TODO tady dojebat ten graf z api
    }
}