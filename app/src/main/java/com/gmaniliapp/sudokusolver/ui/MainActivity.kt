package com.gmaniliapp.sudokusolver.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.gmaniliapp.sudokusolver.R
import com.gmaniliapp.sudokusolver.common.Constants.APP_LINK
import com.gmaniliapp.sudokusolver.ui.sudoku.SudokuListener
import com.gmaniliapp.sudokusolver.util.AppRater

class MainActivity : AppCompatActivity(), SudokuListener {

    private var appRater: AppRater? = null
    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        appRater = AppRater(this)
        progressBar = findViewById(R.id.progress_bar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_rate -> {
                appRater!!.rate()
                true
            }
            R.id.action_share -> {
                shareApp(this)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun manageProgressBar() {
        if (progressBar!!.visibility == View.VISIBLE) {
            progressBar!!.visibility = View.INVISIBLE
        } else {
            progressBar!!.visibility = View.VISIBLE
        }
    }

    fun shareApp(context: Context) {
        val message = getString(R.string.share_message, getString(R.string.app_name), APP_LINK)
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
        intent.putExtra(Intent.EXTRA_TEXT, message)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(Intent.createChooser(intent, getString(R.string.share)))
    }
}