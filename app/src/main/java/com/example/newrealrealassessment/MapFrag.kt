package com.example.newrealrealassessment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider

import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.net.URL


class MapFrag : Fragment(), LocationListener {

    private lateinit var mapView: MapView
    private var currentLocationLat: Double = 0.0
    private var currentLocationLon: Double = 0.0
    private lateinit var mLocationOverlay: MyLocationNewOverlay


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val context = requireContext()
        Configuration.getInstance().load(context, androidx.preference.PreferenceManager.getDefaultSharedPreferences(context))

        val rootView = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = rootView.findViewById(R.id.mapView)

        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        mapView.setMultiTouchControls(true)
        val mapController = mapView.controller
        mapController.setZoom(15.0)
        val startPoint = GeoPoint(48.8583, 2.2944) // Set a default location (Paris coordinates)
        mapController.setCenter(startPoint)

        requestPermissions()

        mLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), mapView)
        mLocationOverlay.enableMyLocation()
        mapView.overlays.add(mLocationOverlay)

        // Add a marker at a specific location
        /*
        val startPoint1 = GeoPoint(50.915860, -1.405040) // New York coordinates
        addMarker2(startPoint1, "test", "test")
*/
        loadPoints()

        return rootView
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
        val newGeoPoint = GeoPoint(newLoc.latitude, newLoc.longitude)
        mapView.controller.setCenter(newGeoPoint)
        mapView.controller.setZoom(15.0)
        mLocationOverlay.enableMyLocation()
        mapView.overlays.add(mLocationOverlay)
        mapView.invalidate() // Refresh the map view
    }

    fun loadPoints() {
        mapView.overlayManager.clear()

        val db = PointsOfInterestDatabase.getDatabase(requireContext().applicationContext)

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                //var allPoints = db.PointsOfInterestDAO().getPointsByFeature("pub")
                var allPoints = db.PointsOfInterestDAO().getAllPoints()
                allPoints.forEachIndexed { index, element ->
                    //println("$element")
                    //var lat = element.pointLat.toDouble()
                    //var lon = element.pointLon.toDouble()
                    //val newMarker = OverlayItem(element.pointName,element.pointDescription,GeoPoint(element.pointLat, element.pointLat)
                    Log.e("TAG", element.name)
                    val latlon = GeoPoint(element.lat, element.lon)
                    addMarker2(
                        latlon,
                        element.name,
                        element.featureType
                    )
                    //addMarker(element.name, element.featureType, element.lon, element.lat, mapView)
                }
            }
        }
    }

    fun addMarker(name: String, featureType: String, lon : Double, lat : Double, map1 : MapView){

        val markerGestureListener = object:ItemizedIconOverlay.OnItemGestureListener<OverlayItem>
        {
            override fun onItemLongPress(i: Int, item:OverlayItem ) : Boolean
            {
                Toast.makeText(requireContext(), item.snippet, Toast.LENGTH_SHORT).show()
                return true
            }

            override fun onItemSingleTapUp(i: Int, item:OverlayItem): Boolean
            {
                Toast.makeText(requireContext(), item.title, Toast.LENGTH_SHORT).show()
                return true
            }
        }
        val items = ItemizedIconOverlay(requireContext(), arrayListOf<OverlayItem>(), markerGestureListener)
        val newMarker = OverlayItem(name, featureType, GeoPoint(lat, lon))
        items.addItem(newMarker)
        map1.overlays.add(items)

    }

    private fun addMarker2(geoPoint: GeoPoint, name: String, featureType: String) {
        val marker = Marker(mapView)
        marker.position = geoPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        marker.title = name // Optional: Set a title
        marker.snippet = featureType // Optional: Set a snippet
        Log.e("MyTag", name)

        mapView.overlays.add(marker)
        mapView.invalidate()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDetach()
    }
}