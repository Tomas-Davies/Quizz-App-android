package com.example.trivia_quizz_app.presentationLayer.views.quizzScreen

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.trivia_quizz_app.QuizzApp
import com.example.trivia_quizz_app.R
import com.example.trivia_quizz_app.RetrofitInstance
import com.example.trivia_quizz_app.presentationLayer.components.ErrorView
import com.example.trivia_quizz_app.presentationLayer.components.LoadingView
import com.example.trivia_quizz_app.presentationLayer.components.ResultRow
import com.example.trivia_quizz_app.presentationLayer.states.QuizzState
import com.example.trivia_quizz_app.repositoryLayer.ApiQuizzRepository
import com.example.trivia_quizz_app.ui.theme.AppTheme


class QuizzScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val quizzName = intent.getStringExtra("quizzName") ?: ""
        val isUserCreated = intent.getBooleanExtra("isUserCreated", false)
        val quizCategory = intent.getIntExtra("quizzCategory", -1)

        val viewModel: QuizzViewModel by viewModels {
            QuizzModelFactory(
                isInternetAvailable(this),
                (application as QuizzApp).quizzRepo,
                ApiQuizzRepository(RetrofitInstance.triviaApi),
                quizzName,
                isUserCreated,
                quizCategory
            )
        }

        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    QuizzScreenContent(viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizzScreenContent(viewModel: QuizzViewModel) {
    val quizState by viewModel.quizzState.collectAsStateWithLifecycle()
    val ctx = LocalContext.current
    val buttonColors = viewModel.btnColors.map { color -> color.collectAsStateWithLifecycle() }
    val defaultBtnColor = MaterialTheme.colorScheme.primary

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(viewModel.quizzName.uppercase()) },
                navigationIcon = {
                    IconButton(onClick = { (ctx as Activity).finish() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(22.dp, 0.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                when (quizState) {
                    is QuizzState.Loading -> {
                        LoadingView()
                    }
                    is QuizzState.NoInternetConnection -> {
                        NoInternetConnectionView(viewModel)
                    }
                    is QuizzState.TranslatingText -> {
                        LoadingView(message = "Překládám do češtiny") // Aktivni pouze pokud je nastavena cestina
                    }
                    is QuizzState.DownloadingLangModel -> {
                        LoadingView(message = "Stahuji překladač") // Aktivni pouze pokud je nastavena cestina
                    }
                    is QuizzState.Question -> {
                        val currentState = quizState as QuizzState.Question
                        viewModel.setDefaultColors(defaultBtnColor)
                        QuizzQuestionView(
                            questionText = currentState.questionText,
                            answers = currentState.answers,
                            viewModel = viewModel,
                            buttonColors = buttonColors,
                            defaultBtnColor = defaultBtnColor,
                            onAnswerSelected = { viewModel.onAnswerSelected(it) }
                        )
                    }
                    is QuizzState.Finished -> {
                        FinishedView(viewModel)
                    }
                    is QuizzState.Error -> {
                        ErrorView(stringResource((quizState as QuizzState.Error).messageId))
                    }
                }
            }
        }

    }
}

@Composable
fun QuizzQuestionView(
    questionText: String,
    answers: List<String>,
    viewModel: QuizzViewModel,
    buttonColors: List<State<Color>>,
    defaultBtnColor: Color,
    onAnswerSelected: (Int) -> Unit
) {
    val showFinishedButton = viewModel.showFinishButton.collectAsStateWithLifecycle()
    val inputEnabled = viewModel.inputEnabled.collectAsStateWithLifecycle()
    val showNextButton = viewModel.showNextButton.collectAsStateWithLifecycle()
    val streak = viewModel.streak.collectAsStateWithLifecycle()
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val currentRound = viewModel.currentRound.collectAsStateWithLifecycle()
    val questionCount = viewModel.questionCount.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Text(text = "${currentRound.value} / ${questionCount.value}")
        Spacer(modifier = Modifier.height(18.dp))
        ElevatedCard(
            modifier = Modifier
                    .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight / 4)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = questionText,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Column(
            modifier = Modifier.fillMaxWidth()
        ){
            answers.forEachIndexed { index, answer ->
                AnswerButton(
                    text = answer,
                    onClick = { onAnswerSelected(index) },
                    enabledState = inputEnabled.value,
                    bgColor = buttonColors[index].value
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))
        StreakIndicator(count = streak.value)
        Spacer(modifier = Modifier.height(18.dp))


        if (showNextButton.value){
            Button(
                onClick = {
                    viewModel.onNextClicked()
                    viewModel.setDefaultColors(defaultBtnColor)
            }) {
                Text(text = stringResource(id = R.string.quiz_next))
            }
        }

        if (showFinishedButton.value) {
            Button(onClick = {
                viewModel.onFinishClicked()
            }) {
                Text(text = stringResource(id = R.string.quiz_result))
            }
        }
    }
}


@Composable
fun AnswerButton(
    text: String,
    onClick: () -> Unit,
    enabledState: Boolean,
    modifier: Modifier = Modifier,
    bgColor: Color = ButtonDefaults.buttonColors().containerColor,
    textColor: Color = ButtonDefaults.buttonColors().contentColor
){
    Button(
        enabled = enabledState,
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = ButtonColors(
            containerColor = bgColor,
            contentColor = textColor,
            disabledContainerColor = bgColor,
            disabledContentColor = textColor
        ),
        contentPadding = PaddingValues(16.dp),
        shape = RoundedCornerShape(25),
        border = BorderStroke(3.dp, MaterialTheme.colorScheme.tertiary)
    ) {
        Text(
            text = text,
            fontSize = 16.sp)
    }
}

@Composable
fun FinishedView(viewModel: QuizzViewModel) {
    val ctx = LocalContext.current
    val medalLimit = viewModel.medalLimit.collectAsStateWithLifecycle().value

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        val medalDrawable = when {
            medalLimit < 33 -> R.drawable.bronze_medal
            medalLimit < 66 -> R.drawable.silver_medal
            else -> R.drawable.gold_medal
        }

        val screenHeight = LocalConfiguration.current.screenHeightDp.dp
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp

        Image(
            painter = painterResource(id = medalDrawable),
            contentDescription = "medal",
            modifier = Modifier.size(screenHeight / 3)
        )

        val actualScore = viewModel.getScore()
        val width = 3 * (screenWidth / 4)

        ResultProgressBar(
            viewModel,
            indicatorProgress = actualScore.toFloat(),
            modifier = Modifier.width(width)
        )

        ElevatedCard {
            Column(
                modifier = Modifier
                    .width(width)
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                val label1 = stringResource(id = R.string.quiz_result_correct)

                ResultRow(name = label1, data = viewModel.correctCount.toString())
                val label2 = stringResource(id = R.string.quiz_result_wrong)
                ResultRow(name = label2, data = viewModel.wrongCount.toString())
                val label3 = stringResource(id = R.string.quiz_result_max_streak)
                ResultRow(name = label3, data = viewModel.maxStreak.toString())
            }
        }


        Button(
            onClick = {
                viewModel.updateAllTotalResultData()
                (ctx as Activity).finish()
            }) {
            Text(stringResource(id = R.string.quiz_finish))
        }
    }
}


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ResultProgressBar(
    viewModel: QuizzViewModel,
    indicatorProgress: Float,
    modifier: Modifier = Modifier,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
){
    var progress by remember { mutableFloatStateOf(0F) }
    val animDuration = 1700
    val progressAnimation by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = animDuration, easing = FastOutSlowInEasing),
        label = ""
    )

    val label = stringResource(id = R.string.quiz_result_score)
    Text(
        text = "$label: ${(progressAnimation * 100).toInt()}",
        fontSize = 40.sp
    )
    LinearProgressIndicator(
        progress = { progressAnimation },
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .height(18.dp)
    )
    LaunchedEffect(lifecycleOwner) {
        progress = indicatorProgress / 100
    }

    when  {
        progressAnimation > 0.66F -> viewModel.medalLimit.value = 66
        progressAnimation > 0.33F -> viewModel.medalLimit.value = 33
        else -> viewModel.medalLimit.value = 0
    }
}



@Composable
private fun StreakIndicator(count: Int, textSize: TextUnit = TextUnit.Unspecified, imageSize: Dp = 20.dp){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.quiz_result_streak) + ": ",
            fontSize = textSize
            )
        Text(
            text = count.toString(),
            fontSize = textSize
            )
        val flameDrawableId = R.drawable.flame_icon
        Spacer(modifier = Modifier.width(4.dp))
        Image(
            painter = painterResource(id = flameDrawableId),
            contentDescription = "Streak",
            modifier = Modifier.size(imageSize))
    }
}

@Composable
fun NoInternetConnectionView(viewModel: QuizzViewModel) {
    val ctx = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp),
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(18.dp))
            Text(text = stringResource(id = R.string.no_internet_conn))
        }

        Button(
            onClick = {
                viewModel.hasInternetConnection = isInternetAvailable(ctx)
                viewModel.prepareViewModel()
                      },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text(text = stringResource(id = R.string.try_again))
        }
    }
}

fun isInternetAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val network = connectivityManager.activeNetwork ?: return false
    val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

    return when {
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
}