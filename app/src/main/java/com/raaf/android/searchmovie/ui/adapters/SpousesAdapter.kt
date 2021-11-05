package com.raaf.android.searchmovie.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.raaf.android.searchmovie.R
import com.raaf.android.searchmovie.dataModel.Spouse

private const val EXTRA_PERSON_ID = "personId"

class SpousesAdapter(private val spouses: List<Spouse>, private val countOfChildren: List<String>, private val divorce: String) : RecyclerView.Adapter<SpousesAdapter.SpousesViewHolder>() {

    interface OnSpouseClickListener {
        fun onSpouseClicked(personId: Int) : Boolean
    }

    private var spouseListener: OnSpouseClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpousesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_spouses, parent, false)
        return SpousesViewHolder(view)
    }

    override fun onBindViewHolder(holder: SpousesViewHolder, position: Int) {
        val spouse = spouses[position]
        holder.name.text = spouse.name
        var detail = makeDetailText(spouse)
        holder.detail.text = detail
        if (spouse.personId != null) holder.getSpouseId(spouse.personId!!)
    }

    override fun getItemCount(): Int = spouses.size

    private fun makeDetailText(spouse: Spouse) : String {
        var divorced = if (spouse.divorced == null || spouse.divorced == false) ""
                            else divorce
        var children = if (spouse.children == null || spouse.children == 0) ""
                            else countOfChildren[spouse.children!! -1]

        return if (divorced != "" && children != "") "$divorced, $children"
        else "$divorced$children"
    }

    fun setOnSpouseClickListener(listener: OnSpouseClickListener) {
        spouseListener = listener
    }

    inner class SpousesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        var spouseId: Int? = null
        val name: TextView = itemView.findViewById(R.id.spouse_name)
        val detail: TextView = itemView.findViewById(R.id.spouse_detail)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (spouseId != null && spouseListener != null) {
                if (spouseListener!!.onSpouseClicked(spouseId!!)) {
                    var bundle = bundleOf(EXTRA_PERSON_ID to spouseId)
                    v.findNavController().navigate(R.id.action_global_personFragment, bundle)
                }
            }
        }

        fun getSpouseId(id: Int) {
            this.spouseId = id
        }
    }
}