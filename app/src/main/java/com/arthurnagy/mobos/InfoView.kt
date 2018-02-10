package com.arthurnagy.mobos

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.content.ContextCompat.startActivity
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ScrollView


class InfoView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ScrollView(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_info, this, true)
        findViewById<View>(R.id.partners).setOnClickListener {
            DetailActivity.start(context, MobOsWebView.Entry.Partners())
        }
        findViewById<View>(R.id.team).setOnClickListener {
            DetailActivity.start(context, MobOsWebView.Entry.Team())
        }
        findViewById<View>(R.id.coc).setOnClickListener {
            DetailActivity.start(context, MobOsWebView.Entry.CoC())
        }
        findViewById<View>(R.id.registration).setOnClickListener {
            DetailActivity.start(context, MobOsWebView.Entry.Registration())
        }
        findViewById<View>(R.id.venue).setOnClickListener {
            val venueIntentUri = Uri.parse("geo:46.752188,23.576563?q=" + Uri.encode("Golden Tulip Ana Dome"))
            val venueIntent = Intent(Intent.ACTION_VIEW, venueIntentUri).apply { `package` = "com.google.android.apps.maps" }
            if (venueIntent.resolveActivity(context.packageManager) != null) {
                startActivity(context, venueIntent, null)
            } else {
                startActivity(context, Intent(Intent.ACTION_VIEW, Uri.parse("https://goo.gl/maps/iByLTDw95Dy")), null)
            }
        }
        findViewById<View>(R.id.github).setOnClickListener {
            startActivity(context, Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ArthurNagy/MobOS-Lite")), null)
        }
    }


}