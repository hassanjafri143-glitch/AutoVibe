package com.example.model

import androidx.annotation.DrawableRes

enum class AppRole {
    CUSTOMER,
    MECHANIC
}

enum class VehicleCategory(val label: String) {
    CAR("Car"),
    MOTORCYCLE("Motorcycle")
}

enum class CustomerOrderStep(val stepNumber: Int, val title: String) {
    VEHICLE(1, "Vehicle"),
    ISSUE_PHOTO(2, "Issue & Photo"),
    FARE_OFFERS(3, "Fare & Offers"),
    TRACKING(4, "Live Tracking")
}

enum class MechanicSpecialization(val label: String) {
    CAR_MECHANIC("Car Mechanic"),
    BIKE_MECHANIC("Bike Mechanic"),
    AUTO_ELECTRICIAN("Auto Electrician"),
    GENERAL("General")
}

sealed interface ImageSource {
    data class Drawable(@DrawableRes val resId: Int) : ImageSource
    data class UriString(val uri: String) : ImageSource
}

data class LahoreArea(
    val id: String,
    val name: String,
    val landmark: String,
    val normalizedX: Float,
    val normalizedY: Float
)

data class CustomerProfile(
    val fullName: String = "Hamza Tariq",
    val phoneNumber: String = "+92 300 4829104",
    val isSignedIn: Boolean = false
)

data class MechanicProfile(
    val fullName: String = "Ustad Shafiq Ahmed",
    val cnicNumber: String = "35202-4819203-1",
    val phoneNumber: String = "+92 321 9482710",
    val specialization: MechanicSpecialization = MechanicSpecialization.CAR_MECHANIC,
    val workshopName: String = "Al-Madina Auto Care",
    val baseAreaName: String = "Gulberg III",
    val workshopImage: ImageSource? = null,
    val isVerified: Boolean = true,
    val isOnline: Boolean = true,
    val rating: Double = 4.9,
    val completedJobs: Int = 218
)

data class NearbyMechanic(
    val id: String,
    val name: String,
    val workshopName: String,
    val specialization: MechanicSpecialization,
    val rating: Double,
    val reviewCount: Int,
    val baseX: Float,
    val baseY: Float,
    val currentX: Float,
    val currentY: Float,
    val phone: String,
    val cnicVerified: Boolean = true,
    val vehicleBadge: String
)

data class MechanicOffer(
    val id: String,
    val mechanic: NearbyMechanic,
    val offeredPkr: Int,
    val customerRequestedPkr: Int,
    val distanceKm: Double,
    val etaMinutes: Int,
    val note: String
) {
    val isCounterOffer: Boolean
        get() = offeredPkr != customerRequestedPkr
}

data class ActiveTrackingJob(
    val mechanic: NearbyMechanic,
    val agreedPkr: Int,
    val vehicleSummary: String,
    val issueSummary: String,
    val customerArea: LahoreArea,
    val etaMinutesRemaining: Int,
    val progressFraction: Float,
    val statusText: String
)

enum class RequestStatus {
    PENDING,
    COUNTER_SENT,
    ACCEPTED,
    COMPLETED
}

data class IncomingCustomerRequest(
    val id: String,
    val customerName: String,
    val customerPhone: String,
    val areaName: String,
    val landmark: String,
    val normalizedX: Float,
    val normalizedY: Float,
    val category: VehicleCategory,
    val vehicleMakeModelVariant: String,
    val issueTags: List<String>,
    val note: String,
    val offeredPkr: Int,
    val distanceKm: Double,
    val photo: ImageSource?,
    val status: RequestStatus = RequestStatus.PENDING,
    val counterOfferPkr: Int? = null
)

data class VehicleMakeData(
    val make: String,
    val models: Map<String, List<String>>
)
