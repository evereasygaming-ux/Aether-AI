package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.HoloAlertRed
import com.example.ui.theme.HoloAmber
import com.example.ui.theme.HoloBlue
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.HoloCyanDim
import com.example.ui.theme.HoloGreen
import com.example.ui.theme.HoloTeal
import com.example.ui.theme.HoloViolet
import com.example.voice.OrbExpression
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data class Point3D(val x: Float, val y: Float, val z: Float)

@Composable
fun HolographicOrb3D(
    modifier: Modifier = Modifier,
    expression: OrbExpression = OrbExpression.NEUTRAL,
    audioWave: Float = 0.2f,
    isSpeaking: Boolean = false,
    isListening: Boolean = false,
    onOrbTap: () -> Unit = {},
    onOrbDoubleTap: () -> Unit = {}
) {
    // 3D rotation angles with gesture support
    var userYaw by remember { mutableFloatStateOf(0f) }
    var userPitch by remember { mutableFloatStateOf(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "Orb3DRotation")
    val autoYaw by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "autoYaw"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val blinkPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "blinkPhase"
    )

    // Pre-calculate 3D particle positions
    val particles = remember {
        val list = mutableListOf<Point3D>()
        val count = 36
        for (i in 0 until count) {
            val u = (i.toFloat() / count) * 2f - 1f
            val theta = i * 2.39996f // Golden ratio
            val r = kotlin.math.sqrt(1f - u * u)
            list.add(Point3D(r * cos(theta), u, r * sin(theta)))
        }
        list
    }

    Box(
        modifier = modifier
            .testTag("holographic_orb_container")
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    userYaw += dragAmount.x * 0.45f
                    userPitch = (userPitch - dragAmount.y * 0.45f).coerceIn(-65f, 65f)
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onOrbTap() },
                    onDoubleTap = { onOrbDoubleTap() }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().testTag("holographic_orb_canvas")) {
            val width = size.width
            val height = size.height
            val center = Offset(width / 2f, height / 2f)
            val baseRadius = (minOf(width, height) / 2.6f) * pulseScale

            val totalYawRad = ((autoYaw + userYaw) * PI / 180.0).toFloat()
            val totalPitchRad = (userPitch * PI / 180.0).toFloat()

            // 1. Draw outer ambient holographic aura
            val auraColor = when (expression) {
                OrbExpression.ALERT -> HoloAlertRed
                OrbExpression.HAPPY -> HoloGreen
                OrbExpression.THINKING -> HoloAmber
                OrbExpression.SPEAKING -> HoloViolet
                OrbExpression.LISTENING -> HoloCyan
                OrbExpression.NEUTRAL -> HoloCyan
            }

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        auraColor.copy(alpha = 0.28f + (audioWave * 0.25f)),
                        HoloBlue.copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = baseRadius * 1.55f
                ),
                radius = baseRadius * 1.55f,
                center = center
            )

            // 2. Draw 3D concentric holographic rings with perspective projection
            drawHolographicRings3D(
                center = center,
                radius = baseRadius,
                yawRad = totalYawRad,
                pitchRad = totalPitchRad,
                audioWave = audioWave,
                baseColor = auraColor
            )

            // 3. Draw 3D Orbiting Quantum Particles
            draw3DParticles(
                particles = particles,
                center = center,
                radius = baseRadius * 1.18f,
                yawRad = totalYawRad,
                pitchRad = totalPitchRad,
                particleColor = auraColor
            )

            // 4. Central Holographic Core Sphere
            val coreRadius = baseRadius * (0.58f + (audioWave * 0.16f))
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF0F1A30).copy(alpha = 0.95f),
                        Color(0xFF0A1120).copy(alpha = 0.88f),
                        auraColor.copy(alpha = 0.35f)
                    ),
                    center = center,
                    radius = coreRadius
                ),
                radius = coreRadius,
                center = center
            )

            drawCircle(
                color = auraColor.copy(alpha = 0.7f + (audioWave * 0.3f)),
                radius = coreRadius,
                center = center,
                style = Stroke(
                    width = 2.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 10f), totalYawRad * 40f)
                )
            )

            // 5. Draw Dynamic Holographic Facial Expressions inside Core
            drawFacialExpression(
                center = center,
                coreRadius = coreRadius,
                expression = expression,
                audioWave = audioWave,
                blinkPhase = blinkPhase,
                tintColor = auraColor,
                yawRad = totalYawRad
            )

            // 6. Draw HUD Telemetry Tick Marks on Perimeter
            drawPerimeterHUD(center = center, radius = baseRadius * 1.32f, yawRad = totalYawRad)
        }
    }
}

