package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.LahoreSampleData
import com.example.model.IncomingCustomerRequest
import com.example.model.MechanicProfile
import com.example.model.RequestStatus
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
import com.example.ui.theme.VerifiedGreenDim

@Composable
fun MechanicModeSheet(
    mechanicProfile: MechanicProfile,
    incomingRequests: List<IncomingCustomerRequest>,
    activeMechanicJob: IncomingCustomerRequest?,
    onToggleOnline: (Boolean) -> Unit,
    onOpenVerificationOnboarding: () -> Unit,
    onAcceptRequest: (IncomingCustomerRequest) -> Unit,
    onOpenCounterOffer: (IncomingCustomerRequest) -> Unit,
    onSkipRequest: (String) -> Unit,
    onCompleteActiveJob: () -> Unit,
    onCallCustomer: (String, String) -> Unit,
    onResetDemoRequests: () -> Unit,
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
                .padding(horizontal = 18.dp, vertical = 12.dp)
                .heightIn(max = 480.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Online / Offline Header Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = if (mechanicProfile.isOnline) "You're Online in Lahore" else "Currently Offline",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (mechanicProfile.isOnline) ElectricLime else MutedText
                    )
                    Text(
                        text = if (mechanicProfile.isOnline) {
                            "${incomingRequests.size} nearby repair requests"
                        } else {
                            "Toggle switch to receive Lahore requests"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedText
                    )
                }

                Switch(
                    checked = mechanicProfile.isOnline,
                    onCheckedChange = onToggleOnline,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MatteBlack,
                        checkedTrackColor = ElectricLime,
                        uncheckedThumbColor = MutedText,
                        uncheckedTrackColor = SlateField
                    ),
                    modifier = Modifier.testTag("mechanic_online_switch")
                )
            }

            // Mechanic CNIC & Workshop Profile Card
            Surface(
                color = ElevatedCard,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, SubtleBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(12.dp)
                ) {
                    if (mechanicProfile.workshopImage != null) {
                        AutoVibeImagePreview(
                            source = mechanicProfile.workshopImage,
                            contentDescription = "Workshop photo",
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    } else {
                        Surface(
                            color = ElectricLime.copy(alpha = 0.16f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.size(54.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Build,
                                    contentDescription = null,
                                    tint = ElectricLime
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = mechanicProfile.fullName,
                                style = MaterialTheme.typography.titleSmall,
                                color = CrispWhite,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (mechanicProfile.isVerified) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = VerifiedGreenDim,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = "Verified Mechanic",
                                            tint = VerifiedGreen,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "Verified Mechanic",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = VerifiedGreen
                                        )
                                    }
                                }
                            }
                        }

                        Text(
                            text = "${mechanicProfile.workshopName} • ${mechanicProfile.specialization.label}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = BrightAmber,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${mechanicProfile.rating} • ${mechanicProfile.completedJobs} jobs • CNIC ${mechanicProfile.cnicNumber}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MutedText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    IconButton(
                        onClick = onOpenVerificationOnboarding,
                        modifier = Modifier.testTag("edit_mechanic_profile_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit mechanic verification",
                            tint = ElectricLime,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Active Mechanic Job Banner (if accepted)
            if (activeMechanicJob != null) {
                Surface(
                    color = ElectricLime.copy(alpha = 0.14f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.5.dp, ElectricLime),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "ACTIVE JOB • ${activeMechanicJob.areaName.uppercase()}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = ElectricLime
                            )
                            Text(
                                text = LahoreSampleData.formatPkr(activeMechanicJob.offeredPkr),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = ElectricLime
                            )
                        }
                        Text(
                            text = "${activeMechanicJob.customerName} — ${activeMechanicJob.vehicleMakeModelVariant}",
                            style = MaterialTheme.typography.titleSmall,
                            color = CrispWhite
                        )
                        Text(
                            text = "${activeMechanicJob.landmark} • ${activeMechanicJob.issueTags.joinToString(", ")}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedText
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = {
                                    onCallCustomer(activeMechanicJob.customerName, activeMechanicJob.customerPhone)
                                },
                                border = BorderStroke(1.dp, ElectricLime),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Call customer",
                                    tint = ElectricLime,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Call Customer", color = ElectricLime)
                            }
                            Button(
                                onClick = onCompleteActiveJob,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ElectricLime,
                                    contentColor = MatteBlack
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1.3f)
                                    .heightIn(min = 48.dp)
                                    .testTag("mechanic_complete_job_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Complete Job", style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                }
            }

            // Live Feed of Incoming Repair Requests
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Incoming Lahore Requests",
                    style = MaterialTheme.typography.titleMedium,
                    color = CrispWhite
                )
                TextButton(onClick = onResetDemoRequests) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = ElectricLime,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Refresh", color = ElectricLime)
                }
            }

            if (!mechanicProfile.isOnline) {
                Surface(
                    color = ElevatedCard,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Go Online above to view and bid on live vehicle breakdown requests across Lahore.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedText,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else if (incomingRequests.isEmpty()) {
                Surface(
                    color = ElevatedCard,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "No pending requests in feed",
                            style = MaterialTheme.typography.titleSmall,
                            color = CrispWhite
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        TextButton(onClick = onResetDemoRequests) {
                            Text("Load Sample Lahore Requests", color = ElectricLime)
                        }
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    incomingRequests.forEach { req ->
                        IncomingRequestCard(
                            request = req,
                            onAccept = { onAcceptRequest(req) },
                            onCounterOffer = { onOpenCounterOffer(req) },
                            onSkip = { onSkipRequest(req.id) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun IncomingRequestCard(
    request: IncomingCustomerRequest,
    onAccept: () -> Unit,
    onCounterOffer: () -> Unit,
    onSkip: () -> Unit
) {
    Surface(
        color = ElevatedCard,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            1.dp,
            if (request.status == RequestStatus.COUNTER_SENT) BrightAmber.copy(alpha = 0.6f) else SubtleBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("incoming_request_${request.id}")
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (request.photo != null) {
                    AutoVibeImagePreview(
                        source = request.photo,
                        contentDescription = "${request.vehicleMakeModelVariant} issue photo",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = request.customerName,
                            style = MaterialTheme.typography.titleSmall,
                            color = CrispWhite
                        )
                        Text(
                            text = LahoreSampleData.formatPkr(request.offeredPkr),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = ElectricLime
                        )
                    }
                    Text(
                        text = request.vehicleMakeModelVariant,
                        style = MaterialTheme.typography.labelMedium,
                        color = CrispWhite
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = BrightAmber,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${request.areaName} (${request.landmark}) • ${request.distanceKm} km",
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                request.issueTags.forEach { tag ->
                    Surface(
                        color = SlateField,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelSmall,
                            color = BrightAmber,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            if (request.note.isNotBlank()) {
                Text(
                    text = "\"${request.note}\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedText
                )
            }

            if (request.status == RequestStatus.COUNTER_SENT && request.counterOfferPkr != null) {
                Surface(
                    color = BrightAmber.copy(alpha = 0.14f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Counter-offer sent: ${LahoreSampleData.formatPkr(request.counterOfferPkr)} • Waiting for ${request.customerName}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = BrightAmber,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(
                    onClick = onSkip,
                    modifier = Modifier.heightIn(min = 48.dp)
                ) {
                    Text("Skip", color = MutedText)
                }

                OutlinedButton(
                    onClick = onCounterOffer,
                    border = BorderStroke(1.dp, BrightAmber),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .testTag("counter_offer_${request.id}")
                ) {
                    Text(
                        text = "Counter-Offer",
                        style = MaterialTheme.typography.labelMedium,
                        color = BrightAmber
                    )
                }

                Button(
                    onClick = onAccept,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricLime,
                        contentColor = MatteBlack
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .testTag("accept_request_${request.id}")
                ) {
                    Text(
                        text = "Accept",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
fun MechanicCounterOfferDialog(
    request: IncomingCustomerRequest,
    onSubmit: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var counterText by remember(request.id) {
        mutableStateOf((request.offeredPkr + 200).toString())
    }
    val parsedPkr = counterText.filter { it.isDigit() }.toIntOrNull() ?: (request.offeredPkr + 200)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CharcoalSurface,
        titleContentColor = CrispWhite,
        textContentColor = MutedText,
        title = {
            Text("Send Counter-Offer", style = MaterialTheme.typography.headlineSmall)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "${request.customerName} offered ${LahoreSampleData.formatPkr(request.offeredPkr)} for ${request.vehicleMakeModelVariant}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedText
                )
                OutlinedTextField(
                    value = counterText,
                    onValueChange = { counterText = it.filter { ch -> ch.isDigit() }.take(5) },
                    label = { Text("Your PKR Counter-Offer") },
                    prefix = { Text("PKR ", color = BrightAmber) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SlateField,
                        unfocusedContainerColor = SlateField,
                        focusedBorderColor = BrightAmber,
                        unfocusedBorderColor = SubtleBorder,
                        focusedTextColor = CrispWhite,
                        unfocusedTextColor = CrispWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(150, 300, 500).forEach { bump ->
                        val target = request.offeredPkr + bump
                        Surface(
                            color = SlateField,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, SubtleBorder),
                            modifier = Modifier.clickable { counterText = target.toString() }
                        ) {
                            Text(
                                text = "+$bump",
                                style = MaterialTheme.typography.labelMedium,
                                color = BrightAmber,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(parsedPkr.coerceAtLeast(300)) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrightAmber,
                    contentColor = MatteBlack
                )
            ) {
                Text("Send Offer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MutedText)
            }
        }
    )
}
