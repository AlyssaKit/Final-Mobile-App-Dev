package edu.oregonstate.cs492.final_project.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.android.material.slider.Slider
import com.google.android.material.snackbar.Snackbar
import edu.oregonstate.cs492.final_project.R
import edu.oregonstate.cs492.final_project.data.Movie
import edu.oregonstate.cs492.final_project.data.Score
import edu.oregonstate.cs492.final_project.data.ScoreDatabase
import kotlinx.coroutines.Dispatchers
import java.util.Timer
import androidx.lifecycle.viewModelScope

import java.util.TimerTask
import kotlin.math.abs

class GameFragment : Fragment() {
    private val viewModel: GameViewModel by viewModels()

    private lateinit var movieDisplay: LinearLayout
    private lateinit var mediaPlayer: MediaPlayer  

    private lateinit var movieTitleTextView: TextView
    private lateinit var movieDescriptionTextView: TextView
    private lateinit var movieImageView: ImageView
    private lateinit var movieYearTextView: TextView
    private lateinit var submitButton: Button
    private lateinit var trailerButton: Button
    private lateinit var slider: Slider
    private lateinit var currentmoviepage: List<Movie>
    private lateinit var scoreText: TextView
    private val timer = Timer();

    private var moviePos: Int = 0;
    private var currentScore: Int = 0;
    private var totalShown: Int = 0;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("GameFragment", "@@@@@@onViewCreated called")
        viewModel.fetchMovies("14eb716d284c56eef5a688801b040259")
        movieTitleTextView = view.findViewById(R.id.movie_title)
        movieDescriptionTextView = view.findViewById(R.id.movie_description)
        movieImageView = view.findViewById(R.id.movie_image)
        movieYearTextView = view.findViewById(R.id.movie_genre_release)
        movieDisplay = view.findViewById(R.id.main_game_fragment)
        slider = view.findViewById(R.id.slider_review_guess)
        submitButton = view.findViewById(R.id.button_submit_answer)
        trailerButton = view.findViewById(R.id.button_launch_yt)
        scoreText = view.findViewById(R.id.tv_score_field)

        viewModel.movies.observe(viewLifecycleOwner, Observer { movies ->
            Log.e("GameFragment", "@@@@@Movies observed: ${movies.size}")
            if (movies.isNotEmpty()) {
                currentmoviepage = movies;
                moviePos = 0;
                updateDisplay();
            }
            else {
                Log.e("GameFragment", "@@@@@@Movies list is empty")
            }
        })

        viewModel.error.observe(viewLifecycleOwner, Observer { error ->
            if (error != null) {
                Log.e("GameFragment", "@@@@@@@@@@Error fetching movies: $error")
                movieDisplay.visibility = View.INVISIBLE
            }
        })
        
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { loading ->
            if (!loading) {
                movieDisplay.visibility = View.VISIBLE
            } else {
                movieDisplay.visibility = View.INVISIBLE
            }
        })

        submitButton.setOnClickListener {
            submitButton.isEnabled = false;
            if (moviePos < 5) {
                totalShown += 1
                if (abs(currentmoviepage[moviePos].voteAverage - slider.value) < 1) {
                    playAudio(R.raw.correct)
                    currentScore += 1
                    scoreText.text = String.format("Correct! Score %d / %d", currentScore, totalShown)
                } else {
                    playAudio(R.raw.incorrect)
                    scoreText.text = String.format("Incorrect! Score %d / %d", currentScore, totalShown)
                }
                moviePos += 1

                if (moviePos < 5) {
                    Handler().postDelayed({
                        updateDisplay()
                    }, 1000)
                } else {
                    submitButton.text="Replay"
                    scoreText.text = String.format("Game over! Final score: %d / %d", currentScore, totalShown)
                    val score = Score(totalMoviesViewed = totalShown, totalCorrectGuesses = currentScore)
                    viewModel.insertScore(score)
                    totalShown = 0
                    currentScore = 0
                    submitButton.isEnabled = true;
                }
            } else {
                submitButton.text = "Submit Answer"
                scoreText.text = "What is this movie's rating?"
                viewModel.fetchMovies("14eb716d284c56eef5a688801b040259")
                moviePos = 0;
            }
        }

        trailerButton.setOnClickListener {
            var movieTitle = movieTitleTextView.text.toString()
            viewTrailer(movieTitle)
        }

        // create media player, setting listeners to start when prepared and reset when completed
        mediaPlayer = MediaPlayer().apply {
            setOnPreparedListener { start() }
            setOnCompletionListener { reset() }
        }
        mediaPlayer.setVolume(1.0f, 1.0f)
    }

    // release media player resources when view is destroyed and player is no longer needed
    override fun onDestroyView() {
        Log.d("GameFragment", "@@@@@@onDestroyView called")
        mediaPlayer?.release()
        super.onDestroyView()
    }

    // reset media player state to Initialized, set data source as desired audio, and prepare player
    /*
    Date: 3/21/2024
    Adapted from: Deep Dive: MediaPlayer Best Practices
    Source URL: https://medium.com/androiddevelopers/deep-dive-mediaplayer-best-practices-feb4d15a66f5
     */
    private fun playAudio(resourceId: Int) {
        val resourceFileDescriptor = context?.resources?.openRawResourceFd(resourceId) ?: return
        this.mediaPlayer.run {
            reset()
            setDataSource(resourceFileDescriptor.fileDescriptor,
                resourceFileDescriptor.startOffset, resourceFileDescriptor.declaredLength)
            prepareAsync()
        }
    }

    private fun viewTrailer(movieTitle: String) {
        // generate uri from movie title
        var titleQuery = movieTitle.replace(' ', '+')
        var uriString = "https://m.youtube.com/results?search_query=$titleQuery+trailer"
        var url = Uri.parse(uriString)

        var intent = Intent(Intent.ACTION_VIEW, url)

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Snackbar.make(
                requireView(),
                "Select an app to view the trailer",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    fun updateDisplay() {
        movieDisplay.visibility = View.INVISIBLE;

        var movie = currentmoviepage[moviePos];

        movieTitleTextView.text = movie.title
        movieDescriptionTextView.text = movie.overview
        movieYearTextView.text = movie.releaseDate.split("-")[0]
        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w500${movie.posterPath}")
            .into(movieImageView)
        slider.value = 5.0F;
        scoreText.text = "What is this movie's rating?"

        movieDisplay.visibility = View.VISIBLE
        submitButton.isEnabled = true;
    }
}
