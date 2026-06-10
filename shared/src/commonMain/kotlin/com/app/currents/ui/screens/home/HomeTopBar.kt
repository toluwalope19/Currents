package com.app.currents.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.currents.ui.theme.Accent
import com.app.currents.ui.theme.CurrentsIcons
import com.app.currents.ui.theme.CurrentsTheme
import org.jetbrains.compose.resources.painterResource

@Composable
fun HomeTopBar(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onBellClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Logo — bolt + Currents
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(CurrentsIcons.Bolt),
                contentDescription = null,
                tint = Accent,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = "Currents",
                color = if (isDarkTheme) MaterialTheme.colorScheme.onBackground else Accent,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp,
            )
        }

        // Right side actions
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Theme toggle pill
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
                        .background(
                            if (!isDarkTheme) Accent else Color.Transparent
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { if (isDarkTheme) onThemeToggle() },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(CurrentsIcons.Sun),
                        contentDescription = "Light mode",
                        tint = if (!isDarkTheme) Color.White
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp),
                    )
                }

                // Moon
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(
                            if (isDarkTheme) Accent else Color.Transparent
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { if (!isDarkTheme) onThemeToggle() },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(CurrentsIcons.Moon),
                        contentDescription = "Dark mode",
                        tint = if (isDarkTheme) Color.White
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }

            // Bell — circular background container
            Box {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = CircleShape,
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onBellClick,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(CurrentsIcons.Bell),
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(18.dp),
                    )
                }
                // Red dot — top right of the container
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color(0xFFFF3B30), CircleShape)
                        .align(Alignment.TopEnd)
                )
            }

            // Avatar
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Accent),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "A",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Preview
@Composable
private fun HomeTopBarDarkPreview() {
    CurrentsTheme(darkTheme = true) {
        HomeTopBar(
            isDarkTheme = true,
            onThemeToggle = {},
            onBellClick = {},
        )
    }
}

@Preview
@Composable
private fun HomeTopBarLightPreview() {
    CurrentsTheme(darkTheme = false) {
        HomeTopBar(
            isDarkTheme = false,
            onThemeToggle = {},
            onBellClick = {},
        )
    }
}