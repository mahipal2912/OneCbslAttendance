package com.one.cbsl.ui.complain.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.one.cbsl.databinding.RawItemCompleteComplainBinding
import com.one.cbsl.ui.complain.model.GetComplainResponse

class CompleteComplainAdapter(var list: List<GetComplainResponse>, var optionListner: OpitionListener) :
        RecyclerView.Adapter<CompleteComplainAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): ViewHolder {
        val binding =
            RawItemCompleteComplainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        holder.binding.apply {
            tvPoRefNo.text = list[position].PurchaseOrderReferenceNumber
            tvComplainNo.text = list[position].Complain_tNumber
            tvComplainDate.text = list[position].ComplaintDate
            tvComplaintype.text = list[position].ComplaintType_Name
            tvClientCode.text = list[position].ClientCode
            tvClientName.text = list[position].ClientName
            tvMachineNo.text = list[position].Machine_Number
            tvItemName.text = list[position].Item_Name
            tvComplaintDetails.text = list[position].ComplaintDetails
            tvCompleteDate.text = list[position].ResolvedDate
            ivAdd.setOnClickListener {
                optionListner.onItemClick(list[position].imagename.toString())

            }
            tvViewDetails.setOnClickListener {
                optionListner.showComplaintLog(list[position].ComplaintId.toString())

            }
        }


    }


    interface OpitionListener {

        fun onItemClick(url: String)
        fun showComplaintLog(ComplaintId: String)
    }

    override fun getItemCount(): Int {
        return list.size;
    }

    class ViewHolder(val binding: RawItemCompleteComplainBinding) : RecyclerView.ViewHolder(binding.root) {
    }
}