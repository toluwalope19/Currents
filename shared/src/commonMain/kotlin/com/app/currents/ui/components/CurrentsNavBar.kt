package com.app.currents.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.currents.ui.theme.Accent
import com.app.currents.ui.theme.CurrentsIcons
import com.app.currents.ui.theme.CurrentsTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

enum class NavTab {
    Home, Explore, Search, Bookmarks, Profile
}

private data class NavItem(
    val tab: NavTab,
    val inactiveIcon: DrawableResource,
    val activeIcon: DrawableResource,
)

private val navItems = listOf(
    NavItem(NavTab.Home,      CurrentsIcons.Home,     CurrentsIcons.HomeFill),
    NavItem(NavTab.Explore,   CurrentsIcons.Grid,     CurrentsIcons.GridFill),
    NavItem(NavTab.Search,    CurrentsIcons.Search,   CurrentsIcons.Search),
    NavItem(NavTab.Bookmarks, CurrentsIcons.Bookmark, CurrentsIcons.BookmarkFill),
    NavItem(NavTab.Profile,   CurrentsIcons.Person,   CurrentsIcons.Person),
)

@Composable
fun CurrentsNavBar(
    selectedTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface),
    ) {
        HorizontalDivider(
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outline,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            navItems.forEach { item ->
                NavBarItem(
                    item = item,
                    isSelected = selectedTab == item.tab,
                    onSelected = { onTabSelected(item.tab) },
                )
            }
        }
    }
}

@Composable
private fun NavBarItem(
    item: NavItem,
    isSelected: Boolean,
    onSelected: () -> Unit,
) {
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) Accent else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "iconColor",
    )

    val offsetY by animateFloatAsState(
        targetValue = if (isSelected) -10f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "offsetY",
    )

    Column(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onSelected,
            )
            .padding(horizontal = 16.dp)
            .height(56.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            painter = painterResource(
                if (isSelected) item.activeIcon else item.inactiveIcon
            ),
            contentDescription = item.tab.name,
            tint = iconColor,
            modifier = Modifier
                .size(24.dp)
                .graphicsLayer { translationY = offsetY },
        )

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .size(4.dp)
                .background(
                    color = if (isSelected) Accent else Color.Transparent,
                    shape = CircleShape,
                )
        )
    }
}

@Preview
@Composable
private fun CurrentsNavBarPreview() {
    CurrentsTheme(darkTheme = true) {
        CurrentsNavBar(
            selectedTab = NavTab.Home,
            onTabSelected = {},
        )
    }
}

@Preview
@Composable
private fun CurrentsNavBarLightPreview() {
    CurrentsTheme(darkTheme = false) {
        CurrentsNavBar(
            selectedTab = NavTab.Search,
            onTabSelected = {},
        )
    }
}