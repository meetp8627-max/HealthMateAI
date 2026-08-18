package com.example.ui.animation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.delay

object HealthMateMotion {
    // Standard Durations
    const val FastDuration = 150
    const val StandardDuration = 250
    const val EmphasisDuration = 400

    // Natural Motion Curves
    val NaturalEaseOut = CubicBezierEasing(0.16f, 1.0f, 0.3f, 1.0f)
    val NaturalEaseInOut = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)

    // Spring Motion Specs
    fun <T> springFast(): AnimationSpec<T> = spring(
        stiffness = Spring.StiffnessHigh,
        dampingRatio = Spring.DampingRatioNoBouncy
    )

    fun <T> springStandard(): AnimationSpec<T> = spring(
        stiffness = Spring.StiffnessMedium,
        dampingRatio = Spring.DampingRatioNoBouncy
    )

    fun <T> springTactile(): AnimationSpec<T> = spring(
        stiffness = Spring.StiffnessMediumLow,
        dampingRatio = 0.75f
    )
}

/**
 * Adds tactile press scale feedback (scale down on press, spring back on release).
 */
fun Modifier.pressScale(
    scaleDown: Float = 0.96f,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null
): Modifier = composed {
    if (!enabled) return@composed this

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) scaleDown else 1.0f,
        animationSpec = HealthMateMotion.springTactile(),
        label = "pressScaleAnimation"
    )

    this
        .scale(animatedScale)
        .then(
            if (onClick != null) {
                Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
            } else {
                Modifier
            }
        )
}

/**
 * Applies a staggered slide-up and fade-in entrance modifier when mounted.
 */
fun Modifier.staggeredEntrance(
    index: Int = 0,
    staggerDelayMs: Long = 45L,
    initialOffsetYDp: Float = 24f
): Modifier = composed {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(index * staggerDelayMs)
        isVisible = true
    }

    val alphaAnim by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = HealthMateMotion.StandardDuration,
            easing = HealthMateMotion.NaturalEaseOut
        ),
        label = "staggeredAlpha"
    )

    val offsetYAnim by animateFloatAsState(
        targetValue = if (isVisible) 0f else initialOffsetYDp,
        animationSpec = spring(
            stiffness = Spring.StiffnessMediumLow,
            dampingRatio = Spring.DampingRatioNoBouncy
        ),
        label = "staggeredOffsetY"
    )

    this
        .graphicsLayer {
            alpha = alphaAnim
            translationY = offsetYAnim * density
        }
}

/**
 * Smoothly animates integer numerical changes (e.g. 64% -> 65% -> 66%).
 */
@Composable
fun AnimatedNumberText(
    value: Int,
    suffix: String = "",
    prefix: String = "",
    style: TextStyle,
    color: Color,
    fontWeight: FontWeight = FontWeight.Bold,
    modifier: Modifier = Modifier
) {
    val animatedValue by animateIntAsState(
        targetValue = value,
        animationSpec = tween(
            durationMillis = HealthMateMotion.EmphasisDuration,
            easing = HealthMateMotion.NaturalEaseOut
        ),
        label = "numericTextAnimation"
    )

    AnimatedContent(
        targetState = animatedValue,
        transitionSpec = {
            if (targetState > initialState) {
                (slideInVertically { height -> height / 2 } + fadeIn(tween(150)))
                    .togetherWith(slideOutVertically { height -> -height / 2 } + fadeOut(tween(120)))
            } else {
                (slideInVertically { height -> -height / 2 } + fadeIn(tween(150)))
                    .togetherWith(slideOutVertically { height -> height / 2 } + fadeOut(tween(120)))
            }.using(SizeTransform(clip = false))
        },
        label = "numericTextContent"
    ) { targetNum ->
        Text(
            text = "$prefix$targetNum$suffix",
            style = style,
            fontWeight = fontWeight,
            color = color,
            modifier = modifier
        )
    }
}
