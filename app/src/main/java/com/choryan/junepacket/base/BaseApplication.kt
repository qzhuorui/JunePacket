package com.choryan.junepacket.base

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex

/**
 * @author: ChoRyan Quan
 * @date: 6/10/21
 */
class BaseApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}