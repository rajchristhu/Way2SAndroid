package com.christhu.way2s.utlility


import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import java.util.*
import kotlin.collections.ArrayList

class SessionMaintainence
/**
 * This is the parmeterized constructor.
 *
 * @param context This is the parameter used to pass the context of an activity.
 */
private constructor(context: Context) {
    private var userpreferences: SharedPreferences? = null
    private var courseID: Int? = null
    private var preferences: SharedPreferences
    /**
     * The Editor.
     */
    private var editor: SharedPreferences.Editor
    /**
     * The Context.
     */
    internal var context: Context

    /**
     * This method is used to read an activity screen name.
     *
     * @return This returns an activity screen name.
     */
    var termsandcon: Boolean
        get() = preferences.getBoolean(TERMANDCON, false)
        set(termsandcon) {
            editor.putBoolean(TERMANDCON, termsandcon)
            editor.commit()
        }


    init {
        this.context = context
        preferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = preferences.edit()

    }

    /**
     * This method is used to clear the sessions.
     */
    fun clearSession() {
        editor.clear()
        editor.commit()
    }

    /**
     * Sets is first loggedin.
     *
     * @param login the login
     */




    companion object {
        /**
         * The constant PREF_NAME.
         */
        val PREF_NAME = "W2S"

        private val TERMANDCON = "termsandcon"


        internal var PRIVATE_MODE = 0
        /**
         * Gets instance.
         *
         * @return the instance
         */
        @SuppressLint("StaticFieldLeak")
        var instance: SessionMaintainence? = null
            private set


        /**
         * Init.
         *
         * @param appContext the app context
         */
        fun init(appContext: Context) {
            if (instance == null) {
                instance = SessionMaintainence(appContext)

            }
        }
    }
}