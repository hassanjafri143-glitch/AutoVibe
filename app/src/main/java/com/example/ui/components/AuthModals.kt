package com.example.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.data.LahoreSampleData
import com.example.model.CustomerProfile
import com.example.model.ImageSource
import com.example.model.MechanicProfile
import com.example.model.MechanicSpecialization
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
import com.example.ui.theme.VerifiedGreenDim

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerAuthModal(
    currentProfile: CustomerProfile,
    onSubmitAuth: (fullName: String, phone: String, isSignUp: Boolean) -> Unit,
    onSignOut: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isSignUp by remember { mutableStateOf(!currentProfile.isSignedIn) }
    var fullName by remember { mutableStateOf(if (currentProfile.isSignedIn) currentProfile.fullName else "") }
    var phoneDigits by remember {
        mutableStateOf(
            currentProfile.phoneNumber.filter { it.isDigit() }.removePrefix("92").take(10)
        )
    }
    var passwordOrOtp by remember { mutableStateOf("") }
    var useOtpMode by remember { mutableStateOf(true) }
    var simulatedOtpBanner by remember { mutableStateOf<String?>(null) }
    var validationError by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CharcoalSurface,
        contentColor = CrispWhite
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
        ) {
            Text(
                text = if (isSignUp) "Create Customer Account" else "Customer Sign In",
                style = MaterialTheme.typography.headlineSmall,
                color = CrispWhite
            )
            Text(
                text = "Instant local prototype login for AutoVibe Lahore.",
                style = MaterialTheme.typography.bodySmall,
                color = MutedText
            )

            // Sign In / Sign Up Toggle
            Surface(
                color = MatteBlack,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, SubtleBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(4.dp)) {
                    listOf(false to "Sign In", true to "Sign Up").forEach { (signUpTab, label) ->
                        val selected = isSignUp == signUpTab
                        Surface(
                            color = if (selected) ElectricLime else MatteBlack,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 42.dp)
                                .clickable {
                                    isSignUp = signUpTab
                                    validationError = null
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (selected) MatteBlack else MutedText
                                )
                            }
                        }
                    }
                }
            }

            if (isSignUp) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = {
                        fullName = it
                        validationError = null
                    },
                    label = { Text("Full Name") },
                    placeholder = { Text("e.g., Hamza Tariq") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = darkInputColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("customer_auth_name_input")
                )
            }

            // Pakistan Contact Number +92 3XX XXXXXXX
            OutlinedTextField(
                value = formatPakistanLocalDigits(phoneDigits),
                onValueChange = { input ->
                    phoneDigits = input.filter { it.isDigit() }.take(10)
                    validationError = null
                },
                label = { Text("Contact Number (Pakistan)") },
                placeholder = { Text("3XX XXXXXXX") },
                prefix = {
                    Text(
                        text = "+92 ",
                        style = MaterialTheme.typography.titleSmall,
                        color = ElectricLime
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        tint = ElectricLime
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = darkInputColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("customer_auth_phone_input")
            )

            // Password or OTP simulation
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = passwordOrOtp,
                    onValueChange = {
                        passwordOrOtp = if (useOtpMode) it.filter { ch -> ch.isDigit() }.take(4) else it
                        validationError = null
                    },
                    label = { Text(if (useOtpMode) "4-Digit OTP" else "Password") },
                    placeholder = { Text(if (useOtpMode) "e.g., 4829" else "Enter password") },
                    leadingIcon = {
                        Icon(
                            imageVector = if (useOtpMode) Icons.Default.Sms else Icons.Default.Lock,
                            contentDescription = null,
                            tint = MutedText
                        )
                    },
                    visualTransformation = if (useOtpMode) {
                        androidx.compose.ui.text.input.VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = if (useOtpMode) KeyboardType.Number else KeyboardType.Password
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = darkInputColors(),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("customer_auth_secret_input")
                )

                if (useOtpMode) {
                    OutlinedButton(
                        onClick = {
                            simulatedOtpBanner = "Simulated SMS OTP sent to +92 ${formatPakistanLocalDigits(phoneDigits)}: 4829"
                            passwordOrOtp = "4829"
                        },
                        border = BorderStroke(1.dp, ElectricLime),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.heightIn(min = 54.dp)
                    ) {
                        Text("Send OTP", color = ElectricLime)
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = {
                    useOtpMode = !useOtpMode
                    passwordOrOtp = ""
                    simulatedOtpBanner = null
                }) {
                    Text(
                        text = if (useOtpMode) "Use Password instead" else "Use SMS OTP simulation",
                        color = BrightAmber
                    )
                }
                if (currentProfile.isSignedIn) {
                    TextButton(onClick = onSignOut) {
                        Text("Sign Out", color = AlertRed)
                    }
                }
            }

            AnimatedVisibility(visible = simulatedOtpBanner != null) {
                simulatedOtpBanner?.let { banner ->
                    Surface(
                        color = VerifiedGreenDim,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = banner,
                            style = MaterialTheme.typography.labelMedium,
                            color = VerifiedGreen,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }

            if (validationError != null) {
                Text(
                    text = validationError!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = AlertRed
                )
            }

            Button(
                onClick = {
                    when {
                        isSignUp && fullName.trim().length < 2 ->
                            validationError = "Please enter your full name."
                        phoneDigits.length < 10 || !phoneDigits.startsWith("3") ->
                            validationError = "Enter a valid Pakistani mobile number starting with 3 (10 digits)."
                        passwordOrOtp.length < 4 ->
                            validationError = if (useOtpMode) "Tap Send OTP or enter 4-digit OTP." else "Password must be at least 4 characters."
                        else -> {
                            val formatted = "+92 ${formatPakistanLocalDigits(phoneDigits)}"
                            onSubmitAuth(
                                if (isSignUp) fullName else currentProfile.fullName,
                                formatted,
                                isSignUp
                            )
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricLime,
                    contentColor = MatteBlack
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp)
                    .testTag("customer_auth_submit_button")
            ) {
                Text(
                    text = if (isSignUp) "Sign Up & Continue" else "Sign In",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MechanicOnboardingModal(
    currentProfile: MechanicProfile,
    onSubmitVerification: (
        fullName: String,
        cnic: String,
        phone: String,
        specialization: MechanicSpecialization,
        workshopName: String,
        baseAreaName: String,
        workshopImage: ImageSource?
    ) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var fullName by remember { mutableStateOf(currentProfile.fullName) }
    var cnicDigits by remember { mutableStateOf(currentProfile.cnicNumber.filter { it.isDigit() }.take(13)) }
    var phoneDigits by remember {
        mutableStateOf(currentProfile.phoneNumber.filter { it.isDigit() }.removePrefix("92").take(10))
    }
    var specialization by remember { mutableStateOf(currentProfile.specialization) }
    var workshopName by remember { mutableStateOf(currentProfile.workshopName) }
    var baseAreaName by remember { mutableStateOf(currentProfile.baseAreaName) }
    var workshopImage by remember { mutableStateOf(currentProfile.workshopImage) }
    var areaMenuExpanded by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf<String?>(null) }

    val workshopPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            workshopImage = ImageSource.UriString(uri.toString())
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CharcoalSurface,
        contentColor = CrispWhite
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Mechanic Verification",
                        style = MaterialTheme.typography.headlineSmall,
                        color = CrispWhite
                    )
                    Text(
                        text = "CNIC verification unlocks your Lahore Verified Mechanic badge.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedText
                    )
                }
                if (currentProfile.isVerified) {
                    Surface(
                        color = VerifiedGreenDim,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = VerifiedGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Verified", style = MaterialTheme.typography.labelSmall, color = VerifiedGreen)
                        }
                    }
                }
            }

            OutlinedTextField(
                value = fullName,
                onValueChange = {
                    fullName = it
                    validationError = null
                },
                label = { Text("Full Name (as per CNIC)") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = darkInputColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("mechanic_name_input")
            )

            OutlinedTextField(
                value = formatCnicDigits(cnicDigits),
                onValueChange = { input ->
                    cnicDigits = input.filter { it.isDigit() }.take(13)
                    validationError = null
                },
                label = { Text("CNIC Number (XXXXX-XXXXXXX-X)") },
                placeholder = { Text("35202-4819203-1") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = darkInputColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("mechanic_cnic_input")
            )

            OutlinedTextField(
                value = formatPakistanLocalDigits(phoneDigits),
                onValueChange = { input ->
                    phoneDigits = input.filter { it.isDigit() }.take(10)
                    validationError = null
                },
                label = { Text("Contact Number (+92)") },
                prefix = { Text("+92 ", color = ElectricLime, fontWeight = FontWeight.Bold) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = darkInputColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("mechanic_phone_input")
            )

            Text(
                text = "Specialization",
                style = MaterialTheme.typography.labelMedium,
                color = MutedText
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MechanicSpecialization.entries.forEach { spec ->
                    val selected = specialization == spec
                    FilterChip(
                        selected = selected,
                        onClick = { specialization = spec },
                        label = { Text(spec.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricLime,
                            selectedLabelColor = MatteBlack,
                            containerColor = SlateField,
                            labelColor = CrispWhite
                        )
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = workshopName,
                    onValueChange = { workshopName = it },
                    label = { Text("Workshop Name") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = darkInputColors(),
                    modifier = Modifier.weight(1.2f)
                )

                ExposedDropdownMenuBox(
                    expanded = areaMenuExpanded,
                    onExpandedChange = { areaMenuExpanded = !areaMenuExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = baseAreaName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Base Area") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = areaMenuExpanded) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = darkInputColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = areaMenuExpanded,
                        onDismissRequest = { areaMenuExpanded = false },
                        containerColor = ElevatedCard
                    ) {
                        LahoreSampleData.lahoreAreas.forEach { area ->
                            DropdownMenuItem(
                                text = { Text(area.name, color = CrispWhite) },
                                onClick = {
                                    baseAreaName = area.name
                                    areaMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Optional Workshop Picture Upload Box with Preview
            Surface(
                color = ElevatedCard,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, SubtleBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (workshopImage == null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                workshopPicker.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                            .padding(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddAPhoto,
                            contentDescription = "Upload workshop picture",
                            tint = ElectricLime
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Upload Workshop Pictures (Optional)",
                                style = MaterialTheme.typography.titleSmall,
                                color = CrispWhite
                            )
                            Text(
                                text = "Builds trust with Lahore car & bike owners",
                                style = MaterialTheme.typography.bodySmall,
                                color = MutedText
                            )
                        }
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(10.dp)
                    ) {
                        AutoVibeImagePreview(
                            source = workshopImage!!,
                            contentDescription = "Workshop preview",
                            modifier = Modifier
                                .size(58.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Workshop Photo Ready",
                                style = MaterialTheme.typography.titleSmall,
                                color = ElectricLime
                            )
                            Text(
                                text = "Tap photo icon to change",
                                style = MaterialTheme.typography.bodySmall,
                                color = MutedText
                            )
                        }
                        IconButton(onClick = {
                            workshopPicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }) {
                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = "Change workshop photo",
                                tint = ElectricLime
                            )
                        }
                        IconButton(onClick = { workshopImage = null }) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Remove workshop photo",
                                tint = AlertRed
                            )
                        }
                    }
                }
            }

            if (validationError != null) {
                Text(
                    text = validationError!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = AlertRed
                )
            }

            Button(
                onClick = {
                    when {
                        fullName.trim().length < 3 ->
                            validationError = "Enter your full name as per CNIC."
                        cnicDigits.length != 13 ->
                            validationError = "CNIC must be 13 digits (XXXXX-XXXXXXX-X)."
                        phoneDigits.length < 10 ->
                            validationError = "Enter a valid 10-digit +92 mobile number."
                        else -> {
                            onSubmitVerification(
                                fullName,
                                formatCnicDigits(cnicDigits),
                                "+92 ${formatPakistanLocalDigits(phoneDigits)}",
                                specialization,
                                workshopName,
                                baseAreaName,
                                workshopImage
                            )
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricLime,
                    contentColor = MatteBlack
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp)
                    .testTag("mechanic_submit_verify_button")
            ) {
                Text(
                    text = "Submit & Verify Mechanic",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun darkInputColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = SlateField,
    unfocusedContainerColor = SlateField,
    focusedBorderColor = ElectricLime,
    unfocusedBorderColor = SubtleBorder,
    focusedTextColor = CrispWhite,
    unfocusedTextColor = CrispWhite,
    focusedLabelColor = ElectricLime,
    unfocusedLabelColor = MutedText
)

fun formatPakistanLocalDigits(digits: String): String {
    val clean = digits.filter { it.isDigit() }.take(10)
    return if (clean.length <= 3) {
        clean
    } else {
        "${clean.substring(0, 3)} ${clean.substring(3)}"
    }
}

fun formatCnicDigits(digits: String): String {
    val clean = digits.filter { it.isDigit() }.take(13)
    return buildString {
        clean.forEachIndexed { index, ch ->
            if (index == 5 || index == 12) append('-')
            append(ch)
        }
    }
}
