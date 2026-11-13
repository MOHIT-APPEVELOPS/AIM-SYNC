package com.aimsync.pro

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
            setBackgroundColor(0xFF0B0E14.toInt()) // Gamer Dark Theme
        }

        val title = TextView(this).apply {
            text = "🎯 AIMSYNC PRO"
            textSize = 24f
            setTextColor(0xFF00F5D4.toInt())
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        layout.addView(title)

        val subtitle = TextView(this).apply {
            text = "Oppo/realme vs iQOO Touch Bridge"
            textSize = 13f
            setTextColor(0xFFAAAAAA.toInt())
            setPadding(0, 0, 0, 30)
        }
        layout.addView(subtitle)

        val label = TextView(this).apply {
            text = "Enter iQOO / Base TPP Sensitivity:"
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 14f
        }
        layout.addView(label)

        val input = EditText(this).apply {
            setText("115")
            setTextColor(0xFF00F5D4.toInt())
            textSize = 18f
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }
        layout.addView(input)

        val result = TextView(this).apply {
            text = "Recommended Oppo/realme TPP: --"
            textSize = 16f
            setTextColor(0xFF00F5D4.toInt())
            setPadding(0, 30, 0, 30)
        }
        layout.addView(result)

        val btn = Button(this).apply {
            text = "CALIBRATE & CONVERT"
            setBackgroundColor(0xFF9B5DE5.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            setOnClickListener {
                val base = input.text.toString().toFloatOrNull() ?: 115f
                // Acceleration Curve Compensation Formula for Oppo/realme
                val converted = (base * 1.14f).roundToInt()
                result.text = "Oppo/realme Calibrated TPP: $converted%\n(Compensated for Touch Acceleration)"
                Toast.makeText(this@MainActivity, "Sensitivity Synced Successfully!", Toast.LENGTH_SHORT).show()
            }
        }
        layout.addView(btn)

        setContentView(layout)
    }
}
