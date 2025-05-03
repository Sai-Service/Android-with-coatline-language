package com.example.apinew

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class HintSpinnerAdapter(context: Context, private val items: List<String>) :
    ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, items) {

    init {
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    override fun isEnabled(position: Int): Boolean {
        return position != 0
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent) as TextView
        if (position == 0) {
            view.setTextColor(context.resources.getColor(android.R.color.darker_gray))
        } else {
            view.setTextColor(context.resources.getColor(android.R.color.black))
        }
        return view
    }
}
