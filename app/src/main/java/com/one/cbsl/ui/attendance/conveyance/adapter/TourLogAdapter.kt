package com.one.cbsl.ui.attendance.conveyance.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.one.cbsl.databinding.ItemTourConveyanceLogBinding
import com.one.cbsl.ui.attendance.conveyance.model.MyConveyanceResponse

class TourLogAdapter(var list: List<MyConveyanceResponse>) :
        RecyclerView.Adapter<TourLogAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): ViewHolder {
        val binding =
            ItemTourConveyanceLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.apply {
            if (position == 0) {
              llHeader.visibility = View.VISIBLE
            }
            tvDate.text = list[position].ConveyanceDate
            tvFare.text = list[position].Fare
            tvFromLocation.text = list[position].FromLocation
            tvToLocation.text = list[position].ToLocation
            tvTransportMode.text = list[position].TransportMode
            tvVoucher.text = list[position].VoucherNo
            tvApproveStatus.text = list[position].HOD_Approval_Status
            tvFooding.text = list[position].fooding
            tvOther.text = list[position].OtherCharge
            tvHotel.text = list[position].LodgingCharge
            tvApproveExp.text = list[position].Approved_amount
            tvStatus.text = list[position].PaidStatus
            tvNeftNo.text = list[position].NEFTNo
            tvTotalExp.text = list[position].TotalExpense
          }
    }

    override fun getItemCount(): Int {
        return list.size;
    }

    class ViewHolder(val binding: ItemTourConveyanceLogBinding) : RecyclerView.ViewHolder(binding.root) {

    }
}