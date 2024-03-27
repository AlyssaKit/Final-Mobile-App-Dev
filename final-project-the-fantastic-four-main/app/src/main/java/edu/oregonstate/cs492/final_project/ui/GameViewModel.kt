package edu.oregonstate.cs492.final_project.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.oregonstate.cs492.final_project.data.Movie
import edu.oregonstate.cs492.final_project.data.MovieRepository
import edu.oregonstate.cs492.final_project.data.MovieService
import edu.oregonstate.cs492.final_project.data.Score
import edu.oregonstate.cs492.final_project.data.ScoreDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val movieService = MovieService.create()
    private val movieRepository = MovieRepository(movieService)

    private val _movies = MutableLiveData<List<Movie>>()
    val movies: LiveData<List<Movie>> = _movies

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun fetchMovies(apiKey: String) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            val result = movieRepository.fetchMovies(apiKey)
            result.onSuccess { movies ->
                _movies.value = movies.shuffled()
            }.onFailure { e ->
                _error.value = e.message
            }
            _isLoading.value = false
        }
    }

    fun insertScore(score: Score) {
        viewModelScope.launch(Dispatchers.IO) {
            val dao = ScoreDatabase.getInstance(getApplication()).scoreDao()
            dao.insert(score)
        }
    }
}
