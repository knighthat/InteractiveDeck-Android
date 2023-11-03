package me.knighthat.interactivedeck.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
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

        handleBackPress()

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

        button.setOnClickListener {
            when (val task = button.task) {
                is GotoPage -> switchProfile(task)
                else        ->
                    ActionRequest(
                        Action(button.uuid, Action.ActionType.PRESS)
                    ).send()
            }
        }
    }

    private fun startObservation() {
        Persistent.observeActive(object : Observer<Profile> {
            override fun update(oldValue: Profile?, newValue: Profile?) {
                reload()
            }
        })
    }

    fun reload() {
        if (Looper.myLooper() == Looper.getMainLooper())
            Persistent.getActive().ifPresent {
                layout.removeAllViews()

                layout.columnCount = it.columns
                layout.rowCount = it.rows

                for (button in it.buttons) {
                    button.layoutParams = gridParams(button, it.columns, it.rows, it.gap)
                    addClickEvent(button)
                    layout.addView(button)
                }
            }
        else
            EventHandler.post(this::reload)
    }

    @MainThread
    private fun switchProfile(task: GotoPage) = Persistent.findProfile(task.uuid).ifPresent(Persistent::setActive)

    @MainThread
    private fun handleBackPress() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                val confirm = AlertDialog.Builder(this@ButtonsLayout)
                confirm.setTitle("Disconnect?")
                confirm.setMessage("Are you sure you want to disconnect from host and go back to main menu?")
                confirm.setPositiveButton("Yes") { _, _ -> Connection.status = Connection.Status.DISCONNECTED }
                confirm.setNegativeButton("No") { _, _ -> }
                confirm.show()

            }
        })
    }

    override fun onStop() {
        super.onStop()
        layout.removeAllViews()
    }
}