package edu.oregonstate.cs492.final_project.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MovieResponse(
    val results: List<Movie>
)

@JsonClass(generateAdapter = true)
data class Movie(
    val title: String,
    @Json(name = "poster_path") val posterPath: String,
    val overview: String,
    @Json(name = "release_date") val releaseDate: String,
    @Json(name = "vote_average") val voteAverage: Double
)
