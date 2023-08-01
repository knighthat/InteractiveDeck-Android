package me.knighthat.interactivedeck.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.GridLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import me.knighthat.interactivedeck.R
import me.knighthat.interactivedeck.component.Buttons
import me.knighthat.interactivedeck.connection.wireless.WirelessController
import me.knighthat.interactivedeck.utils.ColorUtils

class ButtonsLayout : AppCompatActivity() {

    private lateinit var layout: GridLayout

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.buttons_layout)

        layout = findViewById(R.id.buttons_layout)
        Buttons.buttons().forEach { btn ->
            btn.setOnClickListener {
                val color = ColorUtils.randomColor()
                Log.d("[BTN]", "$color")
                btn.setBackgroundColor(color)
            }
            layout.addView(btn)
        }
    }

    override fun onBackPressed() {
        val confirm = AlertDialog.Builder(this)
        confirm.setTitle("Disconnect")
        confirm.setMessage("Are you sure you disconnect from host and go back to main menu?")
        confirm.setPositiveButton("Yes") { _, _ ->
            if (WirelessController.SOCKET != null)
                WirelessController.SOCKET?.close()

            super.onBackPressed()
        }
        confirm.setNegativeButton("No") { _, _ -> }
        confirm.show()
    }

    override fun onPause() {
        layout.removeAllViews()
        Buttons.clear()
        super.onPause()
    }
}