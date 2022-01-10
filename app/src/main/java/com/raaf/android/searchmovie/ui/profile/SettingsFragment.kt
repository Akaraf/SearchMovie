package com.raaf.android.searchmovie.ui.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.raaf.android.searchmovie.R
import com.raaf.android.searchmovie.backgroundJob.services.FirebaseStorageService
import com.raaf.android.searchmovie.ui.utils.showToolbar

private const val TAG = "SettingsFragment"
private const val THEME_AUTO = "Auto"
private const val THEME_LIGHT = "Light"
private const val THEME_DARK = "Dark"
private const val TYPE_TASK_FOR_DIALOG = "TypeTask"
private const val CLEAR_DATA_APP = "ClearDataApp"
private const val SYNCHRONIZE_DATA_ACCOUNT = "SynchronizeDataAccount"
private const val CLEAR_USER_DATA = "CUD"
private const val CONTENT_TYPE = "ContentType"
private const val LICENSE_AGREEMENT_TYPE = "LicenseAgreement"
private const val VIEWING_CONDITIONS_TYPE = "ViewingConditions"
private const val TYPE_COLLECTION = "type"

class SettingsFragment : Fragment(), View.OnClickListener {

    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var confirmCancelDialogFragment: ConfirmCancelDialogFragment
    private lateinit var nav: NavController
    private lateinit var syncAppAndAccountTV: TextView
    private lateinit var clearAppDataTV: TextView
    private lateinit var clearAccountDataTV: TextView
    private lateinit var darkThemeTV: TextView
    private lateinit var alertDialogBuilder: AlertDialog.Builder
    private lateinit var dialogThemeLayout: View
    private lateinit var dialogTheme: BottomSheetDialog
    private lateinit var darkThemeEnableLayout: ConstraintLayout
    private lateinit var darkThemeEnabledCheck: ImageView
    private lateinit var darkThemeDisableLayout: ConstraintLayout
    private lateinit var darkThemeDisabledCheck: ImageView
    private lateinit var darkThemeAutoLayout: ConstraintLayout
    private lateinit var darkThemeAutoCheck: ImageView
    private lateinit var cancelDialogTheme: TextView

    private lateinit var licenseAgreementTV: TextView
    private lateinit var viewingConditionsTV: TextView
    private lateinit var recommendAppTV: TextView

    private lateinit var versionAppTV: TextView
    private lateinit var warningAgeTV: TextView

    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        alertDialogBuilder = AlertDialog.Builder(context)
        confirmCancelDialogFragment = ConfirmCancelDialogFragment()
        confirmCancelDialogFragment.setTargetFragment(this, 1)
        toolbar = requireActivity().findViewById(R.id.toolbar)
        showToolbar(toolbar, getString(R.string.settings))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        syncAppAndAccountTV = view.findViewById(R.id.synchronize_with_account)
        syncAppAndAccountTV.setOnClickListener(this)
        clearAppDataTV = view.findViewById(R.id.clear_app_data)
        clearAppDataTV.setOnClickListener(this)
        clearAccountDataTV = view.findViewById(R.id.clear_account_data)
        clearAccountDataTV.setOnClickListener(this)
        dialogThemeLayout = inflater.inflate(R.layout.popup_theme_element, null)
        darkThemeTV = view.findViewById(R.id.dark_theme_setting)
        darkThemeTV.setOnClickListener {
            dialogTheme.show()
        }
        dialogTheme = BottomSheetDialog(requireContext())
        dialogTheme.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        dialogTheme.setContentView(dialogThemeLayout)
        dialogTheme.setCanceledOnTouchOutside(true)
        dialogTheme.setCancelable(true)
        var winParam = dialogTheme.window!!.attributes
        winParam.gravity = Gravity.BOTTOM
        alertDialogBuilder.setView(dialogThemeLayout)

        darkThemeEnableLayout = dialogThemeLayout.findViewById(R.id.enable_dark_theme_layout)
        darkThemeEnableLayout.setOnClickListener {
            setUIForThemeSetting(THEME_DARK)
            settingsViewModel.changeTheme(THEME_DARK)
            dialogTheme.dismiss()
        }
        darkThemeEnabledCheck = dialogThemeLayout.findViewById(R.id.enable_dark_theme_check)

        darkThemeDisableLayout = dialogThemeLayout.findViewById(R.id.disable_dark_theme_layout)
        darkThemeDisableLayout.setOnClickListener {
            setUIForThemeSetting(THEME_LIGHT)
            settingsViewModel.changeTheme(THEME_LIGHT)
            dialogTheme.dismiss()
        }
        darkThemeDisabledCheck = dialogThemeLayout.findViewById(R.id.disable_dark_theme_check)

        darkThemeAutoLayout = dialogThemeLayout.findViewById(R.id.auto_dark_theme_layout)
        darkThemeAutoLayout.setOnClickListener {
            setUIForThemeSetting(THEME_AUTO)
            settingsViewModel.changeTheme(THEME_AUTO)
            dialogTheme.dismiss()
        }
        darkThemeAutoCheck = dialogThemeLayout.findViewById(R.id.auto_dark_theme_check)

