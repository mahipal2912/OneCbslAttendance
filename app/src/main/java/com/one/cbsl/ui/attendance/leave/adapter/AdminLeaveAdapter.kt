package com.one.cbsl.ui.attendance.leave.adapter

import MyLeaveResponse
import android.app.AlertDialog
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.one.cbsl.databinding.ItemAdminLeavePlanBinding
import com.one.cbsl.databinding.RawItemLeaveBinding
import com.one.cbsl.ui.attendance.leave.adapter.MyLeaveAdapter.ViewHolder

class AdminLeaveAdapter(
    var list: List<MyLeaveResponse>,
    var optionListener: OpitionListener,
    var activity: FragmentActivity
) :
    RecyclerView.Adapter<AdminLeaveAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ItemAdminLeavePlanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {

            if (position == 0) {
                llHeader.visibility = View.VISIBLE
            }
            tvEmpName.text = list[position].EmployeeName
            if (list[position].EmployeeName == "") {
                llApproveReject.visibility = View.INVISIBLE
            }
            tvFromDate.text = list[position].FromDate
            tvToDate.text = list[position].ToDate
            tvReason.text = list[position].Reason
            tvLtype.text = list[position].LeaveType

            if (list[position].ApprovedBy != null) {
                tvApprovedBy.text = list[position].ApprovedBy
            }
            if (list[position].ApprovedStatus != null) {
                when {
                    list[position].ApprovedStatus.equals("Rejected") -> {
                        llApproveReject.visibility = View.INVISIBLE
                        tvStatus.setTextColor(Color.parseColor("#F44336"))
                    }

                    list[position].ApprovedStatus.equals("Approved") -> {
                        tvStatus.setTextColor(Color.parseColor("#4CAF50"))
                        llApproveReject.visibility = View.INVISIBLE
                    }

                    list[position].ApprovedStatus.equals("Pending") -> {
                        tvStatus.setTextColor(Color.parseColor("#FF9800"))
                    }
                }
                tvStatus.text = list[position].ApprovedStatus
            }
            tvApprove.setOnClickListener {
                confirmDialog(
                    "Approve", list[position].id!!,
                    list[position].UserId!!,
                    "Approved"
                )
            }
            tvReject.setOnClickListener {
                try {
                    confirmDialog(
                        "Reject", list[position].id!!,
                        list[position].UserId!!, "Rejected"
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun confirmDialog(s: String, id_: String, userId: String, status: String) {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage("Are you sure you want to $s")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->

                try {
                    optionListener.onItemClick(
                        id_,
                        userId,
                        status
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("No") { dialog, id ->
                // Dismiss the dialog
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }


    interface OpitionListener {
        fun onItemClick(leaveid: String?, userid: String?, status: String?)
    }

    override fun getItemCount(): Int {
        return list.size;
    }

    class ViewHolder(val binding: ItemAdminLeavePlanBinding) : RecyclerView.ViewHolder(binding.root)

}