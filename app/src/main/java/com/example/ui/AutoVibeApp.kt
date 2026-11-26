package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.LahoreSampleData
import com.example.model.AppRole
import com.example.model.CustomerOrderStep
import com.example.ui.components.CallActionDialog
import com.example.ui.components.CustomerAuthModal
import com.example.ui.components.CustomerOrderSheet
import com.example.ui.components.JobCompletedRatingDialog
import com.example.ui.components.LahoreAreaPickerSheet
import com.example.ui.components.LahoreMapCanvas
import com.example.ui.components.MechanicCounterOfferDialog
import com.example.ui.components.MechanicModeSheet
import com.example.ui.components.MechanicOnboardingModal
import com.example.ui.components.TopControlBar
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CrispWhite
import com.example.ui.theme.ElectricLime
import com.example.ui.theme.MatteBlack
import com.example.viewmodel.AutoVibeViewModel

@Composable
fun AutoVibeApp(
    viewModel: AutoVibeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var isCustomerSheetExpanded by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        viewModel.snackbarMessages.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Ensure sheet expands automatically when advancing steps
    LaunchedEffect(uiState.orderStep) {
        isCustomerSheetExpanded = true
    }

    BackHandler(
        enabled = uiState.showCustomerAuthModal ||
            uiState.showMechanicOnboardingModal ||
            uiState.showAreaPickerSheet ||
            uiState.selectedMapMechanic != null ||
            (uiState.currentRole == AppRole.CUSTOMER &&
                (uiState.orderStep == CustomerOrderStep.ISSUE_PHOTO || uiState.orderStep == CustomerOrderStep.FARE_OFFERS))
    ) {
        when {
            uiState.showCustomerAuthModal -> viewModel.setShowCustomerAuthModal(false)
            uiState.showMechanicOnboardingModal -> viewModel.setShowMechanicOnboardingModal(false)
            else -> viewModel.navigateCustomerStepBack()
        }
    }

    val estimatedBottomPadding = when {
        uiState.currentRole == AppRole.MECHANIC -> 340
        !isCustomerSheetExpanded -> 92
        uiState.orderStep == CustomerOrderStep.FARE_OFFERS && uiState.hasScannedOffers -> 380
        else -> 310
    }

    Scaffold(
        containerColor = MatteBlack,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.statusBarsPadding().padding(top = 128.dp)
            ) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = CharcoalSurface,
                    contentColor = CrispWhite,
                    actionColor = ElectricLime
                )
            }
        }
    ) { _ ->
        Box(modifier = Modifier.fillMaxSize()) {
            // 1. Full-Bleed Dark Interactive Map of Lahore
            LahoreMapCanvas(
                role = uiState.currentRole,
                areas = LahoreSampleData.lahoreAreas,
                selectedArea = uiState.selectedArea,
                nearbyMechanics = uiState.nearbyMechanics,
                selectedMapMechanic = uiState.selectedMapMechanic,
                isScanning = uiState.isScanningMechanics,
                activeTrackingJob = uiState.activeTrackingJob,
                incomingRequests = uiState.incomingRequests,
                activeMechanicJob = uiState.activeMechanicJob,
                recenterTrigger = uiState.mapRecenterTrigger,
                bottomPaddingDp = estimatedBottomPadding,
                onSelectArea = { viewModel.selectLahoreArea(it) },
                onSelectMechanic = { viewModel.selectMapMechanic(it) },
                onRecenterClicked = { viewModel.triggerMapRecenter() }
            )

            // 2. Floating Top Bar (Brand + Customer/Mechanic Role Switcher + Auth + Area Selector)
            TopControlBar(
                currentRole = uiState.currentRole,
                selectedArea = uiState.selectedArea,
                customerProfile = uiState.customerProfile,
                mechanicProfile = uiState.mechanicProfile,
                onSwitchRole = { viewModel.switchRole(it) },
                onOpenAuthOrProfile = {
                    if (uiState.currentRole == AppRole.CUSTOMER) {
                        viewModel.setShowCustomerAuthModal(true)
                    } else {
                        viewModel.setShowMechanicOnboardingModal(true)
                    }
                },
                onOpenAreaPicker = { viewModel.setShowAreaPicker(true) },
                modifier = Modifier.align(Alignment.TopCenter)
            )

            // 3. Role-Based Floating Bottom Sheet
            if (uiState.currentRole == AppRole.CUSTOMER) {
                CustomerOrderSheet(
                    uiState = uiState,
                    isExpanded = isCustomerSheetExpanded,
                    onToggleExpanded = { isCustomerSheetExpanded = !isCustomerSheetExpanded },
                    onSelectCategory = { viewModel.selectVehicleCategory(it) },
                    onSelectMake = { viewModel.selectMake(it) },
                    onSelectModel = { viewModel.selectModel(it) },
                    onSelectVariant = { viewModel.selectVariant(it) },
                    onToggleIssueTag = { viewModel.toggleIssueTag(it) },
                    onUpdateIssueNote = { viewModel.updateIssueNote(it) },
                    onSetVehiclePhoto = { viewModel.setVehiclePhoto(it) },
                    onUpdateFare = { viewModel.updateProposedFare(it) },
                    onAdjustFare = { viewModel.adjustProposedFare(it) },
                    onPresetFare = { viewModel.setPresetFare(it) },
                    onFindMechanics = { viewModel.findNearbyMechanics() },
                    onAcceptOffer = { viewModel.acceptOffer(it) },
                    onDeclineOffer = { viewModel.declineOffer(it) },
                    onGoToStep = { viewModel.goToOrderStep(it) },
                    onCallMechanic = { name, phone -> viewModel.openCallDialog(name, phone) },
                    onCompleteJob = { viewModel.completeActiveJob() },
                    onCancelJob = { viewModel.cancelActiveJob() },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            } else {
                MechanicModeSheet(
                    mechanicProfile = uiState.mechanicProfile,
                    incomingRequests = uiState.incomingRequests,
                    activeMechanicJob = uiState.activeMechanicJob,
                    onToggleOnline = { viewModel.toggleMechanicOnline(it) },
                    onOpenVerificationOnboarding = { viewModel.setShowMechanicOnboardingModal(true) },
                    onAcceptRequest = { viewModel.acceptIncomingRequest(it) },
                    onOpenCounterOffer = { viewModel.openCounterOfferDialog(it) },
                    onSkipRequest = { viewModel.skipIncomingRequest(it) },
                    onCompleteActiveJob = { viewModel.completeActiveMechanicJob() },
                    onCallCustomer = { name, phone -> viewModel.openCallDialog(name, phone) },
                    onResetDemoRequests = { viewModel.resetDemoRequests() },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }

    // Modals & Dialogs
    if (uiState.showAreaPickerSheet) {
        LahoreAreaPickerSheet(
            areas = LahoreSampleData.lahoreAreas,
            selectedArea = uiState.selectedArea,
            onSelectArea = { viewModel.selectLahoreArea(it) },
            onDismiss = { viewModel.setShowAreaPicker(false) }
        )
    }

    if (uiState.showCustomerAuthModal) {
        CustomerAuthModal(
            currentProfile = uiState.customerProfile,
            onSubmitAuth = { name, phone, isSignUp ->
                viewModel.completeCustomerAuth(name, phone, isSignUp)
            },
            onSignOut = { viewModel.signOutCustomer() },
            onDismiss = { viewModel.setShowCustomerAuthModal(false) }
        )
    }

    if (uiState.showMechanicOnboardingModal) {
        MechanicOnboardingModal(
            currentProfile = uiState.mechanicProfile,
            onSubmitVerification = { fullName, cnic, phone, spec, workshop, area, image ->
                viewModel.completeMechanicVerification(fullName, cnic, phone, spec, workshop, area, image)
            },
            onDismiss = { viewModel.setShowMechanicOnboardingModal(false) }
        )
    }

    uiState.counterOfferTargetRequest?.let { targetReq ->
        MechanicCounterOfferDialog(
            request = targetReq,
            onSubmit = { newPkr -> viewModel.submitCounterOffer(targetReq.id, newPkr) },
            onDismiss = { viewModel.openCounterOfferDialog(null) }
        )
    }

    uiState.showCallDialogFor?.let { (name, phone) ->
        CallActionDialog(
            name = name,
            phone = phone,
            onDismiss = { viewModel.dismissCallDialog() }
        )
    }

    if (uiState.showJobCompletedRatingDialog) {
        val summary = uiState.lastCompletedSummary
        JobCompletedRatingDialog(
            mechanicName = summary?.first ?: "Mechanic",
            agreedPkr = summary?.second ?: 800,
            onSubmitRating = { stars -> viewModel.submitRatingAndReset(stars) }
        )
    }
}
