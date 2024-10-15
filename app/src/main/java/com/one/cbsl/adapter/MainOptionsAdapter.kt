package com.one.cbsl.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.one.cbsl.R
import com.one.cbsl.databinding.ItemMenuOptionsBinding
import com.one.cbsl.utils.Constants


class MainOptionsAdapter(val optionListener: OptionListener) :
    RecyclerView.Adapter<MainOptionsAdapter.ViewHolder>() {

    private val icons: IntArray = intArrayOf(
        R.drawable.ic_attendance,
        R.drawable.ic_movment,
        R.drawable.ic_conveyance,
        R.drawable.ic_leave,
        R.drawable.ic_voucher,
        R.drawable.ic_head,
        R.drawable.ic_hod
    )

    private var titles: Array<String> = arrayOf(
        Constants.Attendance,
        Constants.Movement,
        Constants.Conveyance,
        Constants.LeavePlan,
        Constants.Voucher,
        Constants.ApprovalHead,
        Constants.ApprovalHod
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMenuOptionsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            ivIcon.setImageResource(icons[position])
            tvTitle.text = titles[position]
            root.setOnClickListener { optionListener.onItemClick(titles[position]) }
        }
    }

    interface OptionListener {
        fun onItemClick(item: String)
    }

    override fun getItemCount(): Int {
        return titles.size
    }

    class ViewHolder(val binding: ItemMenuOptionsBinding) : RecyclerView.ViewHolder(binding.root)
}