        cancelDialogTheme = dialogThemeLayout.findViewById(R.id.cancel_popup_theme)
        cancelDialogTheme.setOnClickListener {
            dialogTheme.dismiss()
        }

        licenseAgreementTV = view.findViewById(R.id.license_agreement)
        licenseAgreementTV.setOnClickListener(this)
        viewingConditionsTV = view.findViewById(R.id.viewing_conditions)
        viewingConditionsTV.setOnClickListener(this)
        recommendAppTV = view.findViewById(R.id.recommend)
        recommendAppTV.setOnClickListener(this)

        versionAppTV = view.findViewById(R.id.version_app)
        warningAgeTV = view.findViewById(R.id.warning_age)

        setUIForThemeSetting(settingsViewModel.checkThemeSetting())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nav = NavHostFragment.findNavController(this@SettingsFragment)
        versionAppTV.text = getString(R.string.version) + " "  + requireContext().packageManager.getPackageInfo(
            requireContext().packageName, 0).versionName
        warningAgeTV.text = getString(R.string.warning_age)
    }

    override fun onResume() {
        super.onResume()
        showToolbar(toolbar, getString(R.string.settings))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            var taskType = data?.getStringExtra(TYPE_TASK_FOR_DIALOG) ?: ""
            when (taskType) {
                CLEAR_DATA_APP -> settingsViewModel.clearApp()
                SYNCHRONIZE_DATA_ACCOUNT -> {
                    var intent = Intent(requireActivity(), FirebaseStorageService::class.java)
                    intent.putExtra(TYPE_COLLECTION, SYNCHRONIZE_DATA_ACCOUNT)
                    requireActivity().startService(intent)
                }
                CLEAR_USER_DATA -> {
                    var intent = Intent(requireActivity(), FirebaseStorageService::class.java)
                    intent.putExtra(TYPE_COLLECTION, CLEAR_USER_DATA)
                    requireActivity().startService(intent)
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.synchronize_with_account -> {
                var bundle = bundleOf()
                bundle.putString(TYPE_TASK_FOR_DIALOG, SYNCHRONIZE_DATA_ACCOUNT)
                confirmCancelDialogFragment.arguments = bundle
                showConfirmCancelDialogFragment(SYNCHRONIZE_DATA_ACCOUNT)
            }
            R.id.clear_account_data -> {
                var bundle = bundleOf()
                bundle.putString(TYPE_TASK_FOR_DIALOG, CLEAR_USER_DATA)
                confirmCancelDialogFragment.arguments = bundle
                showConfirmCancelDialogFragment(CLEAR_USER_DATA)
            }
            R.id.clear_app_data -> {
                var bundle = bundleOf()
                bundle.putString(TYPE_TASK_FOR_DIALOG, CLEAR_DATA_APP)
                confirmCancelDialogFragment.arguments = bundle
                showConfirmCancelDialogFragment(CLEAR_DATA_APP)
            }
            R.id.license_agreement -> {
                var bundle = bundleOf()
                bundle.putString(CONTENT_TYPE, LICENSE_AGREEMENT_TYPE)
                nav.navigate(R.id.action_settingsFragment_to_aboutAppFragment, bundle)
            }
            R.id.viewing_conditions -> {
                var bundle = bundleOf()
                bundle.putString(CONTENT_TYPE, VIEWING_CONDITIONS_TYPE)
                nav.navigate(R.id.action_settingsFragment_to_aboutAppFragment, bundle)
            }
            R.id.recommend -> {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, makeRecommendText())
                startActivity(shareIntent)
            }
        }
    }

    private fun makeRecommendText() : String {
        return "SearchMovie: ${getString(R.string.search_movie_promo)}\n" +
                getString(R.string.example_link_app)
    }

    private fun showConfirmCancelDialogFragment(argument: String) {
        confirmCancelDialogFragment.show(parentFragmentManager, "ConfirmCancelDialogFragment")
    }

    private fun setUIForThemeSetting(setting: String) {
        when (setting) {
            THEME_AUTO -> {
                darkThemeTV.text = "${getString(R.string.dark_theme)}:\n${getString(R.string.automatically)}"
                darkThemeEnabledCheck.visibility = View.INVISIBLE
                darkThemeDisabledCheck.visibility = View.INVISIBLE
                darkThemeAutoCheck.visibility = View.VISIBLE
            }
            THEME_LIGHT -> {
                darkThemeTV.text = "${getString(R.string.dark_theme)}:\n${getString(R.string.disabled)}"
                darkThemeAutoCheck.visibility = View.INVISIBLE
                darkThemeEnabledCheck.visibility = View.INVISIBLE
                darkThemeDisabledCheck.visibility = View.VISIBLE
                //requireActivity().setTheme(R.style.Theme_SearchMovieBug)
            }
            THEME_DARK -> {
                darkThemeTV.text = "${getString(R.string.dark_theme)}:\n${getString(R.string.enabled)}"
                darkThemeDisabledCheck.visibility = View.INVISIBLE
                darkThemeAutoCheck.visibility = View.INVISIBLE
                darkThemeEnabledCheck.visibility = View.VISIBLE

            }
        }
    }
}