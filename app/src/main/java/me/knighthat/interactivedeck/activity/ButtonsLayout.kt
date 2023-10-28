package me.knighthat.interactivedeck.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.GridLayout
import androidx.activity.OnBackPressedCallback
import androidx.annotation.MainThread
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import me.knighthat.interactivedeck.R
import me.knighthat.interactivedeck.component.ibutton.IButton
import me.knighthat.interactivedeck.event.EventHandler
import me.knighthat.interactivedeck.file.Profile
import me.knighthat.interactivedeck.persistent.Persistent
import me.knighthat.interactivedeck.task.GotoPage
import me.knighthat.lib.connection.Connection
import me.knighthat.lib.connection.action.Action
import me.knighthat.lib.connection.request.ActionRequest
import me.knighthat.lib.connection.wireless.WirelessSender
import me.knighthat.lib.observable.Observer

class ButtonsLayout : AppCompatActivity() {

    private lateinit var layout: GridLayout

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        EventHandler.setCurrentActivity(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.buttons_layout)

        layout = findViewById(R.id.buttons_layout)

        startObservation()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackPress()
            }
        })

        Persistent.setActive(Persistent.getDefaultProfile())
    }

    private fun gridParams(button: IButton, columns: Int, rows: Int, gap: Int): GridLayout.LayoutParams {
        val params = GridLayout.LayoutParams()
        params.rowSpec = GridLayout.spec(button.posY, 1, 1f)
        params.columnSpec = GridLayout.spec(button.posX, 1, 1f)
        params.topMargin = if (button.posY == 0) 0 else gap
        params.bottomMargin = if (button.posY == rows - 1) 0 else gap
        params.leftMargin = if (button.posX == 0) 0 else gap
        params.rightMargin = if (button.posX == columns - 1) 0 else gap

        return params
    }

    private fun addClickEvent(button: IButton) {
        if (button.hasOnClickListeners())
            return

        val task = button.task
        button.setOnClickListener {
            if (task !is GotoPage) {
                val action = Action(button.uuid, Action.ActionType.PRESS)
                WirelessSender.send(ActionRequest(action))
            } else
                switchProfile(task)
        }
    }

    private fun startObservation() {
        Persistent.observeActive(object : Observer<Profile> {
            override fun update(oldValue: Profile?, newValue: Profile?) {
                layout.removeAllViews()
                if (newValue == null) return

                layout.columnCount = newValue.columns
                layout.rowCount = newValue.rows

                for (button in newValue.buttons) {
                    button.layoutParams = gridParams(button, newValue.columns, newValue.rows, newValue.gap)
                    addClickEvent(button)
                    layout.addView(button)
                }
            }
        })
    }

    @MainThread
    private fun switchProfile(task: GotoPage) = Persistent.findProfile(task.uuid).ifPresent(Persistent::setActive)

    @MainThread
    private fun handleBackPress() {
        val confirm = AlertDialog.Builder(this)
        confirm.setTitle("Disconnect")
        confirm.setMessage("Are you sure you disconnect from host and go back to main menu?")
        confirm.setPositiveButton("Yes") { _, _ -> Connection.setStatus(Connection.Status.DISCONNECTED) }
        confirm.setNegativeButton("No") { _, _ -> }
        confirm.show()
    }

    override fun onStop() {
        super.onStop()
        layout.removeAllViews()
    }
}