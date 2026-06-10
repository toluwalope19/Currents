package com.app.currents.ui.screens.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.currents.domain.model.Category
import com.app.currents.presentation.onboarding.OnboardingUiEvent
import com.app.currents.presentation.onboarding.OnboardingViewModel
import com.app.currents.ui.components.ScatteredBubbleLayout
import com.app.currents.ui.components.TopicBubble
import com.app.currents.ui.theme.Accent
import com.app.currents.ui.theme.CurrentsTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OnboardingScreen(
    onContinue: () -> Unit,
    onSkip: () -> Unit,
    viewModel: OnboardingViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState

    OnboardingContent(
        selectedCategories = uiState.selectedCategories,
        onCategoryToggled = { viewModel.onEvent(OnboardingUiEvent.OnCategoryToggled(it)) },
        onContinue = {
            viewModel.onEvent(OnboardingUiEvent.OnContinue)
            onContinue()
        },
        onSkip = {
            viewModel.onEvent(OnboardingUiEvent.OnSkip)
            onSkip()
        },
    )
}

@Composable
private fun OnboardingContent(
    selectedCategories: List<Category>,
    onCategoryToggled: (Category) -> Unit,
    onContinue: () -> Unit,
    onSkip: () -> Unit,
) {
    val minimumMet = selectedCategories.size >= 3
    val isDark = MaterialTheme.colorScheme.background == Color(0xFF0A0A0A)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        // Ambient glows — same as splash
        Box(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.BottomStart)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF00B4D8).copy(alpha = 0.2f),
                            Color.Transparent,
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.TopEnd)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Accent.copy(alpha = 0.15f),
                            Color.Transparent,
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.BottomEnd)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFE63946).copy(alpha = 0.15f),
                            Color.Transparent,
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Top bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    ,
            ) {
                TextButton(
                    onClick = onSkip,
                    modifier = Modifier.align(Alignment.CenterStart),
                ) {
                    Text(
                        text = "Skip",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 15.sp,
                    )
                }

                TextButton(
                    onClick = { if (minimumMet) onContinue() },
                    modifier = Modifier.align(Alignment.CenterEnd),
                    enabled = minimumMet,
                ) {
                    Text(
                        text = if (selectedCategories.isEmpty()) "Done"
                        else "Done (${selectedCategories.size})",
                        color = if (minimumMet) Accent
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Headline
            Text(
                text = "What are you into?",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Pick 3 or more topics to personalise your feed",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Bubble layout
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                ScatteredBubbleLayout(
                    categories = Category.onboardingTopics,
                    selectedCategories = selectedCategories,
                    onCategoryToggled = onCategoryToggled,
                )
            }

            // Bottom counter
            val counterColor by animateColorAsState(
                targetValue = if (minimumMet) Color(0xFF5AE9C8)
                else MaterialTheme.colorScheme.onSurfaceVariant,
                animationSpec = spring(stiffness = Spring.StiffnessMedium),
                label = "counterColor",
            )

            Text(
                text = if (minimumMet) "${selectedCategories.size} of 3 minimum ✓"
                else "${selectedCategories.size} of 3 minimum",
                color = counterColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Continue button
            Button(
                onClick = { if (minimumMet) onContinue() },
                enabled = minimumMet,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Accent,
                    disabledContainerColor = Accent.copy(alpha = 0.4f),
                ),
            ) {
                Text(
                    text = "Continue",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}



@Preview
@Composable
private fun OnboardingScreenDarkPreview() {
    CurrentsTheme(darkTheme = true) {
        OnboardingContent(
            selectedCategories = listOf(
                Category.Technology,
                Category.Sports,
                Category.Health,
            ),
            onCategoryToggled = {},
            onContinue = {},
            onSkip = {},
        )
    }
}

@Preview
@Composable
private fun OnboardingScreenLightPreview() {
    CurrentsTheme(darkTheme = false) {
        OnboardingContent(
            selectedCategories = emptyList(),
            onCategoryToggled = {},
            onContinue = {},
            onSkip = {},
        )
    }
}