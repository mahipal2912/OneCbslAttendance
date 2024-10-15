package com.one.cbsl.ui.complain.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.one.cbsl.databinding.RawItemPendingInstallBinding
import com.one.cbsl.ui.complain.model.PendingInstallResponse


class PendingInstallationAdapter(
    var list: List<PendingInstallResponse>, var optionListner: OpitionListener
) :
    RecyclerView.Adapter<PendingInstallationAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            RawItemPendingInstallBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        holder.binding.apply {
            if (position == 0) {
                llHeader.visibility = View.VISIBLE
            }
            tvMachineNumber.text = list[position].MachineNumber
            tvBranchName.text = list[position].BranchName
            tvBranchCode.text = list[position].BranchCode
            tvProjectName.text = list[position].ProjectName
            tvMachineStage.text = list[position].MachineStageName
            tvPoNumber.text = list[position].PONumber
            tvMachineModelName.text = list[position].MachineModelName
            tvDispatchDate.text = list[position].DispatchDate
            tvDeliveryDate.text = list[position].DeliveryDate
            tvScheduleDate.text = list[position].ScheduleDate
            ivAdd.setOnClickListener {
                optionListner.onItemClick(
                    list[position].MachineNumber.toString(),
                    list[position].MachineId,
                    list[position].Clientid,
                    list[position].BranchName
                )
            }
        }


    }


    interface OpitionListener {

        fun onItemClick(machineNo: String, machineId: String, clientId: String, branchname: String)
    }

    override fun getItemCount(): Int {
        return list.size;
    }

    class ViewHolder(val binding: RawItemPendingInstallBinding) :
        RecyclerView.ViewHolder(binding.root)

}