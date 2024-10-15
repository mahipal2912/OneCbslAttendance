package com.one.cbsl.ui.attendance.conveyance.adapter


import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.one.cbsl.databinding.RawItemVoucherBinding
import com.one.cbsl.ui.attendance.conveyance.model.MyConveyanceResponse


class VoucherAdapter(
    var list: List<MyConveyanceResponse>,
    private var opitionListener: OpitionListener
) :
    RecyclerView.Adapter<VoucherAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            RawItemVoucherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.apply {
            if (position == 0) {
                llHeader.visibility = View.VISIBLE
            }
            tvDate.text = list[position].ConveyanceDate
            tvVoucherName.text = list[position].VoucherName
            tvVoucher.text = list[position].VoucherNo
            tvFare.text = list[position].Fare
            tvRemarks.text = list[position].Remarks
            tvHodRemarks.text = list[position].HodRemarks
            tvApproveStatus.text = list[position].Status
            tvImgUrl.text = list[position].imagename
            tvApproveAmt.text = list[position].Approved_amount
            tvHodApprovalStatus.text = list[position].HOD_Approval_Status
            tvPaidStatus.text = list[position].PaidStatus
            tvApprovedDate.text = list[position].HOD_Approved_Date
            tvNeftNo.text = list[position].NEFTNo
            var approval_status: String? = null
            approval_status = list[position].Status
            if (approval_status != null) {
                if (approval_status == "Rejected") {
                    tvApproveStatus.setTextColor(Color.parseColor("#F44336"))
                }
                if (approval_status == "Approved") {
                    tvApproveStatus.setTextColor(Color.parseColor("#4CAF50"))
                }
                if (approval_status == "Hold") {
                    tvApproveStatus.setTextColor(Color.parseColor("#FF9800"))
                }
                tvApproveStatus.text = approval_status
            }
            tvImgUrl.setOnClickListener {
                if (list[position].imagelocation != "") {
                    opitionListener.onItemClick(list[position].imagelocation!!)
                }
            }
        }
    }


    interface OpitionListener {
        fun onItemClick(item: String)
    }

    override fun getItemCount(): Int {
        return list.size;
    }

    class ViewHolder(val binding: RawItemVoucherBinding) : RecyclerView.ViewHolder(binding.root) {
    }
}