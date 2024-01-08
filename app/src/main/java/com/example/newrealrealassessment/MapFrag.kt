package com.example.newrealrealassessment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class MapFrag : Fragment() {

    private lateinit var mapView: MapView
    private lateinit var mLocationOverlay: MyLocationNewOverlay
    private lateinit var roomViewModel: RoomViewModel
    private lateinit var locationViewModel: LocationViewModel
    private var currentLongitude: Double = 0.0
    private var currentLatitude: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val context = requireContext()
        Configuration.getInstance().load(
            context,
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        )

        val rootView = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = rootView.findViewById(R.id.mapView)

        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        mapView.setMultiTouchControls(true)
        val mapController = mapView.controller
        mapController.setZoom(17.0)
        val startPoint = GeoPoint(48.8583, 2.2944)
        mapController.setCenter(startPoint)
        roomViewModel = ViewModelProvider(requireActivity()).get(RoomViewModel::class.java)
        locationViewModel = ViewModelProvider(requireActivity()).get(LocationViewModel::class.java)

        mLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), mapView)
        mLocationOverlay.enableMyLocation()
        mapView.overlays.add(mLocationOverlay)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationViewModel.currentLatitude.observe(viewLifecycleOwner, Observer { newLatitude ->
            //Log.e("CurrentLat", newLatitude.toString())
            currentLatitude = newLatitude
            updateMapView()
        })

        locationViewModel.currentLongitude.observe(viewLifecycleOwner, Observer { newLongitude ->
            //Log.e("CurrentLong", newLongitude.toString())
            currentLongitude = newLongitude
            updateMapView()
        })

        roomViewModel.pointsBySelectedFeature.observe(viewLifecycleOwner) { points ->
            //Log.e("MapFrag", "Markers grabbed by feature: $points")
            clearAllMarkers() // Clear existing markers before adding new ones
            points?.let { list ->
                list.forEach { element ->
                    val latlon = GeoPoint(element.lat, element.lon)
                    addMarker(
                        latlon,
                        element.name,
                        element.featureType
                    )
                }
            }
            mapView.invalidate()
        }
    }

    private fun updateMapView() {
        clearAllMarkers()
        val newGeoPoint = GeoPoint(currentLatitude, currentLongitude)
        mapView.controller.setCenter(newGeoPoint)
        mLocationOverlay.enableMyLocation()
        mapView.overlays.add(mLocationOverlay)
        mapView.invalidate()
    }

    private fun addMarker(geoPoint: GeoPoint, name: String, featureType: String) {
        val marker = Marker(mapView)
        marker.position = geoPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        marker.title = name
        marker.snippet = featureType
        mapView.overlays.add(marker)
        mapView.invalidate()
    }

    fun clearAllMarkers() {
        mapView.overlays
            .filterIsInstance<Marker>()
            .forEach { mapView.overlays.remove(it) }
        mapView.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDetach()
    }
}
