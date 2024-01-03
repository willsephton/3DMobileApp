package com.example.newrealrealassessment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
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
    private lateinit var items: List<Item>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ItemAdapter(mutableListOf()) // Initialize with an empty list
        recyclerView.adapter = adapter
        roomViewModel = ViewModelProvider(requireActivity()).get(RoomViewModel::class.java)

        roomViewModel.pointsBySelectedFeature.observe(viewLifecycleOwner) { points ->
            Log.e("RecyclerFrag", "Points grabbed by feature: $points")
            points?.let {
                val mutablePoints = it.toMutableList()
                adapter.setItems(mutablePoints)
            }
        }

       //roomViewModel.setFeatureChoice("pub") // Set the feature you want to observe
    }
}























