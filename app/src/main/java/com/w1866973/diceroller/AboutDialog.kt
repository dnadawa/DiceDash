package com.w1866973.diceroller

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window

class AboutDialog(context: Context) : Dialog(context) {
    init {
        setCancelable(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.about_dialog)
    }
}