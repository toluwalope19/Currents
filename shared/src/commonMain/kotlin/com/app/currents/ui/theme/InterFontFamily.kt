package com.app.currents.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font
import com.app.currents.shared.generated.resources.Res
import com.app.currents.shared.generated.resources.inter
import com.app.currents.shared.generated.resources.inter_semibold
import com.app.currents.shared.generated.resources.inter_bold
import com.app.currents.shared.generated.resources.inter_extrabold

@Composable
fun interFontFamily() = FontFamily(
    Font(Res.font.inter, FontWeight.Normal),
    Font(Res.font.inter_semibold, FontWeight.SemiBold),
    Font(Res.font.inter_bold, FontWeight.Bold),
    Font(Res.font.inter_extrabold, FontWeight.ExtraBold),
)