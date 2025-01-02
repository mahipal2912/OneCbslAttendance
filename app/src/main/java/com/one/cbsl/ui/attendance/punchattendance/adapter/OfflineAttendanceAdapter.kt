package com.one.cbsl.ui.attendance.punchattendance.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.one.cbsl.databinding.RawItemAttendanceBinding
import com.one.cbsl.databinding.RawOfflineAttendanceBinding
import com.one.cbsl.localdb.AttendanceMaster

class OfflineAttendanceAdapter(var list: List<AttendanceMaster>) :
    RecyclerView.Adapter<OfflineAttendanceAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            RawOfflineAttendanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (position == 0) {
            holder.llHeader.visibility = View.VISIBLE
        }
        holder.apply {
            tvDate.text = list[position].PunchDate
            tvPunchIn.text = list[position].PunchIn
            tvPunchOut.text = list[position].PunchOut
            tvAddress.text = list[position].LocationAddress
            tvOutAddress.text = list[position].PunchOutLocationAddress
            syncDate.text = list[position].syncDate
            syncStatus.text = list[position].status

        }


    }

    override fun getItemCount(): Int {
        return list.size;
    }

    class ViewHolder(binding: RawOfflineAttendanceBinding) : RecyclerView.ViewHolder(binding.root) {
        var tvDate = binding.tvPunchDate
        var tvPunchIn = binding.tvPunchIn
        var tvPunchOut = binding.tvPunchOut
        var tvAddress = binding.tvAddress
        var tvOutAddress = binding.tvOutAddress
        var syncStatus = binding.syncStatus
        var syncDate = binding.syncDate
        var llHeader = binding.llHeader


    }
}