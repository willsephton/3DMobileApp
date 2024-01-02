package com.example.newrealrealassessment

import android.annotation.SuppressLint
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL


class MainActivity : AppCompatActivity(), LocationListener {

    private lateinit var locationViewModel: LocationViewModel
    private lateinit var locationManager: LocationManager

    private var north: Double = 0.0
    private var south: Double = 0.0
    private var west: Double = 0.0
    private var east: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize ViewModel
        locationViewModel = ViewModelProvider(this).get(LocationViewModel::class.java)

        // Initialize LocationManager
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        requestPermissions()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.mapFragmentContainer, MapFrag())
                .commit()

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, RecycleFrag())
                .commit()
        }
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

    override fun onCreateOptionsMenu(menu: Menu) : Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem) : Boolean {
        when(item.itemId) {
            R.id.menu1 -> {
                /*
                val intent = Intent(this,MapChooseActivity::class.java)
                mapChooseLauncher.launch(intent)
                return true */

                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        val db = PointsOfInterestDatabase.getDatabase(application)
                        db.PointsOfInterestDAO().deleteAllPoints()
                    }
                    Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                }
            }
            R.id.menu2 -> {
                /*
                val intent = Intent(this,MyPrefsActivity::class.java)
                startActivity(intent)
                return true */
                Toast.makeText(getApplicationContext(), "menu2", Toast.LENGTH_SHORT).show();
            }


        }
        return false
    }


    private fun fetchDataFromAPI() {
        locationViewModel = ViewModelProvider(this).get(LocationViewModel::class.java)
        // Observe changes in latitude and longitude
        locationViewModel.currentLatitude.observe(this) { latitude ->
            Log.e("mainactivity", latitude.toString())

            latitude?.let {
                // Here, latitude contains the updated value
                north = latitude + 0.01
                south = latitude - 0.01
            }
        }

        locationViewModel.currentLongitude.observe(this) { longitude ->
            longitude?.let {
                // Here, longitude contains the updated value
                west = longitude - 0.01
                east = longitude + 0.01
            }
        }

        val apiUrl =
            "https://hikar.org/webapp/map?bbox=${west},${south},${east},${north}&layers=poi&outProj=4326&format=json"

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = URL(apiUrl).readText()
                parseJson(response)


                withContext(Dispatchers.Main) {
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun parseJson(json: String) {
        val items = mutableListOf<Item>()
        val jsonArray = JSONArray(json)
        val db = PointsOfInterestDatabase.getDatabase(this.applicationContext)
        db.PointsOfInterestDAO().deleteAllPoints()

        for (i in 0 until jsonArray.length()) {
            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
            val osmId = jsonObject.getLong("osm_id")
            val lon = jsonObject.getDouble("lon")
            val lat = jsonObject.getDouble("lat")
            val name = jsonObject.getString("name")
            val featureType = jsonObject.getString("featureType")

            val db = PointsOfInterestDatabase.getDatabase(this.applicationContext)

            val item = Item(0L, osmId, lon, lat, name, featureType)
            //Log.d("parseJSON", name)

            db.PointsOfInterestDAO().insert(item)


        }

    }

    // Implement LocationListener methods
    override fun onLocationChanged(location: Location) {
        // Update the location data in the ViewModel
        locationViewModel.updateLocation(location)

        fetchDataFromAPI()
    }
}
