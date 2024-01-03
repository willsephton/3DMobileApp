package com.example.newrealrealassessment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView



class ItemAdapter(private val itemList: MutableList<Item>) :
    RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val osmIdTextView: TextView = itemView.findViewById(R.id.textViewOsmId)
        val lonTextView: TextView = itemView.findViewById(R.id.textViewLon)
        val latTextView: TextView = itemView.findViewById(R.id.textViewLat)
        val nameTextView: TextView = itemView.findViewById(R.id.textViewName)
        val featureTypeTextView: TextView = itemView.findViewById(R.id.textViewFeatureType)
        val distanceTextView: TextView = itemView.findViewById(R.id.textViewDistance) // New TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.osmIdTextView.text = "OSM ID: ${currentItem.osm_id}"
        holder.lonTextView.text = "Longitude: ${currentItem.lon}"
        holder.latTextView.text = "Latitude: ${currentItem.lat}"
        holder.nameTextView.text = "Name: ${currentItem.name}"
        holder.featureTypeTextView.text = "Feature Type: ${currentItem.featureType}"

        val distanceInKm = currentItem.distance / 1000 // Convert meters to kilometers
        val formattedDistance = String.format("%.2f", distanceInKm) // Round to 2 decimal places

        holder.distanceTextView.text = "Distance: ${formattedDistance}km"
    }
    override fun getItemCount(): Int {
        return itemList.size
    }

    fun setItems(items: MutableList<Item>) {
        itemList.clear()
        itemList.addAll(items)
        notifyDataSetChanged()
    }
}
