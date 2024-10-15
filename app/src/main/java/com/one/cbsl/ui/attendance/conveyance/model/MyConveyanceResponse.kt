package com.one.cbsl.ui.attendance.conveyance.model

import com.google.gson.annotations.SerializedName

data class MyConveyanceResponse(

    @SerializedName("ConveyanceDate") val ConveyanceDate: String,
    @SerializedName("TransportMode") val TransportMode: String,
    @SerializedName("VoucherNo") val VoucherNo: String,
    @SerializedName("Fare") val Fare: String,
    @SerializedName("Remarks") val Remarks: String,
    @SerializedName("FromLocation") val FromLocation: String,
    @SerializedName("fooding") val fooding: String,
    @SerializedName("VoucherName") val VoucherName: String,
    @SerializedName("LodgingCharge") val LodgingCharge: String,
    @SerializedName("OtherCharge") val OtherCharge: String,
    @SerializedName("imagelocation") val imagelocation: String?=null,
    @SerializedName("TotalExpense") val TotalExpense: String?=null,
    @SerializedName("imagename") val imagename: String?=null,
    @SerializedName("ToLocation") val ToLocation: String,
    @SerializedName("Approval_Status") val Status: String?=null,
    @SerializedName("HOD_Approval_Status") val HOD_Approval_Status: String?=null,
    @SerializedName("Approved_amount") val Approved_amount: String?=null,
    @SerializedName("PaidStatus") val PaidStatus: String?=null,
    @SerializedName("HOD_Approved_Date") val HOD_Approved_Date: String?=null,
    @SerializedName("NEFTNo") val NEFTNo: String?=null,
    @SerializedName("HODremarks") val HodRemarks: String?=null
)
