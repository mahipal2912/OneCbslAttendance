package com.one.cbsl

interface MainActivityListener {
    fun setDrawerLocked(shouldLock: Boolean)
    fun updateValues()
    fun openDrawer()
    fun closeDrawer()
    fun isDrawerOpened(): Boolean
}