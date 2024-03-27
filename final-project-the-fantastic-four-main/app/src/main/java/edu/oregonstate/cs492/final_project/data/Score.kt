package edu.oregonstate.cs492.final_project.data
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "score_table")
data class Score(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val totalMoviesViewed: Int,
    val totalCorrectGuesses: Int
)
