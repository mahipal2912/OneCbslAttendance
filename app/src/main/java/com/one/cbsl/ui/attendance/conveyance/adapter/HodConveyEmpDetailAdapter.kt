package com.one.cbsl.ui.attendance.conveyance.adapter

import android.app.Dialog
import android.content.Context
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.one.cbsl.databinding.RawItemHodConveyanceDetailBinding
import com.one.cbsl.ui.attendance.conveyance.model.HodConvEmpDetailResponse
import com.one.cbsl.utils.Constants
import com.one.cbsl.utils.DialogUtils
import com.one.cbsl.utils.SessionManager

class HodConveyEmpDetailAdapter(
    var optionListner: OpitionListener,
    var context: Context,
    var list: List<HodConvEmpDetailResponse>
) :
    RecyclerView.Adapter<HodConveyEmpDetailAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val binding =
            RawItemHodConveyanceDetailBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        try {
            holder.binding.apply {
                if (position == 0) {
                    llHeaderCon.visibility = View.VISIBLE
                }
                tvEmpName.text = list[position].EmployeeName.toString()
                tvVouchers.text = list[position].VoucherNo.toString()
                tvMovementCode.text = list[position].MovementId.toString()
                tvDates.text = list[position].ConveyanceDate.toString()
                tvFromLocations.text = list[position].FromLocation.toString()
                tvToLocations.text = list[position].ToLocation.toString()
                tvFooding.text = list[position].fooding.toString()
                tvTotalexpense.text = list[position].Expense.toString()
                tvKm.text = list[position].KM.toString()
                tvImgUrl.text = list[position].imagename.toString()
                tvTransportModes.text = list[position].TransportMode.toString()
                tvFares.text = list[position].Fare.toString()
                tvRemarks.text = list[position].Remarks.toString()
                tvApproveAmts.text =
                    Editable.Factory.getInstance()
                        .newEditable(list[position].Hod_Approved_amount.toString())
                tvHodRemarkss.text =
                    Editable.Factory.getInstance().newEditable(list[position].HODremarks.toString())


                if (SessionManager.getInstance().getString(Constants.Status) != "Approved") {
                    llHodConveyanceUpdate.visibility = View.VISIBLE
                    llPendingHodHeader.visibility = View.VISIBLE
                    spinSetStatus.adapter = ArrayAdapter(
                        context,
                        android.R.layout.simple_spinner_item,
                        arrayOf("Select..", "Approved", "Rejected", "Hold")
                    )
                }
                var selectedItem: String? = null
                spinSetStatus.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View?,
                            pos: Int,
                            id: Long
                        ) {
                            try {
                                if (pos > 0) {
                                    selectedItem = parent.getItemAtPosition(pos).toString()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        } // to close the onItemSelected


                        override fun onNothingSelected(parent: AdapterView<*>) {

                        }
                    }
                tvImgUrl.setOnClickListener {
                    if (list[position].imagename != "") {
                        optionListner.onImageItemClick(list[position].imagelocation!!)
                    }
                }
                btnSubmit.setOnClickListener {
                    if (!selectedItem?.contains("Select")!!) {
                        selectedItem?.let { it1 ->
                            if (tvApproveAmts.text.toString() != "") {
                                optionListner.onItemClick(
                                    list[position].ConId,
                                    it1,
                                    tvApproveAmts.text.toString(),
                                    tvHodRemarkss.text.toString()
                                )
                            } else {
                                Toast.makeText(
                                    context,
                                    "Enter Approved Amt or Remarks ",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        DialogUtils.showFailedDialog(context, "Select Status")
                    }

                }
            }


        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    interface OpitionListener {

        fun onItemClick(con_id: String, markStatus: String, amt: String, remarks: String);
        fun onImageItemClick(url: String);
    }

    override fun getItemCount(): Int {
        return list.size;
    }

    class ViewHolder(val binding: RawItemHodConveyanceDetailBinding) :
        RecyclerView.ViewHolder(binding.root)

}