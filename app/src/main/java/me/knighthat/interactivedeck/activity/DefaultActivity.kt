package me.knighthat.interactivedeck.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import me.knighthat.interactivedeck.R
import me.knighthat.interactivedeck.connection.wireless.WirelessController
import kotlin.system.exitProcess

class DefaultActivity : AppCompatActivity() {

    companion object {
        lateinit var INSTANCE: DefaultActivity
        lateinit var BTN_LAYOUT: Intent

        @JvmStatic
        fun toast(msg: String) {
            Toast.makeText(INSTANCE, msg, Toast.LENGTH_SHORT).show()
        }
    }

    init {
        // Close program once user close app
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
                exitProcess(0)
            }
        })
    }

    fun startBtnLayout() {
        startActivity(BTN_LAYOUT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        INSTANCE = this
        BTN_LAYOUT = Intent(this, ButtonsLayout::class.java)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default)

        val ipInput = findViewById<TextInputEditText>(R.id.ipInput)
        val portInput = findViewById<TextInputEditText>(R.id.portInput)

        val connectBtn = findViewById<Button>(R.id.connectBtn)
        connectBtn.setOnClickListener {
            WirelessController.connect(ipInput, portInput)
        }
    }
}