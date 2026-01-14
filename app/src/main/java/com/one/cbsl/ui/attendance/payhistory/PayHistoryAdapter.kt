package com.one.cbsl.ui.attendance.payhistory


import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.one.cbsl.databinding.RawItemVoucherBinding
import com.one.cbsl.databinding.RawPayHistoryBinding
import com.one.cbsl.ui.attendance.conveyance.model.MyConveyanceResponse


class PayHistoryAdapter(
    var list: List<PayHistoryResponse>,
    private var opitionListener: OpitionListener
) :
    RecyclerView.Adapter<PayHistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            RawPayHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.apply {
            if (position == 0) {
                llHeader.visibility = View.VISIBLE
            }
            id1.text = list[position].employeeCode
            id2.text = list[position].employeeName
            id3.text = list[position].accountName
            id4.text = list[position].accountNumber
            id5.text = list[position].transactionId
            id6.text = list[position].transactionDate
            id7.text = list[position].creditedAmount
            id8.text = list[position].remarks


        }
    }


    interface OpitionListener {
        fun onItemClick(item: String)
    }

    override fun getItemCount(): Int {
        return list.size;
    }

    class ViewHolder(val binding: RawPayHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
    }
}