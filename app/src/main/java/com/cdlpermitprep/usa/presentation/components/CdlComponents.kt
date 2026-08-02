package com.cdlpermitprep.usa.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.cdlpermitprep.usa.presentation.theme.cardBorder

/**
 * Shared building blocks for the app's neo-brutalist visual language:
 * thick 2dp black outlines, flat bold fills, generous rounded corners, no elevation shadows.
 */

@Composable
fun CdlIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    background: Color = MaterialTheme.colorScheme.surface,
) {
    Box(
        modifier = modifier
            .size(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(background)
            .border(2.dp, MaterialTheme.colorScheme.cardBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun CdlPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(if (enabled) containerColor else containerColor.copy(alpha = 0.4f))
            .border(2.dp, MaterialTheme.colorScheme.cardBorder, RoundedCornerShape(18.dp))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = contentColor, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun CdlOutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(2.dp, MaterialTheme.colorScheme.cardBorder, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun CdlCard(
    modifier: Modifier = Modifier,
    background: Color = MaterialTheme.colorScheme.surface,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(background)
            .border(2.dp, MaterialTheme.colorScheme.cardBorder, RoundedCornerShape(20.dp))
            .padding(16.dp),
        content = content,
    )
}

@Composable
fun CdlTopBar(
    title: String? = null,
    showBack: Boolean = false,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showBack) {
            CdlIconButton(icon = Icons.Filled.ArrowBack, onClick = { onBack?.invoke() })
            Spacer(Modifier.width(12.dp))
        }
        title?.let {
            Text(it, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
        } ?: Spacer(Modifier.weight(1f))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), content = actions)
    }
}

@Composable
fun CdlChip(text: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(color)
            .border(2.dp, MaterialTheme.colorScheme.cardBorder, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 4.dp),
    ) {
        Text(text, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
    }
}

/** Bold folder-tab style collection card used on Home, matching the reference UI. */
@Composable
fun CollectionCard(
    title: String,
    subtitle: String,
    iconColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    badge: String? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(modifier = modifier) {
        // Folder "tab" peeking above the card.
        Box(
            modifier = Modifier
                .padding(start = 14.dp, end = 28.dp)
                .height(14.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                .background(iconColor)
                .border(2.dp, MaterialTheme.colorScheme.cardBorder, RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)),
        )
        Column(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(2.dp, MaterialTheme.colorScheme.cardBorder, RoundedCornerShape(18.dp))
                .clickable(onClick = onClick)
                .padding(14.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconColor)
                        .border(2.dp, MaterialTheme.colorScheme.cardBorder, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(icon, contentDescription = null, tint = Color.Black)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(subtitle, style = MaterialTheme.typography.labelMedium)
                    if (badge != null) {
                        Spacer(Modifier.height(4.dp))
                        CdlChip(badge, color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
            Spacer(Modifier.height(18.dp))
            // Reserve two lines whether or not the title wraps, so cards sharing a grid row
            // are the same height and their folder tabs line up instead of stair-stepping.
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
