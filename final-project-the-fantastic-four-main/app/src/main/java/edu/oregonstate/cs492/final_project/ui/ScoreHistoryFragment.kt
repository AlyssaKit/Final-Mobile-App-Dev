package edu.oregonstate.cs492.final_project.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import edu.oregonstate.cs492.final_project.R
class ScoreHistoryFragment : Fragment() {
    private lateinit var viewModel: ScoreHistoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_score_history, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(ScoreHistoryViewModel::class.java)

        val high = view.findViewById<TextView>(R.id.high_score)
        val scoreHistoryContainer = view.findViewById<LinearLayout>(R.id.score_history_container)

        viewModel.getHighScore.observe(viewLifecycleOwner, { highScore ->
            Log.d("HighScore", "High score: $highScore")
            high.text = highScore?.toString() ?: "0"
        })

        viewModel.recentScores.observe(viewLifecycleOwner, { scores ->
            scoreHistoryContainer.removeAllViews() // Clear previous scores
            scores.forEach { score ->

                val scoreView = TextView(context).apply {
                    text = "${score.totalCorrectGuesses}/${score.totalMoviesViewed}"
                    textSize = 35f
                    setTextColor(Color.WHITE)
                    setPadding(16, 8, 16, 8) // Add padding for better appearance
                }
                scoreHistoryContainer.addView(scoreView)
            }
        })
    }
}
