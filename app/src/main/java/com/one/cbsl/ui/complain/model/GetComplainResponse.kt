package com.one.cbsl.ui.complain.model

import com.google.gson.annotations.SerializedName

class GetComplainResponse(

    @SerializedName("MachineNumber") val Machine_Number: String? = null,
    @SerializedName("WorkOrderReferenceNumber") val PurchaseOrderReferenceNumber: String? = null,
    @SerializedName("ItemName") val Item_Name: String? = null,
    @SerializedName("status") val Status: String? = null,
    @SerializedName("WorkOrderId") val WorkOrderId: String? = null,
    @SerializedName("ComplaintDetails") val ComplaintDetails: String? = null,
    @SerializedName("ComplaintNumber") val Complain_tNumber: String? = null,
    @SerializedName("ComplaintDate") val ComplaintDate: String? = null,
    @SerializedName("ComplaintTypeName") val ComplaintType_Name: String? = null,
    @SerializedName("ComplaintId") val ComplaintId: String? = null,
    @SerializedName("ClientName") val ClientName: String? = null,
    @SerializedName("BranchCode") val BranchCode: String? = null,
    @SerializedName("ClientCode") val ClientCode: String? = null,
    @SerializedName("imagename") val imagename: String? = null,
    @SerializedName("ResolvedDate") val ResolvedDate: String? = null,
    @SerializedName("Remarks") val Remarks: String? = null,
    @SerializedName("ComplaintStatus") val ComplaintStatus: String? = null,
    @SerializedName("BranchContactNumber") val BranchContactNumber: String? = null

)
{
    override fun toString(): String {
        return Complain_tNumber.toString()
    }

}