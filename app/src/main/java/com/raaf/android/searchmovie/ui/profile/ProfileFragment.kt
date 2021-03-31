package com.raaf.android.searchmovie.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.raaf.android.searchmovie.R

class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel

    private lateinit var login: TextView
    private lateinit var email: TextView
    private lateinit var avatar: ImageView

    private lateinit var myFilmsTV: TextView
    private lateinit var myStarsTV: TextView

    private lateinit var settingsTV: TextView
    private lateinit var supportTV: TextView
    private lateinit var historyTV: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        login = view.findViewById(R.id.login)
        email = view.findViewById(R.id.email)
        avatar = view.findViewById(R.id.profile_image)
        myFilmsTV = view.findViewById(R.id.my_films)
        myStarsTV = view.findViewById(R.id.my_stars)
        settingsTV = view.findViewById(R.id.settings)
        supportTV = view.findViewById(R.id.support_service)
        historyTV = view.findViewById(R.id.history)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myFilmsTV.setOnClickListener {
//            it.findNavController().navigate()
        }
    }
}