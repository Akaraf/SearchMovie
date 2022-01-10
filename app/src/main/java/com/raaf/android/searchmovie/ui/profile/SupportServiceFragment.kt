package com.raaf.android.searchmovie.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.raaf.android.searchmovie.R
import com.raaf.android.searchmovie.ui.utils.showToolbar

private const val MAP_EMAIL = "email"
private const val TAG = "Support Service F"
private const val SUPPORT_FLAG = "Support"

class SupportServiceFragment : Fragment() {

    private lateinit var supportServiceViewModel: SupportServiceViewModel
    private lateinit var supportListView: ListView

    private lateinit var sendLayout: LinearLayout
    private lateinit var editEmail: EditText
    private lateinit var editMessageForSupport: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var toolbar: Toolbar

    private lateinit var listThemesOfSupport: List<String>
    private var userEmail = ""
    private var themeForSupport = ""
    private var message = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportServiceViewModel = ViewModelProvider(this).get(SupportServiceViewModel::class.java)
        listThemesOfSupport = listOf(getString(R.string.support_app), getString(R.string.support_content), getString(R.string.support_suggestion),
                getString(R.string.support_logging), getString(R.string.support_other))
        toolbar = requireActivity().findViewById(R.id.toolbar)
        showToolbar(toolbar, getString(R.string.message_subject))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_support_service, container, false)
        supportListView = view.findViewById(R.id.support_themes_list_view)
        supportListView.adapter = ArrayAdapter(this.requireContext(), android.R.layout.simple_list_item_1, listThemesOfSupport)
        sendLayout = view.findViewById(R.id.send_message_layout)
        editEmail = view.findViewById(R.id.user_email_edit_text)
        editMessageForSupport = view.findViewById(R.id.message_for_support_edit_text)
        sendButton = view.findViewById(R.id.send_message_to_support)

        requireArguments().getString(MAP_EMAIL).let {
            userEmail = it ?: ""
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val nav = NavHostFragment.findNavController(this@SupportServiceFragment)

        supportListView.setOnItemClickListener { parent, view, position, id ->
            themeForSupport = listThemesOfSupport[position]
            Log.e(TAG, themeForSupport)
            updateUI()
        }

        sendButton.setOnClickListener {
            userEmail = editEmail.text.toString()
            message = editMessageForSupport.text.toString()
            sendEvent(themeForSupport, userEmail, message)
            nav.previousBackStackEntry?.savedStateHandle?.set(SUPPORT_FLAG, "success")
            nav.popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        showToolbar(toolbar, getString(R.string.message_subject))
    }

    private fun updateUI() {
        showToolbar(toolbar, getString(R.string.feedback))
        supportListView.visibility = GONE
        sendLayout.visibility = VISIBLE
        if (userEmail.isNotEmpty()) editEmail.setText(userEmail)
    }

    private fun sendEvent(typeMessage: String, email: String, messageT: String) {
        var bundle = Bundle()
        bundle.putString("type_of_message", typeMessage)
        bundle.putString("email", email)
        bundle.putString("message", messageT)
        supportServiceViewModel.sendMessage(bundle)
    }
}