package com.app.currents.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.currents.domain.model.Category
import com.app.currents.ui.theme.CurrentsIcons
import com.app.currents.ui.theme.toColor
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterCategoryChips(
    categories: List<Category>,
    selectedCategories: Set<Category>,
    onCategoryToggled: (Category) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        categories.forEach { category ->
            val isSelected = selectedCategories.contains(category)
            val categoryColor = category.toColor()

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .border(
                        width = 1.dp,
                        color = if (isSelected) categoryColor else MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(100.dp),
                    )
                    .background(
                        if (isSelected) categoryColor.copy(alpha = 0.12f)
                        else Color.Transparent
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onCategoryToggled(category) },
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Colored dot
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(categoryColor, CircleShape)
                )

                Text(
                    text = category.label,
                    color = if (isSelected) categoryColor
                    else MaterialTheme.colorScheme.onSurface,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                )

                // Checkmark when selected
                if (isSelected) {
                    Icon(
                        painter = painterResource(CurrentsIcons.Check),
                        contentDescription = null,
                        tint = categoryColor,
                        modifier = Modifier.size(12.dp),
                    )
                }
            }
        }
    }
}