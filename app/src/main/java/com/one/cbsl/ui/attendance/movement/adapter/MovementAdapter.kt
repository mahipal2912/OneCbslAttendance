package com.one.cbsl.ui.attendance.movement.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.one.cbsl.databinding.RawItemMovementBinding
import com.one.cbsl.ui.attendance.movement.model.MovementListResponse
import com.one.cbsl.utils.DialogUtils


class MovementAdapter(
    var list: List<MovementListResponse>,
    private var optionListner: OpitionListener
) :
    RecyclerView.Adapter<MovementAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            RawItemMovementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        try {
            if (position == 0) {
                holder.llHeader.visibility = View.VISIBLE
            }
            holder.tvTaskName.text = list[position].TaskName
            holder.tvDate.text = list[position].movementDate
            holder.tvMovementTime.text = list[position].MovementTime
            holder.tvMovementCheckout.text = list[position].CheckoutTime
            holder.tvFromLocation.text = list[position].fromLocation
            holder.tvTourCode.text = list[position].Tourid
            holder.tvToLocation.text = list[position].toLocation
            holder.tvEstDistance.text = list[position].Estimate_KM + " KM"
            holder.tvMovementCode.text = list[position].movementCode
            holder.itemView.setOnClickListener {
                optionListner.onItemClick(
                    list[position].movementCode,
                    list[position].fromLocation,
                    list[position].Reason,
                    list[position].TaskName,
                    list[position].CheckoutTime
                )

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    interface OpitionListener {

        fun onItemClick(
            movementCode: String?,
            location: String?,
            reason: String?,
            task: String?,
            checkOut: String?
        );
    }

    override fun getItemCount(): Int {
        return list.size;
    }

    class ViewHolder(itemView: RawItemMovementBinding) : RecyclerView.ViewHolder(itemView.root) {
        var tvDate = itemView.tvDate
        var tvMovementTime = itemView.tvMovementTime
        var tvMovementCheckout = itemView.tvMovementCheckout
        var tvFromLocation = itemView.tvFromLocation
        var tvToLocation = itemView.tvToLocation
        var tvTaskName = itemView.tvTaskName
        var tvEstDistance = itemView.tvEstDistance
        var tvTourCode = itemView.tvTourCode
        var tvMovementCode = itemView.tvMovementCode
        var llHeader = itemView.llHeader

    }
}