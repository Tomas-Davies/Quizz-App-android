package com.example.trivia_quizz_app.presentationLayer.views.quizzScreen

import android.app.Activity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trivia_quizz_app.QuizzApp
import com.example.trivia_quizz_app.R
import com.example.trivia_quizz_app.RetrofitInstance
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
                (application as QuizzApp).quizzRepo,
                ApiQuizzRepository(RetrofitInstance.api),
                quizzName,
                isUserCreated,
                quizCategory
            )
        }

        setContent {
            AppTheme {
                QuizScreenContent(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreenContent(viewModel: QuizzViewModel) {
    val quizState by viewModel.quizzState.collectAsState()
    val ctx = LocalContext.current
    val buttonColors = viewModel.btnColors.map { color -> color.collectAsState() }
    val defaultBtnColor = MaterialTheme.colorScheme.primary
    viewModel.setDefaultColors(defaultBtnColor)

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
            modifier = Modifier.padding(22.dp),
        ) {
            Box(modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()) {
                when (quizState) {
                    is QuizzState.Loading -> {
                        LoadingView()
                    }
                    is QuizzState.Question -> {
                        val currentState = quizState as QuizzState.Question
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
                        QuizzFinishedView { /* SHOW STATISTICS */ }
                    }
                    is QuizzState.Error -> {
                        ErrorView((quizState as QuizzState.Error).message)
                    }
                }
            }
        }

    }
}

@Composable
fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
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
    val showFinishedButton = viewModel.showFinishButton.collectAsState()
    val inputEnabled = viewModel.inputEnabled.collectAsState()
    val showNextButton = viewModel.showNextButton.collectAsState()
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight / 4),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = questionText,
                    fontSize = 22.sp
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
        StreakIndicator(viewModel = viewModel)
        Spacer(modifier = Modifier.height(18.dp))

        if (showNextButton.value){
            Button(onClick = {
                viewModel.onNextClicked()
                viewModel.setDefaultColors(defaultBtnColor)
            }) {
                Text(text = stringResource(id = R.string.quiz_next))
            }
        }

        if (showFinishedButton.value) {
            val ctx = LocalContext.current
            Button(onClick = {
                viewModel.onFinishClicked()
                (ctx as Activity).finish()
            /* TODO prechod na shrnuti kvizu */
            }) {
                Text(text = stringResource(id = R.string.quiz_finish))
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
            fontSize = 20.sp)
    }
}

@Composable
fun QuizzFinishedView(onFinish: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Quiz Finished!")

        Button(onClick = onFinish) {
            Text("Finish Quiz")
        }
    }
}

@Composable
fun ErrorView(errorMessage: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Error: $errorMessage",
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun ProgressIndicator(viewModel: QuizzViewModel){
    val questions = viewModel.quizzData
    LazyHorizontalGrid(
        rows = GridCells.Fixed(questions.size),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        items(questions.size){idx ->
            ProgressIndicatorItem(viewModel = viewModel, id = idx)
        }
    }
}

@Composable
private fun ProgressIndicatorItem(viewModel: QuizzViewModel, id: Int){

}

@Composable
private fun StreakIndicator(viewModel: QuizzViewModel){
    val streak = viewModel.streak.collectAsState()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Streak: ")
        Text(text = streak.value.toString())
        val flameDrawableId = R.drawable.flame_icon
        Image(
            painter = painterResource(id = flameDrawableId),
            contentDescription = "Streak",
            modifier = Modifier.size(20.dp))
    }
}