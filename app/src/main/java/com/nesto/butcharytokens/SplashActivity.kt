package com.nesto.butcharytokens

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView


class SplashActivity : Activity() {
    var logo: ImageView? = null
    var set: SharedPreferences? = null
    private val SPLASH_DURATION = 3000 // 2 seconds
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    public override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        sharedPreferences = this.getSharedPreferences("sharedpreferences", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        logo = findViewById<View>(R.id.imageview) as ImageView
        val animation1 = AnimationUtils.loadAnimation(
            applicationContext, R.anim.modal_in
        )
        logo!!.startAnimation(animation1)
        set = PreferenceManager
            .getDefaultSharedPreferences(this@SplashActivity)
        val handler = Handler()
        handler.postDelayed({

            var typeVal = sharedPreferences.getString("usertype", "")

            if (typeVal.equals("customer")) {
                val intent = Intent(this@SplashActivity, DeptSelectionActivity::class.java)
                finish()
                this@SplashActivity.startActivity(intent)
                overridePendingTransition(
                    R.anim.fade_in,
                    R.anim.fade_out
                )
            } else if(typeVal.equals("store")) {
                val intent = Intent(this@SplashActivity, StoreActivity::class.java)
                finish()
                this@SplashActivity.startActivity(intent)
                overridePendingTransition(
                    R.anim.fade_in,
                    R.anim.fade_out
                )

            }else{
                val intent = Intent(this@SplashActivity, SettingsActivity::class.java)
                finish()
                this@SplashActivity.startActivity(intent)
                overridePendingTransition(
                    R.anim.fade_in,
                    R.anim.fade_out
                )
            }
        }, SPLASH_DURATION.toLong())
    }
}