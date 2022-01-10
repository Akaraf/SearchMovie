package com.raaf.android.searchmovie.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.raaf.android.searchmovie.R
import com.raaf.android.searchmovie.ui.utils.showToolbar

private const val CONTENT_TYPE = "ContentType"
private const val LICENSE_AGREEMENT_TYPE = "LicenseAgreement"
private const val VIEWING_CONDITIONS_TYPE = "ViewingConditions"

class AboutAppFragment : Fragment() {

    private var contentType = ""
    private lateinit var contentTV: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contentType = requireArguments().getString(CONTENT_TYPE) ?:""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_about_app, container, false)
        contentTV = view.findViewById(R.id.content_text_view)
        contentTV.highlightColor = requireContext().getColor(R.color.light_gray)
        //contentTV.setTextColor(R.xml.text_selector)
        //contentTV.setBackgroundColor(requireContext().resources.getColor(R.color.text_select_color))
        if (contentType.contains(LICENSE_AGREEMENT_TYPE)) {
            contentTV.text = getString(R.string.license_agreement_text)
            showToolbar(requireActivity().findViewById(R.id.toolbar), getString(R.string.license_agreement))
        }
        if (contentType.contains(VIEWING_CONDITIONS_TYPE)) {
            contentTV.text = getString(R.string.viewing_conditions_text)
            showToolbar(requireActivity().findViewById(R.id.toolbar), getString(R.string.viewing_conditions))
        }
        return view
    }
}