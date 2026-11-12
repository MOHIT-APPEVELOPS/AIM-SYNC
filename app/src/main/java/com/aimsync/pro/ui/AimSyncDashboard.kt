package com.aimsync.pro.ui

import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aimsync.pro.engine.DeviceProfile
import com.aimsync.pro.engine.GyroCalibrator
import com.aimsync.pro.engine.TouchCurveEngine
import com.aimsync.pro.system.SystemOptimizer

private val BgDark = Color(0xFF0B0E14)
private val CardDark = Color(0xFF151A24)
private val NeonCyan = Color(0xFF00F5D4)
private val NeonPurple = Color(0xFF9B5DE5)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AimSyncDashboard() {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var sourceBrand by remember { mutableStateOf(DeviceProfile.IQOO_VIVO) }
    var targetBrand by remember { mutableStateOf(DeviceProfile.REALME_GT) }

    // Default iQOO competitive baseline sensitivities
    var baseTpp by remember { mutableFloatStateOf(115f) }
    var baseRedDot by remember { mutableFloatStateOf(55f) }
    var base3x by remember { mutableFloatStateOf(28f) }
    var baseGyroNoScope by remember { mutableFloatStateOf(320f) }
    var baseGyroRedDot by remember { mutableFloatStateOf(300f) }
    var baseGyro3x by remember { mutableFloatStateOf(210f) }

    // Gyro Calibration state
    val calibrator = remember { GyroCalibrator(context) }
    var gyroProgress by remember { mutableFloatStateOf(0f) }
    var gyroDrift by remember { mutableFloatStateOf(0f) }
    var gyroStatus by remember { mutableStateOf("Tap Calibrate (Keep phone flat)") }

    // Real-time Touch Sampling Rate (Hz) meter state
    var lastTouchTimeNs by remember { mutableLongStateOf(0L) }
    var measuredHz by remember { mutableIntStateOf(0) }

    DisposableEffect(Unit) {
        onDispose { calibrator.stop() }
    }

    val converted = remember(
        sourceBrand, targetBrand, baseTpp, baseRedDot, base3x,
        baseGyroNoScope, baseGyroRedDot, baseGyro3x
    ) {
        TouchCurveEngine.convertSensitivity(
            context = context,
            source = sourceBrand,
            target = targetBrand,
            baseTpp = baseTpp.toInt(),
            baseRedDot = baseRedDot.toInt(),
            base3x = base3x.toInt(),
            baseGyroNoScope = baseGyroNoScope.toInt(),
            baseGyroRedDot = baseGyroRedDot.toInt(),
            baseGyro3x = baseGyro3x.toInt()
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("AIMSYNC PRO", color = NeonCyan, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                Text("Cross-OS Touch & Gyro Bridge", color = Color.Gray, fontSize = 12.sp)
            }
            Surface(color = Color(0xFF102A26), shape = RoundedCornerShape(8.dp)) {
                Text(
                    "ANTI-CHEAT SAFE",
                    color = NeonCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        // 1. Device Curve Selector Card
        Card(colors = CardDefaults.cardColors(containerColor = CardDark), shape = RoundedCornerShape(14.dp)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("1. Touch Curve Profile", color = Color.White, fontWeight = FontWeight.Bold)

                Text("Feel You Want (Reference):", color = Color.LightGray, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DeviceProfile.entries.forEach { profile ->
                        FilterChip(
                            selected = sourceBrand == profile,
                            onClick = { sourceBrand = profile },
                            label = { Text(profile.name.substringBefore("_"), fontSize = 12.sp) }
                        )
                    }
                }

                Text("Your Current Phone OS:", color = Color.LightGray, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DeviceProfile.entries.forEach { profile ->
                        FilterChip(
                            selected = targetBrand == profile,
                            onClick = { targetBrand = profile },
                            label = { Text(profile.name.substringBefore("_"), fontSize = 12.sp) }
                        )
                    }
                }
            }
        }

        // 2. Input & Converted Sensitivity Output
        Card(colors = CardDefaults.cardColors(containerColor = CardDark), shape = RoundedCornerShape(14.dp)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("2. Calibrated BGMI Sensitivity", color = Color.White, fontWeight = FontWeight.Bold)

                SensitivitySlider("Base TPP No-Scope", baseTpp, 50f..250f) { baseTpp = it }
                SensitivitySlider("Base Red Dot ADS", baseRedDot, 20f..150f) { baseRedDot = it }
                SensitivitySlider("Base 3x Scope", base3x, 10f..100f) { base3x = it }
                SensitivitySlider("Base Gyro Red Dot", baseGyroRedDot, 100f..400f) { baseGyroRedDot = it }

                HorizontalDivider(color = Color.DarkGray)

                Text("APPLY THESE VALUES IN BGMI:", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    StatBadge("TPP", "${converted.tppNoScope}%")
                    StatBadge("Red Dot", "${converted.redDot}%")
                    StatBadge("2x", "${converted.scope2x}%")
                    StatBadge("3x", "${converted.scope3x}%")
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    StatBadge("Gyro TPP", "${converted.gyroNoScope}%")
                    StatBadge("Gyro Dot", "${converted.gyroRedDot}%")
                    StatBadge("Gyro 3x", "${converted.gyro3x}%")
                    StatBadge("4x ADS", "${converted.scope4x}%")
                }
            }
        }

        // 3. Hardware Gyro Zero-Drift Calibrator
        Card(colors = CardDefaults.cardColors(containerColor = CardDark), shape = RoundedCornerShape(14.dp)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("3. Hardware Gyroscope Stabilizer", color = Color.White, fontWeight = FontWeight.Bold)
                Text(gyroStatus, color = Color.LightGray, fontSize = 12.sp)

                LinearProgressIndicator(
                    progress = { gyroProgress },
                    modifier = Modifier.fillMaxWidth().height(6.dp),
                    color = NeonPurple
                )

                Button(
                    onClick = {
                        gyroStatus = "Calibrating sensor bias... Do not move phone"
                        calibrator.startCalibration { prog, drift, done ->
                            gyroProgress = prog
                            gyroDrift = drift
                            if (done) {
                                gyroStatus = "Calibrated! Residual Drift: ${"%.5f".format(drift)} rad/s"
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("CALIBRATE GYRO SENSOR", fontWeight = FontWeight.Bold)
                }
            }
        }

        // 4. Live Touch Polling Rate (Hz) Pad
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .background(CardDark, RoundedCornerShape(14.dp))
                .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                .pointerInteropFilter { event ->
                    if (event.action == MotionEvent.ACTION_MOVE) {
                        val now = System.nanoTime()
                        if (lastTouchTimeNs != 0L) {
                            val deltaSec = (now - lastTouchTimeNs) / 1_000_000_000.0
                            if (deltaSec > 0) {
                                val instantHz = (1.0 / deltaSec).toInt()
                                if (instantHz in 60..2000) {
                                    measuredHz = (measuredHz * 0.7 + instantHz * 0.3).toInt()
                                }
                            }
                        }
                        lastTouchTimeNs = now
                    } else if (event.action == MotionEvent.ACTION_UP) {
                        lastTouchTimeNs = 0L
                    }
                    true
                },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    if (measuredHz > 0) "$measuredHz Hz" else "SWIPE HERE TO TEST TOUCH RATE",
                    color = NeonCyan,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text("Real-time Touch Sampling Analyzer", color = Color.Gray, fontSize = 11.sp)
            }
        }

        // 5. System Touch & Gaming Tweaks
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = { SystemOptimizer.openPointerSpeedSettings(context) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Pointer Speed", fontSize = 12.sp, color = Color.White)
            }
            OutlinedButton(
                onClick = { SystemOptimizer.openTouchDelaySettings(context) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Touch Delay", fontSize = 12.sp, color = Color.White)
            }
            OutlinedButton(
                onClick = { SystemOptimizer.toggleGamingDnd(context) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Gaming DND", fontSize = 12.sp, color = NeonCyan)
            }
        }
    }
}

@Composable
private fun SensitivitySlider(label: String, value: Float, range: ClosedFloatingPointRange<Float>, onChange: (Float) -> Unit) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = Color.LightGray, fontSize = 12.sp)
            Text("${value.toInt()}%", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Slider(value = value, onValueChange = onChange, valueRange = range)
    }
}

@Composable
private fun StatBadge(title: String, value: String) {
    Column(
        modifier = Modifier
            .background(Color(0xFF1E2533), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, color = Color.Gray, fontSize = 10.sp)
        Text(value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}
