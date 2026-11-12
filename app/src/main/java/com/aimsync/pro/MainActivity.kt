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
        
        // Dynamic UI layout for AimSync Pro Calculator
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
            setBackgroundColor(0xFF0B0E14.toInt())
        }

        val title = TextView(this).apply {
            text = "🎯 AIMSYNC PRO"
            textSize = 22f
            setTextColor(0xFF00F5D4.toInt())
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        layout.addView(title)

        val subtitle = TextView(this).apply {
            text = "Oppo/realme to iQOO Sensitivity Bridge"
            textSize = 12f
            setTextColor(0xFF888888.toInt())
            setPadding(0, 0, 0, 24)
        }
        layout.addView(subtitle)

        val inputLabel = TextView(this).apply {
            text = "Enter your Base TPP Sensitivity:"
            setTextColor(0xFFFFFFFF.toInt())
        }
        layout.addView(inputLabel)

        val inputEdit = EditText(this).apply {
            setText("115")
            setTextColor(0xFF00F5D4.toInt())
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }
        layout.addView(inputEdit)

        val resultText = TextView(this).apply {
            text = "Converted Oppo/realme TPP: --"
            textSize = 16f
            setTextColor(0xFFFFFFFF.toInt())
            setPadding(0, 24, 0, 24)
        }
        layout.addView(resultText)

        val calcButton = Button(this).apply {
            text = "CONVERT SENSITIVITY"
            setBackgroundColor(0xFF9B5DE5.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            setOnClickListener {
                val base = inputEdit.text.toString().toFloatOrNull() ?: 115f
                // Curve conversion formula (Oppo/realme acceleration compensation)
                val converted = (base * 1.14f).roundToInt()
                resultText.text = "Recommended Oppo/realme TPP: $converted%\n(Applied 1.14x curve compensation)"
                Toast.makeText(this@MainActivity, "Sensitivity Calibrated!", Toast.LENGTH_SHORT).show()
            }
        }
        layout.addView(calcButton)

        setContentView(layout)
    }
}
