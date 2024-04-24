package com.example.trivia_quizz_app.presentationLayer.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

@Composable
fun ErrorMessage(
    modifier: Modifier = Modifier,
    message: String,
    size: TextUnit = TextUnit.Unspecified
){
    Row(
        modifier = modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(modifier = Modifier.size(18.dp),
            imageVector = Icons.Filled.Clear,
            contentDescription = "Error",
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.size(4.dp))
        Text(text = message,
            fontSize = size,
            color = MaterialTheme.colorScheme.error
        )
    }
}