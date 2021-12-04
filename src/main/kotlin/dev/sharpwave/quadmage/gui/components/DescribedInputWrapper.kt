package dev.sharpwave.quadmage.gui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DescribedInputWrapper(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.padding(5.dp)
    ) {
        Text(title, style = MaterialTheme.typography.subtitle2)
        content()
    }
}