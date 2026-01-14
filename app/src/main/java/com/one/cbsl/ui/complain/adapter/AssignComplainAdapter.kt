package com.one.cbsl.ui.complain.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.one.cbsl.CbslMain
import com.one.cbsl.databinding.RawComplaintDetailsBinding
import com.one.cbsl.ui.complain.model.GetComplainResponse
import com.one.cbsl.utils.Cbsl
import com.one.cbsl.utils.Constants
import com.one.cbsl.utils.SessionManager

class AssignComplainAdapter(
    var list: List<GetComplainResponse>,
    var optionListener: OptionListener
) : RecyclerView.Adapter<AssignComplainAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RawComplaintDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val complaint = list[position]

        holder.binding.apply {
            if (position == 0) {
                llHeader.visibility = View.VISIBLE
            }
            tvPoRefNo.text = complaint.PurchaseOrderReferenceNumber
            tvComplainNo.text = complaint.Complain_tNumber
            tvComplainDate.text = complaint.ComplaintDate
            tvComplaintype.text = complaint.ComplaintType_Name
            tvClientCode.text = complaint.ClientCode
            tvClientName.text = complaint.ClientName
            tvMachineNo.text = complaint.Machine_Number
            tvItemName.text = complaint.Item_Name
            tvComplaintDetails.text = complaint.ComplaintDetails
            tvBranchContact.text = complaint.BranchContactNumber
            tvComplaintStatus.text = complaint.ComplaintStatus

            ivAdd.setOnClickListener {
                if (SessionManager.getInstance().getString(Constants.GROUP_ID) == "19") {
                    if (complaint.Assignedone.toString() == "1") {
                        optionListener.onItemClick(
                            complaint.ComplaintId.toString(),
                            complaint.WorkOrderId.toString()
                        )
                    } else {
                        optionListener.showComplaintLog(complaint.ComplaintId.toString())
                    }
                } else {
                    optionListener.onItemClick(
                        complaint.ComplaintId.toString(),
                        complaint.WorkOrderId.toString()
                    )
                }

            }
            tvLog.setOnClickListener {
                optionListener.showComplaintLog(complaint.ComplaintId.toString())
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(val binding: RawComplaintDetailsBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface OptionListener {
        fun onItemClick(ComplaintId: String, workorderId: String)
        fun showComplaintLog(ComplaintId: String)
    }
}
