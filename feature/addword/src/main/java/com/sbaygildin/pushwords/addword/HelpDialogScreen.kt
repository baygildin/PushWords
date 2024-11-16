package com.sbaygildin.pushwords.addword

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp


@Composable
fun HelpDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.txt_how_to_use_the_app)) },
        text = {
            Column {
                Text(text = stringResource(id = R.string.txt_help_makefile_txt))
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = stringResource(id = R.string.txt_help_makefile_txt_example))
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = stringResource(id = R.string.txt_help_makefile_txt_explanation))
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = stringResource(id = R.string.txt_help_makefile_txt_important))
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        }
    )
}

