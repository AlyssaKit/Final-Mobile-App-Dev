package edu.oregonstate.cs492.final_project.data
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ScoreDao {
    @Insert
    suspend fun insert(score: Score)

    @Query("SELECT * FROM score_table ORDER BY id DESC LIMIT 5")
    fun getRecentScores(): LiveData<List<Score>>

    @Query("SELECT totalCorrectGuesses FROM score_table ORDER BY totalCorrectGuesses DESC LIMIT 1")
    fun getHighScore():LiveData<Int>

}
