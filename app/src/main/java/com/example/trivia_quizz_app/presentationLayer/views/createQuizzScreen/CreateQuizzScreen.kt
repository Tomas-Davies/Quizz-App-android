package com.example.trivia_quizz_app.presentationLayer.views.createQuizzScreen

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.trivia_quizz_app.QuizzApp
import com.example.trivia_quizz_app.R
import com.example.trivia_quizz_app.dataLayer.entities.QuestionAAnswers
import com.example.trivia_quizz_app.ui.theme.AppTheme
import com.example.trivia_quizz_app.presentationLayer.components.CustomTextField
import com.example.trivia_quizz_app.presentationLayer.components.ErrorMessage


class CreateQuizzScreen: AppCompatActivity() {

    private val viewModel: CreateQuizzViewModel by viewModels {
        CreateQuizzModelFactory((application as QuizzApp).quizzRepo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
private fun MainView(viewModel: CreateQuizzViewModel){
    val ctx = LocalContext.current
    val activity = ctx as Activity
    var quizzName by remember { mutableStateOf("") }
    var nameAvailable by remember { mutableStateOf(true) }
    var nameFilled by remember { mutableStateOf(true) }
    var hasQuestions by remember { mutableIntStateOf(-1) }
    val localFocusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.create_quizz_heading).uppercase()) },
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
                actions = {

                })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                nameAvailable = viewModel.nameIsAvailable(quizzName)
                nameFilled = quizzName.isNotEmpty()
                hasQuestions = viewModel.checkIfHasQuestions()
                if (nameAvailable && hasQuestions == 1){
                    viewModel.quizzName = quizzName
                    viewModel.addQuizzToRepository()
                    activity.setResult(Activity.RESULT_OK)
                    activity.finish()
                }
            }) {
                Icon(imageVector = Icons.Filled.Check, contentDescription = "Save")
            }
        }
    ) {innerPadding ->
        Column(
            modifier = Modifier
                .padding(0.dp, innerPadding.calculateTopPadding(), 0.dp, 0.dp)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        localFocusManager.clearFocus()
                    })
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (nameAvailable || hasQuestions != 0){
                Spacer(modifier = Modifier.size(24.dp))
            }
            if (!nameFilled){
                val message = R.string.create_quizz_input_error
                ErrorMessage(message = stringResource(id = message))
            }
            if (hasQuestions == 0){
                val message = R.string.create_quizz_question_error
                ErrorMessage(message = stringResource(id = message))
            }
            if (!nameAvailable){
                val message = R.string.create_quizz_name_taken_error
                ErrorMessage(message = stringResource(id = message))
            }

            CustomTextField(
                label = {Text(text = stringResource(id = R.string.create_quizz_name))},
                value = quizzName,
                onValueChange = {quizzName = it},
                singleLine = true,
                modifier = Modifier.padding(18.dp, 0.dp)
            )
            Spacer(modifier = Modifier.size(16.dp))

            Questions(quizzName, viewModel)
        }
    }
}

@Composable
private fun Questions(quizName: String, viewModel: CreateQuizzViewModel){
    var question by remember { mutableStateOf("") }
    var correctAnswer by remember {mutableStateOf("")}
    var wrongAnswer1 by remember {mutableStateOf("")}
    var wrongAnswer2 by remember {mutableStateOf("")}
    var wrongAnswer3 by remember {mutableStateOf("")}
    var count by remember { mutableIntStateOf(viewModel.questions.size) }
    var isValid by remember { mutableStateOf(true) }
    val wrongAnswerColor = MaterialTheme.colorScheme.errorContainer
    val ctx = LocalContext.current

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp, 20.dp, 18.dp, 0.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Absolute.SpaceBetween
    ){
        Text(
            text = stringResource(id = R.string.create_quizz_questions)
        )
        Text(
            text = stringResource(id = R.string.create_quizz_count) + ": $count"
        )
    }
    HorizontalDivider(thickness = 3.dp)

    if (!isValid){
        val message = R.string.create_quizz_input_error
        ErrorMessage(message = stringResource(id = message))
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CustomTextField(
            label = { Text(text = stringResource(id = R.string.create_quizz_question))},
            value = question,
            onValueChange = {question = it},
            singleLine = true,
            modifier = Modifier.padding(18.dp, 0.dp)
        )
        CustomTextField(
            label = {
                Text(text = stringResource(id = R.string.create_quizz_correct_answer),
                    color = MaterialTheme.colorScheme.onPrimaryContainer)
                    },
            value = correctAnswer,
            onValueChange = {correctAnswer = it},
            singleLine = true,
            modifier = Modifier.padding(18.dp, 0.dp),
            tint = MaterialTheme.colorScheme.primaryContainer
        )
        CustomTextField(
            label = {
                Text(text = stringResource(id = R.string.create_quizz_wrong_answer),
                    color = MaterialTheme.colorScheme.onErrorContainer)
                    },
            value = wrongAnswer1,
            onValueChange = {wrongAnswer1 = it},
            singleLine = true,
            modifier = Modifier.padding(18.dp, 0.dp),
            tint = wrongAnswerColor
        )
        CustomTextField(
            label = {
                Text(
                    text = stringResource(id = R.string.create_quizz_wrong_answer),
                    color = MaterialTheme.colorScheme.onErrorContainer)
                    },
            value = wrongAnswer2,
            onValueChange = {wrongAnswer2 = it},
            singleLine = true,
            modifier = Modifier.padding(18.dp, 0.dp),
            tint = wrongAnswerColor
        )
        CustomTextField(
            label = {
                Text(
                    text = stringResource(id = R.string.create_quizz_wrong_answer),
                    color = MaterialTheme.colorScheme.onErrorContainer)
                    },
            value = wrongAnswer3,
            onValueChange = {wrongAnswer3 = it},
            singleLine = true,
            modifier = Modifier.padding(18.dp, 0.dp),
            tint = wrongAnswerColor
        )
        val newQuestion = QuestionAAnswers(
            quizzName = quizName, question = question, correctAnswer = correctAnswer,
            wrongAnswer1 = wrongAnswer1, wrongAnswer2 = wrongAnswer2, wrongAnswer3 = wrongAnswer3)
        val toastMessage = stringResource(id = R.string.create_quizz_question_added)
        ElevatedButton(
            onClick = {
                val input = listOf(question, correctAnswer, wrongAnswer1, wrongAnswer2, wrongAnswer3)
                isValid = viewModel.checkQuestionsInput(input)
                if (isValid){
                    count++
                    viewModel.addQuestion(newQuestion)
                    Toast.makeText(ctx, toastMessage, Toast.LENGTH_SHORT).show()
                    question = ""
                    correctAnswer = ""
                    wrongAnswer1 = ""
                    wrongAnswer2 = ""
                    wrongAnswer3 = ""
                }
        }) {
            Text(text = stringResource(id = R.string.create_quizz_add_question))
        }
        HorizontalDivider(thickness = 3.dp)
        Spacer(modifier = Modifier.size(24.dp))
    }
}