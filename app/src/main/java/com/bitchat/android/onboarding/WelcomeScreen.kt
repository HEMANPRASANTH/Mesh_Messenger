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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val context = androidx.compose.ui.platform.LocalContext.current
    var showLanguageMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        // Language Selector (Top Right)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 8.dp)
        ) {
            IconButton(
                onClick = { showLanguageMenu = true },
                modifier = Modifier.size(48.dp) // "konjam big ga irukanum"
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "Select Language",
                    modifier = Modifier.fillMaxSize(),
                    tint = colorScheme.primary
                )
            }
            
            DropdownMenu(
                expanded = showLanguageMenu,
                onDismissRequest = { showLanguageMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("English") },
                    onClick = { 
                        showLanguageMenu = false 
                        com.bitchat.android.util.LocaleManager.setLocale(context, "en")
                        (context as? android.app.Activity)?.recreate()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Hindi (हिंदी)") },
                    onClick = { 
                        showLanguageMenu = false 
                        com.bitchat.android.util.LocaleManager.setLocale(context, "hi")
                        (context as? android.app.Activity)?.recreate()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Tamil (தமிழ்)") },
                    onClick = { 
                        showLanguageMenu = false 
                        com.bitchat.android.util.LocaleManager.setLocale(context, "ta")
                        (context as? android.app.Activity)?.recreate()
                    }
                )
            }
        }

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
                // Placeholder for potential "Sign In" (restore backup) functionality
                // For now, it flows into the same onboarding
                // Removed "Already have an account" as per user request
            }
        }
    }
}
