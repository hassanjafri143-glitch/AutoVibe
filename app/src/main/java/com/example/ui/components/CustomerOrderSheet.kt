package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.LahoreSampleData
import com.example.model.CustomerOrderStep
import com.example.model.ImageSource
import com.example.model.MechanicOffer
import com.example.model.VehicleCategory
import com.example.ui.theme.AlertRed
import com.example.ui.theme.BrightAmber
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CrispWhite
import com.example.ui.theme.ElectricLime
import com.example.ui.theme.ElevatedCard
import com.example.ui.theme.MatteBlack
import com.example.ui.theme.MutedText
import com.example.ui.theme.SlateField
import com.example.ui.theme.SubtleBorder
import com.example.ui.theme.VerifiedGreen
import com.example.viewmodel.AutoVibeUiState

@Composable
fun CustomerOrderSheet(
    uiState: AutoVibeUiState,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    onSelectCategory: (VehicleCategory) -> Unit,
    onSelectMake: (String) -> Unit,
    onSelectModel: (String) -> Unit,
    onSelectVariant: (String) -> Unit,
    onToggleIssueTag: (String) -> Unit,
    onUpdateIssueNote: (String) -> Unit,
    onSetVehiclePhoto: (ImageSource?) -> Unit,
    onUpdateFare: (String) -> Unit,
    onAdjustFare: (Int) -> Unit,
    onPresetFare: (Int) -> Unit,
    onFindMechanics: () -> Unit,
    onAcceptOffer: (MechanicOffer) -> Unit,
    onDeclineOffer: (String) -> Unit,
    onGoToStep: (CustomerOrderStep) -> Unit,
    onCallMechanic: (String, String) -> Unit,
    onCompleteJob: () -> Unit,
    onCancelJob: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = CharcoalSurface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        border = BorderStroke(1.dp, SubtleBorder),
        shadowElevation = 16.dp,
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 560.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 18.dp, vertical = 10.dp)
        ) {
            // Drag Handle & Header Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpanded() }
                    .padding(vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = ElectricLime,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "STEP ${uiState.orderStep.stepNumber}/4",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MatteBlack,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = uiState.orderStep.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = CrispWhite
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(SubtleBorder)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                        contentDescription = if (isExpanded) "Minimize sheet" else "Expand sheet",
                        tint = MutedText,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Step Progress Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp, bottom = 10.dp)
            ) {
                CustomerOrderStep.entries.forEach { step ->
                    val active = step.stepNumber <= uiState.orderStep.stepNumber
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (active) ElectricLime else SlateField)
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp)
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 8.dp)
                ) {
                    when (uiState.orderStep) {
                        CustomerOrderStep.VEHICLE -> Step1VehicleSelection(
                            uiState = uiState,
                            onSelectCategory = onSelectCategory,
                            onSelectMake = onSelectMake,
                            onSelectModel = onSelectModel,
                            onSelectVariant = onSelectVariant,
                            onNext = { onGoToStep(CustomerOrderStep.ISSUE_PHOTO) }
                        )

                        CustomerOrderStep.ISSUE_PHOTO -> Step2IssueAndPhoto(
                            uiState = uiState,
                            onToggleIssueTag = onToggleIssueTag,
                            onUpdateNote = onUpdateIssueNote,
                            onSetPhoto = onSetVehiclePhoto,
                            onBack = { onGoToStep(CustomerOrderStep.VEHICLE) },
                            onNext = { onGoToStep(CustomerOrderStep.FARE_OFFERS) }
                        )

                        CustomerOrderStep.FARE_OFFERS -> Step3FareAndOffers(
                            uiState = uiState,
                            onUpdateFare = onUpdateFare,
                            onAdjustFare = onAdjustFare,
                            onPresetFare = onPresetFare,
                            onFindMechanics = onFindMechanics,
                            onAcceptOffer = onAcceptOffer,
                            onDeclineOffer = onDeclineOffer,
                            onBack = { onGoToStep(CustomerOrderStep.ISSUE_PHOTO) }
                        )

                        CustomerOrderStep.TRACKING -> Step4LiveTracking(
                            uiState = uiState,
                            onCallMechanic = onCallMechanic,
                            onCompleteJob = onCompleteJob,
                            onCancelJob = onCancelJob
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Step1VehicleSelection(
    uiState: AutoVibeUiState,
    onSelectCategory: (VehicleCategory) -> Unit,
    onSelectMake: (String) -> Unit,
    onSelectModel: (String) -> Unit,
    onSelectVariant: (String) -> Unit,
    onNext: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Select Your Vehicle",
            style = MaterialTheme.typography.titleLarge,
            color = CrispWhite
        )

        // Category Toggle: Car vs Motorcycle
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            VehicleCategory.entries.forEach { category ->
                val selected = uiState.vehicleCategory == category
                Surface(
                    color = if (selected) ElectricLime.copy(alpha = 0.16f) else SlateField,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.5.dp, if (selected) ElectricLime else SubtleBorder),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 52.dp)
                        .testTag("category_${category.name.lowercase()}")
                        .clickable { onSelectCategory(category) }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Icon(
                            imageVector = if (category == VehicleCategory.CAR) {
                                Icons.Default.DirectionsCar
                            } else {
                                Icons.Default.TwoWheeler
                            },
                            contentDescription = category.label,
                            tint = if (selected) ElectricLime else MutedText,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = category.label,
                            style = MaterialTheme.typography.titleSmall,
                            color = if (selected) ElectricLime else CrispWhite
                        )
                    }
                }
            }
        }

        // Cascading Dropdowns: Make -> Model -> Variant
        CascadingDropdownField(
            label = "Make",
            selectedValue = uiState.selectedMake,
            options = uiState.availableMakes.map { it.make },
            onSelected = onSelectMake,
            testTag = "dropdown_make"
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            CascadingDropdownField(
                label = "Model",
                selectedValue = uiState.selectedModel,
                options = uiState.availableModels,
                onSelected = onSelectModel,
                testTag = "dropdown_model",
                modifier = Modifier.weight(1f)
            )
            CascadingDropdownField(
                label = "Variant",
                selectedValue = uiState.selectedVariant,
                options = uiState.availableVariants,
                onSelected = onSelectVariant,
                testTag = "dropdown_variant",
                modifier = Modifier.weight(1.25f)
            )
        }

        Button(
            onClick = onNext,
            colors = ButtonDefaults.buttonColors(
                containerColor = ElectricLime,
                contentColor = MatteBlack
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp)
                .testTag("step1_next_button")
        ) {
            Text(
                text = "Next: Describe Issue",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CascadingDropdownField(
    label: String,
    selectedValue: String,
    options: List<String>,
    onSelected: (String) -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, style = MaterialTheme.typography.labelSmall) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SlateField,
                unfocusedContainerColor = SlateField,
                focusedBorderColor = ElectricLime,
                unfocusedBorderColor = SubtleBorder,
                focusedTextColor = CrispWhite,
                unfocusedTextColor = CrispWhite,
                focusedLabelColor = ElectricLime,
                unfocusedLabelColor = MutedText
            ),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .testTag(testTag)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = ElevatedCard
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            color = if (option == selectedValue) ElectricLime else CrispWhite
                        )
                    },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Step2IssueAndPhoto(
    uiState: AutoVibeUiState,
    onToggleIssueTag: (String) -> Unit,
    onUpdateNote: (String) -> Unit,
    onSetPhoto: (ImageSource?) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            onSetPhoto(ImageSource.UriString(uri.toString()))
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "What's wrong with your ${uiState.selectedModel}?",
            style = MaterialTheme.typography.titleLarge,
            color = CrispWhite
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            LahoreSampleData.issueTags.forEach { tag ->
                val selected = tag in uiState.selectedIssueTags
                FilterChip(
                    selected = selected,
                    onClick = { onToggleIssueTag(tag) },
                    label = {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ElectricLime,
                        selectedLabelColor = MatteBlack,
                        containerColor = SlateField,
                        labelColor = CrispWhite
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selected,
                        borderColor = SubtleBorder,
                        selectedBorderColor = ElectricLime
                    )
                )
            }
        }

        OutlinedTextField(
            value = uiState.issueNote,
            onValueChange = onUpdateNote,
            label = { Text("Additional details (optional)") },
            placeholder = { Text("e.g., Car died near Liberty Chowk signal") },
            maxLines = 2,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SlateField,
                unfocusedContainerColor = SlateField,
                focusedBorderColor = ElectricLime,
                unfocusedBorderColor = SubtleBorder,
                focusedTextColor = CrispWhite,
                unfocusedTextColor = CrispWhite,
                focusedLabelColor = ElectricLime,
                unfocusedLabelColor = MutedText
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("issue_note_input")
        )

        // Upload Picture of Vehicle / Issue
        Surface(
            color = ElevatedCard,
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, if (uiState.vehiclePhoto != null) ElectricLime else SubtleBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.vehiclePhoto == null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("upload_vehicle_photo_button")
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                    ) {
                        Surface(
                            color = ElectricLime.copy(alpha = 0.14f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AddAPhoto,
                                    contentDescription = "Upload photo",
                                    tint = ElectricLime,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Upload Picture of Vehicle / Issue",
                                style = MaterialTheme.typography.titleSmall,
                                color = CrispWhite
                            )
                            Text(
                                text = "Helps Lahore mechanics bring the right tools",
                                style = MaterialTheme.typography.bodySmall,
                                color = MutedText
                            )
                        }
                    }

                    // Quick sample photo chip for demo convenience
                    TextButton(
                        onClick = {
                            val sampleRes = if (uiState.vehicleCategory == VehicleCategory.CAR) {
                                R.drawable.img_sample_corolla_issue
                            } else {
                                R.drawable.img_sample_bike_issue
                            }
                            onSetPhoto(ImageSource.Drawable(sampleRes))
                        },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            text = "Or attach sample ${uiState.vehicleCategory.label.lowercase()} photo",
                            style = MaterialTheme.typography.labelSmall,
                            color = BrightAmber
                        )
                    }
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(10.dp)
                ) {
                    AutoVibeImagePreview(
                        source = uiState.vehiclePhoto,
                        contentDescription = "Uploaded vehicle issue photo",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Vehicle Photo Attached",
                            style = MaterialTheme.typography.titleSmall,
                            color = ElectricLime
                        )
                        Text(
                            text = "Will be shared with nearby mechanics",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedText
                        )
                    }
                    IconButton(onClick = { onSetPhoto(null) }) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Remove photo",
                            tint = AlertRed
                        )
                    }
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = onBack,
                border = BorderStroke(1.dp, SubtleBorder),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.heightIn(min = 52.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = CrispWhite
                )
            }
            Button(
                onClick = onNext,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricLime,
                    contentColor = MatteBlack
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 52.dp)
                    .testTag("step2_next_button")
            ) {
                Text(
                    text = "Next: Offer Call-Out Fare",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun Step3FareAndOffers(
    uiState: AutoVibeUiState,
    onUpdateFare: (String) -> Unit,
    onAdjustFare: (Int) -> Unit,
    onPresetFare: (Int) -> Unit,
    onFindMechanics: () -> Unit,
    onAcceptOffer: (MechanicOffer) -> Unit,
    onDeclineOffer: (String) -> Unit,
    onBack: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Offer Your Call-Out Fare",
                    style = MaterialTheme.typography.titleLarge,
                    color = CrispWhite
                )
                Text(
                    text = "${uiState.fullVehicleSummary} • ${uiState.selectedArea.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Edit issue",
                    tint = MutedText
                )
            }
        }

        // PKR Fare Bidding Box
        Surface(
            color = ElevatedCard,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, SubtleBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        color = SlateField,
                        shape = CircleShape,
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { onAdjustFare(-100) }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Decrease fare by 100 PKR",
                                tint = CrispWhite
                            )
                        }
                    }

                    OutlinedTextField(
                        value = uiState.proposedFareText,
                        onValueChange = onUpdateFare,
                        prefix = {
                            Text(
                                text = "PKR ",
                                style = MaterialTheme.typography.titleMedium,
                                color = ElectricLime
                            )
                        },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.headlineSmall.copy(color = CrispWhite),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SlateField,
                            unfocusedContainerColor = SlateField,
                            focusedBorderColor = ElectricLime,
                            unfocusedBorderColor = SubtleBorder
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("fare_pkr_input")
                    )

                    Surface(
                        color = SlateField,
                        shape = CircleShape,
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { onAdjustFare(100) }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase fare by 100 PKR",
                                tint = ElectricLime
                            )
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf(600, 800, 1200, 1500).forEach { preset ->
                        val active = uiState.proposedFareInt == preset
                        Surface(
                            color = if (active) ElectricLime.copy(alpha = 0.2f) else SlateField,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, if (active) ElectricLime else SubtleBorder),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onPresetFare(preset) }
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                Text(
                                    text = "$preset",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (active) ElectricLime else CrispWhite
                                )
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = onFindMechanics,
            enabled = !uiState.isScanningMechanics,
            colors = ButtonDefaults.buttonColors(
                containerColor = ElectricLime,
                contentColor = MatteBlack
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp)
                .testTag("find_mechanics_button")
        ) {
            if (uiState.isScanningMechanics) {
                CircularProgressIndicator(
                    color = MatteBlack,
                    strokeWidth = 2.5.dp,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Scanning ${uiState.selectedArea.name}...",
                    style = MaterialTheme.typography.labelLarge
                )
            } else {
                Text(
                    text = if (uiState.hasScannedOffers) "Refresh Nearby Mechanics" else "Find Nearby Mechanics",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        // Mechanic Bidding Offer Cards
        if (uiState.hasScannedOffers) {
            if (uiState.mechanicOffers.isEmpty()) {
                Surface(
                    color = ElevatedCard,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "All offers declined. Increase your PKR fare slightly or tap Refresh Nearby Mechanics.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedText,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            } else {
                Text(
                    text = "${uiState.mechanicOffers.size} Mechanics Responded in Lahore",
                    style = MaterialTheme.typography.labelMedium,
                    color = ElectricLime
                )
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    uiState.mechanicOffers.forEach { offer ->
                        MechanicOfferCard(
                            offer = offer,
                            onAccept = { onAcceptOffer(offer) },
                            onDecline = { onDeclineOffer(offer.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MechanicOfferCard(
    offer: MechanicOffer,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    val mech = offer.mechanic
    Surface(
        color = ElevatedCard,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            1.dp,
            if (offer.isCounterOffer) BrightAmber.copy(alpha = 0.5f) else ElectricLime.copy(alpha = 0.5f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("offer_card_${mech.id}")
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        color = SlateField,
                        shape = CircleShape,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Build,
                                contentDescription = null,
                                tint = ElectricLime,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = mech.name,
                                style = MaterialTheme.typography.titleSmall,
                                color = CrispWhite
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "CNIC Verified",
                                tint = VerifiedGreen,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Text(
                            text = mech.workshopName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = LahoreSampleData.formatPkr(offer.offeredPkr),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (offer.isCounterOffer) BrightAmber else ElectricLime
                    )
                    Text(
                        text = if (offer.isCounterOffer) "Counter-offer" else "Matches fare",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (offer.isCounterOffer) BrightAmber else VerifiedGreen
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = BrightAmber,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${mech.rating} (${mech.reviewCount})",
                        style = MaterialTheme.typography.labelSmall,
                        color = CrispWhite
                    )
                }
                Text(
                    text = "• ${offer.distanceKm} km away • ${offer.etaMinutes} min ETA",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedText
                )
            }

            Text(
                text = offer.note,
                style = MaterialTheme.typography.bodySmall,
                color = MutedText
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onDecline,
                    border = BorderStroke(1.dp, SubtleBorder),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .testTag("decline_offer_${mech.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = AlertRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Decline", color = CrispWhite)
                }

                Button(
                    onClick = onAccept,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricLime,
                        contentColor = MatteBlack
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1.3f)
                        .heightIn(min = 48.dp)
                        .testTag("accept_offer_${mech.id}")
                ) {
                    Text(
                        text = "Accept Offer",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun Step4LiveTracking(
    uiState: AutoVibeUiState,
    onCallMechanic: (String, String) -> Unit,
    onCompleteJob: () -> Unit,
    onCancelJob: () -> Unit
) {
    val job = uiState.activeTrackingJob ?: return
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Live Status Pill & ETA
        Surface(
            color = ElectricLime.copy(alpha = 0.14f),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, ElectricLime.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = job.statusText,
                        style = MaterialTheme.typography.titleSmall,
                        color = ElectricLime,
                        modifier = Modifier.weight(1f)
                    )
                    Surface(
                        color = ElectricLime,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (job.etaMinutesRemaining == 0) "ARRIVED" else "${job.etaMinutesRemaining} MIN",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MatteBlack,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                LinearProgressIndicator(
                    progress = { job.progressFraction },
                    color = ElectricLime,
                    trackColor = SlateField,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                )
            }
        }

        // Mechanic & Vehicle Summary Card
        Surface(
            color = ElevatedCard,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, SubtleBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            color = ElectricLime,
                            shape = CircleShape,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Build,
                                    contentDescription = null,
                                    tint = MatteBlack
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = job.mechanic.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = CrispWhite
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "CNIC Verified",
                                    tint = VerifiedGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = "${job.mechanic.workshopName} • CNIC Verified",
                                style = MaterialTheme.typography.bodySmall,
                                color = VerifiedGreen
                            )
                            Text(
                                text = "${job.mechanic.vehicleBadge} • ${job.mechanic.rating}★",
                                style = MaterialTheme.typography.labelSmall,
                                color = MutedText
                            )
                        }
                    }

                    Surface(
                        color = SlateField,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = LahoreSampleData.formatPkr(job.agreedPkr),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = ElectricLime,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                Surface(
                    color = SlateField,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "${job.vehicleSummary} • ${job.customerArea.name}",
                            style = MaterialTheme.typography.labelMedium,
                            color = CrispWhite
                        )
                        Text(
                            text = "Issues: ${job.issueSummary}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedText
                        )
                    }
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = { onCallMechanic(job.mechanic.name, job.mechanic.phone) },
                border = BorderStroke(1.dp, ElectricLime),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 50.dp)
                    .testTag("call_mechanic_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Call Mechanic",
                    tint = ElectricLime,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Call", color = ElectricLime)
            }

            Button(
                onClick = onCompleteJob,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricLime,
                    contentColor = MatteBlack
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1.7f)
                    .heightIn(min = 50.dp)
                    .testTag("mark_job_complete_button")
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Mark Job Complete",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        TextButton(
            onClick = onCancelJob,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Cancel & Pick Another Mechanic",
                style = MaterialTheme.typography.labelMedium,
                color = AlertRed
            )
        }
    }
}

@Composable
fun AutoVibeImagePreview(
    source: ImageSource,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    when (source) {
        is ImageSource.Drawable -> {
            Image(
                painter = painterResource(id = source.resId),
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = modifier
            )
        }
        is ImageSource.UriString -> {
            AsyncImage(
                model = Uri.parse(source.uri),
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = modifier
            )
        }
    }
}

@Composable
fun CallActionDialog(
    name: String,
    phone: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CharcoalSurface,
        titleContentColor = CrispWhite,
        textContentColor = MutedText,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = null,
                    tint = ElectricLime
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Call $name")
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Contact Number: $phone", color = CrispWhite, style = MaterialTheme.typography.titleSmall)
                Text("Verified AutoVibe Lahore hotline bridge active.")
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                    runCatching { context.startActivity(intent) }
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricLime,
                    contentColor = MatteBlack
                )
            ) {
                Text("Open Dialer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = MutedText)
            }
        }
    )
}

@Composable
fun JobCompletedRatingDialog(
    mechanicName: String,
    agreedPkr: Int,
    onSubmitRating: (Int) -> Unit
) {
    var selectedStars by remember { mutableIntStateOf(5) }
    AlertDialog(
        onDismissRequest = { onSubmitRating(selectedStars) },
        containerColor = CharcoalSurface,
        titleContentColor = CrispWhite,
        textContentColor = MutedText,
        title = {
            Text("Repair Completed!", style = MaterialTheme.typography.headlineSmall)
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Paid ${LahoreSampleData.formatPkr(agreedPkr)} to $mechanicName",
                    style = MaterialTheme.typography.titleSmall,
                    color = ElectricLime
                )
                Text(
                    text = "Rate your Lahore mechanic experience:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedText
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    (1..5).forEach { star ->
                        IconButton(onClick = { selectedStars = star }) {
                            Icon(
                                imageVector = if (star <= selectedStars) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "$star stars",
                                tint = BrightAmber,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmitRating(selectedStars) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricLime,
                    contentColor = MatteBlack
                ),
                modifier = Modifier.testTag("submit_rating_button")
            ) {
                Text("Done")
            }
        }
    )
}
