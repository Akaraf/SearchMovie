package com.raaf.android.searchmovie.ui.profile

import android.content.res.Resources
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.drawToBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.raaf.android.searchmovie.R

private const val TAG = "Profile_Fragment"
private const val EXTRA_USER_EMAIL = "USER_EMAIL"
private const val EXTRA_USER_ID = "USER_ID"
private const val EXTRA_USER_CONDITION = "USER_CONDITION"
private const val MAP_EMAIL = "email"
private const val MAP_U_NAME = "userName"

class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel

    var isUserLogIn = false
    private lateinit var login: TextView
    private lateinit var email: TextView
    private lateinit var avatar: ImageView
    private lateinit var userLayout: ConstraintLayout

    lateinit var myFilmsTV: TextView
    private lateinit var myStarsTV: TextView

    private lateinit var settingsTV: TextView
    private lateinit var supportTV: TextView
    private lateinit var historyTV: TextView

    private lateinit var logButton: Button

    private var userEmail = ""
    private var userName = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        userLayout = view.findViewById(R.id.user_layout)
        login = view.findViewById(R.id.user_id)
        email = view.findViewById(R.id.email)
        avatar = view.findViewById(R.id.profile_image)
        myFilmsTV = view.findViewById(R.id.my_films)
        myStarsTV = view.findViewById(R.id.my_stars)
        settingsTV = view.findViewById(R.id.settings)
        supportTV = view.findViewById(R.id.support_service)
        historyTV = view.findViewById(R.id.history)
        logButton = view.findViewById(R.id.log_button)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileViewModel.firebaseUser.observe(
                viewLifecycleOwner,
                Observer { user ->
                    if(user != null) {
                        Log.e(TAG, "User condition is change")
                        isUserLogIn = true
                        userEmail = user.getValue(MAP_EMAIL)
                        userName = user.getValue(MAP_U_NAME)
                        updateUI(isUserLogIn)
                    } else {
                        Log.e(TAG, "User condition is change and its NULL")
                    }
                }
        )

        myFilmsTV.setOnClickListener {
//            it.findNavController().navigate()
        }

        logButton.setOnClickListener {
            if (isUserLogIn) {
                isUserLogIn = false
                updateUI(isUserLogIn)
                profileViewModel.logOut()
            }
            else {
                isUserLogIn = true //its will be not consistent value if user is not logged in authFragment
                NavHostFragment.findNavController(this@ProfileFragment).navigate(R.id.action_navigation_profile_to_authFragment)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        //Check if user is signed in
        profileViewModel.getUser()
    }

    fun setUserAvatar() {
        //this method will be use if ill add users avatar in app
    }

    private fun updateUI(boolean: Boolean) {
        if (!boolean) {
            Log.e(TAG, "update UI is running. Run with false variant")
            logButton.setText(R.string.sign_in)
            email.text = ""
            login.text = ""
            userLayout.visibility = GONE
        } else {
            Log.e(TAG, "update UI is running. Run with true variant")
            logButton.setText(R.string.logout)
            email.text = userName
            login.text = userEmail
            userLayout.visibility = VISIBLE
        }
    }
}