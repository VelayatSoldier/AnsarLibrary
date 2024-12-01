package ir.ansar.library.utils

import android.content.Context
import android.content.SharedPreferences

object SettingsUtils {

    lateinit var settings :SharedPreferences

    fun initSettings(context: Context){
        settings =  context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    }
}