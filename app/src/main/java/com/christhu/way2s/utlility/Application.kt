package com.christhu.way2s.utlility
import android.app.Application
import android.content.Context


class Application : Application() {
    var context: Context? = null
    override fun onCreate() {
        super.onCreate()
        SessionMaintainence.init(this)
        context = applicationContext

    }
}
