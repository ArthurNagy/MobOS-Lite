/*
 * Copyright (c) 2018 Halcyon Mobile
 * http://www.halcyonmobile.com
 * All rights reserved.
 */

package com.arthurnagy.mobos

import android.content.Context
import android.net.ConnectivityManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.ViewSwitcher
import com.arthurnagy.mobos.R.id.progress


class ContentView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    private val switcher by lazy { findViewById<ViewSwitcher>(R.id.switcher) }
    private val content by lazy { findViewById<MobOsWebView>(R.id.content) }
    private val progressBar by lazy { findViewById<ProgressBar>(R.id.progress) }
    private val message by lazy { findViewById<TextView>(R.id.message) }
    private val icon by lazy { findViewById<ImageView>(R.id.icon) }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_content, this, true)
        content.callback = object : MobOsWebView.Callback {

            override fun onProgressChanged(progress: Int) {
                progressBar.progress = progress
                if (progress == 100 && switcher.displayedChild == 0) {
                    switcher.showNext()
                }
            }

            override fun onTitleReceived(title: String) = Unit
        }
    }

    fun loadContent(entry: MobOsWebView.Entry) {
        if (isConnectedToInternet()) {
            content.load(entry)
            message.setText(R.string.loading)
            icon.setImageResource(R.drawable.ic_cloud_24dp)
        } else {
            message.setText(R.string.no_connection_error)
            icon.setImageResource(R.drawable.ic_wifi_off_24dp)
        }
        if (progress != 100 && switcher.displayedChild != 0) {
            switcher.showNext()
        }
    }

    private fun isConnectedToInternet(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

}