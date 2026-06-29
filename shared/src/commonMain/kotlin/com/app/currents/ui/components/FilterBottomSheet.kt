package com.app.currents.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.currents.domain.model.Category
import com.app.currents.ui.theme.Accent
import com.app.currents.ui.theme.CurrentsIcons
import com.app.currents.ui.theme.CurrentsTheme
import com.app.currents.ui.theme.toColor
import org.jetbrains.compose.resources.painterResource

data class ExploreFilter(
    val selectedCategories: Set<Category> = setOf(Category.World, Category.Technology),
    val showLive: Boolean = true,
    val showBreaking: Boolean = true,
    val showArticles: Boolean = true,
    val showVideo: Boolean = false,
    val selectedRegion: String = "Global",
    val selectedTimeFrame: String = "Any time",
)

val filterCategories = listOf(
    Category.World,
    Category.Technology,
    Category.Business,
    Category.Sports,
    Category.Health,
    Category.Science,
    Category.Politics,
    Category.Culture,
    Category.Entertainment,
    Category.Food,
)

val regions = listOf("Global", "North America", "Europe", "Asia", "Africa", "Middle East")
val timeFrames = listOf("Any time", "Last hour", "Today", "This week", "This month")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    filter: ExploreFilter,
    onFilterChanged: (ExploreFilter) -> Unit,
    onDismiss: () -> Unit,
    onApply: (ExploreFilter) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    var currentFilter by remember { mutableStateOf(filter) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Filters",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Reset",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { currentFilter = ExploreFilter() },
                        ),
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            painter = painterResource(CurrentsIcons.Close),
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // Categories section
            FilterSectionHeader(
                title = "CATEGORIES",
                trailing = "${currentFilter.selectedCategories.size} selected",
            )

            Spacer(modifier = Modifier.height(12.dp))

            FilterCategoryChips(
                categories = filterCategories,
                selectedCategories = currentFilter.selectedCategories,
                onCategoryToggled = { category ->
                    val updated = currentFilter.selectedCategories.toMutableSet()
                    if (updated.contains(category)) updated.remove(category)
                    else updated.add(category)
                    currentFilter = currentFilter.copy(selectedCategories = updated)
                },
                modifier = Modifier.padding(horizontal = 20.dp),
            )

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // Content Type section
            FilterSectionHeader(title = "CONTENT TYPE")
            Spacer(modifier = Modifier.height(8.dp))

            ContentTypeRow(
                label = "Live now",
                iconRes = CurrentsIcons.Live,
                iconTint = Color(0xFFFF3B30),
                checked = currentFilter.showLive,
                onCheckedChange = { currentFilter = currentFilter.copy(showLive = it) },
            )
            ContentTypeRow(
                label = "Breaking",
                iconRes = CurrentsIcons.Bolt,
                iconTint = Accent,
                checked = currentFilter.showBreaking,
                onCheckedChange = { currentFilter = currentFilter.copy(showBreaking = it) },
            )
            ContentTypeRow(
                label = "Articles",
                iconRes = CurrentsIcons.Bookmark,
                iconTint = Color(0xFF3FB7FF),
                checked = currentFilter.showArticles,
                onCheckedChange = { currentFilter = currentFilter.copy(showArticles = it) },
            )
            ContentTypeRow(
                label = "Video",
                iconRes = CurrentsIcons.Play,
                iconTint = Color(0xFF5AE9C8),
                checked = currentFilter.showVideo,
                onCheckedChange = { currentFilter = currentFilter.copy(showVideo = it) },
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // Region section
            FilterSectionHeader(title = "REGION")
            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(regions) { region ->
                    RegionPill(
                        label = region,
                        isSelected = currentFilter.selectedRegion == region,
                        onClick = { currentFilter = currentFilter.copy(selectedRegion = region) },
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // Time Frame section
            FilterSectionHeader(title = "TIME FRAME")
            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(timeFrames) { timeFrame ->
                    RegionPill(
                        label = timeFrame,
                        isSelected = currentFilter.selectedTimeFrame == timeFrame,
                        onClick = {
                            currentFilter = currentFilter.copy(selectedTimeFrame = timeFrame)
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Apply button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Accent)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            onFilterChanged(currentFilter)
                            onApply(currentFilter)
                        },
                    )
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Show results",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}







@Preview
@Composable
private fun FilterBottomSheetDarkPreview() {
    CurrentsTheme(darkTheme = true) {
        FilterBottomSheet(
            filter = ExploreFilter(),
            onFilterChanged = {},
            onDismiss = {},
            onApply = {},
        )
    }
}

@Preview
@Composable
private fun FilterBottomSheetLightPreview() {
    CurrentsTheme(darkTheme = false) {
        FilterBottomSheet(
            filter = ExploreFilter(),
            onFilterChanged = {},
            onDismiss = {},
            onApply = {},
        )
    }
}