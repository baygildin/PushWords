package com.sbaygildin.pushwords.wordlist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sbaygildin.pushwords.data.model.WordTranslation


@Composable
fun WordlistScreen(
    viewModel: WordlistViewModel,
    onAddWordClick: () -> Unit,
    onEditClick: (WordTranslation) -> Unit,
    onDeleteClick: (WordTranslation) -> Unit,
) {
    val words by viewModel.wordList.collectAsState()


    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(words) { word ->
                WordItem(
                    word = word,
                    onEditClick = { onEditClick(word) },
                    onDeleteClick = { onDeleteClick(word) }
                )
            }
        }

        FloatingActionButton(
            onClick = onAddWordClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 96.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Word")
        }
    }

}