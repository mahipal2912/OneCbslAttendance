package com.one.cbsl.ui.complain.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.one.cbsl.databinding.RawComplainLogBinding
import com.one.cbsl.ui.complain.model.ComplaintLogResponse


class ComplaintLogAdapter(
    var list: List<ComplaintLogResponse>
) :
    RecyclerView.Adapter<ComplaintLogAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            RawComplainLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        holder.binding.apply {
            if (position == 0) {
                llHeader.visibility = View.VISIBLE
            }
            tvMachineNo.text = list[position].MachineNumber
            tvCreateDate.text = list[position].CDate
            tvAssignTo.text = list[position].AssignTo
            tvAssignBy.text = list[position].CBy
            tvPendingreason.text = list[position].PendingReason
            tvRemarks.text = list[position].Remarks
            tvPodnumber.text = list[position].PodNumber
        }
    }

    override fun getItemCount(): Int {
        return list.size;
    }

    class ViewHolder(val binding: RawComplainLogBinding) : RecyclerView.ViewHolder(binding.root) {

    }
}