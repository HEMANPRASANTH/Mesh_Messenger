package com.bitchat.android.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bitchat.android.ui.components.GlassButton
import com.bitchat.android.ui.components.GlassCard
import com.bitchat.android.ui.components.GlassTextField

@Composable
fun AccountContent(
    currentNickname: String,
    onNicknameChanged: (String) -> Unit,
    onOpenFeatures: () -> Unit,
    onOpenSettings: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(currentNickname) }
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar Placeholder
        GlassCard(
            modifier = Modifier.size(120.dp),
            shape = androidx.compose.foundation.shape.CircleShape
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = colorScheme.primary
                )
            }
        }

        // Name Section
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isEditing) {
                    GlassTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        label = "Nickname",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    GlassButton(
                        text = "Save",
                        onClick = {
                            onNicknameChanged(editedName)
                            isEditing = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(
                        text = currentNickname,
                        style = MaterialTheme.typography.headlineSmall,
                        color = colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = { isEditing = true }) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Change Name")
                    }
                }
            }
        }

        // Features Button
        GlassButton(
            text = "Features",
            onClick = onOpenFeatures,
            modifier = Modifier.fillMaxWidth()
        )

        // Settings Button
        GlassButton(
            text = "Settings",
            onClick = onOpenSettings,
            modifier = Modifier.fillMaxWidth()
        )

        // Create Account Button (Requested Feature)
        GlassButton(
            text = "Create Account",
            onClick = { /* TODO: Implement Account Creation Flow */ },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        // Logout Button
        Spacer(modifier = Modifier.weight(1f))

        // Logout Premium Box
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onLogout() },
            backgroundColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f), // Subtle red tint
            borderColor = MaterialTheme.colorScheme.error.copy(alpha = 0.5f) // Red border
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Log Out",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    currentNickname: String,
    onNicknameChanged: (String) -> Unit,
    onOpenFeatures: () -> Unit,
    onOpenSettings: () -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Account") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = colorScheme.primary,
                    navigationIconContentColor = colorScheme.onBackground
                )
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        AccountContent(
            currentNickname = currentNickname,
            onNicknameChanged = onNicknameChanged,
            onOpenFeatures = onOpenFeatures,
            onOpenSettings = onOpenSettings,
            onLogout = onLogout,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        )
    }
}