private fun DrawScope.drawHolographicRings3D(
    center: Offset,
    radius: Float,
    yawRad: Float,
    pitchRad: Float,
    audioWave: Float,
    baseColor: Color
) {
    val cameraDist = 4.0f
    val ringSteps = 48

    // Three 3D rings with different spatial orientations
    val rings = listOf(
        Triple(0f, 0f, radius * 1.05f),                    // Equator ring
        Triple((PI / 4f).toFloat(), 0f, radius * 1.15f),   // 45 deg tilt ring
        Triple(-(PI / 3f).toFloat(), (PI / 6f).toFloat(), radius * 0.92f) // Counter ring
    )

    rings.forEachIndexed { index, (tiltX, tiltY, ringRad) ->
        val path = Path()
        var firstPoint: Offset? = null

        val dashEffect = if (index == 1) {
            PathEffect.dashPathEffect(floatArrayOf(14f, 8f), 0f)
        } else null

        for (i in 0..ringSteps) {
            val angle = (i.toFloat() / ringSteps) * 2f * PI.toFloat()
            // 3D coordinate on unit circle
            val rawX = cos(angle) * ringRad
            val rawY = sin(angle) * ringRad
            val rawZ = 0f

            // Apply ring internal tilt
            val y1 = rawY * cos(tiltX) - rawZ * sin(tiltX)
            val z1 = rawY * sin(tiltX) + rawZ * cos(tiltX)

            val x2 = rawX * cos(tiltY) + z1 * sin(tiltY)
            val z2 = -rawX * sin(tiltY) + z1 * cos(tiltY)

            // Apply view Yaw and Pitch
            val xRot = x2 * cos(yawRad) + z2 * sin(yawRad)
            val zRot = -x2 * sin(yawRad) + z2 * cos(yawRad)

            val yFinal = y1 * cos(pitchRad) - zRot * sin(pitchRad)
            val zFinal = y1 * sin(pitchRad) + zRot * cos(pitchRad)

            // Perspective scale
            val normalizedZ = zFinal / (radius * 1.8f)
            val scale = cameraDist / (cameraDist + normalizedZ)
            val screenX = center.x + xRot * scale
            val screenY = center.y + yFinal * scale

            if (i == 0) {
                path.moveTo(screenX, screenY)
                firstPoint = Offset(screenX, screenY)
            } else {
                path.lineTo(screenX, screenY)
            }

            // Draw micro nodes at key intervals
            if (i % 8 == 0 && scale > 0.95f) {
                drawCircle(
                    color = if (index == 0) HoloCyan else HoloViolet,
                    radius = (2.2.dp.toPx() * scale) + (audioWave * 1.5f),
                    center = Offset(screenX, screenY)
                )
            }
        }

        firstPoint?.let { path.lineTo(it.x, it.y) }

        val strokeColor = when (index) {
            0 -> baseColor.copy(alpha = 0.75f)
            1 -> HoloViolet.copy(alpha = 0.55f)
            else -> HoloTeal.copy(alpha = 0.65f)
        }

        drawPath(
            path = path,
            color = strokeColor,
            style = Stroke(
                width = (1.8f + (audioWave * 1.5f)).dp.toPx(),
                pathEffect = dashEffect
            )
        )
    }
}

