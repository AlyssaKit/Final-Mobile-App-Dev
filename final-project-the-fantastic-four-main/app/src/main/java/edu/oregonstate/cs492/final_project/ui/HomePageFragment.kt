package edu.oregonstate.cs492.final_project.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.preference.PreferenceManager
import edu.oregonstate.cs492.final_project.R
class HomePageFragment : Fragment() {
    private lateinit var playGameButton: Button
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var prefs: SharedPreferences
    private lateinit var user_name: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        playGameButton = view.findViewById(R.id.play_game_button)
        navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        var navController = navHostFragment.navController
        val userName = prefs.getString(getString(R.string.user_name), "Valued User :)")
        user_name = view.findViewById(R.id.user_name)
        user_name.text = userName
        bind() // Call bind to set the initial user name
        playGameButton.setOnClickListener {
            navController.navigate(R.id.gameFragment)
        }
    }

    private fun bind() {
        val userName = prefs.getString(getString(R.string.user_name), "Valued User :)")
        user_name.text = userName
    }

    override fun onResume() {
        super.onResume()
        // Update the user name when the fragment is resumed
        bind()
    }
}
