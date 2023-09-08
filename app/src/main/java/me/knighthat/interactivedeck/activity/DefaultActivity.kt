package me.knighthat.interactivedeck.activity

import android.content.Context
import android.os.Bundle
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import me.knighthat.interactivedeck.R
import me.knighthat.interactivedeck.connection.wireless.WirelessController
import me.knighthat.interactivedeck.console.Log
import me.knighthat.interactivedeck.event.EventHandler
import me.knighthat.interactivedeck.vars.ActiveProfile
import me.knighthat.interactivedeck.vars.Memory
import me.knighthat.interactivedeck.vars.Settings
import kotlin.system.exitProcess

class DefaultActivity : AppCompatActivity() {

    init {
        // Close program once user close app
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
                exitProcess(0)
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        EventHandler.DEF_ACTIVITY = this
        Memory.activeProfile = ViewModelProvider(this)[ActiveProfile::class.java]
        Settings.PREFERENCES = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default)

        val ipInput: TextInputEditText = findViewById(R.id.ipInput)
        val portInput: TextInputEditText = findViewById(R.id.portInput)

        loadLastSettings(ipInput, portInput)

        findViewById<Button>(R.id.connectBtn).setOnClickListener {
            if (ipInput.text == null || portInput.text == null) return@setOnClickListener
            this.attempt2Connect(ipInput.text.toString(), portInput.text.toString())
        }
    }

    private fun loadLastSettings(ipInput: TextInputEditText, portInput: TextInputEditText) {
        val address = Settings.address()
        ipInput.setText(address)
        Log.deb(address)

        val port = Settings.port()
        portInput.setText(port)
    }

    private fun attempt2Connect(ip: String, portString: String) {
        try {
            val port = portString.toInt()

            Log.info("Sending pairing request to $ip:$port", false)

            WirelessController(ip, port).start()
        } catch (e: NumberFormatException) {
            val warn = "Port must be a number between 1 and 65535"
            Log.warn(warn)
        }
    }
}