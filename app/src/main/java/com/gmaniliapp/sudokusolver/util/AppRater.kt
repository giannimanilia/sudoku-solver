package com.gmaniliapp.sudokusolver.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.gmaniliapp.sudokusolver.R
import com.gmaniliapp.sudokusolver.common.Constants
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*

class AppRater(private val context: Context) {

    private var count = 0

    private fun scheduleRate() {
        val preferenceManager = PreferenceManager(context, "apprater")

        // Don't show again
        if (preferenceManager.getBoolean("dontshowagain", false)) return
        count = preferenceManager.getInt("count", 0) + 1
        preferenceManager.putInt("count", count)

        // Check launches since last remind
        val lastCount: Int
        if (preferenceManager.getBoolean("remindmelater", false)) {
            lastCount = preferenceManager.getInt("remind_count", 0)
            if (lastCount + LAUNCHES_UNTIL_PROMPT > count) return
        }
        var lastLaunch = preferenceManager.getDate("date_last_launch")
        if (lastLaunch == null) {
            lastLaunch = Date()
            preferenceManager.putDate("date_last_launch", lastLaunch)
        }
        val calendar = Calendar.getInstance()
        calendar.time = lastLaunch
        calendar.add(Calendar.DATE, DAYS_UNTIL_PROMPT)
        val nextLaunch = calendar.time

        // Check on launches and date interval
        if (count >= LAUNCHES_UNTIL_PROMPT) {
            if (Date().compareTo(nextLaunch) >= 0) {
                displayDialog()
            }
        }
    }

    /**
     * Display dialog to rate app
     */
    private fun displayDialog() {
        // Create dialog
        val dialog = MaterialAlertDialogBuilder(context, R.style.MaterialDialogTheme)
                .setTitle(context.getString(R.string.rate_app))
                .setMessage(context.getString(R.string.rate_app_confirmation, context.getString(R.string.app_name)))
                .setPositiveButton(context.getString(R.string.yes), null)
                .setNegativeButton(context.getString(R.string.rate_app_do_not_ask_again), null)
                .setNeutralButton(context.getString(R.string.rate_app_remind_me_later), null)
                .create()
        dialog.setOnShowListener { dialogInterface: DialogInterface? ->
            val positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positive.setOnClickListener { view1: View? ->
                // Avoid creation of dialog in the future and rate
                val preferenceManager = PreferenceManager(context, "apprater")
                preferenceManager.putBoolean("dontshowagain", true)
                rate()
                dialog.dismiss()
            }
            val negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            negative.setOnClickListener { view1: View? ->
                // Avoid creation of dialog in the future
                val preferenceManager = PreferenceManager(context, "apprater")
                preferenceManager.putBoolean("dontshowagain", true)
                dialog.dismiss()
            }
            val neutral = dialog.getButton(AlertDialog.BUTTON_NEUTRAL)
            neutral.setOnClickListener { view1: View? ->
                // Save status of this launch
                val preferenceManager = PreferenceManager(context, "apprater")
                preferenceManager.putInt("remind_count", count)
                preferenceManager.putBoolean("remindmelater", true)
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    /**
     * Rate app on play store
     */
    fun rate() {
        val uri = Uri.parse("market://details?id=" + Constants.APP_PACKAGE)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            context.startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + Constants.APP_PACKAGE)))
        }
    }

    companion object {
        private const val DAYS_UNTIL_PROMPT = 3
        private const val LAUNCHES_UNTIL_PROMPT = 4
    }

    init {
        scheduleRate()
    }
}