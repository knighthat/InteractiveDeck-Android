package me.knighthat.interactivedeck.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import me.knighthat.interactivedeck.R
import me.knighthat.interactivedeck.connection.wireless.WirelessController
import me.knighthat.interactivedeck.console.Log
import me.knighthat.interactivedeck.vars.Settings
import kotlin.system.exitProcess

class DefaultActivity : AppCompatActivity() {

    companion object {
        @JvmField
        val HANDLER = Handler(Looper.getMainLooper())

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

        Settings.PREFERENCES = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default)

        val ipInput = findViewById<TextInputEditText>(R.id.ipInput)
        val portInput = findViewById<TextInputEditText>(R.id.portInput)

        loadLastSettings(ipInput, portInput)

        val connectBtn = findViewById<Button>(R.id.connectBtn)
        connectBtn.setOnClickListener {
            if (ipInput.text == null || portInput.text == null) return@setOnClickListener

            try {
                val ip = ipInput.text.toString()
                val port = portInput.text.toString().toInt()

                Log.deb("Sending pairing request to $ip:$port")

                WirelessController(ip, port).start()
            } catch (e: NumberFormatException) {
                val warn = "Port must be a number between 1 and 65535"
                toast(warn)
                Log.warn(warn)
            }
        }
    }

    private fun loadLastSettings(ipInput: TextInputEditText, portInput: TextInputEditText) {
        val address = Settings.address()
        ipInput.setText(address)
        Log.deb(address)

        val port = Settings.port()
        portInput.setText(port)
    }
}