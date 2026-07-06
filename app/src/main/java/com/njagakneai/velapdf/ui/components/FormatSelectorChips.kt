package com.njagakneai.velapdf.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.njagakneai.velapdf.data.model.EditOutputFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormatSelectorChips(
    selectedFormat: EditOutputFormat,
    onFormatSelected: (EditOutputFormat) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        EditOutputFormat.values().forEach { format ->
            FilterChip(
                selected = format == selectedFormat,
                onClick = { onFormatSelected(format) },
                label = {
                    Text(
                        text = format.name,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            )
        }
    }
}
