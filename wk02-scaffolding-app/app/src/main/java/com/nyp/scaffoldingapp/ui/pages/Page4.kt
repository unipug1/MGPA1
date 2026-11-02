package com.nyp.scaffoldingapp.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Page 4 — the game launcher.
 *
 * The button is deliberately NOT wired up. Connecting it to a LibGDX
 * Activity is Assignment 2, and it is taught in the Week 9 session:
 *
 *   - launching an Activity for a result
 *   - passing a value back, and what happens when there isn't one
 *   - the lifecycle trap that leaks on the second launch
 *
 * See the Week 9 slides. Do not guess at it from here.
 */
@Composable
fun Page4(modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Page 4", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            "Sample Content",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = {
                // TODO (Week 9): launch your game Activity for a result.
            },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("START")
        }

        Spacer(Modifier.height(16.dp))

        Text(
            "Not connected yet — see the Week 9 slides.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
