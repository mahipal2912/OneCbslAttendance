package com.one.cbsl.ui.attendance.conveyance.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.one.cbsl.databinding.RawItemConveyanceBinding
import com.one.cbsl.ui.attendance.conveyance.model.MyConveyanceResponse


class ConveynaceAdapter(
    var list: List<MyConveyanceResponse>,
    private var optionListener: OptionListener
) : RecyclerView.Adapter<ConveynaceAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = RawItemConveyanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val conveyance = list[position]

        holder.binding.apply {
            if (position == 0) {
                llHeader.visibility = View.VISIBLE
            }
            tvDate.text = conveyance.ConveyanceDate
            tvFare.text = conveyance.Fare
            tvFromLocation.text = conveyance.FromLocation
            tvToLocation.text = conveyance.ToLocation
            tvTransportMode.text = conveyance.TransportMode
            tvRemarks.text = conveyance.Remarks
            tvVoucher.text = conveyance.VoucherNo
            tvFooding.text = conveyance.fooding
            tvHodRemarks.text = conveyance.HodRemarks ?: ""
            tvApproveStatus.text = conveyance.Status ?: ""
            tvTotalexpense.text = conveyance.TotalExpense ?: ""
            tvImgUrl.text = conveyance.imagename ?: ""
            tvApproveAmt.text = conveyance.Approved_amount ?: ""
            tvHodApprovalStatus.text = conveyance.HOD_Approval_Status ?: ""
            tvPaidStatus.text = conveyance.PaidStatus ?: ""
            tvApprovedDate.text = conveyance.HOD_Approved_Date ?: ""
            tvNeftNo.text = conveyance.NEFTNo ?: ""

            when (conveyance.Status) {
                "Rejected" -> tvApproveStatus.setTextColor(Color.parseColor("#F44336"))
                "Approved" -> tvApproveStatus.setTextColor(Color.parseColor("#4CAF50"))
                "Hold" -> tvApproveStatus.setTextColor(Color.parseColor("#FF9800"))
            }

            tvApproveStatus.text = conveyance.Status ?: ""

            tvImgUrl.setOnClickListener {
                if (conveyance.imagelocation?.isNotEmpty() == true) {
                    optionListener.onItemClick(conveyance.imagelocation)
                }
            }
        }
    }

    interface OptionListener {
        fun onItemClick(item: String)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(val binding: RawItemConveyanceBinding) : RecyclerView.ViewHolder(binding.root)
}
