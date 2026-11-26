package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LahoreSampleData
import com.example.model.ActiveTrackingJob
import com.example.model.AppRole
import com.example.model.IncomingCustomerRequest
import com.example.model.LahoreArea
import com.example.model.MechanicSpecialization
import com.example.model.NearbyMechanic
import com.example.ui.theme.BrightAmber
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CrispWhite
import com.example.ui.theme.DimText
import com.example.ui.theme.ElectricLime
import com.example.ui.theme.ElevatedCard
import com.example.ui.theme.MapCanal
import com.example.ui.theme.MapPark
import com.example.ui.theme.MapRoadDark
import com.example.ui.theme.MapRoadMajor
import com.example.ui.theme.MapWater
import com.example.ui.theme.MatteBlack
import com.example.ui.theme.MutedText
import com.example.ui.theme.SubtleBorder
import com.example.ui.theme.VerifiedGreen
import kotlin.math.roundToInt

@Composable
fun LahoreMapCanvas(
    role: AppRole,
    areas: List<LahoreArea>,
    selectedArea: LahoreArea,
    nearbyMechanics: List<NearbyMechanic>,
    selectedMapMechanic: NearbyMechanic?,
    isScanning: Boolean,
    activeTrackingJob: ActiveTrackingJob?,
    incomingRequests: List<IncomingCustomerRequest>,
    activeMechanicJob: IncomingCustomerRequest?,
    recenterTrigger: Int,
    bottomPaddingDp: Int,
    onSelectArea: (LahoreArea) -> Unit,
    onSelectMechanic: (NearbyMechanic?) -> Unit,
    onRecenterClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    LaunchedEffect(recenterTrigger) {
        scale = 1f
        offset = Offset.Zero
    }

    val infiniteTransition = rememberInfiniteTransition(label = "map_pulse")
    val pulseProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_ring"
    )
    val radarSweep by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_sweep"
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MatteBlack)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    val newScale = (scale * zoom).coerceIn(1f, 2.2f)
                    val maxPanX = size.width * 0.35f * newScale
                    val maxPanY = size.height * 0.30f * newScale
                    scale = newScale
                    offset = Offset(
                        x = (offset.x + pan.x).coerceIn(-maxPanX, maxPanX),
                        y = (offset.y + pan.y).coerceIn(-maxPanY, maxPanY)
                    )
                }
            }
    ) {
        val density = LocalDensity.current
        val mapWidthPx = with(density) { maxWidth.toPx() }
        // Keep active Lahore map points in the visible upper/middle region above the bottom sheet
        val mapHeightPx = with(density) { (maxHeight - (bottomPaddingDp * 0.52f).dp).toPx().coerceAtLeast(320f) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offset.x
                    translationY = offset.y
                }
        ) {
            // 1. Dark Lahore Cartography Vector Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = mapWidthPx
                val h = mapHeightPx

                // Subtle urban block grid
                val gridStepX = w / 11f
                val gridStepY = h / 11f
                for (i in 1..10) {
                    drawLine(
                        color = MapRoadDark,
                        start = Offset(i * gridStepX, 0f),
                        end = Offset(i * gridStepX, size.height),
                        strokeWidth = 1.5f
                    )
                    drawLine(
                        color = MapRoadDark,
                        start = Offset(0f, i * gridStepY),
                        end = Offset(w, i * gridStepY),
                        strokeWidth = 1.5f
                    )
                }

                // Parks: Lawrence Gardens (Bagh-e-Jinnah) & Model Town Central Park
                drawRoundRect(
                    color = MapPark,
                    topLeft = Offset(w * 0.45f, h * 0.25f),
                    size = Size(w * 0.08f, h * 0.06f),
                    cornerRadius = CornerRadius(18f, 18f)
                )
                drawCircle(
                    color = MapPark,
                    radius = w * 0.055f,
                    center = Offset(w * 0.44f, h * 0.56f)
                )

                // Ravi River (NW edge)
                val raviPath = Path().apply {
                    moveTo(0f, h * 0.16f)
                    cubicTo(w * 0.15f, h * 0.12f, w * 0.25f, h * 0.05f, w * 0.42f, 0f)
                }
                drawPath(
                    path = raviPath,
                    color = MapWater,
                    style = Stroke(width = 34f, cap = StrokeCap.Round)
                )

                // Iconic Lahore Canal Bank Road waterway
                val canalPath = Path().apply {
                    moveTo(w * 0.04f, h * 0.78f)
                    cubicTo(w * 0.28f, h * 0.56f, w * 0.55f, h * 0.42f, w * 0.96f, h * 0.14f)
                }
                drawPath(
                    path = canalPath,
                    color = MapCanal,
                    style = Stroke(width = 16f, cap = StrokeCap.Round)
                )
                drawPath(
                    path = canalPath,
                    color = MapRoadMajor,
                    style = Stroke(width = 3f, cap = StrokeCap.Round)
                )

                // Lahore Ring Road Outer Arc (East & South)
                val ringRoadPath = Path().apply {
                    moveTo(w * 0.72f, 0f)
                    cubicTo(w * 0.95f, h * 0.26f, w * 0.94f, h * 0.68f, w * 0.68f, h * 0.90f)
                    cubicTo(w * 0.44f, h * 0.96f, w * 0.20f, h * 0.92f, 0f, h * 0.85f)
                }
                drawPath(
                    path = ringRoadPath,
                    color = MapRoadMajor,
                    style = Stroke(width = 9f, cap = StrokeCap.Round)
                )

                // Ferozepur Road Spine (N-S)
                drawLine(
                    color = MapRoadMajor,
                    start = Offset(w * 0.48f, h * 0.04f),
                    end = Offset(w * 0.46f, h * 0.96f),
                    strokeWidth = 7f,
                    cap = StrokeCap.Round
                )

                // Mall Road (Shahrah-e-Quaid-e-Azam)
                drawLine(
                    color = MapRoadMajor,
                    start = Offset(w * 0.22f, h * 0.20f),
                    end = Offset(w * 0.80f, h * 0.28f),
                    strokeWidth = 6f,
                    cap = StrokeCap.Round
                )

                // Main Boulevard Gulberg -> DHA link
                drawLine(
                    color = MapRoadMajor,
                    start = Offset(w * 0.55f, h * 0.34f),
                    end = Offset(w * 0.78f, h * 0.65f),
                    strokeWidth = 5.5f,
                    cap = StrokeCap.Round
                )

                // Khayaban-e-Jinnah / Johar Town -> Wapda Town
                drawLine(
                    color = MapRoadMajor,
                    start = Offset(w * 0.38f, h * 0.55f),
                    end = Offset(w * 0.16f, h * 0.88f),
                    strokeWidth = 5f,
                    cap = StrokeCap.Round
                )

                // Radar Scan Pulse when finding nearby mechanics
                val customerCenter = Offset(
                    x = selectedArea.normalizedX * w,
                    y = selectedArea.normalizedY * h
                )
                if (isScanning && role == AppRole.CUSTOMER) {
                    val maxR = w * 0.38f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                ElectricLime.copy(alpha = 0.24f * (1f - radarSweep)),
                                Color.Transparent
                            ),
                            center = customerCenter,
                            radius = (maxR * radarSweep).coerceAtLeast(10f)
                        ),
                        radius = (maxR * radarSweep).coerceAtLeast(10f),
                        center = customerCenter
                    )
                    drawCircle(
                        color = ElectricLime.copy(alpha = 0.75f * (1f - radarSweep)),
                        radius = (maxR * radarSweep).coerceAtLeast(8f),
                        center = customerCenter,
                        style = Stroke(width = 3f)
                    )
                }

                // Active Customer Job Tracking Animated Route Line
                if (role == AppRole.CUSTOMER && activeTrackingJob != null) {
                    val start = Offset(
                        x = activeTrackingJob.mechanic.baseX * w,
                        y = activeTrackingJob.mechanic.baseY * h
                    )
                    val end = Offset(
                        x = activeTrackingJob.customerArea.normalizedX * w,
                        y = activeTrackingJob.customerArea.normalizedY * h
                    )
                    val mid = Offset(
                        x = (start.x + end.x) / 2f + w * 0.04f,
                        y = (start.y + end.y) / 2f - h * 0.03f
                    )
                    val routePath = Path().apply {
                        moveTo(start.x, start.y)
                        quadraticTo(mid.x, mid.y, end.x, end.y)
                    }
                    // Dashed upcoming path
                    drawPath(
                        path = routePath,
                        color = ElectricLime.copy(alpha = 0.35f),
                        style = Stroke(
                            width = 6f,
                            cap = StrokeCap.Round,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 14f), 0f)
                        )
                    )
                    // Completed solid glow segment from mechanic's current pos to customer
                    val currentMechPos = Offset(
                        x = activeTrackingJob.mechanic.baseX * w +
                            (activeTrackingJob.customerArea.normalizedX - activeTrackingJob.mechanic.baseX) * w * activeTrackingJob.progressFraction,
                        y = activeTrackingJob.mechanic.baseY * h +
                            (activeTrackingJob.customerArea.normalizedY - activeTrackingJob.mechanic.baseY) * h * activeTrackingJob.progressFraction
                    )
                    drawLine(
                        color = ElectricLime,
                        start = currentMechPos,
                        end = end,
                        strokeWidth = 7f,
                        cap = StrokeCap.Round
                    )
                }

                // Active Mechanic Mode Route Line to Customer
                if (role == AppRole.MECHANIC && activeMechanicJob != null) {
                    val mechOrigin = Offset(w * 0.56f, h * 0.42f)
                    val custTarget = Offset(
                        x = activeMechanicJob.normalizedX * w,
                        y = activeMechanicJob.normalizedY * h
                    )
                    drawLine(
                        color = ElectricLime,
                        start = mechOrigin,
                        end = custTarget,
                        strokeWidth = 6f,
                        cap = StrokeCap.Round,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 12f), 0f)
                    )
                }

                // Customer Pin Radar Pulse
                if (role == AppRole.CUSTOMER) {
                    val ringRadius = 18f + pulseProgress * 56f
                    drawCircle(
                        color = ElectricLime.copy(alpha = (1f - pulseProgress) * 0.45f),
                        radius = ringRadius,
                        center = customerCenter
                    )
                    drawCircle(
                        color = ElectricLime.copy(alpha = 0.25f),
                        radius = 22f,
                        center = customerCenter
                    )
                }
            }

            // 2. Clickable Lahore Area Landmarks
            areas.forEach { area ->
                val isSelected = area.id == selectedArea.id && role == AppRole.CUSTOMER
                val px = (area.normalizedX * mapWidthPx).roundToInt()
                val py = (area.normalizedY * mapHeightPx).roundToInt()

                if (!isSelected) {
                    Surface(
                        color = CharcoalSurface.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, SubtleBorder.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .offset { IntOffset(px - 75, py - 18) }
                            .clickable { onSelectArea(area) }
                    ) {
                        Text(
                            text = area.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedText,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // 3. Role-specific Map Markers
            if (role == AppRole.CUSTOMER) {
                // Moving / Clickable Nearby Mechanic Pins
                nearbyMechanics.forEach { mech ->
                    val isAssigned = activeTrackingJob?.mechanic?.id == mech.id
                    val isAnyTracking = activeTrackingJob != null
                    if (!isAnyTracking || isAssigned) {
                        val mx = (mech.currentX * mapWidthPx).roundToInt()
                        val my = (mech.currentY * mapHeightPx).roundToInt()
                        val isSelected = selectedMapMechanic?.id == mech.id

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .offset { IntOffset(mx - 44, my - 44) }
                                .clickable { onSelectMechanic(mech) }
                        ) {
                            Surface(
                                color = if (isAssigned || isSelected) ElectricLime else ElevatedCard,
                                shape = CircleShape,
                                border = BorderStroke(
                                    width = if (isAssigned) 2.5.dp else 1.5.dp,
                                    color = if (isAssigned || isSelected) CrispWhite else ElectricLime
                                ),
                                shadowElevation = 6.dp,
                                modifier = Modifier.size(if (isAssigned) 40.dp else 34.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = when (mech.specialization) {
                                            MechanicSpecialization.BIKE_MECHANIC -> Icons.Default.TwoWheeler
                                            MechanicSpecialization.CAR_MECHANIC -> Icons.Default.DirectionsCar
                                            else -> Icons.Default.Build
                                        },
                                        contentDescription = mech.name,
                                        tint = if (isAssigned || isSelected) MatteBlack else ElectricLime,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Surface(
                                color = MatteBlack.copy(alpha = 0.88f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (isAssigned) "ETA ${activeTrackingJob?.etaMinutesRemaining}m" else "${mech.rating}★",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = if (isAssigned) ElectricLime else CrispWhite,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                }

                // Customer Location Pin (Pulsing Lime)
                val cx = (selectedArea.normalizedX * mapWidthPx).roundToInt()
                val cy = (selectedArea.normalizedY * mapHeightPx).roundToInt()
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.offset { IntOffset(cx - 56, cy - 34) }
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(ElectricLime)
                            .border(3.dp, CrispWhite, CircleShape)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = ElectricLime,
                        shape = RoundedCornerShape(10.dp),
                        shadowElevation = 6.dp
                    ) {
                        Text(
                            text = "You • ${selectedArea.name}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MatteBlack,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            } else {
                // Mechanic Mode: Incoming Lahore Customer Request Pins in Amber
                incomingRequests.forEach { req ->
                    val rx = (req.normalizedX * mapWidthPx).roundToInt()
                    val ry = (req.normalizedY * mapHeightPx).roundToInt()
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.offset { IntOffset(rx - 48, ry - 36) }
                    ) {
                        Surface(
                            color = BrightAmber,
                            shape = CircleShape,
                            border = BorderStroke(2.dp, CrispWhite),
                            shadowElevation = 6.dp,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsCar,
                                    contentDescription = req.areaName,
                                    tint = MatteBlack,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Surface(
                            color = MatteBlack.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, BrightAmber.copy(alpha = 0.6f))
                        ) {
                            Text(
                                text = LahoreSampleData.formatPkr(req.offeredPkr),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = BrightAmber,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Active Mechanic Job target pin
                if (activeMechanicJob != null) {
                    val tx = (activeMechanicJob.normalizedX * mapWidthPx).roundToInt()
                    val ty = (activeMechanicJob.normalizedY * mapHeightPx).roundToInt()
                    Surface(
                        color = ElectricLime,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.offset { IntOffset(tx - 60, ty - 28) }
                    ) {
                        Text(
                            text = "Job: ${activeMechanicJob.areaName}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MatteBlack,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Map Subtle Bottom Vignette so bottom sheet integrates cleanly
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, MatteBlack.copy(alpha = 0.85f))
                    )
                )
        )

        // Floating Re-Center Button & Clicked Mechanic Quick Card
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = (bottomPaddingDp + 14).dp)
        ) {
            AnimatedVisibility(
                visible = selectedMapMechanic != null && role == AppRole.CUSTOMER,
                enter = fadeIn() + slideInVertically { it / 2 },
                exit = fadeOut() + slideOutVertically { it / 2 }
            ) {
                selectedMapMechanic?.let { mech ->
                    val dist = LahoreSampleData.computeDistanceKm(
                        mech.currentX,
                        mech.currentY,
                        selectedArea.normalizedX,
                        selectedArea.normalizedY
                    )
                    val eta = LahoreSampleData.computeEtaMinutes(dist)
                    Surface(
                        color = CharcoalSurface,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, ElectricLime.copy(alpha = 0.5f)),
                        shadowElevation = 10.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, bottom = 10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Surface(
                                color = ElectricLime.copy(alpha = 0.16f),
                                shape = CircleShape,
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Build,
                                        contentDescription = null,
                                        tint = ElectricLime,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = mech.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = CrispWhite,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
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
                                    text = "${mech.workshopName} • ${mech.specialization.label}",
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
                                        text = "${mech.rating} (${mech.reviewCount}) • $dist km • $eta min",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CrispWhite
                                    )
                                }
                            }
                            IconButton(
                                onClick = { onSelectMechanic(null) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close mechanic preview",
                                    tint = MutedText,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            Surface(
                color = CharcoalSurface,
                shape = CircleShape,
                border = BorderStroke(1.dp, SubtleBorder),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("recenter_map_button")
                    .clickable { onRecenterClicked() }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Re-center Lahore map",
                        tint = ElectricLime,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // Map watermark badge on top-left corner below header
        Surface(
            color = MatteBlack.copy(alpha = 0.72f),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 148.dp)
        ) {
            Text(
                text = "LAHORE LIVE GRID • ${if (role == AppRole.CUSTOMER) "${nearbyMechanics.size} UNITS ACTIVE" else "${incomingRequests.size} REQUESTS"}",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    letterSpacing = 0.6.sp
                ),
                color = DimText,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
