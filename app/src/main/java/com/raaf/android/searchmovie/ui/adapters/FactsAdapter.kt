package com.raaf.android.searchmovie.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.raaf.android.searchmovie.R

class FactsAdapter(private val factItems: List<String>) : RecyclerView.Adapter<FactsAdapter.FactsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FactsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_fact, parent, false)
        return FactsViewHolder(view)
    }

    override fun onBindViewHolder(holder: FactsViewHolder, position: Int) {
        val factItem = factItems[position]
        holder.fact.text = factItem
    }

    override fun getItemCount(): Int = factItems.size


    inner class FactsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val fact: TextView = itemView.findViewById(R.id.fact_text)
    }
}