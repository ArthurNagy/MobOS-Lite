/*
 * Copyright (c) 2018 Halcyon Mobile
 * http://www.halcyonmobile.com
 * All rights reserved.
 */

package com.arthurnagy.mobos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity


class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contentView = ContentView(this)
        setContentView(contentView)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        contentView.setWebViewMarginTop(resources.getDimensionPixelOffset(R.dimen.web_view_margin_top) + resources.getDimensionPixelOffset(R.dimen.detail_content_top_padding))
        contentView.loadContent(MobOsWebView.Entry.Detail(intent.getStringExtra(URL)))
        contentView.onTitleReceived = { title = it }
    }

    override fun onSupportNavigateUp(): Boolean {
        supportFinishAfterTransition()
        return true
    }

    companion object {
        private const val URL = "url"
        fun start(context: Context, url: String) {
            context.startActivity(Intent(context, DetailActivity::class.java).putExtra(URL, url))
        }
    }

}