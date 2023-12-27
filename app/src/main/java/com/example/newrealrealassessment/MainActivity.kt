package com.example.newrealrealassessment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ItemAdapter(mutableListOf()) // Initialize with an empty list
        recyclerView.adapter = adapter

        fetchDataFromAPI()
    }

    private fun fetchDataFromAPI() {
        val apiUrl = "https://hikar.org/webapp/map?bbox=-1.5,50.9,-1.4,51&layers=poi&outProj=4326&format=json"

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
