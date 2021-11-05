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
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.bundleOf
import androidx.core.view.drawToBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseUser
import com.raaf.android.searchmovie.R
import com.raaf.android.searchmovie.ui.hideToolbar

private const val TAG = "ProfileFragment"
private const val EXTRA_USER_EMAIL = "USER_EMAIL"
private const val EXTRA_USER_ID = "USER_ID"
private const val EXTRA_USER_CONDITION = "USER_CONDITION"
private const val MAP_IS_USER_LOGIN = "is"
private const val MAP_EMAIL = "email"
private const val MAP_U_NAME = "userName"
private const val AUTH_FLAG = "Auth"
private const val SUPPORT_FLAG = "Support"
private const val EXTRA_FIRST_TYPE = "firstType"
private const val EXTRA_SECOND_TYPE = "secondType"
private const val F_T_WATCHED = "Watched films"
private const val F_T_MY_FILMS_ALL = "my films"
private const val F_T_MY_STARS = "my stars"


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
    private lateinit var toolbar: Toolbar

    private var userEmail = ""
    private var userName = ""
    private var authEmail = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        toolbar = requireActivity().findViewById(R.id.toolbar)
        hideToolbar(toolbar)
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

        val nav = NavHostFragment.findNavController(this@ProfileFragment)

        nav.currentBackStackEntry?.savedStateHandle?.getLiveData<String>(AUTH_FLAG)
                ?.observe(viewLifecycleOwner) {
                    Log.e(TAG, "!!!!!!!!!!!!!")
                    Log.e(TAG, it)
                    if (it.contains("@")) {
                        authEmail = it
                        Log.e(TAG, "@")
                        userEmail = it
                        userName = it.split("@")[0]
                        isUserLogIn = true
                        if (userName.isNotBlank()) updateUI(true, it)
                    }
                    makeToastFromAuth("r")
                }

        nav.currentBackStackEntry?.savedStateHandle?.getLiveData<String>(SUPPORT_FLAG)
                ?.observe(viewLifecycleOwner) {
                    Log.e(TAG, "Get result from Support")
                    makeToastFromSupport(it)
                }

        profileViewModel.firebaseUser.observe(
                viewLifecycleOwner,
                Observer { user ->
                    if(user != null) {
                        Log.e(TAG, "User condition is change")
                        isUserLogIn = true
                        if (user.email.isNotBlank() && authEmail.isBlank()) {
                            userEmail = user.email
                            userName = user.email.split("@")[0]
                        }
                    }
                    if (userName.isNotBlank()) updateUI(isUserLogIn, null)
                }
        )

        myFilmsTV.setOnClickListener {
            var bundle = bundleOf(EXTRA_SECOND_TYPE to F_T_MY_FILMS_ALL, EXTRA_FIRST_TYPE to F_T_MY_FILMS_ALL)
            nav.navigate(R.id.action_global_detailCategoryFragment, bundle)
        }

        myStarsTV.setOnClickListener {
            var bundle = bundleOf(EXTRA_SECOND_TYPE to F_T_MY_STARS, EXTRA_FIRST_TYPE to F_T_MY_STARS)
            nav.navigate(R.id.action_global_detailCategoryFragment, bundle)
        }

        historyTV.setOnClickListener {
            var bundle = bundleOf(EXTRA_SECOND_TYPE to F_T_WATCHED, EXTRA_FIRST_TYPE to F_T_WATCHED)
            nav.navigate(R.id.action_global_detailCategoryFragment, bundle)
        }

        supportTV.setOnClickListener {
            var bundle = bundleOf(MAP_EMAIL to userEmail)
            nav.navigate(R.id.action_navigation_profile_to_supportServiceFragment, bundle)
        }

        settingsTV.setOnClickListener {
            nav.navigate(R.id.action_navigation_profile_to_settingsFragment)
        }

        logButton.setOnClickListener {
            if (isUserLogIn) {
                isUserLogIn = false
                updateUI(isUserLogIn, null)
                profileViewModel.logOut()
            }
            else {
                profileViewModel.firebaseUser.removeObservers(viewLifecycleOwner)
                nav.navigate(R.id.action_navigation_profile_to_authFragment)
            }
        }
    }

    private fun updateUI(boolean: Boolean, fromAuth: String?) {
        if (!boolean) {
            Log.e(TAG, "update UI is running. Run with false variant")
            logButton.setText(R.string.sign_in)
            email.text = ""
            login.text = ""
            userLayout.visibility = GONE
        } else {
            Log.e(TAG, "update UI is running. Run with true variant")
            logButton.setText(R.string.logout)
            if (fromAuth != null) {
                email.text = fromAuth.split("@")[0]
                login.text = fromAuth
            } else {
                email.text = userName
                login.text = userEmail
            }
            userLayout.visibility = VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        hideToolbar(toolbar)
    }

    fun makeToastFromAuth(result: String) {
        Toast.makeText(requireContext(), getText(R.string.success_auth), Toast.LENGTH_LONG).show()
    }

    fun makeToastFromSupport(result: String) {
        Toast.makeText(requireContext(), getText(R.string.success), Toast.LENGTH_LONG).show()
    }

}