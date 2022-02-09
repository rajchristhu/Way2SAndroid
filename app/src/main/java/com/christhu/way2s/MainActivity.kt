package com.christhu.way2s

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.christhu.way2s.databinding.ActivityMainBinding
import com.christhu.way2s.map.locs.GpsTracker
import com.google.android.gms.location.FusedLocationProviderClient
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding

    lateinit var viewModel: MainViewModel
    var lat: Double? = null
    var longs: Double? = null
    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    public var gpsTracker: GpsTracker? = null
    private val retrofitService = RetrofitService.getInstance()
    val adapter = MainAdapter(this,lat,longs)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        start()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, MyViewModelFactory(MainRepository(retrofitService))).get(MainViewModel::class.java)

        binding.recyclerview.adapter = adapter

        viewModel.dataList.observe(this, Observer {
            Log.d(TAG, "onCreate: $it")
            adapter.setDataList(it)
        })

        viewModel.errorMessage.observe(this, Observer {

        })
        viewModel.getAllData()
    }
    fun start() {
        if (checkPermissions()) {
//
                getLastLocation()

        } else {
            requestPermission()
        }
    }
    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                gpsTracker = GpsTracker(this)
                if (gpsTracker!!.canGetLocation()) {
                    val latitude: Double = gpsTracker!!.getLatitude()
                    val longitude: Double = gpsTracker!!.getLongitude()
                    lat = latitude
                    longs = longitude

                } else {
                    gpsTracker!!.showSettingsAlert()
                }

            } else {
                alert("Kindly enable your location ") {
                    title = "Alert"
//                    isCancelable = false
                    okButton {
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        startActivity(intent)
                    }
                }.show()
//                Toast.makeText(makeTextthis, "Turn on location", Toast.LENGTH_LONG).show()
            }
        } else {
            requestPermission()
        }
    }
}