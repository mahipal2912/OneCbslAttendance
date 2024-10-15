package com.one.cbsl.ui.attendance.leave.adapter

import MyLeaveResponse
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.one.cbsl.R
import com.one.cbsl.databinding.RawItemLeaveBinding

class MyLeaveAdapter(private var list: List<MyLeaveResponse>) :
    RecyclerView.Adapter<MyLeaveAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RawItemLeaveBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            llHeader.visibility = if (position == 0) View.VISIBLE else View.GONE

            tvFromDate.text = list[position].FromDate
            tvToDate.text = list[position].ToDate
            tvReason.text = list[position].Reason
            tvMarkby.text = list[position].Leavemarkby
            tvLtype.text = list[position].LeaveType

            // Set ApprovedBy if it's not null
            tvApprovedBy.text = list[position].ApprovedBy ?: ""

            // Set ApprovedStatus and its color
            list[position].ApprovedStatus?.let { status ->
                tvStatus.text = status
                tvStatus.setTextColor(
                    when (status) {
                        "Rejected" -> Color.parseColor("#F44336")
                        "Approved" -> Color.parseColor("#4CAF50")
                        "Pending" -> Color.parseColor("#FF9800")
                        else -> Color.BLACK // Default color
                    }
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(val binding: RawItemLeaveBinding) : RecyclerView.ViewHolder(binding.root)
}
