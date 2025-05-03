package com.example.apinew

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class VehicleAdapter(private val context: Context, private val dataSource: List<Vehicle>) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView = convertView ?: inflater.inflate(android.R.layout.simple_list_item_2, parent, false)

        val vehicle = getItem(position) as Vehicle

        val text1 = rowView.findViewById<TextView>(android.R.id.text1)
        val text2 = rowView.findViewById<TextView>(android.R.id.text2)

        text1.text = vehicle.CMNID.toString()
        text2.text = vehicle.CMNDESC

        return rowView
    }
}
