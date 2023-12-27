package com.example.newrealrealassessment

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL
import android.Manifest


class MainActivity : AppCompatActivity(), LocationListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter
    private var currentLocationLat : Double = 0.0
    private var currentLocationLon : Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ItemAdapter(mutableListOf()) // Initialize with an empty list
        recyclerView.adapter = adapter

        requestPermissions()
    }

    fun requestPermissions() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                0
            )
        } else {
            startGps()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            0 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startGps()
                } else {
                    AlertDialog.Builder(this).setPositiveButton("OK", null)
                        .setMessage("GPS will not work as you have denied access").show()
                }
            }
        }
    }

    // Suppress lint check (sanity check) about missing permission
    // We check permissions above, so don't need to do it here
    @SuppressLint("MissingPermission")
    fun startGps() {
        val mgr = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0f, this)
    }

    override fun onLocationChanged(newLoc: Location) {
        //Toast.makeText (this, "Location=${newLoc.latitude},${newLoc.longitude}", Toast.LENGTH_LONG).show()
        currentLocationLat = newLoc.latitude
        currentLocationLon = newLoc.longitude
        fetchDataFromAPI(currentLocationLat, currentLocationLon)
    }


    private fun fetchDataFromAPI(currentLocationLat: Double, currentLocationLon: Double) {
        val north = currentLocationLat + 0.01
        val south = currentLocationLat - 0.01
        val west = currentLocationLon - 0.01
        val east = currentLocationLon + 0.01
        val apiUrl = "https://hikar.org/webapp/map?bbox=${west},${south},${east},${north}&layers=poi&outProj=4326&format=json"
        Toast.makeText (this, "Location=${currentLocationLat},${currentLocationLon}", Toast.LENGTH_LONG).show()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = URL(apiUrl).readText()
                val items = parseJson(response)
                withContext(Dispatchers.Main) {
                    adapter.setItems(items)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun parseJson(json: String): List<Item> {
        val items = mutableListOf<Item>()
        val jsonArray = JSONArray(json)

        for (i in 0 until jsonArray.length()) {
            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
            val osmId = jsonObject.getLong("osm_id")
            val lon = jsonObject.getDouble("lon")
            val lat = jsonObject.getDouble("lat")
            val name = jsonObject.getString("name")
            val featureType = jsonObject.getString("featureType")

            val item = Item(osmId, lon, lat, name, featureType)
            items.add(item)
        }

        return items
    }

}