private fun DrawScope.draw3DParticles(
    particles: List<Point3D>,
    center: Offset,
    radius: Float,
    yawRad: Float,
    pitchRad: Float,
    particleColor: Color
) {
    val cameraDist = 3.5f

    particles.forEach { p ->
        val rawX = p.x * radius
        val rawY = p.y * radius
        val rawZ = p.z * radius

        // Rotate in 3D
        val xRot = rawX * cos(yawRad) + rawZ * sin(yawRad)
        val zRot = -rawX * sin(yawRad) + rawZ * cos(yawRad)

        val yFinal = rawY * cos(pitchRad) - zRot * sin(pitchRad)
        val zFinal = rawY * sin(pitchRad) + zRot * cos(pitchRad)

        val normalizedZ = zFinal / (radius * 1.5f)
        val scale = (cameraDist / (cameraDist + normalizedZ)).coerceIn(0.5f, 1.8f)

        val screenX = center.x + xRot * scale
        val screenY = center.y + yFinal * scale

        // Particles in front are brighter and larger
        val alpha = ((scale - 0.5f) / 1.3f).coerceIn(0.2f, 0.95f)
        val pRadius = 2.4.dp.toPx() * scale

        drawCircle(
            color = particleColor.copy(alpha = alpha),
            radius = pRadius,
            center = Offset(screenX, screenY)
        )
    }
}

