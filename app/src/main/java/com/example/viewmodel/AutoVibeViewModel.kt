package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.LahoreSampleData
import com.example.model.ActiveTrackingJob
import com.example.model.AppRole
import com.example.model.CustomerOrderStep
import com.example.model.CustomerProfile
import com.example.model.ImageSource
import com.example.model.IncomingCustomerRequest
import com.example.model.LahoreArea
import com.example.model.MechanicOffer
import com.example.model.MechanicProfile
import com.example.model.MechanicSpecialization
import com.example.model.NearbyMechanic
import com.example.model.RequestStatus
import com.example.model.VehicleCategory
import com.example.model.VehicleMakeData
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AutoVibeUiState(
    val currentRole: AppRole = AppRole.CUSTOMER,
    val selectedArea: LahoreArea = LahoreSampleData.lahoreAreas.first(),
    val customerProfile: CustomerProfile = CustomerProfile(),
    val mechanicProfile: MechanicProfile = MechanicProfile(
        workshopImage = ImageSource.Drawable(R.drawable.img_sample_workshop)
    ),
    val showCustomerAuthModal: Boolean = false,
    val showMechanicOnboardingModal: Boolean = false,
    val showAreaPickerSheet: Boolean = false,
    val showCallDialogFor: Pair<String, String>? = null, // Name to Phone
    val showJobCompletedRatingDialog: Boolean = false,
    val lastCompletedSummary: Pair<String, Int>? = null,

    // Map & Mechanics
    val nearbyMechanics: List<NearbyMechanic> = LahoreSampleData.nearbyMechanics,
    val selectedMapMechanic: NearbyMechanic? = null,
    val mapRecenterTrigger: Int = 0,

    // Customer Order Flow
    val orderStep: CustomerOrderStep = CustomerOrderStep.VEHICLE,
    val vehicleCategory: VehicleCategory = VehicleCategory.CAR,
    val selectedMake: String = "Toyota",
    val selectedModel: String = "Corolla",
    val selectedVariant: String = "GLi 1.3 Automatic",
    val selectedIssueTags: Set<String> = setOf("Engine Won't Start"),
    val issueNote: String = "",
    val vehiclePhoto: ImageSource? = null,
    val proposedFareText: String = "800",
    val isScanningMechanics: Boolean = false,
    val hasScannedOffers: Boolean = false,
    val mechanicOffers: List<MechanicOffer> = emptyList(),
    val activeTrackingJob: ActiveTrackingJob? = null,

    // Mechanic Mode Flow
    val incomingRequests: List<IncomingCustomerRequest> = LahoreSampleData.initialIncomingRequests,
    val activeMechanicJob: IncomingCustomerRequest? = null,
    val counterOfferTargetRequest: IncomingCustomerRequest? = null
) {
    val availableMakes: List<VehicleMakeData>
        get() = if (vehicleCategory == VehicleCategory.CAR) {
            LahoreSampleData.carCatalog
        } else {
            LahoreSampleData.motorcycleCatalog
        }

    val availableModels: List<String>
        get() = availableMakes.firstOrNull { it.make == selectedMake }?.models?.keys?.toList()
            ?: emptyList()

    val availableVariants: List<String>
        get() = availableMakes.firstOrNull { it.make == selectedMake }?.models?.get(selectedModel)
            ?: emptyList()

    val proposedFareInt: Int
        get() = proposedFareText.filter { it.isDigit() }.toIntOrNull()?.coerceIn(200, 50000) ?: 800

    val fullVehicleSummary: String
        get() = "$selectedMake $selectedModel $selectedVariant"
}

class AutoVibeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AutoVibeUiState())
    val uiState: StateFlow<AutoVibeUiState> = _uiState.asStateFlow()

    private val _snackbarMessages = MutableSharedFlow<String>(extraBufferCapacity = 4)
    val snackbarMessages: SharedFlow<String> = _snackbarMessages.asSharedFlow()

    private var trackingJob: Job? = null
    private var scanJob: Job? = null

    init {
        startMechanicPatrolLoop()
    }

    private fun startMechanicPatrolLoop() {
        viewModelScope.launch {
            var tick = 0.0
            while (true) {
                delay(120L)
                tick += 0.06
                _uiState.update { state ->
                    val updated = state.nearbyMechanics.mapIndexed { idx, mech ->
                        val activeMechId = state.activeTrackingJob?.mechanic?.id
                        if (activeMechId == mech.id && state.activeTrackingJob != null) {
                            val progress = state.activeTrackingJob.progressFraction
                            val targetX = state.activeTrackingJob.customerArea.normalizedX
                            val targetY = state.activeTrackingJob.customerArea.normalizedY
                            val nx = mech.baseX + (targetX - mech.baseX) * progress
                            val ny = mech.baseY + (targetY - mech.baseY) * progress
                            mech.copy(currentX = nx, currentY = ny)
                        } else {
                            val phase = tick + idx * 1.15
                            val dx = (sin(phase) * 0.016).toFloat()
                            val dy = (cos(phase * 0.85) * 0.014).toFloat()
                            mech.copy(
                                currentX = (mech.baseX + dx).coerceIn(0.10f, 0.90f),
                                currentY = (mech.baseY + dy).coerceIn(0.14f, 0.88f)
                            )
                        }
                    }
                    state.copy(nearbyMechanics = updated)
                }
            }
        }
    }

    fun switchRole(role: AppRole) {
        _uiState.update {
            it.copy(
                currentRole = role,
                selectedMapMechanic = null
            )
        }
    }

    fun triggerMapRecenter() {
        _uiState.update { it.copy(mapRecenterTrigger = it.mapRecenterTrigger + 1) }
    }

    fun selectLahoreArea(area: LahoreArea) {
        _uiState.update { state ->
            state.copy(
                selectedArea = area,
                showAreaPickerSheet = false,
                selectedMapMechanic = null
            )
        }
        // If already viewing offers, refresh distance/ETA dynamically
        if (_uiState.value.hasScannedOffers && _uiState.value.orderStep == CustomerOrderStep.FARE_OFFERS) {
            generateOffersForCurrentState(area)
        }
    }

    fun setShowAreaPicker(show: Boolean) {
        _uiState.update { it.copy(showAreaPickerSheet = show) }
    }

    fun selectMapMechanic(mechanic: NearbyMechanic?) {
        _uiState.update { it.copy(selectedMapMechanic = mechanic) }
    }

    // Auth & Onboarding Modals
    fun setShowCustomerAuthModal(show: Boolean) {
        _uiState.update { it.copy(showCustomerAuthModal = show) }
    }

    fun setShowMechanicOnboardingModal(show: Boolean) {
        _uiState.update { it.copy(showMechanicOnboardingModal = show) }
    }

    fun completeCustomerAuth(fullName: String, formattedPhone: String, isSignUp: Boolean) {
        val displayName = fullName.trim().ifBlank { "Lahore Driver" }
        _uiState.update {
            it.copy(
                customerProfile = CustomerProfile(
                    fullName = displayName,
                    phoneNumber = formattedPhone,
                    isSignedIn = true
                ),
                showCustomerAuthModal = false
            )
        }
        val actionWord = if (isSignUp) "Account created" else "Signed in"
        _snackbarMessages.tryEmit("$actionWord — Welcome, $displayName!")
    }

    fun signOutCustomer() {
        _uiState.update {
            it.copy(
                customerProfile = it.customerProfile.copy(isSignedIn = false),
                showCustomerAuthModal = false
            )
        }
        _snackbarMessages.tryEmit("Signed out of customer profile.")
    }

    fun completeMechanicVerification(
        fullName: String,
        cnic: String,
        phone: String,
        specialization: MechanicSpecialization,
        workshopName: String,
        baseAreaName: String,
        workshopImage: ImageSource?
    ) {
        _uiState.update { state ->
            state.copy(
                mechanicProfile = state.mechanicProfile.copy(
                    fullName = fullName.trim(),
                    cnicNumber = cnic,
                    phoneNumber = phone,
                    specialization = specialization,
                    workshopName = workshopName.trim().ifBlank { "Lahore Mobile Auto Care" },
                    baseAreaName = baseAreaName,
                    workshopImage = workshopImage ?: state.mechanicProfile.workshopImage,
                    isVerified = true,
                    isOnline = true
                ),
                showMechanicOnboardingModal = false
            )
        }
        _snackbarMessages.tryEmit("CNIC Verified! Your Verified Mechanic badge is now live.")
    }

    // Step 1: Vehicle Selection
    fun selectVehicleCategory(category: VehicleCategory) {
        val catalog = if (category == VehicleCategory.CAR) {
            LahoreSampleData.carCatalog
        } else {
            LahoreSampleData.motorcycleCatalog
        }
        val firstMake = catalog.first()
        val firstModel = firstMake.models.keys.first()
        val firstVariant = firstMake.models[firstModel]?.firstOrNull().orEmpty()
        val defaultFare = if (category == VehicleCategory.CAR) "800" else "500"
        _uiState.update {
            it.copy(
                vehicleCategory = category,
                selectedMake = firstMake.make,
                selectedModel = firstModel,
                selectedVariant = firstVariant,
                proposedFareText = defaultFare,
                hasScannedOffers = false,
                mechanicOffers = emptyList()
            )
        }
    }

    fun selectMake(make: String) {
        val makeData = _uiState.value.availableMakes.firstOrNull { it.make == make } ?: return
        val firstModel = makeData.models.keys.first()
        val firstVariant = makeData.models[firstModel]?.firstOrNull().orEmpty()
        _uiState.update {
            it.copy(
                selectedMake = make,
                selectedModel = firstModel,
                selectedVariant = firstVariant
            )
        }
    }

    fun selectModel(model: String) {
        val makeData = _uiState.value.availableMakes.firstOrNull { it.make == _uiState.value.selectedMake } ?: return
        val firstVariant = makeData.models[model]?.firstOrNull().orEmpty()
        _uiState.update {
            it.copy(
                selectedModel = model,
                selectedVariant = firstVariant
            )
        }
    }

    fun selectVariant(variant: String) {
        _uiState.update { it.copy(selectedVariant = variant) }
    }

    // Step 2: Issue & Photo
    fun toggleIssueTag(tag: String) {
        _uiState.update { state ->
            val updated = if (tag in state.selectedIssueTags) {
                if (state.selectedIssueTags.size > 1) state.selectedIssueTags - tag else state.selectedIssueTags
            } else {
                state.selectedIssueTags + tag
            }
            state.copy(selectedIssueTags = updated)
        }
    }

    fun updateIssueNote(note: String) {
        _uiState.update { it.copy(issueNote = note) }
    }

    fun setVehiclePhoto(photo: ImageSource?) {
        _uiState.update { it.copy(vehiclePhoto = photo) }
    }

    // Step navigation
    fun goToOrderStep(step: CustomerOrderStep) {
        _uiState.update { it.copy(orderStep = step) }
    }

    fun navigateCustomerStepBack(): Boolean {
        val state = _uiState.value
        return when {
            state.showAreaPickerSheet -> {
                setShowAreaPicker(false)
                true
            }
            state.selectedMapMechanic != null -> {
                selectMapMechanic(null)
                true
            }
            state.orderStep == CustomerOrderStep.ISSUE_PHOTO -> {
                goToOrderStep(CustomerOrderStep.VEHICLE)
                true
            }
            state.orderStep == CustomerOrderStep.FARE_OFFERS -> {
                goToOrderStep(CustomerOrderStep.ISSUE_PHOTO)
                true
            }
            else -> false
        }
    }

    // Step 3: Fare & Offers
    fun updateProposedFare(rawText: String) {
        val digits = rawText.filter { it.isDigit() }.take(5)
        _uiState.update { it.copy(proposedFareText = digits) }
    }

    fun adjustProposedFare(delta: Int) {
        val next = (_uiState.value.proposedFareInt + delta).coerceIn(300, 15000)
        _uiState.update { it.copy(proposedFareText = next.toString()) }
    }

    fun setPresetFare(amount: Int) {
        _uiState.update { it.copy(proposedFareText = amount.toString()) }
    }

    fun findNearbyMechanics() {
        val current = _uiState.value
        if (current.proposedFareInt < 250) {
            _snackbarMessages.tryEmit("Please enter at least PKR 250 call-out fare.")
            return
        }
        scanJob?.cancel()
        scanJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isScanningMechanics = true,
                    hasScannedOffers = false,
                    mechanicOffers = emptyList(),
                    selectedMapMechanic = null
                )
            }
            delay(1100L)
            val area = _uiState.value.selectedArea
            generateOffersForCurrentState(area)
            // Also post live request into the Mechanic Mode feed for live cross-role demo!
            val newReq = IncomingCustomerRequest(
                id = "req_live_${System.currentTimeMillis() % 10000}",
                customerName = current.customerProfile.fullName,
                customerPhone = current.customerProfile.phoneNumber,
                areaName = area.name,
                landmark = area.landmark,
                normalizedX = area.normalizedX,
                normalizedY = area.normalizedY,
                category = current.vehicleCategory,
                vehicleMakeModelVariant = current.fullVehicleSummary,
                issueTags = current.selectedIssueTags.toList(),
                note = current.issueNote.ifBlank { "Need roadside diagnostic & repair assistance." },
                offeredPkr = current.proposedFareInt,
                distanceKm = 1.6,
                photo = current.vehiclePhoto ?: ImageSource.Drawable(
                    if (current.vehicleCategory == VehicleCategory.CAR) {
                        R.drawable.img_sample_corolla_issue
                    } else {
                        R.drawable.img_sample_bike_issue
                    }
                )
            )
            _uiState.update { state ->
                val filteredOld = state.incomingRequests.filterNot { it.id.startsWith("req_live_") }
                state.copy(
                    isScanningMechanics = false,
                    hasScannedOffers = true,
                    incomingRequests = listOf(newReq) + filteredOld
                )
            }
        }
    }

    private fun generateOffersForCurrentState(area: LahoreArea) {
        val state = _uiState.value
        val baseFare = state.proposedFareInt
        val sortedMechs = state.nearbyMechanics
            .sortedBy { mech ->
                val dist = LahoreSampleData.computeDistanceKm(
                    mech.currentX,
                    mech.currentY,
                    area.normalizedX,
                    area.normalizedY
                )
                val specBonus = when {
                    state.vehicleCategory == VehicleCategory.MOTORCYCLE &&
                        mech.specialization == MechanicSpecialization.BIKE_MECHANIC -> -1.5
                    state.vehicleCategory == VehicleCategory.CAR &&
                        mech.specialization == MechanicSpecialization.CAR_MECHANIC -> -1.0
                    else -> 0.0
                }
                dist + specBonus
            }
            .take(4)

        val offers = sortedMechs.mapIndexed { idx, mech ->
            val dist = LahoreSampleData.computeDistanceKm(
                mech.currentX,
                mech.currentY,
                area.normalizedX,
                area.normalizedY
            )
            val eta = LahoreSampleData.computeEtaMinutes(dist)
            val offerPrice = when (idx) {
                0 -> baseFare
                1 -> ((baseFare * 1.15).roundToInt() / 50) * 50
                2 -> baseFare
                else -> ((baseFare * 1.25).roundToInt() / 50) * 50
            }
            val note = when (idx) {
                0 -> "Available right now with full diagnostic toolkit."
                1 -> "Includes spare parts check & jump-start support."
                2 -> "Closest mobile unit near ${area.name}."
                else -> "Senior specialist — can fix on spot."
            }
            MechanicOffer(
                id = "offer_${mech.id}_${idx}",
                mechanic = mech,
                offeredPkr = offerPrice,
                customerRequestedPkr = baseFare,
                distanceKm = dist,
                etaMinutes = eta,
                note = note
            )
        }
        _uiState.update { it.copy(mechanicOffers = offers, hasScannedOffers = true) }
    }

    fun declineOffer(offerId: String) {
        _uiState.update { state ->
            state.copy(mechanicOffers = state.mechanicOffers.filterNot { it.id == offerId })
        }
    }

    fun acceptOffer(offer: MechanicOffer) {
        val state = _uiState.value
        val area = state.selectedArea
        val issueSummary = state.selectedIssueTags.joinToString(" • ")
        val initialJob = ActiveTrackingJob(
            mechanic = offer.mechanic,
            agreedPkr = offer.offeredPkr,
            vehicleSummary = state.fullVehicleSummary,
            issueSummary = issueSummary,
            customerArea = area,
            etaMinutesRemaining = offer.etaMinutes,
            progressFraction = 0.08f,
            statusText = "${offer.mechanic.name} is en route to ${area.name}"
        )
        _uiState.update {
            it.copy(
                activeTrackingJob = initialJob,
                orderStep = CustomerOrderStep.TRACKING,
                selectedMapMechanic = null
            )
        }
        startTrackingSimulation(offer.etaMinutes, offer.mechanic.name, area.name)
    }

    private fun startTrackingSimulation(initialEta: Int, mechName: String, areaName: String) {
        trackingJob?.cancel()
        trackingJob = viewModelScope.launch {
            val steps = 40
            for (i in 1..steps) {
                delay(400L)
                val fraction = (0.08f + (i.toFloat() / steps) * 0.92f).coerceIn(0f, 1f)
                val remainingEta = ((1f - fraction) * initialEta).roundToInt().coerceAtLeast(0)
                val statusText = when {
                    fraction < 0.45f -> "$mechName is en route to $areaName"
                    fraction < 0.85f -> "Approaching $areaName via main road"
                    fraction < 1.0f -> "$mechName is 100m away!"
                    else -> "$mechName has arrived at your location"
                }
                _uiState.update { state ->
                    state.activeTrackingJob?.let { job ->
                        state.copy(
                            activeTrackingJob = job.copy(
                                progressFraction = fraction,
                                etaMinutesRemaining = remainingEta,
                                statusText = statusText
                            )
                        )
                    } ?: state
                }
            }
        }
    }

    fun openCallDialog(name: String, phone: String) {
        _uiState.update { it.copy(showCallDialogFor = name to phone) }
    }

    fun dismissCallDialog() {
        _uiState.update { it.copy(showCallDialogFor = null) }
    }

    fun completeActiveJob() {
        trackingJob?.cancel()
        val job = _uiState.value.activeTrackingJob ?: return
        _uiState.update {
            it.copy(
                showJobCompletedRatingDialog = true,
                lastCompletedSummary = job.mechanic.name to job.agreedPkr
            )
        }
    }

    fun submitRatingAndReset(stars: Int) {
        val mechName = _uiState.value.lastCompletedSummary?.first ?: "Mechanic"
        _uiState.update {
            it.copy(
                showJobCompletedRatingDialog = false,
                lastCompletedSummary = null,
                activeTrackingJob = null,
                orderStep = CustomerOrderStep.VEHICLE,
                hasScannedOffers = false,
                mechanicOffers = emptyList()
            )
        }
        _snackbarMessages.tryEmit("Rated $mechName $stars★ — Thank you for using AutoVibe!")
    }

    fun cancelActiveJob() {
        trackingJob?.cancel()
        _uiState.update {
            it.copy(
                activeTrackingJob = null,
                orderStep = CustomerOrderStep.FARE_OFFERS
            )
        }
        _snackbarMessages.tryEmit("Job cancelled. You can pick another mechanic.")
    }

    // Mechanic Mode Actions
    fun toggleMechanicOnline(online: Boolean) {
        _uiState.update {
            it.copy(mechanicProfile = it.mechanicProfile.copy(isOnline = online))
        }
        _snackbarMessages.tryEmit(
            if (online) "You are now Online in Lahore" else "You are now Offline"
        )
    }

    fun acceptIncomingRequest(request: IncomingCustomerRequest) {
        val accepted = request.copy(status = RequestStatus.ACCEPTED)
        _uiState.update { state ->
            state.copy(
                activeMechanicJob = accepted,
                incomingRequests = state.incomingRequests.filterNot { it.id == request.id }
            )
        }
        _snackbarMessages.tryEmit("Accepted ${request.customerName}'s request for ${LahoreSampleData.formatPkr(request.offeredPkr)}")
    }

    fun openCounterOfferDialog(request: IncomingCustomerRequest?) {
        _uiState.update { it.copy(counterOfferTargetRequest = request) }
    }

    fun submitCounterOffer(requestId: String, counterPkr: Int) {
        _uiState.update { state ->
            state.copy(
                counterOfferTargetRequest = null,
                incomingRequests = state.incomingRequests.map { req ->
                    if (req.id == requestId) {
                        req.copy(
                            status = RequestStatus.COUNTER_SENT,
                            counterOfferPkr = counterPkr
                        )
                    } else {
                        req
                    }
                }
            )
        }
        _snackbarMessages.tryEmit("Counter-offer ${LahoreSampleData.formatPkr(counterPkr)} sent to customer!")
    }

    fun skipIncomingRequest(requestId: String) {
        _uiState.update { state ->
            state.copy(
                incomingRequests = state.incomingRequests.filterNot { it.id == requestId }
            )
        }
    }

    fun completeActiveMechanicJob() {
        val job = _uiState.value.activeMechanicJob ?: return
        _uiState.update { state ->
            state.copy(
                activeMechanicJob = null,
                mechanicProfile = state.mechanicProfile.copy(
                    completedJobs = state.mechanicProfile.completedJobs + 1
                )
            )
        }
        _snackbarMessages.tryEmit("Job completed! ${LahoreSampleData.formatPkr(job.offeredPkr)} added to Lahore earnings.")
    }

    fun resetDemoRequests() {
        _uiState.update {
            it.copy(incomingRequests = LahoreSampleData.initialIncomingRequests)
        }
    }
}
