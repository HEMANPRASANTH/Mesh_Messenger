package com.bitchat.android.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bitchat.android.R
import com.bitchat.android.ui.components.GlassButton
import com.bitchat.android.ui.components.GlassCard

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        GlassCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "Mesh",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )
                )

                Text(
                    text = "Decentralized Communication\nNo Internet Required",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = colorScheme.onSurface.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                GlassButton(
                    text = "Create Account",
                    onClick = onGetStarted,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Placeholder for potential "Sign In" (restore backup) functionality
                // For now, it flows into the same onboarding
                androidx.compose.material3.TextButton(onClick = { /* TODO: Implement Restore */ }) {
                    Text("Already have an account? Sign In", color = colorScheme.primary)
                }
            }
        }
    }
}
