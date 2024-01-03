package com.example.newrealrealassessment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecycleFrag : Fragment(R.layout.recycle_layout) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter
    private lateinit var roomViewModel: RoomViewModel
    private lateinit var locationViewModel: LocationViewModel
    private var currentLongitude: Double = 0.0
    private var currentLatitude: Double = 0.0
    private lateinit var items: List<Item>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ItemAdapter(mutableListOf(), requireContext()) // Initialize with an empty list
        recyclerView.adapter = adapter
        roomViewModel = ViewModelProvider(requireActivity()).get(RoomViewModel::class.java)
        locationViewModel = ViewModelProvider(requireActivity()).get(LocationViewModel::class.java)

        locationViewModel.currentLatitude.observe(viewLifecycleOwner, Observer { newLatitude ->
            Log.e("CurrentLat", newLatitude.toString())
            currentLatitude = newLatitude
        })

        locationViewModel.currentLongitude.observe(viewLifecycleOwner, Observer { newLongitude ->
            Log.e("CurrentLong", newLongitude.toString())
            currentLongitude = newLongitude
        })

        roomViewModel.pointsBySelectedFeature.observe(viewLifecycleOwner) { points ->
            Log.e("RecyclerFrag", "Points grabbed by feature: $points")
            points?.let {
                val mutablePoints = it.toMutableList()
                for (item in mutablePoints) {
                    val distance = Algorithms.haversineDist(item.lon, item.lat, currentLongitude, currentLatitude)
                    item.distance = distance // Update the distance for each item
                    Log.e("RecyclerFrag", "Distance: $distance")
                }
                adapter.setItems(mutablePoints)
            }
        }

       //roomViewModel.setFeatureChoice("pub") // Set the feature you want to observe
    }
}























