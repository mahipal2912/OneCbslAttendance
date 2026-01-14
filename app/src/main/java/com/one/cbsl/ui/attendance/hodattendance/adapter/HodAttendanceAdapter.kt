package com.one.cbsl.ui.attendance.hodattendance.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.one.cbsl.databinding.ItemHodAttendanceBinding
import com.one.cbsl.ui.attendance.hodattendance.model.EmployeeAttendanceResponse


class HodAttendanceAdapter(
    private val optionListener: OptionListener,
    private val context: Context,
    private val list: List<EmployeeAttendanceResponse>
) : RecyclerView.Adapter<HodAttendanceAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHodAttendanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val employee = list[position]

        holder.binding.apply {
            if (position == 0) {
                llHeader.visibility = View.VISIBLE
            }

            tvEmployeeName.text = employee.employeeName
            tvEmployeeCode.text = employee.employeeCode
            tvMobileNo.text = employee.mobileNo
            tvFacility.text = employee.facility
            tvReporting.text = employee.reporting

            btnMarkStatus.setOnClickListener {
                    optionListener.onItemClick(employee.userId,employee.employeeCode, employee.CityName,employee.faceData)

            }
        }
    }

    override fun getItemCount(): Int = list.size

    interface OptionListener {
        fun onItemClick(userid:String,employeeCode: String, facility: String,registerType:String)
    }

    class ViewHolder(val binding: ItemHodAttendanceBinding) : RecyclerView.ViewHolder(binding.root)
}
