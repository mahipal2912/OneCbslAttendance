package com.one.cbsl.ui.attendance.hodattendance.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.one.cbsl.databinding.RawItemFacilityHodBinding
import com.one.cbsl.ui.attendance.conveyance.model.HodConveyanceResponse
import com.one.cbsl.utils.SessionManager

class HodFacilityAttendanceAdapter(
    var optionListner: OpitionListener,
    var context: Context,
    var list: List<HodConveyanceResponse>
) :
    RecyclerView.Adapter<HodFacilityAttendanceAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            RawItemFacilityHodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.apply {
            tvFacilityName.text = list[position].fName
            tvApproveTotal.text = list[position].total
            tvPendingTotal.text = list[position].Present
            tvRejectTotal.text = list[position].Absent
            tvRejectTotal.setOnClickListener {
                optionListner.onItemClick(list[position].FacilityId, "Pending")
                SessionManager.getInstance().putString("Status", "Pending")
            }
            /*  tvApproveTotal.setOnClickListener {
                optionListner.onItemClick(list[position].FacilityId, "Approved")
                SessionManager.getInstance().putString("Status", "Approved")
            }
            tvPendingTotal.setOnClickListener {
                optionListner.onItemClick(list[position].FacilityId, "")
                SessionManager.getInstance().putString("Status", "")
            }
            tvRejectTotal.setOnClickListener {
                optionListner.onItemClick(list[position].FacilityId, "Rejected")
                SessionManager.getInstance().putString("Status", "Rejected")
            }*/
        }
    }


    interface OpitionListener {

        fun onItemClick(facilityid: String, status: String);

    }

    override fun getItemCount(): Int {
        return list.size;
    }

    class ViewHolder(val binding: RawItemFacilityHodBinding) :
        RecyclerView.ViewHolder(binding.root)
}