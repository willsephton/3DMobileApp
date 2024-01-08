package com.example.newrealrealassessment

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PointsOfInterestDAO {

    @Query("SELECT * FROM pointsofinterest WHERE osm_id=:osm_id")
    fun getPointById(osm_id: Long): Item?

    @Query("SELECT * FROM pointsofinterest WHERE featureType=:featureType")
    fun getPointsByFeature(featureType: String): LiveData<List<Item>>

    @Query("SELECT * FROM pointsofinterest")
    fun getAllPoints(): LiveData<List<Item>>

    @Query("DELETE FROM pointsofinterest")
    fun deleteAllPoints()

    @Insert
    fun insert(item: Item) : Long

    @Update
    fun update(item: Item) : Int

    @Delete
    fun delete(item: Item) : Int
}