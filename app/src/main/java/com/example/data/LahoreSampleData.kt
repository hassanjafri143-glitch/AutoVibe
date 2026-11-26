package com.example.data

import com.example.R
import com.example.model.ImageSource
import com.example.model.IncomingCustomerRequest
import com.example.model.LahoreArea
import com.example.model.MechanicSpecialization
import com.example.model.NearbyMechanic
import com.example.model.VehicleCategory
import com.example.model.VehicleMakeData
import kotlin.math.hypot
import kotlin.math.roundToInt

object LahoreSampleData {

    val lahoreAreas: List<LahoreArea> = listOf(
        LahoreArea(
            id = "gulberg",
            name = "Gulberg III",
            landmark = "Liberty Chowk / Main Blvd",
            normalizedX = 0.56f,
            normalizedY = 0.42f
        ),
        LahoreArea(
            id = "dha",
            name = "DHA Phase 5",
            landmark = "Penta Square / Ring Rd",
            normalizedX = 0.78f,
            normalizedY = 0.65f
        ),
        LahoreArea(
            id = "johar_town",
            name = "Johar Town",
            landmark = "Emporium Mall / Expo Rd",
            normalizedX = 0.25f,
            normalizedY = 0.64f
        ),
        LahoreArea(
            id = "model_town",
            name = "Model Town",
            landmark = "Link Road / Central Park",
            normalizedX = 0.45f,
            normalizedY = 0.56f
        ),
        LahoreArea(
            id = "township",
            name = "Township",
            landmark = "College Rd / Akbar Chowk",
            normalizedX = 0.34f,
            normalizedY = 0.78f
        ),
        LahoreArea(
            id = "mall_road",
            name = "Mall Road",
            landmark = "GPO Chowk / Anarkali",
            normalizedX = 0.46f,
            normalizedY = 0.22f
        ),
        LahoreArea(
            id = "cavalry",
            name = "Cavalry Ground",
            landmark = "Cantt Commercial Blvd",
            normalizedX = 0.69f,
            normalizedY = 0.44f
        ),
        LahoreArea(
            id = "wapda_town",
            name = "Wapda Town",
            landmark = "Main Roundabout",
            normalizedX = 0.19f,
            normalizedY = 0.84f
        )
    )

    val carCatalog: List<VehicleMakeData> = listOf(
        VehicleMakeData(
            make = "Toyota",
            models = mapOf(
                "Corolla" to listOf("GLi 1.3 Automatic", "XLi 1.3 Manual", "Altis 1.6 X", "Altis Grande 1.8 CVT"),
                "Yaris" to listOf("GLi MT 1.3", "ATIV X CVT 1.5", "Aero CVT 1.5"),
                "Fortuner" to listOf("2.7 G Petrol", "Sigma 4 Diesel", "Legender 4x4"),
                "Hilux Revo" to listOf("V Automatic 2.8", "Rocco 4x4")
            )
        ),
        VehicleMakeData(
            make = "Honda",
            models = mapOf(
                "Civic" to listOf("Oriel 1.8 i-VTEC", "VTi Prosmatec", "RS Turbo 1.5"),
                "City" to listOf("1.2L CVT", "Aspire 1.5 CVT", "1.3 i-VTEC Manual"),
                "BR-V" to listOf("i-VTEC S 7-Seater", "i-VTEC Standard")
            )
        ),
        VehicleMakeData(
            make = "Suzuki",
            models = mapOf(
                "Alto" to listOf("VX Non-AC", "VXR 660cc", "VXL AGS Automatic"),
                "Cultus" to listOf("VXR Manual", "VXL Manual", "AGS Automatic"),
                "WagonR" to listOf("VXR 1000cc", "VXL AGS"),
                "Swift" to listOf("GL Manual", "GL CVT", "GLX CVT")
            )
        ),
        VehicleMakeData(
            make = "KIA",
            models = mapOf(
                "Sportage" to listOf("Alpha 2.0", "FWD 2.0", "AWD Black Limited"),
                "Picanto" to listOf("1.0 Manual", "1.0 Automatic"),
                "Stonic" to listOf("EX 1.4", "EX+ 1.4")
            )
        ),
        VehicleMakeData(
            make = "Hyundai",
            models = mapOf(
                "Tucson" to listOf("GLS Sport FWD", "Ultimate AWD"),
                "Elantra" to listOf("GLS 2.0", "GL 1.6")
            )
        ),
        VehicleMakeData(
            make = "Changan",
            models = mapOf(
                "Alsvin" to listOf("Comfort 1.3", "Lumiere 1.5 DCT"),
                "Oshan X7" to listOf("Comfort 7-Seater", "FutureSense")
            )
        )
    )

