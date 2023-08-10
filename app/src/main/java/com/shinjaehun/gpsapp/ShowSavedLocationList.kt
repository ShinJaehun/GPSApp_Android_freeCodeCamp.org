package com.shinjaehun.gpsapp

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.shinjaehun.gpsapp.databinding.ActivityShowSavedLocationListBinding

class ShowSavedLocationList : AppCompatActivity() {

    private lateinit var binding: ActivityShowSavedLocationListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowSavedLocationListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val myApplication: MyApplication = applicationContext as MyApplication
        var savedLocations = myApplication.myLocations

        binding.lvWayPoints.adapter = ArrayAdapter<Location>(this, android.R.layout.simple_list_item_1, savedLocations)
    }
}