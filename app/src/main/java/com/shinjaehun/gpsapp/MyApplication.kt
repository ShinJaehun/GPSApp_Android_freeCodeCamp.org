package com.shinjaehun.gpsapp

import android.app.Application
import android.location.Location

class MyApplication: Application() {

    lateinit var singleton: MyApplication

    lateinit var myLocations: MutableList<Location>

    companion object {
//        lateinit var singleton: MyApplication
//        lateinit var myLocations: List<Location>
    }

//    private var singleton: MyApplication = this
//        get() {
//            return singleton
//        }
//
//    private var myLocations: List<Location> = listOf()
//        get() {
//            return myLocations
//        }

    override fun onCreate() {
        super.onCreate()
        singleton = this
        myLocations = mutableListOf()
    }

}