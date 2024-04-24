package com.example.trivia_quizz_app.presentationLayer.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.trivia_quizz_app.ui.theme.md_theme_dark_secondaryContainer
import com.example.trivia_quizz_app.ui.theme.md_theme_light_secondaryContainer


@Composable
fun CustomTextField(
    label: @Composable (() -> Unit)? = null,
    value: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = false,
    modifier: Modifier,
    tint: Color = if (isSystemInDarkTheme()) md_theme_dark_secondaryContainer else md_theme_light_secondaryContainer
){
    TextField(
        label = label,
        value = value,
        onValueChange = onValueChange,
        singleLine = singleLine,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            unfocusedContainerColor = tint,
            focusedContainerColor = tint,
            errorContainerColor = tint
        )
    )
}