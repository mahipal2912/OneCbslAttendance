package com.one.cbsl.ui.attendance.home.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.one.cbsl.R
import com.one.cbsl.databinding.RawDashboardViewBinding
import com.one.cbsl.ui.attendance.home.model.DashboardResponse
import com.one.cbsl.utils.Constants
import com.one.cbsl.utils.SessionManager

class HomeAdapter(var dashboardResponse: DashboardResponse, var optionListener: OpitionListener) :
    RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    private val icons: IntArray = intArrayOf(
        R.drawable.ic_attendance,
        R.drawable.ic_movment,
        R.drawable.ic_conveyance,
        R.drawable.ic_voucher, R.drawable.ic_leave
    )
    private var titles: Array<String> = arrayOf(
        Constants.Attendance,
        Constants.Movement,
        Constants.Conveyance,
        Constants.Voucher, Constants.LeavePlan
    )

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            RawDashboardViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.icons.setImageResource(icons[position])
        holder.titles.text = titles[position]

        when (position) {
            0 -> holder.tv_value.text = dashboardResponse.TotalAttendance
            1 -> holder.tv_value.text = dashboardResponse.TotalMovement
            2 -> holder.tv_value.text = dashboardResponse.TotalConveyance
            3 -> holder.tv_value.text = dashboardResponse.TotalVoucher
            4 -> holder.tv_value.text = dashboardResponse.LeavePlan
        }

        holder.titles.setOnClickListener {
            if (SessionManager.getInstance()
                    .getString(Constants.UserTypeID) == Constants.TYPE_ADMIN || SessionManager.getInstance()
                    .getString(Constants.UserTypeID) == Constants.TYPE_HOD
            ) {
                optionListener.onDashboardClick(titles[position])
            } else {
                optionListener.onDashboardClick(titles[position])
            }
        }
    }

    interface OpitionListener {

        fun onDashboardClick(item: String);
    }

    override fun getItemCount(): Int {
        return titles.size;
    }

    class ViewHolder(binding: RawDashboardViewBinding) : RecyclerView.ViewHolder(binding.root) {
        var titles = binding.tvTitle
        var icons = binding.ivIcon
        var tv_value = binding.tvValue

    }
}