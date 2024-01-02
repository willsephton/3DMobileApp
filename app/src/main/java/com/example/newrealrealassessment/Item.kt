package com.example.newrealrealassessment

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pointsofinterest")

data class Item(
    @PrimaryKey(autoGenerate = true) val id : Long,
    @ColumnInfo val osm_id: Long,
    @ColumnInfo val lon: Double,
    @ColumnInfo val lat: Double,
    @ColumnInfo val name: String,
    @ColumnInfo val featureType: String
)
