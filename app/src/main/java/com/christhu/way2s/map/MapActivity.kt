package com.christhu.way2s.map

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.christhu.way2s.R
import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.AsyncTask
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.log

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private var originLatitude: Double = 28.5021359
    private var originLongitude: Double = 77.4054901
    private var destinationLatitude: Double = 28.5151087
    private var destinationLongitude: Double = 77.3932163
    var autocompleteFragment: AutocompleteSupportFragment? = null
    private lateinit var etPlace: EditText
    var autocompleteFragmentEnd: AutocompleteSupportFragment? = null
    private lateinit var etPlaceEnd: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        val apiKey = "key"
        // Initializing the Places API with the help of our API_KEY

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }
        // Map Fragment
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.place_autocomplete_fragment) as AutocompleteSupportFragment
        Places.initialize(applicationContext, apiKey)
        etPlace =
            autocompleteFragment!!.view?.findViewById(R.id.places_autocomplete_search_input) as EditText
        etPlace.textSize = 16F
        // Create a new Places client instance.
        val placesClient = Places.createClient(this)
        autocompleteFragment!!.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG
            )
        )
        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment!!.setOnPlaceSelectedListener(object : PlaceSelectionListener,
            com.google.android.libraries.places.widget.listener.PlaceSelectionListener {
            override fun onPlaceSelected(p0: Place) {
                val startLatLong = p0.latLng
                originLatitude=startLatLong!!.latitude
                originLongitude=startLatLong.longitude
            }

            override fun onPlaceSelected(p0: com.google.android.gms.location.places.Place?) {
                val ss = p0!!.address
                try {
                } catch (e: Exception) {
                }
            }

            override fun onError(p0: Status) {
            }
        })


        autocompleteFragmentEnd =
            supportFragmentManager.findFragmentById(R.id.place_autocomplete_fragment_end) as AutocompleteSupportFragment
        Places.initialize(applicationContext, apiKey)
        etPlaceEnd =
            autocompleteFragmentEnd!!.view?.findViewById(R.id.places_autocomplete_search_input) as EditText
        etPlaceEnd.textSize = 16F
        // Create a new Places client instance.
        val placesClientEnd = Places.createClient(this)
        autocompleteFragmentEnd!!.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG
            )
        )
        // Initialize the AutocompleteSupportFragment.
        autocompleteFragmentEnd!!.setOnPlaceSelectedListener(object : PlaceSelectionListener,
            com.google.android.libraries.places.widget.listener.PlaceSelectionListener {
            override fun onPlaceSelected(p0: Place) {
                val endLatLong = p0.latLng
                destinationLatitude=endLatLong!!.latitude
                destinationLongitude=endLatLong.longitude
//                centerMapOnMyLocation(p0.latLng)
            }

            override fun onPlaceSelected(p0: com.google.android.gms.location.places.Place?) {
                val ss = p0!!.address
                try {
                } catch (e: Exception) {
                }
            }

            override fun onError(p0: Status) {
            }
        })

        val gd = findViewById<Button>(R.id.directions)
        gd.setOnClickListener {
            mapFragment.getMapAsync {
                mMap = it
                val originLocation = LatLng(originLatitude, originLongitude)
                mMap.addMarker(MarkerOptions().position(originLocation))
                val destinationLocation = LatLng(destinationLatitude, destinationLongitude)
                mMap.addMarker(MarkerOptions().position(destinationLocation))
                val urll = getDirectionURL(originLocation, destinationLocation, apiKey)
                GetDirection(urll).execute()
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(originLocation, 14F))
            }
        }

    }

    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0!!
        val originLocation = LatLng(originLatitude, originLongitude)
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(originLocation))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(originLocation, 18F))
    }

    private fun getDirectionURL(origin: LatLng, dest: LatLng, secret: String): String {
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}" +
                "&destination=${dest.latitude},${dest.longitude}" +
                "&sensor=false" +
                "&mode=driving" +
                "&key=$secret"
    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetDirection(val url: String) :
        AsyncTask<Void, Void, List<List<LatLng>>>() {
        override fun doInBackground(vararg params: Void?): List<List<LatLng>> {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body!!.string()

            val result = ArrayList<List<LatLng>>()
            try {
                val respObj = Gson().fromJson(data, MapData::class.java)
                val path = ArrayList<LatLng>()
                for (i in 0 until respObj.routes[0].legs[0].steps.size) {
                    path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                }
                result.add(path)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(result: List<List<LatLng>>) {
            val lineoption = PolylineOptions()
            for (i in result.indices) {
                lineoption.addAll(result[i])
                lineoption.width(10f)
                lineoption.color(Color.RED)
                lineoption.geodesic(true)
            }
            mMap.addPolyline(lineoption)
        }
    }

    fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val latLng = LatLng((lat.toDouble() / 1E5), (lng.toDouble() / 1E5))
            poly.add(latLng)
        }
        return poly
    }

}