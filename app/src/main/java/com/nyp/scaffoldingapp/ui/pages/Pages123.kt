package com.nyp.scaffoldingapp.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Pages 1 to 3 are placeholders. They exist so the navigation bar has
 * somewhere to go.
 *
 * Replace them. The rubric asks for four sections, each doing a DIFFERENT
 * job — four labels is one section, four times.
 */

@Composable
fun Page1(modifier: Modifier = Modifier) = Placeholder("Page 1", modifier)

@Composable
fun Page2(modifier: Modifier = Modifier) = Placeholder("Page 2", modifier)

@Composable
fun Page3(modifier: Modifier = Modifier) = Placeholder("Page 3", modifier)

@Composable
private fun Placeholder(label: String, modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            "Replace this with your own content.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
