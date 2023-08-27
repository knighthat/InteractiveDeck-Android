package me.knighthat.interactivedeck.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.GridLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import me.knighthat.interactivedeck.R
import me.knighthat.interactivedeck.component.action.PressAction
import me.knighthat.interactivedeck.connection.request.ActionRequest
import me.knighthat.interactivedeck.connection.wireless.WirelessController
import me.knighthat.interactivedeck.connection.wireless.WirelessSender
import me.knighthat.interactivedeck.task.GotoPage
import me.knighthat.interactivedeck.task.Task
import me.knighthat.interactivedeck.vars.Memory

class ButtonsLayout : AppCompatActivity() {

    private lateinit var layout: GridLayout

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.buttons_layout)

        layout = findViewById(R.id.buttons_layout)

        Memory.aLive.observe(this) {
            layout.removeAllViews()

            it.buttons.forEach { btn ->
                btn.setOnClickListener {

                    if (btn.task == null) {
                        val action = PressAction(btn)
                        val request = ActionRequest(action)

                        WirelessSender.send(request)
                    } else
                        this.switchProfile(btn.task!!)
                }
                layout.addView(btn)
            }
        }
    }

    private fun switchProfile(task: Task) {
        if (task !is GotoPage) return

        val profile = Memory.getProfile(task.uuid) ?: return
        Memory.active = profile
    }

    override fun onBackPressed() {

        val confirm = AlertDialog.Builder(this)
        confirm.setTitle("Disconnect")
        confirm.setMessage("Are you sure you disconnect from host and go back to main menu?")
        confirm.setPositiveButton("Yes") { _, _ ->
            super.onBackPressed()

            if (WirelessController.SOCKET != null)
                WirelessController.SOCKET!!.close()
            finish()
        }
        confirm.setNegativeButton("No") { _, _ -> }
        confirm.show()
    }

    override fun onStop() {
        super.onStop()
        layout.removeAllViews()
    }
}