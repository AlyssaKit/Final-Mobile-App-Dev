package edu.oregonstate.cs492.final_project.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response

interface MovieService {
    @GET("discover/movie")
    suspend fun fetchMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US",
        @Query("sort_by") sortBy: String = "primary_release_date.desc",
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("include_video") includeVideo: Boolean = false,
        @Query("page") page: Int = (1..500).random(),
        @Query("vote_count.gte") voteCount: Int = 100,
        @Query("vote_average.gte") voteLowLimit: Double = 1.0,
        @Query("vote_average.lte") voteHighLimit: Double = 9.0
    ): Response<MovieResponse>

    companion object {
        private const val BASE_URL = "https://api.themoviedb.org/3/"

        fun create(): MovieService {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(MovieService::class.java)
        }
    }
}
