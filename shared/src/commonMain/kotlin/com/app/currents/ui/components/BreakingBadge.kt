package com.app.currents.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.currents.ui.theme.CategoryBreaking
import com.app.currents.ui.theme.CurrentsTheme
import androidx.compose.foundation.background

@Composable
fun BreakingBadge(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .border(
                width = 1.dp,
                color = CategoryBreaking,
                shape = RoundedCornerShape(100.dp),
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(
                    color = CategoryBreaking,
                    shape = CircleShape,
                )
        )
        Text(
            text = "BREAKING",
            color = CategoryBreaking,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
        )
    }
}

@Preview
@Composable
private fun BreakingBadgeDarkPreview() {
    CurrentsTheme(darkTheme = true) {
        BreakingBadge()
    }
}

@Preview
@Composable
private fun BreakingBadgeLightPreview() {
    CurrentsTheme(darkTheme = false) {
        BreakingBadge()
    }
}