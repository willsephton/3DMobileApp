package com.example.newrealrealassessment

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Item::class], version = 1, exportSchema = false)
public abstract class PointsOfInterestDatabase : RoomDatabase() {
    abstract fun PointsOfInterestDAO(): PointsOfInterestDAO

    companion object {
        private var instance: PointsOfInterestDatabase? = null

        fun getDatabase(ctx: Context): PointsOfInterestDatabase {
            var tmpInstance = instance
            if (tmpInstance == null) {
                tmpInstance = Room.databaseBuilder(
                    ctx.applicationContext,
                    PointsOfInterestDatabase::class.java,
                    "PointsOfInterest"
                ).build()
                instance = tmpInstance
            }
            return tmpInstance
        }
    }
}