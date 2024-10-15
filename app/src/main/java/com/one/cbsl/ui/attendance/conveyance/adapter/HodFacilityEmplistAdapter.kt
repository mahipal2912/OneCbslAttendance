package com.one.cbsl.ui.attendance.conveyance.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.one.cbsl.databinding.RawItemHodEmpConveyanceBinding
import com.one.cbsl.ui.attendance.conveyance.model.HodConveyanceEmpResponse
import com.one.cbsl.utils.SessionManager

class HodFacilityEmplistAdapter(
    var context: Context,
    var optionlistner: OpitionListener,
    var list: List<HodConveyanceEmpResponse>
) :
    RecyclerView.Adapter<HodFacilityEmplistAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            RawItemHodEmpConveyanceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {

            if (position == 0) {
                cvEmpHeader.visibility = View.VISIBLE
            }
            tvEmpName.text = list[position].EmployeeName
            tvVoucherTotal.text = list[position].TotalCount
            tvTotalAmt.text = list[position].TotalAmmount

            tvEmpName.setOnClickListener {
                SessionManager.getInstance().putString("EmpId", list[position].UserId)
                optionlistner.onItemClick(list[position].UserId)
            }

        }
    }


    interface OpitionListener {

        fun onItemClick(userid: String);
    }

    override fun getItemCount(): Int {
        return list.size;
    }

    class ViewHolder(val binding: RawItemHodEmpConveyanceBinding) :
        RecyclerView.ViewHolder(binding.root)
}