package com.bitchat.android.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.SettingsSystemDaydream
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.bitchat.android.ui.theme.LocalThemeControl
import com.bitchat.android.ui.components.GlassCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {
    val themeControl = LocalThemeControl.current
    val colorScheme = MaterialTheme.colorScheme
    val context = androidx.compose.ui.platform.LocalContext.current
    
    // State for Advanced Settings
    LaunchedEffect(Unit) { com.bitchat.android.nostr.PoWPreferenceManager.init(context) }
    val powEnabled by com.bitchat.android.nostr.PoWPreferenceManager.powEnabled.collectAsState()
    val powDifficulty by com.bitchat.android.nostr.PoWPreferenceManager.powDifficulty.collectAsState()
    var backgroundEnabled by remember { mutableStateOf(com.bitchat.android.service.MeshServicePreferences.isBackgroundEnabled(true)) }
    
    val torMode = remember { mutableStateOf(com.bitchat.android.net.TorPreferenceManager.get(context)) }
    val torProvider = remember { com.bitchat.android.net.ArtiTorManager.getInstance() }
    val torStatus by torProvider.statusFlow.collectAsState()
    val torAvailable = remember { torProvider.isTorAvailable() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Appearance Section
            SettingsSection(title = "Appearance") {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "App Theme",
                            style = MaterialTheme.typography.titleMedium,
                            color = colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        ThemeOptionRow(
                            label = "System Default",
                            icon = Icons.Default.SettingsSystemDaydream,
                            selected = themeControl.currentMode == 0,
                            onClick = { themeControl.onThemeChanged(0) }
                        )
                        ThemeOptionRow(
                            label = "Light Mode",
                            icon = Icons.Default.LightMode,
                            selected = themeControl.currentMode == 1,
                            onClick = { themeControl.onThemeChanged(1) }
                        )
                        ThemeOptionRow(
                            label = "Dark Mode",
                            icon = Icons.Default.DarkMode,
                            selected = themeControl.currentMode == 2,
                            onClick = { themeControl.onThemeChanged(2) }
                        )
                    }
                }
            }

            // Advanced / Network Section
            SettingsSection(title = "Network & Advanced") {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Background Mode
                        SettingsToggleRow(
                            title = "Background Mesh",
                            subtitle = "Keep connection alive when app is closed",
                            checked = backgroundEnabled,
                            onCheckedChange = { enabled ->
                                backgroundEnabled = enabled
                                com.bitchat.android.service.MeshServicePreferences.setBackgroundEnabled(enabled)
                                if (!enabled) {
                                    com.bitchat.android.service.MeshForegroundService.stop(context)
                                } else {
                                    com.bitchat.android.service.MeshForegroundService.start(context)
                                }
                            }
                        )
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = colorScheme.outline.copy(alpha = 0.1f))

                        // PoW Toggle
                        SettingsToggleRow(
                            title = "Proof of Work (PoW)",
                            subtitle = "Enhance security for messages",
                            checked = powEnabled,
                            onCheckedChange = { com.bitchat.android.nostr.PoWPreferenceManager.setPowEnabled(it) }
                        )
                        
                        if (powEnabled) {
                             Spacer(modifier = Modifier.height(8.dp))
                             Text(
                                text = "Difficulty: $powDifficulty bits",
                                style = MaterialTheme.typography.bodySmall,
                                color = colorScheme.onSurface.copy(alpha = 0.7f)
                             )
                             Slider(
                                value = powDifficulty.toFloat(),
                                onValueChange = { com.bitchat.android.nostr.PoWPreferenceManager.setPowDifficulty(it.toInt()) },
                                valueRange = 0f..32f,
                                steps = 31
                             )
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = colorScheme.outline.copy(alpha = 0.1f))

                        // Tor Toggle
                        SettingsToggleRow(
                            title = "Tor Network",
                            subtitle = if (torAvailable) "Anonymize traffic via Tor" else "Tor is not available on this device",
                            checked = torMode.value == com.bitchat.android.net.TorMode.ON,
                            onCheckedChange = { enabled ->
                                if (torAvailable) {
                                    torMode.value = if (enabled) com.bitchat.android.net.TorMode.ON else com.bitchat.android.net.TorMode.OFF
                                    com.bitchat.android.net.TorPreferenceManager.set(context, torMode.value)
                                }
                            },
                            enabled = torAvailable
                        )
                        
                        if (torMode.value == com.bitchat.android.net.TorMode.ON) {
                             Spacer(modifier = Modifier.height(8.dp))
                             Text(
                                text = if (torStatus.running) "Connected: ${torStatus.bootstrapPercent}%" else "Connecting...",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (torStatus.running && torStatus.bootstrapPercent >= 100) Color(0xFF00E5FF) else Color.Gray // Ice Blue for connected
                             )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        content()
    }
}

@Composable
fun SettingsToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF00E5FF), // Ice Blue
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}

@Composable
fun ThemeOptionRow(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) Color(0xFF00E5FF) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            fontWeight = if (selected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
        )
        Spacer(modifier = Modifier.weight(1f))
        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = Color(0xFF00E5FF)
            )
        }
    }
}
