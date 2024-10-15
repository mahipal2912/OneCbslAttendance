package com.one.cbsl.ui.complain.model

import com.google.gson.annotations.SerializedName

class InitialComplainResponse(

    @SerializedName("MachineNumber") val MachineNumber: String? = null,
    @SerializedName("BranchName") val BranchName: String? = null,
    @SerializedName("BranchCode") val BranchCode: String? = null,
    @SerializedName("StateName") val StateName: String? = null,
    @SerializedName("CityName") val CityName: String? = null,
    @SerializedName("WorkOrderReferenceNumber") val PurchaseOrderReferenceNumber: String? = null,
    @SerializedName("WorkOrderId") val PurchaseOrderId: String? = null,
    @SerializedName("ItemName") val ItemName: String? = null,
    @SerializedName("ItemId") val ItemId: String? = null,
    @SerializedName("THName") val THName: String? = null,
    @SerializedName("ASMName") val ASMName: String? = null,
    @SerializedName("SEName") val SEName: String? = null,
    @SerializedName("status") val Status: String? = null

) {
    override fun toString(): String {
        return ItemName.toString()
    }
}
