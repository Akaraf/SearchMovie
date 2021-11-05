package com.raaf.android.searchmovie.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.raaf.android.searchmovie.R
import kotlinx.coroutines.awaitAll
import java.util.*

private const val TAG = "AuthFragment"
private const val SUCCESS_FLAG = "Success"
private const val ERROR_FLAG = "Error"
private const val INCORRECT_EMAIL = "Incorrect"
private const val AUTH_FLAG = "Auth"

class AuthFragment : Fragment() {

    private lateinit var authViewModel: AuthViewModel

    private lateinit var actionTextView: TextView
    private lateinit var actionButton: Button
    private var actionIsRegistration = false
    private lateinit var actionChange: Button

    private lateinit var editEmail: EditText
    private lateinit var editPassword: EditText
    private lateinit var editConfirmPassword: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        val view = inflater.inflate(R.layout.fragment_auth, container, false)
        editEmail = view.findViewById(R.id.edit_login)
        editPassword = view.findViewById(R.id.edit_password)
        editConfirmPassword = view.findViewById(R.id.edit_confirm_password)
        actionTextView = view.findViewById(R.id.action_name)
        actionButton = view.findViewById(R.id.button_action)
        actionChange = view.findViewById(R.id.button_change_action)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        actionChange.setOnClickListener {
            actionIsRegistration = !actionIsRegistration
            updateUI(actionIsRegistration)
        }

        actionButton.setOnClickListener {
            makeAction(actionIsRegistration, editEmail.text.toString(), editPassword.text.toString(), editConfirmPassword.text.toString())
        }
    }

    fun makeAction(boolean: Boolean, email: String, password: String, confirmPassword: String) {
        val nav = NavHostFragment.findNavController(this@AuthFragment)
        if (boolean) {
            if (password == confirmPassword) {
                var flag = authViewModel.createAccount(email, password, this@AuthFragment)

                if (flag == ERROR_FLAG) {
                    Toast.makeText(this.context, this.getText(R.string.authentication_failed), Toast.LENGTH_LONG).show()
                    return
                }
                if (flag == INCORRECT_EMAIL) {
                    Toast.makeText(this.context, this.getText(R.string.invalid_email), Toast.LENGTH_LONG).show()
                    return
                }
                else {
                    //nav.previousBackStackEntry?.savedStateHandle?.set(AUTH_FLAG, true)
                    nav.currentBackStackEntry?.savedStateHandle?.set(AUTH_FLAG, email)
                    nav.previousBackStackEntry?.savedStateHandle?.set(AUTH_FLAG, email)
                    nav.popBackStack()//nav.navigate(R.id.action_global_navigation_profile)
                }

            } else {
                Toast.makeText(this.context, this.getText(R.string.invalid_password), Toast.LENGTH_LONG).show()
                return
            }
        } else {
            if (email.isNotBlank() && password.isNotBlank()){
                authViewModel.logIn(email, password, this@AuthFragment)
                nav.currentBackStackEntry?.savedStateHandle?.set(AUTH_FLAG, email)
                nav.previousBackStackEntry?.savedStateHandle?.set(AUTH_FLAG, email)
                nav.popBackStack() //nav.navigate(R.id.action_global_navigation_profile)
            } else Toast.makeText(this.context, this.getString(R.string.empty_field), Toast.LENGTH_SHORT).show()
        }

    }

    fun updateUI(boolean: Boolean) {
        if (boolean) {
            actionChange.setText(R.string.sign_in)
            actionTextView.setText(R.string.create_account)
            actionButton.setText(R.string.sign_up)
            editConfirmPassword.visibility = VISIBLE
        } else {
            actionChange.setText(R.string.create_account)
            actionTextView.setText(R.string.sign_in)
            actionButton.setText(R.string.sign_in)
            editConfirmPassword.visibility = GONE
        }
    }
}