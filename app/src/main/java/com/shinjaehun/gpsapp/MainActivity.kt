package com.shinjaehun.gpsapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.tasks.OnSuccessListener
import com.shinjaehun.gpsapp.databinding.ActivityMainBinding

const val LOCATION_UPDATE_INTERVAL = 5000L
const val FASTEST_LOCATION_INTERVAL = 5000L
const val PERMISSION_REQUEST_CODE = 1001
const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest

    private var currentLocation: Location? = null
    private lateinit var savedLocations: MutableList<Location>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationRequest = LocationRequest().apply {
            interval = LOCATION_UPDATE_INTERVAL
            fastestInterval = FASTEST_LOCATION_INTERVAL
//            priority = PRIORITY_BALANCED_POWER_ACCURACY
            priority = PRIORITY_HIGH_ACCURACY // 이걸 돌려놔야 GPS 값을 제대로 받아오는데...
        }

//        locationCallBack = object : LocationCallback() {
//
//            //            override fun onLocationResult(locationResult: LocationResult?) {
////                locationResult ?: return
////                for (location in locationResult.locations){
////                    // Update UI with location data
////                    // ...
////                }
////            }
//            override fun onLocationResult(locationResult: LocationResult) {
//                super.onLocationResult(locationResult)
//                // 자동 생성
////                updateUIValues(locationResult.lastLocation)
//                locationResult.lastLocation?.let { updateUIValues(it) }
//
//            }
//        }
//

//        updateGPS()
        if (checkPermissionForLocation(this)) {
            updateGPS()
        }

        binding.swGps.setOnClickListener {
            if (binding.swGps.isChecked) {
                locationRequest.priority = PRIORITY_HIGH_ACCURACY
                binding.tvSensor.text = "Using GPS sensors"
            } else {
                locationRequest.priority = PRIORITY_BALANCED_POWER_ACCURACY
                binding.tvSensor.text = "Using Towers + WIFI"
            }
        }

        binding.swLocationsupdates.setOnClickListener {
            if (binding.swLocationsupdates.isChecked) {
                startLocationUpdates()
            } else {
                stopLocationUpdates()
            }
        }

        binding.btnNewWayPoint.setOnClickListener {
            val myApplication: MyApplication = applicationContext as MyApplication
            savedLocations = myApplication.myLocations
            if (currentLocation != null) {
                savedLocations.add(currentLocation!!)
                binding.tvCountOfCrumbs.text = savedLocations.size.toString()

            } else {
                Log.i(TAG, "currentLocation is null")
            }
        }

        binding.btnShowWayPointList.setOnClickListener {
            val intent = Intent(MainActivity@this, ShowSavedLocationList::class.java)
            startActivity(intent)
        }

        binding.btnShowMap.setOnClickListener {
            val intent = Intent(MainActivity@this, MapsActivity::class.java)
            startActivity(intent)
        }

    }

    private fun stopLocationUpdates() {
        binding.tvUpdates.text = "Location is not being tracked"
        binding.tvLat.text = "Not tracking location"
        binding.tvLon.text = "Not tracking location"
        binding.tvSpeed.text = "Not tracking location"
        binding.tvAddress.text = "Not tracking location"
        binding.tvAccuracy.text = "Not tracking location"
        binding.tvAltitude.text = "Not tracking location"
        binding.tvSensor.text = "Not tracking location"

        fusedLocationProviderClient.removeLocationUpdates(mLocationCallback)
    }

//    private fun startLocationUpdates() {
//        binding.tvUpdates.text = "Location is being tracked"
//
//        // 자동 생성
//        // permission check를 여기서 또 하래???
////        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null)
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null)
//            return
//        }
//
//        updateGPS()
//    }

    private fun startLocationUpdates() {
        binding.tvUpdates.text = "Location is being tracked"

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
//        fusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, null)
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.getMainLooper())
    }

    // 시스템으로 부터 위치 정보를 콜백으로 받음
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            // 시스템에서 받은 location 정보를 onLocationChanged()에 전달
//            onLocationChanged(locationResult.lastLocation)
            locationResult?.locations?.let { locations ->
                for (location in locations) {
                    currentLocation = location
                    updateUIValues(location)
                }
            }
//            updateUIValues(locationResult.lastLocation) // 이렇게 하면 마지막에 찍은 위치만 계속 보여줌
        }
    }

//    fun onLocationChanged(location: Location?) {
//        if (location != null) {
//            Log.i(TAG, "lat: ${location.latitude} long: ${location.longitude}")
//        } else {
//            Log.i(TAG, "location is null")
//        }
//    }

    private fun updateGPS() {
        // 더 간결하게!
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                updateUIValues(location);
                currentLocation = location
            }
        }
    }

    // original code
//    private fun updateGPS() {
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
//        // 어쨌든 location이 null 이 올 수 있다고 하니까...
//
//        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
//                if (location != null) {
//                    updateUIValues(location);
//                }
//            }
//
//            // 큰 차이는 없음
////            fusedLocationProviderClient.lastLocation.addOnCompleteListener { taskLocation ->
////                if (taskLocation.isSuccessful && taskLocation.result != null) {
////                    updateUIValues(taskLocation.result);
////                }
////            }
//
//        } else {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                requestPermissions(
//                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
//                    PERMISSION_REQUEST_CODE
//                )
//            }
//        }
//
//        // location이 null이라서 이렇게도 해봤는데 큰 차이 없음
////        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
////            LocationServices.getFusedLocationProviderClient(this).run {
////                lastLocation.addOnSuccessListener {  location ->
////                    if (location == null) {
////                        Log.i(TAG, "location is null")
////                    } else {
////                        location
////                    }
////                }
////            }
////        } else {
////            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
////            }
////        }
//    }

    private fun updateUIValues(location: Location?) {
        if (location != null) {
            Log.i(TAG, "lat: ${location.latitude} long: ${location.longitude}")

            binding.tvLat.text = location.latitude.toString()
            binding.tvLon.text = location.longitude.toString()
            binding.tvAccuracy.text = location.accuracy.toString()

            if (location.hasAltitude()) {
                binding.tvAltitude.text = location.altitude.toString()
            } else {
                binding.tvAltitude.text = "Not available"
            }

            if (location.hasSpeed()) {
                binding.tvSpeed.text = location.speed.toString()
            } else {
                binding.tvSpeed.text = "Not available"
            }

            val geocoder : Geocoder = Geocoder(MainActivity@this)
            try {
                var address = geocoder.getFromLocation(location.latitude, location.longitude, 1) as List<Address>
                binding.tvAddress.text = address.get(0).getAddressLine(0)
            } catch (e : Exception) {
                binding.tvAddress.text = "Unable to get street address"
            }

            // 걍 버튼 클릭할 때 숫자 표시하는게 낫지 머...
//            val myApplication: MyApplication = applicationContext as MyApplication
//            savedLocations = myApplication.myLocations
//            binding.tvCountOfCrumbs.text = savedLocations.size.toString()


        } else {
            Log.i(TAG, "location is null")
        }

    }

    private fun checkPermissionForLocation(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                true
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
                false
            }
        } else {
            true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSION_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    startLocationUpdates()
                    updateGPS()
                } else {
                    Toast.makeText(this, "This map requires permissions to be granted in order to work properly.", Toast.LENGTH_LONG).show()
                    finish()
                }
        }
    }
}