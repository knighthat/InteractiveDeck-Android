package me.knighthat.interactivedeck.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.GridLayout
import androidx.activity.OnBackPressedCallback
import androidx.annotation.MainThread
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

        startObservation()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackPress()
            }
        })
    }

    private fun startObservation() {
        Memory.aLive.observe(this) {
            layout.removeAllViews()

            layout.columnCount = it.columns()
            layout.rowCount = it.rows()

            for (button in it.buttons()) {
                val params = GridLayout.LayoutParams()
                params.rowSpec = GridLayout.spec(button.y, 1, 1f)
                params.columnSpec = GridLayout.spec(button.x, 1, 1f)
                params.topMargin = if (button.y == 0) 0 else it.gap()
                params.bottomMargin = if (button.y == it.rows() - 1) 0 else it.gap()
                params.leftMargin = if (button.x == 0) 0 else it.gap()
                params.rightMargin = if (button.x == it.columns() - 1) 0 else it.gap()
                button.layoutParams = params


                if (!button.hasOnClickListeners())
                    button.setOnClickListener {
                        if (button.task == null) {
                            val action = PressAction(button)
                            val request = ActionRequest(action)

                            WirelessSender.send(request)
                        } else
                            this.switchProfile(button.task!!)
                    }
                layout.addView(button)
            }
        }
    }

    @MainThread
    private fun switchProfile(task: Task) {
        if (task !is GotoPage) return

        Memory
            .getProfile(task.uuid)
            .ifPresent { Memory.active = it }
    }

    @MainThread
    private fun handleBackPress() {
        val confirm = AlertDialog.Builder(this)
        confirm.setTitle("Disconnect")
        confirm.setMessage("Are you sure you disconnect from host and go back to main menu?")
        confirm.setPositiveButton("Yes") { _, _ ->
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