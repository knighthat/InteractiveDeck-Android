package me.knighthat.interactivedeck.activity

import android.os.Bundle
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import me.knighthat.interactivedeck.R
import me.knighthat.interactivedeck.connection.wireless.WirelessController
import me.knighthat.interactivedeck.event.EventHandler
import me.knighthat.interactivedeck.logging.Logger
import me.knighthat.interactivedeck.vars.Settings
import me.knighthat.lib.logging.Log
import kotlin.system.exitProcess

class DefaultActivity : AppCompatActivity() {

    private fun init() {
        Log.setLogger(Logger())

        EventHandler.DEF_ACTIVITY = this
        Settings.PREFERENCES = getSharedPreferences("AppSettings", MODE_PRIVATE)
    }

    private fun addEvents() {
        // Close program once user close app
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
                exitProcess(0)
            }
        })
    }

    private fun connectTo(ipInput: TextInputEditText, portInput: TextInputEditText) {
        val address = ipInput.text
        val port = portInput.text

        if (address.isNullOrBlank()) {
            Log.warn("Missing IP Address!")
            return
        }

        if (port.isNullOrBlank()) {
            Log.warn("Missing Port!")
            return
        }

        Log.info("Sending pairing request to $address:$port")

        runCatching {

            WirelessController(
                address.toString(),
                port.toString().toInt()
            ).start()

        }.onFailure {
            Log.warn("Port must be a number between 1 and 65535")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        addEvents()

        setContentView(R.layout.activity_default)

        val ipInput: TextInputEditText = findViewById(R.id.ipInput)
        val portInput: TextInputEditText = findViewById(R.id.portInput)

        // Load last settings
        ipInput.setText(Settings.address())
        portInput.setText(Settings.port())

        findViewById<Button>(R.id.connectBtn).setOnClickListener {
            connectTo(ipInput, portInput)
        }
    }
}