private fun DrawScope.drawFacialExpression(
    center: Offset,
    coreRadius: Float,
    expression: OrbExpression,
    audioWave: Float,
    blinkPhase: Float,
    tintColor: Color,
    yawRad: Float
) {
    val eyeSpacing = coreRadius * 0.45f
    val eyeY = center.y - (coreRadius * 0.15f)
    val mouthY = center.y + (coreRadius * 0.35f)

    when (expression) {
        OrbExpression.NEUTRAL -> {
            // Elegant digital cyber-eyes with gentle blink
            val isBlinking = blinkPhase in 0.92f..0.98f
            val eyeHeight = if (isBlinking) 2f else coreRadius * 0.16f
            val eyeWidth = coreRadius * 0.22f

            // Left eye
            drawOval(
                color = tintColor,
                topLeft = Offset(center.x - eyeSpacing - (eyeWidth / 2f), eyeY - (eyeHeight / 2f)),
                size = Size(eyeWidth, eyeHeight)
            )
            // Right eye
            drawOval(
                color = tintColor,
                topLeft = Offset(center.x + eyeSpacing - (eyeWidth / 2f), eyeY - (eyeHeight / 2f)),
                size = Size(eyeWidth, eyeHeight)
            )
            // Subtle digital mouth line
            drawLine(
                color = tintColor.copy(alpha = 0.6f),
                start = Offset(center.x - (coreRadius * 0.18f), mouthY),
                end = Offset(center.x + (coreRadius * 0.18f), mouthY),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        OrbExpression.LISTENING -> {
            // Concentric dilated acoustic iris
            val irisRadius = coreRadius * (0.32f + (audioWave * 0.24f))
            drawCircle(
                color = HoloCyan.copy(alpha = 0.85f),
                radius = irisRadius,
                center = center,
                style = Stroke(width = 3.dp.toPx())
            )
            drawCircle(
                color = HoloTeal.copy(alpha = 0.6f),
                radius = irisRadius * 0.55f,
                center = center,
                style = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f))
            )
            drawCircle(
                color = Color.White,
                radius = 3.5.dp.toPx(),
                center = center
            )
        }

        OrbExpression.THINKING -> {
            // High-speed spinning quantum glyph rings
            rotate(degrees = (yawRad * 180f / PI.toFloat()) * 3.5f, pivot = center) {
                drawCircle(
                    color = HoloAmber,
                    radius = coreRadius * 0.38f,
                    center = center,
                    style = Stroke(
                        width = 2.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 12f, 4f, 12f), 0f)
                    )
                )
                drawCircle(
                    color = HoloCyan,
                    radius = coreRadius * 0.22f,
                    center = center,
                    style = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                )
                drawCircle(
                    color = HoloAmber,
                    radius = 4.dp.toPx(),
                    center = Offset(center.x + coreRadius * 0.38f, center.y)
                )
            }
        }

        OrbExpression.SPEAKING -> {
            // Expressive animated eyes
            val eyeWidth = coreRadius * 0.22f
            val eyeHeight = coreRadius * (0.16f + (audioWave * 0.1f))

            drawOval(
                color = tintColor,
                topLeft = Offset(center.x - eyeSpacing - (eyeWidth / 2f), eyeY - (eyeHeight / 2f)),
                size = Size(eyeWidth, eyeHeight)
            )
            drawOval(
                color = tintColor,
                topLeft = Offset(center.x + eyeSpacing - (eyeWidth / 2f), eyeY - (eyeHeight / 2f)),
                size = Size(eyeWidth, eyeHeight)
            )

            // Dynamic audio frequency bar waveform mouth
            val barCount = 7
            val totalWidth = coreRadius * 0.55f
            val startX = center.x - (totalWidth / 2f)
            val stepX = totalWidth / (barCount - 1)

            for (i in 0 until barCount) {
                val factor = when (i) {
                    0, 6 -> 0.4f
                    1, 5 -> 0.75f
                    2, 4 -> 1.1f
                    else -> 1.4f
                }
                val barH = (coreRadius * 0.08f) + (audioWave * coreRadius * 0.35f * factor)
                val bx = startX + (i * stepX)
                drawLine(
                    color = tintColor,
                    start = Offset(bx, mouthY - (barH / 2f)),
                    end = Offset(bx, mouthY + (barH / 2f)),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }

        OrbExpression.HAPPY -> {
            // Smiling inverted arc eyes: ^ ^
            val eyeRadius = coreRadius * 0.18f
            // Left eye arc
            val leftEyePath = Path().apply {
                moveTo(center.x - eyeSpacing - eyeRadius, eyeY + 4f)
                quadraticTo(center.x - eyeSpacing, eyeY - (eyeRadius * 1.1f), center.x - eyeSpacing + eyeRadius, eyeY + 4f)
            }
            drawPath(path = leftEyePath, color = HoloGreen, style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round))

            // Right eye arc
            val rightEyePath = Path().apply {
                moveTo(center.x + eyeSpacing - eyeRadius, eyeY + 4f)
                quadraticTo(center.x + eyeSpacing, eyeY - (eyeRadius * 1.1f), center.x + eyeSpacing + eyeRadius, eyeY + 4f)
            }
            drawPath(path = rightEyePath, color = HoloGreen, style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round))

            // Wide smile
            val smilePath = Path().apply {
                moveTo(center.x - (coreRadius * 0.22f), mouthY - 4f)
                quadraticTo(center.x, mouthY + (coreRadius * 0.2f), center.x + (coreRadius * 0.22f), mouthY - 4f)
            }
            drawPath(path = smilePath, color = HoloGreen, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
        }

        OrbExpression.ALERT -> {
            // Hexagonal warning shield
            val hexPath = Path()
            val hexR = coreRadius * 0.42f
            for (i in 0 until 6) {
                val rad = (i * 60f) * PI.toFloat() / 180f
                val hx = center.x + hexR * cos(rad)
                val hy = center.y + hexR * sin(rad)
                if (i == 0) hexPath.moveTo(hx, hy) else hexPath.lineTo(hx, hy)
            }
            hexPath.close()
            drawPath(path = hexPath, color = HoloAlertRed, style = Stroke(width = 3.dp.toPx()))

            // Exclamation sign
            drawLine(
                color = HoloAlertRed,
                start = Offset(center.x, center.y - (hexR * 0.45f)),
                end = Offset(center.x, center.y + (hexR * 0.1f)),
                strokeWidth = 3.5.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawCircle(
                color = HoloAlertRed,
                radius = 3.dp.toPx(),
                center = Offset(center.x, center.y + (hexR * 0.32f))
            )
        }
    }
}

private fun DrawScope.drawPerimeterHUD(center: Offset, radius: Float, yawRad: Float) {
    val tickCount = 24
    for (i in 0 until tickCount) {
        val angle = (i.toFloat() / tickCount) * 2f * PI.toFloat() + (yawRad * 0.2f)
        val isMajor = i % 6 == 0
        val rInner = if (isMajor) radius - 14f else radius - 7f
        val rOuter = radius

        val x1 = center.x + rInner * cos(angle)
        val y1 = center.y + rInner * sin(angle)
        val x2 = center.x + rOuter * cos(angle)
        val y2 = center.y + rOuter * sin(angle)

        drawLine(
            color = if (isMajor) HoloCyan.copy(alpha = 0.7f) else HoloCyanDim.copy(alpha = 0.35f),
            start = Offset(x1, y1),
            end = Offset(x2, y2),
            strokeWidth = if (isMajor) 2.dp.toPx() else 1.dp.toPx()
        )
    }
}
