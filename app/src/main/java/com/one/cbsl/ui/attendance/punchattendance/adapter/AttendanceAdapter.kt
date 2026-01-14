package com.one.cbsl.ui.attendance.punchattendance.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.one.cbsl.databinding.RawItemAttendanceBinding
import com.one.cbsl.ui.attendance.punchattendance.model.AttendanceResponse

class AttendanceAdapter(var list: List<AttendanceResponse>) :
    RecyclerView.Adapter<AttendanceAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            RawItemAttendanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
            tvStatus.text = list[position].Astatus
            tvCode.text = list[position].EmployeeCode
            tvWorkingHour.text = list[position].WorkingHours
            approvedBy.text = list[position].approvedby
            approveDate.text = list[position].approveddate
            markstatus.text = list[position].MarksStatus
            hodStatus.text = list[position].hodStatus

        }


    }

    override fun getItemCount(): Int {
        return list.size;
    }

    class ViewHolder(binding: RawItemAttendanceBinding) : RecyclerView.ViewHolder(binding.root) {
        var tvDate = binding.tvPunchDate
        var tvPunchIn = binding.tvPunchIn
        var tvPunchOut = binding.tvPunchOut
        var tvStatus = binding.tvStatus
        var tvWorkingHour = binding.tvWorkingHour
        var markstatus = binding.markstatus
        var hodStatus = binding.hodStatus
        var tvCode = binding.tvCode
        var approvedBy = binding.approvedBy
        var approveDate = binding.approveDate
        var llHeader = binding.llHeader


    }
}