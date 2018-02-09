/*
 * Copyright (c) 2018 Halcyon Mobile
 * http://www.halcyonmobile.com
 * All rights reserved.
 */

package com.arthurnagy.mobos

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.TabLayout
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

class MainActivity : AppCompatActivity() {

    private val pages = mutableMapOf<Int, ViewGroup>()
    private val container by lazy { findViewById<FrameLayout>(R.id.page_container) }
    private val tab by lazy { findViewById<TabLayout>(R.id.tab) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            toolbar.setTitle(getTitleResource(item.itemId))
            loadPage(item.itemId)
            true
        }
        bottomNavigationView.selectedItemId = R.id.agenda
    }

    private fun loadPage(itemId: Int) {
        container.removeAllViews()
        tab.visibility = if (itemId == R.id.agenda) View.VISIBLE else View.GONE
        val page: ViewGroup = pages[itemId] ?: when (itemId) {
            R.id.agenda -> {
                createAgendaPager().also {
                    pages[itemId] = it
                    tab.setupWithViewPager(it)
                }

            }
            R.id.speakers -> {
                ContentView(this)
                        .apply { loadContent(MobOsWebView.Entry.Speakers()) }
                        .also { pages[itemId] = it }
            }
            R.id.info -> {
                InfoView(this).also { pages[itemId] = it }
            }
            else -> throw IllegalStateException("Shouldn't be here")
        }
        container.addView(page)

    }

    private fun createAgendaPager() = (LayoutInflater.from(this).inflate(R.layout.view_agenda, container, false) as ViewPager).apply {
        adapter = object : PagerAdapter() {
            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                val content = ContentView(context).apply { loadContent(MobOsWebView.Entry.Agenda(position)) }
                container.addView(content)
                return content
            }

            override fun isViewFromObject(view: View, obj: Any) = view == obj

            override fun getCount() = 2

            override fun getPageTitle(position: Int): String = "Day ${position + 1}"
        }
    }

    private fun getTitleResource(itemId: Int) = when (itemId) {
        R.id.agenda -> R.string.agenda
        R.id.speakers -> R.string.speakers
        R.id.info -> R.string.info
        else -> R.string.app_name
    }

}
