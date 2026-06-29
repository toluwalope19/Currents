package com.app.currents.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.currents.domain.model.Category
import com.app.currents.presentation.profile.*
import com.app.currents.ui.theme.Accent
import com.app.currents.ui.theme.CurrentsIcons
import com.app.currents.ui.theme.toColor
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreen(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onSignOut: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ProfileUiEffect.NavigateToOnboarding -> onSignOut()
                ProfileUiEffect.ShowEditProfile -> { /* TODO */ }
                ProfileUiEffect.ShowComingSoon -> { /* TODO: show snackbar */ }
            }
        }
    }

    ProfileContent(
        uiState = uiState.copy(isDarkTheme = isDarkTheme),
        onEvent = { event ->
            if (event is ProfileUiEvent.OnThemeToggle) onThemeToggle()
            else viewModel.onEvent(event)
        },
    )
}

@Composable
private fun ProfileContent(
    uiState: ProfileUiState,
    onEvent: (ProfileUiEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        Spacer(Modifier.height(12.dp))

        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Profile",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Icon(
                painter = painterResource(CurrentsIcons.Settings),
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .size(22.dp)
                    .clickable { onEvent(ProfileUiEvent.OnSettings) },
            )
        }

        Spacer(Modifier.height(20.dp))

        // Avatar + name + email + edit
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Accent),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = uiState.name.take(1).uppercase(),
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Column {
                    Text(
                        text = uiState.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = uiState.email,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                    .clickable { onEvent(ProfileUiEvent.OnEditProfile) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Text(
                    text = "✎ Edit",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // Stats row — reuse same pattern as Bookmarks
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ProfileStatItem(Modifier.weight(1f), uiState.following.toString(), "Following")
            ProfileStatItem(Modifier.weight(1f), uiState.saved.toString(), "Saved")
            ProfileStatItem(Modifier.weight(1f), uiState.dayStreak.toString(), "Day streak")
        }

        Spacer(Modifier.height(20.dp))

        // Currents+ card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF6B4FE0), Color(0xFF9B7FFF))
                    )
                )
                .padding(16.dp),
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⭐", fontSize = 16.sp)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Currents+",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Ad-free reading, unlimited AI summaries, and full archive access.",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    lineHeight = 18.sp,
                )
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .clickable { onEvent(ProfileUiEvent.OnUpgrade) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = "Upgrade — \$4.99/mo",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // YOUR INTERESTS
        SectionLabel("YOUR INTERESTS")
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Manage",
                fontSize = 13.sp,
                color = Accent,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onEvent(ProfileUiEvent.OnManageInterests) },
            )
        }
        Spacer(Modifier.height(8.dp))
        androidx.compose.foundation.layout.FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            uiState.interests.forEach { category ->
                InterestChip(category)
            }
            // + Add chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
                    .clickable { onEvent(ProfileUiEvent.OnManageInterests) }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text(
                    text = "+ Add",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // READING section
        SectionLabel("READING")
        Spacer(Modifier.height(8.dp))
        SettingsItem(
            icon = CurrentsIcons.History,
            title = "Reading history",
            subtitle = "142 articles this month",
            onClick = { onEvent(ProfileUiEvent.OnReadingHistory) },
        )
        SettingsItem(
            icon = CurrentsIcons.Offline,
            title = "Saved for offline",
            subtitle = "3 articles · 8.2 MB",
            onClick = { onEvent(ProfileUiEvent.OnSavedForOffline) },
        )
        SettingsItemToggle(
            icon = CurrentsIcons.Bell,
            title = "Notifications",
            checked = uiState.notificationsEnabled,
            onToggle = { onEvent(ProfileUiEvent.OnToggleNotifications) },
        )

        Spacer(Modifier.height(24.dp))

        // PREFERENCES section
        SectionLabel("PREFERENCES")
        Spacer(Modifier.height(8.dp))
        SettingsItemThemeToggle(
            icon = CurrentsIcons.Appearance,
            title = "Appearance",
            isDark = uiState.isDarkTheme,
            onToggle = { onEvent(ProfileUiEvent.OnThemeToggle(!uiState.isDarkTheme)) },
        )
        SettingsItem(
            icon = CurrentsIcons.TextSize,
            title = "Text size",
            subtitle = "Default",
            onClick = { onEvent(ProfileUiEvent.OnTextSize) },
        )
        SettingsItem(
            icon = CurrentsIcons.Globe,
            title = "Region & language",
            subtitle = "United States · English",
            onClick = { onEvent(ProfileUiEvent.OnRegionLanguage) },
        )

        Spacer(Modifier.height(24.dp))

        // ACCOUNT section
        SectionLabel("ACCOUNT")
        Spacer(Modifier.height(8.dp))
        SettingsItem(
            icon = CurrentsIcons.Privacy,
            title = "Privacy & data",
            onClick = { onEvent(ProfileUiEvent.OnPrivacyData) },
        )
        SettingsItem(
            icon = CurrentsIcons.Help,
            title = "Help & support",
            onClick = { onEvent(ProfileUiEvent.OnHelpSupport) },
        )

        Spacer(Modifier.height(24.dp))

        // Sign out
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, Color(0xFFE53935).copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                .clickable { onEvent(ProfileUiEvent.OnSignOut) }
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "→ Sign out",
                color = Color(0xFFE53935),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Currents v4.2.0",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(24.dp))
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

@Composable
private fun ProfileStatItem(modifier: Modifier, value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 14.dp, vertical = 14.dp),
    ) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun InterestChip(category: Category) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, category.toColor().copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(category.toColor()),
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = category.label,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun SettingsItem(
    icon: org.jetbrains.compose.resources.DrawableResource,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(18.dp),
            )
        }
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Icon(
            painter = painterResource(CurrentsIcons.ChevronRight),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp),
        )
    }
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
        thickness = 0.5.dp,
    )
}

@Composable
private fun SettingsItemToggle(
    icon: org.jetbrains.compose.resources.DrawableResource,
    title: String,
    checked: Boolean,
    onToggle: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(18.dp),
            )
        }
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        Switch(
            checked = checked,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Accent,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        )
    }
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
        thickness = 0.5.dp,
    )
}

@Composable
private fun SettingsItemThemeToggle(
    icon: org.jetbrains.compose.resources.DrawableResource,
    title: String,
    isDark: Boolean,
    onToggle: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(18.dp),
            )
        }
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        // Exact same pill as HomeTopBar
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(100.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(100.dp),
                )
                .padding(horizontal = 4.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Sun
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(if (!isDark) Accent else Color.Transparent)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { if (isDark) onToggle() },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(CurrentsIcons.Sun),
                    contentDescription = "Light mode",
                    tint = if (!isDark) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp),
                )
            }
            // Moon
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Accent else Color.Transparent)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { if (!isDark) onToggle() },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(CurrentsIcons.Moon),
                    contentDescription = "Dark mode",
                    tint = if (isDark) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
        thickness = 0.5.dp,
    )
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
private fun ProfileScreenDarkPreview() {
    com.app.currents.ui.theme.CurrentsTheme(darkTheme = true) {
        ProfileContent(
            uiState = ProfileUiState(isDarkTheme = true),
            onEvent = {},
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
private fun ProfileScreenLightPreview() {
    com.app.currents.ui.theme.CurrentsTheme(darkTheme = false) {
        ProfileContent(
            uiState = ProfileUiState(isDarkTheme = false),
            onEvent = {},
        )
    }
}