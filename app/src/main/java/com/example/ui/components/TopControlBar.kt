package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppRole
import com.example.model.CustomerProfile
import com.example.model.LahoreArea
import com.example.model.MechanicProfile
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CrispWhite
import com.example.ui.theme.ElectricLime
import com.example.ui.theme.ElevatedCard
import com.example.ui.theme.MatteBlack
import com.example.ui.theme.MutedText
import com.example.ui.theme.SlateField
import com.example.ui.theme.SubtleBorder
import com.example.ui.theme.VerifiedGreen

@Composable
fun TopControlBar(
    currentRole: AppRole,
    selectedArea: LahoreArea,
    customerProfile: CustomerProfile,
    mechanicProfile: MechanicProfile,
    onSwitchRole: (AppRole) -> Unit,
    onOpenAuthOrProfile: () -> Unit,
    onOpenAreaPicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 560.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Top Header Row: Brand + Role Switcher + Profile Pill
            Surface(
                color = CharcoalSurface.copy(alpha = 0.94f),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, SubtleBorder),
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 7.dp)
                ) {
                    // Brand Logo
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(ElectricLime)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Build,
                                contentDescription = "AutoVibe",
                                tint = MatteBlack,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "AutoVibe",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                ),
                                color = CrispWhite
                            )
                            Text(
                                text = "LAHORE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    letterSpacing = 0.8.sp
                                ),
                                color = ElectricLime
                            )
                        }
                    }

                    // Role Switcher Segmented Control
                    Surface(
                        color = MatteBlack,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, SlateField)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(3.dp)
                        ) {
                            RoleTabButton(
                                label = "Customer",
                                selected = currentRole == AppRole.CUSTOMER,
                                onClick = { onSwitchRole(AppRole.CUSTOMER) },
                                modifier = Modifier.testTag("role_customer_button")
                            )
                            RoleTabButton(
                                label = "Mechanic",
                                selected = currentRole == AppRole.MECHANIC,
                                onClick = { onSwitchRole(AppRole.MECHANIC) },
                                modifier = Modifier.testTag("role_mechanic_button")
                            )
                        }
                    }

                    // Auth / Account Pill
                    Surface(
                        color = SlateField,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            1.dp,
                            if ((currentRole == AppRole.CUSTOMER && customerProfile.isSignedIn) ||
                                (currentRole == AppRole.MECHANIC && mechanicProfile.isVerified)
                            ) {
                                VerifiedGreen.copy(alpha = 0.5f)
                            } else {
                                SubtleBorder
                            }
                        ),
                        modifier = Modifier
                            .heightIn(min = 38.dp)
                            .testTag("auth_profile_button")
                            .clickable { onOpenAuthOrProfile() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp)
                        ) {
                            val isVerified = (currentRole == AppRole.CUSTOMER && customerProfile.isSignedIn) ||
                                (currentRole == AppRole.MECHANIC && mechanicProfile.isVerified)
                            Icon(
                                imageVector = if (isVerified) Icons.Default.Verified else Icons.Default.Person,
                                contentDescription = "Account",
                                tint = if (isVerified) VerifiedGreen else ElectricLime,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = when (currentRole) {
                                    AppRole.CUSTOMER -> if (customerProfile.isSignedIn) {
                                        customerProfile.fullName.substringBefore(" ")
                                    } else {
                                        "Sign In"
                                    }
                                    AppRole.MECHANIC -> if (mechanicProfile.isVerified) "CNIC" else "Verify"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = CrispWhite,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // Lahore Location Selector Bar (Customer Mode)
            AnimatedVisibility(visible = currentRole == AppRole.CUSTOMER) {
                Surface(
                    color = CharcoalSurface.copy(alpha = 0.94f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, SubtleBorder),
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .testTag("lahore_location_selector")
                        .clickable { onOpenAreaPicker() }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Lahore pickup location",
                            tint = ElectricLime,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Service Location • Lahore",
                                style = MaterialTheme.typography.labelSmall,
                                color = MutedText
                            )
                            Text(
                                text = "${selectedArea.name} — ${selectedArea.landmark}",
                                style = MaterialTheme.typography.titleSmall,
                                color = CrispWhite,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Surface(
                            color = SlateField,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Change",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ElectricLime
                                )
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = ElectricLime,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RoleTabButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (selected) ElectricLime else MatteBlack,
        shape = RoundedCornerShape(11.dp),
        modifier = modifier
            .heightIn(min = 34.dp)
            .clickable { onClick() }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = if (selected) MatteBlack else MutedText
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LahoreAreaPickerSheet(
    areas: List<LahoreArea>,
    selectedArea: LahoreArea,
    onSelectArea: (LahoreArea) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var searchQuery by remember { mutableStateOf("") }
    val filteredAreas = remember(searchQuery, areas) {
        if (searchQuery.isBlank()) {
            areas
        } else {
            areas.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                    it.landmark.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CharcoalSurface,
        contentColor = CrispWhite
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
        ) {
            Text(
                text = "Select Service Area in Lahore",
                style = MaterialTheme.typography.headlineSmall,
                color = CrispWhite
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Mechanics will navigate directly to your chosen neighborhood.",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedText
            )
            Spacer(modifier = Modifier.height(14.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search Gulberg, DHA, Johar Town...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MutedText
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SlateField,
                    unfocusedContainerColor = SlateField,
                    focusedBorderColor = ElectricLime,
                    unfocusedBorderColor = SubtleBorder,
                    focusedTextColor = CrispWhite,
                    unfocusedTextColor = CrispWhite
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("lahore_area_search_input")
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.heightIn(max = 360.dp)
            ) {
                items(filteredAreas, key = { it.id }) { area ->
                    val isSelected = area.id == selectedArea.id
                    Surface(
                        color = if (isSelected) ElectricLime.copy(alpha = 0.14f) else ElevatedCard,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, if (isSelected) ElectricLime else SubtleBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("area_option_${area.id}")
                            .clickable { onSelectArea(area) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = if (isSelected) ElectricLime else MutedText,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = area.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = CrispWhite
                                )
                                Text(
                                    text = area.landmark,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MutedText
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = ElectricLime
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
