package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.model.LivestockBreed
import com.example.model.PurchasePurpose
import com.example.model.TeethStatus
import com.example.ui.theme.GoldenWheatAccent
import com.example.ui.theme.JadeGreenPrimary
import com.example.ui.viewmodel.LivestockViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    viewModel: LivestockViewModel,
    onDismiss: () -> Unit
) {
    val selectedBreed by viewModel.selectedBreedFilter.collectAsState()
    val selectedPurpose by viewModel.selectedPurposeFilter.collectAsState()
    val weightRange by viewModel.weightRange.collectAsState()
    val selectedTeeth by viewModel.selectedTeethFilter.collectAsState()
    val liveAuctionOnly by viewModel.filterLiveAuctionOnly.collectAsState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "فیلتر و دسته‌بندی تخصصی دام سبک",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "بستن",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Only Live Auctions Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "فقط مزایده‌های زنده آنلاین",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "نمایش دام‌های با پیشنهاد قیمت در حال انقضا",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = liveAuctionOnly,
                    onCheckedChange = { viewModel.filterLiveAuctionOnly.value = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = GoldenWheatAccent,
                        checkedTrackColor = GoldenWheatAccent.copy(alpha = 0.5f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Purchase Purpose
            Text(
                text = "هدف خرید:",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PurchasePurpose.values().forEach { purpose ->
                    FilterChip(
                        selected = selectedPurpose == purpose,
                        onClick = { viewModel.selectedPurposeFilter.value = purpose },
                        label = { Text(purpose.titleFa) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = JadeGreenPrimary,
                            selectedLabelColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Breeds
            Text(
                text = "نژادهای بومی و اصلاح‌شده:",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // "همه نژادها" option
                FilterChip(
                    selected = selectedBreed == null,
                    onClick = { viewModel.selectedBreedFilter.value = null },
                    label = { Text("همه نژادها") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = JadeGreenPrimary,
                        selectedLabelColor = MaterialTheme.colorScheme.surface
                    )
                )

                LivestockBreed.values().forEach { breed ->
                    FilterChip(
                        selected = selectedBreed == breed,
                        onClick = { viewModel.selectedBreedFilter.value = breed },
                        label = { Text(breed.titleFa) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = JadeGreenPrimary,
                            selectedLabelColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Weight Range Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "بازه وزن زنده (کیلوگرم):",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${PersianNumberHelper.formatWeightKg(weightRange.start.toDouble())} تا ${PersianNumberHelper.formatWeightKg(weightRange.endInclusive.toDouble())}",
                    style = MaterialTheme.typography.bodySmall,
                    color = JadeGreenPrimary
                )
            }
            RangeSlider(
                value = weightRange,
                onValueChange = { viewModel.weightRange.value = it },
                valueRange = 15f..130f,
                steps = 22,
                colors = SliderDefaults.colors(
                    thumbColor = JadeGreenPrimary,
                    activeTrackColor = JadeGreenPrimary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Teeth / Age Status
            Text(
                text = "سن و وضعیت دندان (تأیید سلامت دهان):",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedTeeth == null,
                    onClick = { viewModel.selectedTeethFilter.value = null },
                    label = { Text("همه سنین") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = JadeGreenPrimary,
                        selectedLabelColor = MaterialTheme.colorScheme.surface
                    )
                )
                TeethStatus.values().forEach { teeth ->
                    FilterChip(
                        selected = selectedTeeth == teeth,
                        onClick = { viewModel.selectedTeethFilter.value = teeth },
                        label = { Text(teeth.titleFa) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = JadeGreenPrimary,
                            selectedLabelColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        viewModel.resetFilters()
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null)
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Text("حذف فیلترها")
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1.5f),
                    colors = ButtonDefaults.buttonColors(containerColor = JadeGreenPrimary)
                ) {
                    Text("مشاهده نتایج")
                }
            }
        }
    }
}