    val motorcycleCatalog: List<VehicleMakeData> = listOf(
        VehicleMakeData(
            make = "Honda",
            models = mapOf(
                "CD 70" to listOf("Standard 2025", "Dream 70cc"),
                "CG 125" to listOf("Standard Kick", "Self-Start Special Edition", "Gold Edition"),
                "CB 150F" to listOf("Standard Red/Black", "Special Edition Matte")
            )
        ),
        VehicleMakeData(
            make = "Suzuki",
            models = mapOf(
                "GS 150" to listOf("Standard Kick/Self", "SE Alloy Rim"),
                "GD 110S" to listOf("Standard Euro-II", "Special Edition"),
                "GR 150" to listOf("Standard Matte")
            )
        ),
        VehicleMakeData(
            make = "Yamaha",
            models = mapOf(
                "YBR 125" to listOf("Standard Metallic", "Matte Black"),
                "YBR 125G" to listOf("Off-Road Matte Grey", "Metallic Red"),
                "YB 125Z" to listOf("DX Disc Brake", "Standard")
            )
        ),
        VehicleMakeData(
            make = "United",
            models = mapOf(
                "US 70" to listOf("Standard", "Alloy Rim"),
                "US 125" to listOf("Standard")
            )
        ),
        VehicleMakeData(
            make = "Road Prince",
            models = mapOf(
                "RP 70 Passion" to listOf("Standard"),
                "RX3 250" to listOf("Touring Edition")
            )
        )
    )

    val issueTags: List<String> = listOf(
        "Engine Won't Start",
        "Flat Tire / Puncture",
        "Battery Dead",
        "Overheating",
        "Brakes / Clutch",
        "General Tuning",
        "AC / Electrical"
    )

    val nearbyMechanics: List<NearbyMechanic> = listOf(
        NearbyMechanic(
            id = "mech_1",
            name = "Ustad Shafiq",
            workshopName = "Al-Madina Motors Gulberg",
            specialization = MechanicSpecialization.CAR_MECHANIC,
            rating = 4.9,
            reviewCount = 214,
            baseX = 0.59f,
            baseY = 0.37f,
            currentX = 0.59f,
            currentY = 0.37f,
            phone = "+92 321 9482710",
            vehicleBadge = "Mobile Van • OBD-II"
        ),
        NearbyMechanic(
            id = "mech_2",
            name = "Bilal Raza",
            workshopName = "Liberty Auto Care",
            specialization = MechanicSpecialization.AUTO_ELECTRICIAN,
            rating = 4.8,
            reviewCount = 168,
            baseX = 0.51f,
            baseY = 0.48f,
            currentX = 0.51f,
            currentY = 0.48f,
            phone = "+92 301 4459120",
            vehicleBadge = "Jump-Start & ECU Kit"
        ),
        NearbyMechanic(
            id = "mech_3",
            name = "Kamran Electric",
            workshopName = "DHA Auto Tech Phase 5",
            specialization = MechanicSpecialization.CAR_MECHANIC,
            rating = 4.7,
            reviewCount = 129,
            baseX = 0.72f,
            baseY = 0.57f,
            currentX = 0.72f,
            currentY = 0.57f,
            phone = "+92 333 4192841",
            vehicleBadge = "Hybrid & EFI Specialist"
        ),
        NearbyMechanic(
            id = "mech_4",
            name = "Asif Ali",
            workshopName = "Johar Bike & Car Point",
            specialization = MechanicSpecialization.BIKE_MECHANIC,
            rating = 4.9,
            reviewCount = 305,
            baseX = 0.31f,
            baseY = 0.61f,
            currentX = 0.31f,
            currentY = 0.61f,
            phone = "+92 345 8491023",
            vehicleBadge = "Rapid Bike Rescue"
        ),
        NearbyMechanic(
            id = "mech_5",
            name = "Tariq Mahmood",
            workshopName = "Model Town Pitstop",
            specialization = MechanicSpecialization.GENERAL,
            rating = 4.6,
            reviewCount = 97,
            baseX = 0.43f,
            baseY = 0.52f,
            currentX = 0.43f,
            currentY = 0.52f,
            phone = "+92 300 7182934",
            vehicleBadge = "Tire & Brake Mobile Rig"
        ),
        NearbyMechanic(
            id = "mech_6",
            name = "Zeeshan Khan",
            workshopName = "Mall Road Express Garage",
            specialization = MechanicSpecialization.GENERAL,
            rating = 4.8,
            reviewCount = 186,
            baseX = 0.48f,
            baseY = 0.28f,
            currentX = 0.48f,
            currentY = 0.28f,
            phone = "+92 312 4098123",
            vehicleBadge = "24/7 Emergency Unit"
        )
    )

