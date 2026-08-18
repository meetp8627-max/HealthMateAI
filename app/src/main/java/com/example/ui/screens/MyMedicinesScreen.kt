package com.example.ui.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.MedicineCard
import com.example.ui.components.glass.GlassAtmosphericBackground
import com.example.ui.components.glass.GlassButton
import com.example.ui.components.glass.GlassButtonVariant
import com.example.ui.components.glass.GlassCard
import com.example.ui.components.glass.GlassChip
import com.example.ui.components.glass.GlassInput
import com.example.ui.components.glass.GlassLevel
import com.example.ui.theme.PolishBluePrimary
import com.example.ui.theme.PolishSlate300
import com.example.ui.theme.PolishSlate400
import com.example.ui.theme.PolishSlate500
import com.example.ui.theme.PolishSlate900
import com.example.ui.viewmodel.HealthMateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyMedicinesScreen(
    viewModel: HealthMateViewModel,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToScan: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val medicines by viewModel.filteredMedicines.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val activeFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()

    val filters = listOf("All", "Active", "Expired", "With Reminders", "Without Reminders")

    GlassAtmosphericBackground(modifier = modifier) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "My Medicines",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else PolishSlate900
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Glass Search Input
                GlassInput(
                    value = searchQuery,
                    onValueChange = { viewModel.searchQuery.value = it },
                    placeholder = "Search by medicine name or ingredient...",
                    leadingIcon = Icons.Default.Search,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                )

                // Filter Chips (Glass Chips)
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    items(filters) { filterName ->
                        val selected = activeFilter == filterName
                        GlassChip(
                            text = filterName,
                            selected = selected,
                            onClick = { viewModel.selectedFilter.value = filterName }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                if (medicines.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp, vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        GlassCard(
                            level = GlassLevel.Soft,
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Medication,
                                    contentDescription = null,
                                    modifier = Modifier.size(56.dp),
                                    tint = if (isDark) PolishSlate500 else PolishSlate300
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No Medicines Found",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color.White else PolishSlate900
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Scan a medicine package or search with a different keyword",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isDark) PolishSlate400 else PolishSlate500
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                GlassButton(
                                    text = "Add New Medicine",
                                    icon = Icons.Default.Add,
                                    variant = GlassButtonVariant.PrimaryAccent,
                                    onClick = onNavigateToScan
                                )
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 110.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(medicines) { med ->
                            MedicineCard(
                                medicine = med,
                                onClick = { onNavigateToDetail(med.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
