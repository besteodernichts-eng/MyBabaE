package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.PricingEngine
import com.example.ui.theme.GoldenWheatAccent
import com.example.ui.theme.JadeGreenPrimary

@Composable
fun DynamicWeightPredictor(
    currentAgeMonths: Int,
    currentDietLevel: Int,
    baseWeightKg: Double,
    basePricePerKg: Long,
    onAgeChanged: (Int) -> Unit,
    onDietChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val estimatedWeight = PricingEngine.predictWeight(baseWeightKg, currentAgeMonths, currentDietLevel)
    val estimatedValueToman = (estimatedWeight * basePricePerKg).toLong()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "ماشین‌حساب هوشمند پیش‌بینی رشد و وزن زنده",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "محاسبه پویای وزن و قیمت نهایی بر اساس سن و رژیم تغذیه دام",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Column: The 2 Interactive Sliders
                Column(modifier = Modifier.weight(1.3f)) {
                    // Age Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "سن دام:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${PersianNumberHelper.toPersianDigits(currentAgeMonths)} ماهه",
                            style = MaterialTheme.typography.labelMedium,
                            color = JadeGreenPrimary
                        )
                    }

                    Slider(
                        value = currentAgeMonths.toFloat(),
                        onValueChange = { onAgeChanged(it.toInt()) },
                        valueRange = 4f..30f,
                        steps = 25,
                        colors = SliderDefaults.colors(
                            thumbColor = JadeGreenPrimary,
                            activeTrackColor = JadeGreenPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Diet Quality Slider
                    val dietLabel = when (currentDietLevel) {
                        1 -> "علوفه مرتعی (معمولی)"
                        2 -> "پرواری صنعتی (متوسط)"
                        else -> "کنسانتره ممتاز (پرمیوم)"
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "رژیم غذایی:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = dietLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldenWheatAccent
                        )
                    }

                    Slider(
                        value = currentDietLevel.toFloat(),
                        onValueChange = { onDietChanged(it.toInt()) },
                        valueRange = 1f..3f,
                        steps = 1,
                        colors = SliderDefaults.colors(
                            thumbColor = GoldenWheatAccent,
                            activeTrackColor = GoldenWheatAccent
                        )
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Vertical Divider
                Box(
                    modifier = Modifier
                        .height(130.dp)
                        .width(1.dp)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Right Column: Output Predicted Metric Box (matching attachment)
                Column(
                    modifier = Modifier.weight(0.9f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "وزن تخمینی:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = PersianNumberHelper.formatWeightKg(estimatedWeight),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = JadeGreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(JadeGreenPrimary.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = PersianNumberHelper.formatToman(estimatedValueToman),
                            style = MaterialTheme.typography.labelSmall,
                            color = JadeGreenPrimary,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "ارزش روز بر مبنای نرخ پایه",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
