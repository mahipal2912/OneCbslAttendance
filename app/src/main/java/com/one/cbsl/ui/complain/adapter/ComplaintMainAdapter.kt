package com.one.cbsl.ui.complain.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.one.cbsl.R
import com.one.cbsl.databinding.RawDashboardComplainBinding
import com.one.cbsl.databinding.RawDashboardViewBinding
import com.one.cbsl.ui.attendance.home.model.DashboardResponse
import com.one.cbsl.utils.Constants
import com.one.cbsl.utils.SessionManager

class ComplaintMainAdapter(
    var dashboardResponse: DashboardResponse,
    var optionListener: OpitionListener
) :
    RecyclerView.Adapter<ComplaintMainAdapter.ViewHolder>() {

    private val icons: IntArray = intArrayOf(
        R.drawable.ic_pending_complaint,
        R.drawable.ic_pending_complaint,
        R.drawable.ic_installation, R.drawable.ic_complete
    )
    private var titles: Array<String> = arrayOf(
        Constants.PendingComplaint,
        Constants.PendingPmr,
        Constants.PendingInstallation, Constants.CloseComplaint
    )

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            RawDashboardComplainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.icons.setImageResource(icons[position])
        holder.titles.text = titles[position]

        when (position) {
            0 -> holder.tv_value.text = dashboardResponse.Pending
            1 -> holder.tv_value.text = dashboardResponse.pmr
            2 -> holder.tv_value.text = dashboardResponse.InstallPending
            3 -> holder.tv_value.text = dashboardResponse.closeComplaint
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

    class ViewHolder(binding: RawDashboardComplainBinding) : RecyclerView.ViewHolder(binding.root) {
        var titles = binding.tvPmrTitle
        var icons = binding.ivIcon
        var tv_value = binding.tvPmrValue

    }
}