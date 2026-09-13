package com.victor.restart.feature.reusable


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun OfflineBanner(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.errorContainer
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Unicode glyph stand-in for the wifi-off icon.
            // Alternatives: "\u26A0" (⚠) or "\u2716" (✖) if this doesn't render.
            Text(
                text = "\uD83D\uDCF6", // 📶
                color = MaterialTheme.colorScheme.onErrorContainer
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = "You're offline",
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}