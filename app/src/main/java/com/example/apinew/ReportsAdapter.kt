//package com.example.apinew
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//
//class ReportsAdapter(private val reportsList: List<ReportItem>) :
//    RecyclerView.Adapter<ReportsAdapter.ViewHolder>() {
//
//    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val batchNameTextView: TextView = itemView.findViewById(R.id.batch_name_textview)
//        val vinTextView: TextView = itemView.findViewById(R.id.vin_textview)
//        val variantCdTextView: TextView = itemView.findViewById(R.id.variant_cd_textview)
//        val dmsInvNoTextView: TextView = itemView.findViewById(R.id.dms_inv_no_textview)
//        val chassisNumTextView: TextView = itemView.findViewById(R.id.chassis_num_textview)
//        val modelCdTextView: TextView = itemView.findViewById(R.id.model_cd_textview)
//        val engineNumTextView: TextView = itemView.findViewById(R.id.engine_num_textview)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val itemView = LayoutInflater.from(parent.context)
//            .inflate(R.layout.report_item, parent, false)
//        return ViewHolder(itemView)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val reportItem = reportsList[position]
//        holder.batchNameTextView.text = reportItem.batchName
//        holder.vinTextView.text = reportItem.vin
//        holder.variantCdTextView.text = reportItem.chassis_no
//        holder.dmsInvNoTextView.text = reportItem.fuel_desc
//        holder.chassisNumTextView.text = reportItem.model_desc
//        holder.modelCdTextView.text = reportItem.colour
//    }
//
//    override fun getItemCount(): Int {
//        return reportsList.size
//    }
//}
