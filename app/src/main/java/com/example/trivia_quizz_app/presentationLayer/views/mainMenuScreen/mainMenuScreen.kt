package com.example.trivia_quizz_app.presentationLayer.views.mainMenuScreen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trivia_quizz_app.QuizzApp
import com.example.trivia_quizz_app.R
import com.example.trivia_quizz_app.dataLayer.entities.Quizz
import com.example.trivia_quizz_app.presentationLayer.views.createQuizzScreen.CreateQuizzScreen
import com.example.trivia_quizz_app.presentationLayer.views.quizzScreen.QuizzScreen
import com.example.trivia_quizz_app.ui.theme.AppTheme


class MainMenuScreen: AppCompatActivity() {

    private val viewModel: MainMenuViewModel by viewModels {
        MainMenuModelFactory((application as QuizzApp).quizzRepo)
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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1){
            if (resultCode == Activity.RESULT_OK){
                viewModel.loadQuizzes()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainView(viewModel: MainMenuViewModel){
    val ctx = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.quizzes_heading).uppercase())},
                actions = {}
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                openCreateQuizzActivity(ctx)
            }) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
            }
        },
    ) {innerPadding ->
        MainMenu(viewModel = viewModel, padding = innerPadding)
    }
}


@Composable
private fun MainMenu(viewModel: MainMenuViewModel, padding: PaddingValues){
    val quizzesAndQuestions = viewModel.quizzes

    LazyVerticalGrid(
        modifier = Modifier.padding(18.dp, padding.calculateTopPadding(), 18.dp, 0.dp),
        horizontalArrangement = Arrangement.spacedBy(36.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        columns = GridCells.Adaptive(100.dp),
    ) {

        items(quizzesAndQuestions){ quizzAndQuestions ->
            key(quizzAndQuestions.quizz.quizzName) {
                QuizzCard(quizzAndQuestions.quizz, viewModel)
            }
        }
    }
}


@Composable
private fun QuizzCard(quizz: Quizz, viewModel: MainMenuViewModel){
    var cardWidth by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    var starActive by remember { mutableStateOf(quizz.isFavourited) }
    val isUserCreated: Boolean = quizz.image == 0
    val ctx = LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ElevatedCard(
            modifier = Modifier
                .onGloballyPositioned { cords ->
                    cardWidth = with(density) { cords.size.width.toDp() }
                }
                .fillMaxWidth()
                .aspectRatio(1f),
            onClick = { openQuizzActivity(ctx, quizz.quizzName, isUserCreated, quizz.category) },
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    var starDrawableId = R.drawable.star_small_dark
                    if (starActive || quizz.isFavourited) starDrawableId = R.drawable.star_small

                    Image(
                        painter = painterResource(id = starDrawableId),
                        contentDescription = "favourite",
                        modifier = Modifier
                            .padding(8.dp)
                            .size(cardWidth / 5)
                            .clickable {
                                starActive = !starActive
                                quizz.isFavourited = starActive
                                viewModel.update(quizz)
                            }
                    )
                    if (isUserCreated){
                        val deleteDrawableId = R.drawable.delete_small
                        Image(
                            painter = painterResource(id = deleteDrawableId),
                            contentDescription = "favourite",
                            modifier = Modifier
                                .padding(8.dp)
                                .size(cardWidth / 5)
                                .clickable {
                                    viewModel.deleteQuiz(quizz)
                                    viewModel.loadQuizzes()
                                }
                        )
                    }
                }

                if (!isUserCreated){
                    Image(
                        painter = painterResource(id = quizz.image),
                        contentDescription = "category image",
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = quizz.quizzName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
        if (!isUserCreated) Text(text = quizz.quizzName)
    }
}


fun openCreateQuizzActivity(ctx: Context){
    val intent = Intent(ctx, CreateQuizzScreen::class.java)
    val activity = (ctx as Activity)
    activity.startActivityForResult(intent, 1)
}

fun openQuizzActivity(ctx: Context, quizName: String, isUserCreated: Boolean = false, quizCat: Int){
    val intent = Intent(ctx, QuizzScreen::class.java)
    intent.putExtra("quizzName", quizName)
    intent.putExtra("isUserCreated", isUserCreated)
    intent.putExtra("quizzCategory", quizCat)
    ctx.startActivity(intent)
}
