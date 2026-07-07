package com.app.currents.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.currents.ui.theme.Accent
import com.app.currents.ui.theme.CurrentsIcons
import com.app.currents.ui.theme.CurrentsTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun SplashScreen(
    onSplashComplete: (showOnboarding: Boolean) -> Unit,
    isOnboardingComplete: Boolean?,
) {
    val scale = remember { Animatable(0.8f) }
    val iconAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val textOffsetY = remember { Animatable(30f) }
    val bottomAlpha = remember { Animatable(0f) }
    val shouldShowOnboarding = remember { mutableStateOf<Boolean?>(null) }

    // Pulse ring
    val pulseTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "pulseScale",
    )
    val pulseAlpha by pulseTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "pulseAlpha",
    )

    // Indeterminate progress bar
    val progressTransition = rememberInfiniteTransition(label = "progress")
    val progressOffset by progressTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "progressOffset",
    )

    LaunchedEffect(isOnboardingComplete) {
        if (isOnboardingComplete != null && shouldShowOnboarding.value == null) {
            shouldShowOnboarding.value = !(isOnboardingComplete)
        }
    }


    LaunchedEffect(Unit) {
        // Step 1 — icon scales + fades in
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(1200, easing = FastOutSlowInEasing),
            )
        }
        launch {
            iconAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(1200),
            )
        }

        // Step 2 — text slides up + fades in (400ms after icon starts)
        delay(400)
        launch {
            textAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(800),
            )
        }
        launch {
            textOffsetY.animateTo(
                targetValue = 0f,
                animationSpec = tween(800, easing = FastOutSlowInEasing),
            )
        }

        // Step 3 — bottom elements fade in (300ms after text starts)
        delay(300)
        bottomAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(600),
        )

        delay(4000)
        val showOnboarding = shouldShowOnboarding.value ?: false
        onSplashComplete(showOnboarding)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D12)),
        contentAlignment = Alignment.Center,
    ) {
        // Ambient teal glow — bottom left
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.BottomStart)
                .graphicsLayer {
                    translationX = -60.dp.toPx()
                    translationY = 60.dp.toPx()
                }
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF00B4D8).copy(alpha = 0.25f),
                            Color.Transparent,
                        )
                    )
                )
        )

        // Ambient red/pink glow — bottom right
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.BottomEnd)
                .graphicsLayer {
                    translationX = 60.dp.toPx()
                    translationY = 60.dp.toPx()
                }
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFE63946).copy(alpha = 0.2f),
                            Color.Transparent,
                        )
                    )
                )
        )

        // Purple ambient glow behind icon
        Box(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.Center)
                .graphicsLayer { this.alpha = iconAlpha.value }
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Accent.copy(alpha = 0.25f),
                            Color.Transparent,
                        )
                    )
                )
        )

        // Icon + text column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Icon with pulse ring
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(140.dp)
                    .graphicsLayer { this.alpha = iconAlpha.value },
            ) {
                // Expanding pulse ring
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .scale(pulseScale)
                        .graphicsLayer { this.alpha = pulseAlpha }
                        .border(
                            width = 1.5.dp,
                            color = Accent.copy(alpha = pulseAlpha),
                            shape = RoundedCornerShape((24 * pulseScale).dp),
                        )
                )

                // App icon
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .scale(scale.value)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Accent),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(CurrentsIcons.Bolt),
                        contentDescription = "Currents",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp),
                    )
                }
            }

            // Text slides up + fades in
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.graphicsLayer {
                    this.alpha = textAlpha.value
                    translationY = textOffsetY.value
                },
            ) {
                Text(
                    text = "Currents",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.6).sp,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "The world, in focus.",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                )
            }
        }

        // Indeterminate progress bar — fades in with bottom elements
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 72.dp)
                .graphicsLayer { this.alpha = bottomAlpha.value }
                .size(width = 120.dp, height = 2.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(Color.White.copy(alpha = 0.2f)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.4f)
                    .graphicsLayer {
                        translationX = progressOffset * 120.dp.toPx()
                    }
                    .background(Accent)
            )
        }

        // Bottom label — fades in with bottom elements
        Text(
            text = "CURRENTS NEWS",
            color = Color.White.copy(alpha = 0.3f),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 2.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
                .graphicsLayer { this.alpha = bottomAlpha.value },
        )
    }
}

@Preview
@Composable
private fun SplashScreenDarkPreview() {
    CurrentsTheme(darkTheme = true) {
        SplashScreen(
            onSplashComplete = {},
            isOnboardingComplete = false,
        )
    }
}