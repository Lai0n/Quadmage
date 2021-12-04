package dev.sharpwave.quadmage.gui.core

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import dev.sharpwave.quadmage.gui.core.model.ContentState
import dev.sharpwave.quadmage.image.ProcessedImage

val LocalContentState: ProvidableCompositionLocal<ContentState> = compositionLocalOf { ContentState() }