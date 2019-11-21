package com.example.tp5

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.widget.Toast
import org.jetbrains.anko.Android

/***
 *
 * on met une variable bool
 * et on y accée a partir de l activitymain pour connaitre sa valeur
 * **/

class BatterieLow: BroadcastReceiver(){
    var valeur=-1

    override fun onReceive(context: Context, intent: Intent?) {

        val level= intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val voltage = intent?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)

        if (level != null) {
            valeur=level
        }
        //Toast.makeText(context, "$level", Toast.LENGTH_LONG).show()



        //context.setTheme(android.R.style.ThemeOverlay_Material_Dark)

    }


}