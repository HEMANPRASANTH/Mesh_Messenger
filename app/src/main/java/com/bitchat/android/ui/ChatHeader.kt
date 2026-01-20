package com.bitchat.android.ui


import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.res.stringResource
import com.bitchat.android.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bitchat.android.core.ui.utils.singleOrTripleClickable
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Header components for ChatScreen
 * Extracted from ChatScreen.kt for better organization
 */




@Composable
fun TorStatusDot(
    modifier: Modifier = Modifier
) {
    val torProvider = remember { com.bitchat.android.net.ArtiTorManager.getInstance() }
    val torStatus by torProvider.statusFlow.collectAsState()
    
    if (torStatus.mode != com.bitchat.android.net.TorMode.OFF) {
        val dotColor = when {
            torStatus.running && torStatus.bootstrapPercent < 100 -> Color(0xFFFF9500) // Orange - bootstrapping
            torStatus.running && torStatus.bootstrapPercent >= 100 -> Color(0xFF00E5FF) // Ice Blue - connected
            else -> Color.Red // Red - error/disconnected
        }
        Canvas(
            modifier = modifier
        ) {
            val radius = size.minDimension / 2
            drawCircle(
                color = dotColor,
                radius = radius,
                center = Offset(size.width / 2, size.height / 2)
            )
        }
    }
}

@Composable
fun NoiseSessionIcon(
    sessionState: String?,
    modifier: Modifier = Modifier
) {
    val (icon, color, contentDescription) = when (sessionState) {
        "uninitialized" -> Triple(
            Icons.Outlined.NoEncryption,
            Color(0x87878700), // Grey - ready to establish
            stringResource(R.string.cd_ready_for_handshake)
        )
        "handshaking" -> Triple(
            Icons.Outlined.Sync,
            Color(0x87878700), // Grey - in progress
            stringResource(R.string.cd_handshake_in_progress)
        )
        "established" -> Triple(
            Icons.Filled.Lock,
            Color(0xFFFF9500), // Orange - secure
            stringResource(R.string.cd_encrypted)
        )
        else -> { // "failed" or any other state
            Triple(
                Icons.Outlined.Warning,
                Color(0xFFFF4444), // Red - error
                stringResource(R.string.cd_handshake_failed)
            )
        }
    }
    
    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = color
    )
}

@Composable
fun NicknameEditor(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    
    // Auto-scroll to end when text changes (simulates cursor following)
    LaunchedEffect(value) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.at_symbol),
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.primary.copy(alpha = 0.8f)
        )
        
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = colorScheme.primary,
                fontFamily = FontFamily.Monospace
            ),
            cursorBrush = SolidColor(colorScheme.primary),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { 
                    focusManager.clearFocus()
                }
            ),
            modifier = Modifier
                .widthIn(max = 120.dp)
                .horizontalScroll(scrollState)
        )
    }
}

