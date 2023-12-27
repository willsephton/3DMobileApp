package com.example.newrealrealassessment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

class RecycleFrag : Fragment(R.layout.recycle_layout), LocationListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter
    private var currentLocationLat: Double = 0.0
    private var currentLocationLon: Double = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ItemAdapter(mutableListOf()) // Initialize with an empty list
        recyclerView.adapter = adapter

        requestPermissions()
    }

    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
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
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startGps()
                } else {
                    AlertDialog.Builder(requireContext())
                        .setPositiveButton("OK", null)
                        .setMessage("GPS will not work as you have denied access")
                        .show()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startGps() {
        val mgr = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0f, this)
    }

    override fun onLocationChanged(newLoc: Location) {
        currentLocationLat = newLoc.latitude
        currentLocationLon = newLoc.longitude
        fetchDataFromAPI(currentLocationLat, currentLocationLon)
    }

    private fun fetchDataFromAPI(currentLocationLat: Double, currentLocationLon: Double) {
        val north = currentLocationLat + 0.01
        val south = currentLocationLat - 0.01
        val west = currentLocationLon - 0.01
        val east = currentLocationLon + 0.01
        val apiUrl =
            "https://hikar.org/webapp/map?bbox=${west},${south},${east},${north}&layers=poi&outProj=4326&format=json"

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

    override fun onProviderEnabled(provider: String) {}

    override fun onProviderDisabled(provider: String) {}

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
}
