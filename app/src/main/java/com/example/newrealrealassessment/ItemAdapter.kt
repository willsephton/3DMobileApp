package com.example.newrealrealassessment

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView



class ItemAdapter(private val itemList: MutableList<Item>, private val context: Context) :
    RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

        // Notification setup
    private val notifiedItems = mutableSetOf<Long>()


    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val osmIdTextView: TextView = itemView.findViewById(R.id.textViewOsmId)
        val lonTextView: TextView = itemView.findViewById(R.id.textViewLon)
        val latTextView: TextView = itemView.findViewById(R.id.textViewLat)
        val nameTextView: TextView = itemView.findViewById(R.id.textViewName)
        val featureTypeTextView: TextView = itemView.findViewById(R.id.textViewFeatureType)
        val distanceTextView: TextView = itemView.findViewById(R.id.textViewDistance) // New TextView
    }

    init {
        notifiedItems.clear()
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

        var distance = currentItem.distance
        val distanceInKm = distance / 1000 // Convert meters to kilometers
        val formattedDistance = String.format("%.2f", distanceInKm) // Round to 2 decimal places

        holder.distanceTextView.text = "Distance: ${formattedDistance}km"

        if (currentItem.osm_id !in notifiedItems) {
            distance = currentItem.distance
            if (distance < 100) {
                sendNotification(currentItem)
                notifiedItems.add(currentItem.osm_id)
            }
        }

    }
    override fun getItemCount(): Int {
        return itemList.size
    }

    fun setItems(items: MutableList<Item>) {
        itemList.clear()
        itemList.addAll(items)
        notifyDataSetChanged()
    }

    fun sendNotification(item: Item) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelID = "DISTANCE_CHANNEL" // Set your channel ID here
            val channelName = "Distance Channel"
            val channelDescription = "Notification channel for distance alerts"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(channelID, channelName, importance).apply {
                description = channelDescription
            }

            val nMgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            nMgr.createNotificationChannel(channel)

            val notification = Notification.Builder(context, channelID)
                .setContentTitle("Name: ${item.name}")
                .setContentText("Feature Type: ${item.featureType}, Longitude: ${item.lon}, Latitude: ${item.lat}, OSM ID: ${item.osm_id}")
                .setSmallIcon(R.drawable.message)
                .build()

            nMgr.notify(item.osm_id.toInt(), notification)
        }
}}
