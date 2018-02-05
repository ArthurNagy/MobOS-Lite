/*
 * Copyright (c) 2018 Halcyon Mobile
 * http://www.halcyonmobile.com
 * All rights reserved.
 */

package com.arthurnagy.mobos

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient

@SuppressLint("SetJavaScriptEnabled")
class MobOsWebView : WebView {

    var callback: Callback? = null
    var entry: Entry? = null

    @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr)

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView, url: String) {
                entry?.let { view.loadUrl(showOnlyContentJsCode(it)) }
            }
        }
        webChromeClient = object : WebChromeClient() {

            override fun onReceivedTitle(view: WebView, title: String) {
                callback?.onTitleReceived(title)
            }

            override fun onProgressChanged(view: WebView, newProgress: Int) {
                callback?.onProgressChanged(newProgress)
            }
        }
        addJavascriptInterface(WebAppInterface(), "Android")
        settings.javaScriptEnabled = true
        settings.allowContentAccess = true
        settings.allowFileAccess = true
        settings.databaseEnabled = true
        settings.domStorageEnabled = true
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
        scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
        isScrollbarFadingEnabled = true
        isFocusableInTouchMode = true
    }

    fun load(entry: Entry) {
        this.entry = entry
        this.loadUrl("$BASE_URL${entry.key}")
    }

    interface Callback {
        fun onProgressChanged(progress: Int)
        fun onTitleReceived(title: String)
    }

    sealed class Entry(val key: String, val contentKey: String) {
        data class Agenda(val day: Int) : Entry("agenda", "entry-content")
        object Speakers : Entry("speakers", "hentry")
    }

    private class WebAppInterface {

        @JavascriptInterface
        fun log(message: String) {
            Log.d("MobOsWebView", message)
        }

    }

    companion object {
        const val BASE_URL = "http://romobos.com/5th-edition/"
        fun showOnlyContentJsCode(entry: Entry) = """javascript:
                                    function hideAllExceptContent() {
                                        var el = document.getElementsByClassName('${entry.contentKey}')[0];
                                        while (el && el != document.body) {
                                            var parent = el.parentNode;
                                            var siblings = parent.childNodes;
                                            for (var i = 0, len = siblings.length; i < len; i++) {
                                                if (siblings[i] != el && siblings[i].nodeType == 1) {
                                                    siblings[i].style.display = 'none';
                                                }
                                            }
                                            el = parent;
                                        }
                                    };
                                    try {
                                        hideAllExceptContent();
                                    } catch(err) {
                                        Android.log(err.message);
                                    }
                                    ${hideAdditionalElements(entry)}"""

        private fun hideAdditionalElements(entry: Entry): String = when (entry) {
            Entry.Speakers -> """
                function hideAdditionalElements() {
                    document.getElementsByClassName('entry-header')[0].style.display = 'none';
                    var content = document.getElementsByClassName('${entry.contentKey}')[0];
                    var divs = content.getElementsByClassName('employee');
                    for (var index = 0, length = divs.length; index < length; index++) {
                        var speaker = divs[index];
                        var speakerPosition = speaker.getElementsByClassName('employee-position')[0].innerText;
                        if (speakerPosition.includes('iOS')) {
                            speaker.style.display = 'none';
                        }
                    }
                };
                try {
                    hideAdditionalElements();
                } catch(err) {
                    Android.log(err.message);
                }
                """
            is Entry.Agenda -> """
                function hideAdditionalElements() {
                   var androidTables = document.getElementsByClassName('table1');
                   var el = androidTables[${entry.day}];
                   while (el && el != document.body) {
                       var parent = el.parentNode;
                       var siblings = parent.childNodes;
                       for (var i = 0, len = siblings.length; i < len; i++) {
                           if (siblings[i] != el && siblings[i].nodeType == 1) {
                               siblings[i].style.display = 'none';
                           }
                       }
                       el = parent;
                   }
                   Android.log(androidTables.length);
                   for (var index = 0, length = androidTables.length; index < length; index++) {
                        var table = androidTables[index];
                        var tableBody = table.getElementsByTagName('TBODY')[0];
                        var tableRows = tableBody.getElementsByTagName('TR');
                        Android.log(tableRows[0].innerHtml);
                        tableRows[0].style.display = 'none';
                    }
                };
                try {
                    hideAdditionalElements();
                } catch(err) {
                    Android.log(err.message);
                }
            """
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && canGoBack()) {
            goBack()
            return true
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event)
    }

}