package com.one.cbsl.ui.attendance.conveyance.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.one.cbsl.databinding.ItemTourConveyanceBinding
import com.one.cbsl.ui.attendance.conveyance.model.GetTourResponse

class TourConveyanceAdapter(
    var list: List<GetTourResponse>,
    private var opitionListener: OpitionListener
) :
    RecyclerView.Adapter<TourConveyanceAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ItemTourConveyanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.apply {
            if (position == 0) {
                llHeader.visibility = View.VISIBLE
            }
            tvDate.text = list[position].FromDate
            tvToDate.text = list[position].ToDate
            tvFare.text = list[position].TransportCharge
            tvLodgingChare.text = list[position].LoadingCharge
            tvBoardingCharge.text = list[position].BoardingCharge
            tvOtherCharge.text = list[position].OtherCharge
            tvVoucher.text = list[position].TourId
            tvHodRemarks.text = list[position].HODremarks
            tvTotal.text = list[position].Total_amount
            tvApproveAmt.text = list[position].Approved_amount
        }

        holder.binding.tvVoucher.setOnClickListener {
            opitionListener.onTourIdClick(list[position].TourId)
        }
    }


    interface OpitionListener {
        fun onTourIdClick(tourid: String)
    }

    override fun getItemCount(): Int {
        return list.size;
    }

    class ViewHolder(val binding: ItemTourConveyanceBinding) : RecyclerView.ViewHolder(binding.root)
}