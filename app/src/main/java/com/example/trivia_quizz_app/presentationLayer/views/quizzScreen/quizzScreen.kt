package com.example.trivia_quizz_app.presentationLayer.views.quizzScreen

import android.app.Activity
import android.os.Bundle
import android.util.Log
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
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.trivia_quizz_app.ui.theme.AppTheme


class QuizzScreen: AppCompatActivity() {
    private lateinit var viewModel: QuizzViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val quizzName = intent.getStringExtra("quizzName") ?: ""

        viewModel = viewModels<QuizzViewModel> {
            QuizzModelFactory((application as QuizzApp).quizzRepo, quizzName)
        }.value

        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainView(viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainView(viewModel: QuizzViewModel){
    val ctx = LocalContext.current
    val activity = ctx as Activity
    val correctAnswer = viewModel.currentCorrectAnswer.observeAsState()
    val currentWrongAnswer1 = viewModel.currentWrongAnswer1.observeAsState()
    val currentWrongAnswer2 = viewModel.currentWrongAnswer2.observeAsState()
    val currentWrongAnswer3 = viewModel.currentWrongAnswer3.observeAsState()
    val shuffledAnswers = listOf(correctAnswer, currentWrongAnswer1, currentWrongAnswer2, currentWrongAnswer3).shuffled()
    viewModel.correctButtonId = shuffledAnswers.indexOf(correctAnswer)
    val btn1Color = viewModel.btn1Color.observeAsState()
    val btn2Color = viewModel.btn2Color.observeAsState()
    val btn3Color = viewModel.btn3Color.observeAsState()
    val btn4Color = viewModel.btn4Color.observeAsState()
    val defaultBtnColor = MaterialTheme.colorScheme.primary
    setDefaultColors(viewModel, defaultBtnColor)


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = viewModel.quizzName.uppercase()) },
                navigationIcon = {
                    IconButton(onClick = {
                        activity.finish()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {}
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            QuizzScreenView(
                viewModel,
                shuffledAnswers,
                btn1Color,
                btn2Color,
                btn3Color,
                btn4Color
            )
        }
    }
}

@Composable
private fun QuizzScreenView(
    viewModel: QuizzViewModel,
    shuffledAnswers: List<State<String?>>,
    btn1Color: State<Color?>,
    btn2Color: State<Color?>,
    btn3Color: State<Color?>,
    btn4Color: State<Color?>
    ){
    val ctx = LocalContext.current
    val question = viewModel.currentQuestion.observeAsState()
    val resultShowing = viewModel.resultShowing.observeAsState()
    val finished = viewModel.finished.observeAsState()
    val answerBtnEnabled = viewModel.answerBtnEnabled.observeAsState()
    val defaultBtnColor = MaterialTheme.colorScheme.primary


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,

    ) {
        val screenHeight = LocalConfiguration.current.screenHeightDp.dp
        ElevatedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight / 4),
                contentAlignment = Alignment.Center
            ){
                question.value?.let {
                    Text(
                        text = it,
                        fontSize = 22.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))
        val answr1 = shuffledAnswers[0]
        answr1.value.let {answer ->
            answerBtnEnabled.value?.let {
                if (answer != null) {
                    btn1Color.value?.let { bgColor ->
                        AnswerButton(
                            text = answer,
                            onClick = { viewModel.onAnswerClicked(0) },
                            enabledState = it,
                            bgColor = bgColor
                        )
                    }
                }
            }
        }
        val answr2 = shuffledAnswers[1]
        answr2.value.let {answer ->
            answerBtnEnabled.value?.let {
                if (answer != null) {
                    btn2Color.value?.let { bgColor ->
                        AnswerButton(
                            text = answer,
                            onClick = { viewModel.onAnswerClicked(1) },
                            enabledState = it,
                            bgColor = bgColor
                        )
                    }
                }
            }
        }
        val answr3 = shuffledAnswers[2]
        answr3.value.let {answer ->
            answerBtnEnabled.value?.let {
                if (answer != null) {
                    btn3Color.value?.let { bgColor ->
                        AnswerButton(
                            text = answer,
                            onClick = { viewModel.onAnswerClicked(2) },
                            enabledState = it,
                            bgColor = bgColor
                        )
                    }
                }
            }
        }
        val answr4 = shuffledAnswers[3]
        answr4.value.let {answer ->
            answerBtnEnabled.value?.let {
                if (answer != null) {
                    btn4Color.value?.let { bgColor ->
                        AnswerButton(
                            text = answer,
                            onClick = { viewModel.onAnswerClicked(3) },
                            enabledState = it,
                            bgColor = bgColor
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        //ProgressIndicator(viewModel)
        //StreakIndicator(viewModel)
        if (resultShowing.value == true){
            Button(onClick = {
                viewModel.nextRound()
                setDefaultColors(viewModel, defaultBtnColor)
            }) {
                Text(text = stringResource(id = R.string.quiz_next))
            }
        }

        if (finished.value == true){
            Button(onClick = { (ctx as Activity).finish() /* TODO prechod na shrnuti kvizu */}) {
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
private fun ProgressIndicator(viewModel: QuizzViewModel){
    val questions = viewModel.quizzQuestions
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
    val streak = viewModel.streak.observeAsState()

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
            modifier = Modifier.size(40.dp))
    }
}

private fun setDefaultColors(viewModel: QuizzViewModel, defaultBtnColor: Color){
    viewModel.btn1Color.value = defaultBtnColor
    viewModel.btn2Color.value = defaultBtnColor
    viewModel.btn3Color.value = defaultBtnColor
    viewModel.btn4Color.value = defaultBtnColor
}