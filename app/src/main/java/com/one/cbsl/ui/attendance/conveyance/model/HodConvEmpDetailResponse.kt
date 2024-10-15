package com.one.cbsl.ui.attendance.conveyance.model

import com.google.gson.annotations.SerializedName

data class HodConvEmpDetailResponse(

//Employee
        @SerializedName("Id") val ConId: String,
        @SerializedName("EmployeeName") val EmployeeName: String?=null,
        @SerializedName("ApprovedAmount") val ApprovedAmount: String?="",
        @SerializedName("VoucherNo") val VoucherNo: String?=null,
        @SerializedName("MovementId") val MovementId: String?=null,
        @SerializedName("Fare") val Fare: String?=null,
        @SerializedName("Remarks") val Remarks: String?=null,
        @SerializedName("HODremarks") val HODremarks: String?="",
        @SerializedName("ConveyanceDate") val ConveyanceDate: String?=null,
        @SerializedName("FromLocation") val FromLocation: String?=null,
        @SerializedName("ToLocation") val ToLocation: String?=null,
        @SerializedName("TotalExpense") val Expense: String?=null,
        @SerializedName("Fooding") val fooding: String?=null,
        @SerializedName("KM") val KM: String?=null,
        @SerializedName("imagelocation") val imagelocation: String?=null,
        @SerializedName("imagename") val imagename: String?=null,
        @SerializedName("Approved_amount") val Hod_Approved_amount: String?="",
        @SerializedName("TransportMode") val TransportMode: String?=null,
        @SerializedName("status") val Status: String?=null



)
