package edu.oregonstate.cs492.final_project.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

const val DATABASE_NAME = "score-db"

@Database(entities = [Score::class], version = 1)
abstract class ScoreDatabase : RoomDatabase() {
    abstract fun scoreDao(): ScoreDao

    companion object {
        @Volatile
        private var instance: ScoreDatabase? = null

        fun getInstance(context: Context): ScoreDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also {
                    instance = it
                }
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                ScoreDatabase::class.java,
                DATABASE_NAME
            ).build()
    }
}