    val initialIncomingRequests: List<IncomingCustomerRequest> = listOf(
        IncomingCustomerRequest(
            id = "req_101",
            customerName = "Saad Mahmood",
            customerPhone = "+92 300 8492011",
            areaName = "Gulberg III",
            landmark = "Near Main Boulevard",
            normalizedX = 0.55f,
            normalizedY = 0.41f,
            category = VehicleCategory.CAR,
            vehicleMakeModelVariant = "Toyota Corolla GLi 1.3 Automatic",
            issueTags = listOf("Overheating", "Engine Won't Start"),
            note = "Radiator steamed near Liberty signal, temperature gauge hit red.",
            offeredPkr = 1200,
            distanceKm = 1.4,
            photo = ImageSource.Drawable(R.drawable.img_sample_corolla_issue)
        ),
        IncomingCustomerRequest(
            id = "req_102",
            customerName = "Usman Ghani",
            customerPhone = "+92 322 4109822",
            areaName = "Johar Town",
            landmark = "Emporium Mall Gate 2",
            normalizedX = 0.26f,
            normalizedY = 0.65f,
            category = VehicleCategory.MOTORCYCLE,
            vehicleMakeModelVariant = "Honda CG 125 Self-Start Special Edition",
            issueTags = listOf("Flat Tire / Puncture", "General Tuning"),
            note = "Rear tube punctured near Expo roundabout, need quick roadside fix.",
            offeredPkr = 550,
            distanceKm = 2.9,
            photo = ImageSource.Drawable(R.drawable.img_sample_bike_issue)
        ),
        IncomingCustomerRequest(
            id = "req_103",
            customerName = "Ayesha Siddiqui",
            customerPhone = "+92 334 9021844",
            areaName = "DHA Phase 5",
            landmark = "Penta Square Basement Exit",
            normalizedX = 0.77f,
            normalizedY = 0.64f,
            category = VehicleCategory.CAR,
            vehicleMakeModelVariant = "Honda Civic Oriel 1.8 i-VTEC",
            issueTags = listOf("Battery Dead"),
            note = "Car isn't cranking after leaving headlights on. Need jump-start or battery test.",
            offeredPkr = 1000,
            distanceKm = 3.6,
            photo = null
        ),
        IncomingCustomerRequest(
            id = "req_104",
            customerName = "Faisal Nawaz",
            customerPhone = "+92 315 6620193",
            areaName = "Model Town",
            landmark = "Link Road Bank Stop",
            normalizedX = 0.46f,
            normalizedY = 0.57f,
            category = VehicleCategory.CAR,
            vehicleMakeModelVariant = "Suzuki Alto VXL AGS Automatic",
            issueTags = listOf("Brakes / Clutch", "AC / Electrical"),
            note = "AGS gear indicator blinking and car refuses to shift into Drive.",
            offeredPkr = 1400,
            distanceKm = 2.1,
            photo = null
        )
    )

    fun computeDistanceKm(x1: Float, y1: Float, x2: Float, y2: Float): Double {
        val raw = hypot((x1 - x2).toDouble(), (y1 - y2).toDouble()) * 16.5
        return ((raw.coerceAtLeast(0.6)) * 10.0).roundToInt() / 10.0
    }

    fun computeEtaMinutes(distanceKm: Double): Int {
        return (distanceKm * 3.2 + 2.0).roundToInt().coerceIn(4, 28)
    }

    fun formatPkr(amount: Int): String {
        val str = amount.toString()
        return if (str.length > 3) {
            "PKR ${str.dropLast(3)},${str.takeLast(3)}"
        } else {
            "PKR $str"
        }
    }
}
