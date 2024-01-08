package com.example.newrealrealassessment

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pointsofinterest")

data class Item(
    @PrimaryKey val osm_id: Long,
    @ColumnInfo val lon: Double,
    @ColumnInfo val lat: Double,
    @ColumnInfo val name: String,
    @ColumnInfo val featureType: String,
    @ColumnInfo var distance: Double = 0.0 // Field to hold the distance

)
