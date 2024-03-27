package edu.oregonstate.cs492.final_project.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import edu.oregonstate.cs492.final_project.data.Score
import edu.oregonstate.cs492.final_project.data.ScoreDatabase

class ScoreHistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = ScoreDatabase.getInstance(application).scoreDao()
    val recentScores = dao.getRecentScores()
    val getHighScore = dao.getHighScore()

}