@Composable
fun PeerCounter(
    connectedPeers: List<String>,
    joinedChannels: Set<String>,
    hasUnreadChannels: Map<String, Int>,
    isConnected: Boolean,
    selectedLocationChannel: com.bitchat.android.geohash.ChannelID?,
    geohashPeople: List<GeoPerson>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    
    // Compute channel-aware people count and color (matches iOS logic exactly)
    val (peopleCount, countColor) = when (selectedLocationChannel) {
        is com.bitchat.android.geohash.ChannelID.Location -> {
            // Geohash channel: show geohash participants
            val count = geohashPeople.size
            val green = Color(0xFF00E5FF) // Ice Blue for location
            Pair(count, if (count > 0) green else Color.Gray)
        }
        is com.bitchat.android.geohash.ChannelID.Mesh,
        null -> {
            // Mesh channel: show Bluetooth-connected peers (excluding self)
            val count = connectedPeers.size
            val meshBlue = Color(0xFF007AFF) // iOS-style blue for mesh
            Pair(count, if (isConnected && count > 0) meshBlue else Color.Gray)
        }
    }
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.clickable { onClick() }.padding(end = 8.dp) // Added right margin to match "bitchat" logo spacing
    ) {
        Icon(
            imageVector = Icons.Default.Group,
            contentDescription = when (selectedLocationChannel) {
                is com.bitchat.android.geohash.ChannelID.Location -> stringResource(R.string.cd_geohash_participants)
                else -> stringResource(R.string.cd_connected_peers)
            },
            modifier = Modifier.size(16.dp),
            tint = countColor
        )
        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = "$peopleCount",
            style = MaterialTheme.typography.bodyMedium,
            color = countColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        
        if (joinedChannels.isNotEmpty()) {
            Text(
                text = stringResource(R.string.channel_count_prefix) + "${joinedChannels.size}",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isConnected) Color(0xFF00E5FF) else Color.Red,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ChatHeaderContent(
    selectedPrivatePeer: String?,
    currentChannel: String?,
    nickname: String,
    viewModel: ChatViewModel,
    onBackClick: () -> Unit,
    onSidebarClick: () -> Unit,
    onTripleClick: () -> Unit,
    onShowAppInfo: () -> Unit,
    onLocationChannelsClick: () -> Unit,
    onLocationNotesClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    when {
        currentChannel != null -> {
            // Channel header
            ChannelHeader(
                channel = currentChannel,
                onBackClick = onBackClick,
                onLeaveChannel = { viewModel.leaveChannel(currentChannel) },
                onSidebarClick = onSidebarClick
            )
        }
        else -> {
            // Main header
            MainHeader(
                nickname = nickname,
                onNicknameChange = viewModel::setNickname,
                onTitleClick = onShowAppInfo,
                onTripleTitleClick = onTripleClick,
                onSidebarClick = onSidebarClick,
                onLocationChannelsClick = onLocationChannelsClick,
                onLocationNotesClick = onLocationNotesClick,
                viewModel = viewModel
            )
        }
    }
}



@Composable
private fun ChannelHeader(
    channel: String,
    onBackClick: () -> Unit,
    onLeaveChannel: () -> Unit,
    onSidebarClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    
    Box(modifier = Modifier.fillMaxWidth()) {
        // Back button - positioned all the way to the left with minimal margin
        Button(
            onClick = onBackClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = colorScheme.primary
            ),
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp), // Reduced horizontal padding
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = (-8).dp) // Move even further left to minimize margin
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    modifier = Modifier.size(16.dp),
                    tint = colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.chat_back),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.primary
                )
            }
        }
        
        // Title - perfectly centered regardless of other elements
        Text(
            text = stringResource(R.string.chat_channel_prefix, channel),
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFFFF9500), // Orange to match input field
            modifier = Modifier
                .align(Alignment.Center)
                .clickable { onSidebarClick() }
        )
        
        // Leave button - positioned on the right
        TextButton(
            onClick = onLeaveChannel,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Text(
                text = stringResource(R.string.chat_leave),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Red
            )
        }
    }
}

