package com.example

import com.example.data.LahoreSampleData
import com.example.model.AppRole
import com.example.model.CustomerOrderStep
import com.example.model.MechanicSpecialization
import com.example.model.RequestStatus
import com.example.model.VehicleCategory
import com.example.ui.components.formatCnicDigits
import com.example.ui.components.formatPakistanLocalDigits
import com.example.viewmodel.AutoVibeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AutoVibeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `cascading vehicle selection updates make model and variant`() {
        val vm = AutoVibeViewModel()
        vm.selectVehicleCategory(VehicleCategory.MOTORCYCLE)
        val bikeState = vm.uiState.value
        assertEquals(VehicleCategory.MOTORCYCLE, bikeState.vehicleCategory)
        assertEquals("Honda", bikeState.selectedMake)
        assertEquals("CD 70", bikeState.selectedModel)

        vm.selectMake("Suzuki")
        assertEquals("GS 150", vm.uiState.value.selectedModel)
        assertTrue(vm.uiState.value.availableVariants.isNotEmpty())
    }

    @Test
    fun `pakistan phone and cnic formatting produce valid masks`() {
        assertEquals("300 4829104", formatPakistanLocalDigits("3004829104"))
        assertEquals("35202-4819203-1", formatCnicDigits("3520248192031"))
    }

    @Test
    fun `customer order bidding and tracking flow transitions cleanly`() = runTest {
        val vm = AutoVibeViewModel()
        val dha = LahoreSampleData.lahoreAreas.first { it.id == "dha" }
        vm.selectLahoreArea(dha)
        vm.goToOrderStep(CustomerOrderStep.FARE_OFFERS)
        vm.setPresetFare(1200)
        vm.findNearbyMechanics()

        advanceTimeBy(1300L)
        val offers = vm.uiState.value.mechanicOffers
        assertEquals(4, offers.size)

        val firstOffer = offers.first()
        vm.acceptOffer(firstOffer)
        assertEquals(CustomerOrderStep.TRACKING, vm.uiState.value.orderStep)
        assertNotNull(vm.uiState.value.activeTrackingJob)

        vm.completeActiveJob()
        assertTrue(vm.uiState.value.showJobCompletedRatingDialog)
        vm.submitRatingAndReset(5)
        assertFalse(vm.uiState.value.showJobCompletedRatingDialog)
        assertEquals(CustomerOrderStep.VEHICLE, vm.uiState.value.orderStep)
    }

    @Test
    fun `mechanic mode counter offer and verification work locally`() {
        val vm = AutoVibeViewModel()
        vm.switchRole(AppRole.MECHANIC)
        assertEquals(AppRole.MECHANIC, vm.uiState.value.currentRole)

        vm.completeMechanicVerification(
            fullName = "Rana Imran",
            cnic = "35202-1122334-5",
            phone = "+92 321 1234567",
            specialization = MechanicSpecialization.AUTO_ELECTRICIAN,
            workshopName = "Imran EFI Lab",
            baseAreaName = "Johar Town",
            workshopImage = null
        )
        assertTrue(vm.uiState.value.mechanicProfile.isVerified)
        assertEquals("Rana Imran", vm.uiState.value.mechanicProfile.fullName)

        val req = vm.uiState.value.incomingRequests.first()
        vm.submitCounterOffer(req.id, 1500)
        val updatedReq = vm.uiState.value.incomingRequests.first { it.id == req.id }
        assertEquals(RequestStatus.COUNTER_SENT, updatedReq.status)
        assertEquals(1500, updatedReq.counterOfferPkr)

        vm.acceptIncomingRequest(updatedReq)
        assertNotNull(vm.uiState.value.activeMechanicJob)
        vm.completeActiveMechanicJob()
        assertEquals(219, vm.uiState.value.mechanicProfile.completedJobs)
    }
}
