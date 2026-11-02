package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.GoldenWheatAccent
import com.example.ui.theme.JadeGreenPrimary

@Composable
fun ThreeSixtyViewer(
    imageResName: String,
    rotationAngle: Float,
    isAutoRotating: Boolean,
    onAngleChange: (Float) -> Unit,
    onToggleAutoRotate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val drawableRes = when (imageResName) {
        "sample_shal_sheep" -> R.drawable.sample_shal_sheep
        "sample_boer_goat" -> R.drawable.sample_boer_goat
        else -> R.drawable.sample_dorper_sheep
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.Black.copy(alpha = 0.04f))
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val newAngle = rotationAngle - (dragAmount.x * 0.8f)
                    onAngleChange(newAngle)
                }
            }
    ) {
        // Rotated / Transformed Livestock View
        Image(
            painter = painterResource(id = drawableRes),
            contentDescription = "نمای چندرسانه‌ای دام",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    // Subtle 3D perspective distortion based on angle to simulate 360 walk-around
                    val normAngle = (rotationAngle % 360f + 360f) % 360f
                    val flipHorizontal = normAngle in 90f..270f
                    scaleX = if (flipHorizontal) -1f else 1f
                    rotationY = (normAngle % 90f) - 45f
                }
        )

        // 360 Badge Top Right
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black.copy(alpha = 0.55f))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Autorenew,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "نمای ۳۶۰°",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp
                )
            }
        }

        // Center Overlay Rotation Guidance Indicator (as seen in attachment screenshot)
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .clip(RoundedCornerShape(50))
                .background(Color.Black.copy(alpha = 0.60f))
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Cached,
                    contentDescription = null,
                    tint = GoldenWheatAccent,
                    modifier = Modifier
                        .size(18.dp)
                        .graphicsLayer {
                            rotationZ = rotationAngle
                        }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "لمس و چرخش ۳۶۰ درجه",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "${PersianNumberHelper.toPersianDigits(rotationAngle.toInt() % 360)}° زاویه دید",
                        color = GoldenWheatAccent,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // Bottom Controls Bar with Play/Pause & Scrubber (matching attachment video controls)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.45f))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onToggleAutoRotate,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isAutoRotating) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isAutoRotating) "توقف" else "پخش خودکار ۳۶۰ درجه",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Slider(
                    value = (rotationAngle % 360f + 360f) % 360f,
                    onValueChange = onAngleChange,
                    valueRange = 0f..360f,
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = JadeGreenPrimary,
                        activeTrackColor = JadeGreenPrimary,
                        inactiveTrackColor = Color.White.copy(alpha = 0.4f)
                    )
                )
            }
        }
    }
}
