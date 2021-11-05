package com.raaf.android.searchmovie.ui.profile

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.raaf.android.searchmovie.R

private const val TAG = "ConfirmCancelD"
private const val TYPE_TASK_FOR_DIALOG = "TypeTask"
private const val CLEAR_DATA_APP = "ClearDataApp"
private const val SYNCHRONIZE_DATA_ACCOUNT = "SynchronizeDataAccount"
private const val CLEAR_USER_DATA = "CUD"

class ConfirmCancelDialogFragment : DialogFragment() {

    private lateinit var questionText: TextView
    private lateinit var cancelText: TextView
    private lateinit var confirmText: TextView
    private var typeTask = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG, "oncreate")
        typeTask = requireArguments().getString(TYPE_TASK_FOR_DIALOG) ?: ""
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = requireActivity().layoutInflater.inflate(R.layout.fragment_yes_no_dialog,null)
        questionText = view.findViewById(R.id.question_text_dialog)
        cancelText = view.findViewById(R.id.cancel_text_dialog)
        confirmText = view.findViewById(R.id.confirm_text_dialog)
        cancelText.setOnClickListener {
            this.dialog!!.cancel()
        }
        confirmText.setOnClickListener {
            var intent = Intent()
            intent.putExtra(TYPE_TASK_FOR_DIALOG, typeTask)
            targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
            this.dialog!!.cancel()
        }
        val builder = AlertDialog.Builder(requireActivity())
        Log.e(TAG, "make question")
        makeQuestionText()
        builder.setView(view)
            .setCancelable(true)
            .create()
        return builder.create()
    }


    private fun makeQuestionText(){
        Log.e(TAG, typeTask)
        when {
            typeTask.contains(CLEAR_DATA_APP) -> {
                Log.e(TAG, "app")
                questionText.text = getString(R.string.clear_app_question)
            }
            typeTask.contains(SYNCHRONIZE_DATA_ACCOUNT) -> {
                Log.e(TAG, "sync")
                questionText.text = getString(R.string.synchronize_question)
            }
            typeTask.contains(CLEAR_USER_DATA) -> {
                Log.e(TAG, "acc")
                questionText.text = getString(R.string.clear_account_question)
            }
        }
    }
}