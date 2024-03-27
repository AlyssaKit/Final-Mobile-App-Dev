package edu.oregonstate.cs492.final_project.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MovieRepository(private val service: MovieService) {
    suspend fun fetchMovies(apiKey: String): Result<List<Movie>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = service.fetchMovies(apiKey)
                if (response.isSuccessful) {
                    val movies = response.body()?.results ?: emptyList()
                    Result.success(movies)
                } else {
                    Result.failure(Exception("Failed to fetch movies"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