@Composable
private fun MainHeader(
    nickname: String,
    onNicknameChange: (String) -> Unit,
    onTitleClick: () -> Unit,
    onTripleTitleClick: () -> Unit,
    onSidebarClick: () -> Unit,
    onLocationChannelsClick: () -> Unit,
    onLocationNotesClick: () -> Unit,
    viewModel: ChatViewModel
) {
    val colorScheme = MaterialTheme.colorScheme
    val connectedPeers by viewModel.connectedPeers.collectAsStateWithLifecycle()
    val joinedChannels by viewModel.joinedChannels.collectAsStateWithLifecycle()
    val isConnected by viewModel.isConnected.collectAsStateWithLifecycle()
    val selectedLocationChannel by viewModel.selectedLocationChannel.collectAsStateWithLifecycle()
    val geohashPeople by viewModel.geohashPeople.collectAsStateWithLifecycle()
    val hasUnreadPrivateMessages by viewModel.unreadPrivateMessages.collectAsStateWithLifecycle()
    
    // Tor & PoW Status for Menu
    val context = androidx.compose.ui.platform.LocalContext.current
    val torProvider = remember { com.bitchat.android.net.ArtiTorManager.getInstance() }
    val torStatus by torProvider.statusFlow.collectAsState()
    
    val powEnabled by com.bitchat.android.nostr.PoWPreferenceManager.powEnabled.collectAsState()
    val powDifficulty by com.bitchat.android.nostr.PoWPreferenceManager.powDifficulty.collectAsState()

    // Menu State
    var showMenu by remember { mutableStateOf(false) }

    // Header Container "Neat Board"
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp) // Increased vertical padding
            .height(72.dp), // Bigger "Board"
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp), // More rounded
        color = colorScheme.surface.copy(alpha = 0.6f), // Glassy background
        border = androidx.compose.foundation.BorderStroke(1.dp, colorScheme.outline.copy(alpha = 0.2f)),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Nickname Only (Brand Removed)
            Row(verticalAlignment = Alignment.CenterVertically) {
                NicknameEditor(
                    value = nickname,
                    onValueChange = onNicknameChange
                )
            }

            // Right: Shield + Hamburger Menu
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Large Shield Icon
                Icon(
                    imageVector = Icons.Outlined.Security,
                    contentDescription = "Security Status",
                    tint = Color(0xFF00E5FF), // Ice Blue
                    modifier = Modifier
                        .size(32.dp) // Large visibility
                        .padding(end = 12.dp)
                )

                Box {
                    IconButton(onClick = { showMenu = true }) {
                        // Badge for unread DMs on the menu icon
                        Box {
                             Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            if (hasUnreadPrivateMessages.isNotEmpty()) {
                                Badge(modifier = Modifier.align(Alignment.TopEnd))
                            }
                        }
                    }
                }

                // Premium Dropdown Menu
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(colorScheme.surface.copy(alpha = 0.95f))
                ) {
                    // 1. Network Status
                    DropdownMenuItem(
                        text = { 
                            Column {
                                Text("Network", style = MaterialTheme.typography.labelSmall, color = colorScheme.secondary)
                                val peerCount = connectedPeers.filter { it != viewModel.meshService.myPeerID }.size
                                Text(if (isConnected) "Connected: $peerCount Peers" else "Disconnected", color = if (isConnected) Color(0xFF00E5FF) else Color.Gray)
                            }
                        },
                        leadingIcon = { Icon(Icons.Default.Hub, contentDescription = null, tint = Color(0xFF00E5FF)) },
                        onClick = { 
                            onSidebarClick()
                            showMenu = false 
                        }
                    )
                    
                    // 2. Channel Info (#mesh)
                    val (channelName, channelColor) = when (selectedLocationChannel) {
                        is com.bitchat.android.geohash.ChannelID.Location -> {
                            val hash = (selectedLocationChannel as com.bitchat.android.geohash.ChannelID.Location).channel.geohash
                            "#$hash" to Color(0xFF00E5FF)
                        }
                        else -> "#mesh" to Color(0xFF007AFF)
                    }
                    
                    DropdownMenuItem(
                        text = { 
                            Column {
                                Text("Current Channel", style = MaterialTheme.typography.labelSmall, color = colorScheme.secondary)
                                Text(channelName, color = channelColor, fontFamily = FontFamily.Monospace) 
                            }
                        },
                        leadingIcon = { Icon(Icons.Default.Tag, contentDescription = null, tint = channelColor) },
                        onClick = { 
                            onLocationChannelsClick() 
                            showMenu = false 
                        }
                    )

                    HorizontalDivider(color = colorScheme.outline.copy(alpha = 0.2f))

                    // 3. Tor Status (if active)
                     if (torStatus.mode != com.bitchat.android.net.TorMode.OFF) {
                        DropdownMenuItem(
                            text = { Text("Tor Network: ${if (torStatus.running) "${torStatus.bootstrapPercent}%" else "Off"}") },
                            leadingIcon = { Icon(Icons.Outlined.Security, contentDescription = null, tint = Color(0xFFFF9500)) },
                            onClick = { /* No action, just status */ }
                        )
                    }

                    // 4. PoW Status (if active)
                    if (powEnabled) {
                        DropdownMenuItem(
                            text = { Text("PoW Security: $powDifficulty bits") },
                            leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = Color(0xFF00E5FF)) },
                            onClick = { /* No action */ }
                        )
                    }
                    
                    // 5. Unread DMs (if any)
                    if (hasUnreadPrivateMessages.isNotEmpty()) {
                         DropdownMenuItem(
                            text = { Text("Unread Messages") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFFFF9500)) },
                            onClick = { 
                                viewModel.openLatestUnreadPrivateChat()
                                showMenu = false
                            }
                        )
                    }
                    
                    // 6. Location Notes
                     DropdownMenuItem(
                        text = { Text("Location Notes") },
                        leadingIcon = { Icon(Icons.Outlined.Place, contentDescription = null) },
                        onClick = { 
                            onLocationNotesClick()
                            showMenu = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LocationChannelsButton(
    viewModel: ChatViewModel,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    
    // Get current channel selection from location manager
    val selectedChannel by viewModel.selectedLocationChannel.collectAsStateWithLifecycle()
    val teleported by viewModel.isTeleported.collectAsStateWithLifecycle()
    
    val (badgeText, badgeColor) = when (selectedChannel) {
        is com.bitchat.android.geohash.ChannelID.Mesh -> {
            "#mesh" to Color(0xFF007AFF) // iOS blue for mesh
        }
        is com.bitchat.android.geohash.ChannelID.Location -> {
            val geohash = (selectedChannel as com.bitchat.android.geohash.ChannelID.Location).channel.geohash
            "#$geohash" to Color(0xFF00E5FF) // Ice Blue for location
        }
        null -> "#mesh" to Color(0xFF007AFF) // Default to mesh
    }
    
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = badgeColor
        ),
        contentPadding = PaddingValues(start = 4.dp, end = 0.dp, top = 2.dp, bottom = 2.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = badgeText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace
                ),
                color = badgeColor,
                maxLines = 1
            )
            
            // Teleportation indicator (like iOS)
            if (teleported) {
                Spacer(modifier = Modifier.width(2.dp))
                Icon(
                    imageVector = Icons.Default.PinDrop,
                    contentDescription = stringResource(R.string.cd_teleported),
                    modifier = Modifier.size(12.dp),
                    tint = badgeColor
                )
            }
        }
    }
}
