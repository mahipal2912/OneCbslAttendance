package com.one.cbsl.ui.attendance.conveyance.model

import com.google.gson.annotations.SerializedName

data class HodConveyanceEmpResponse(
//Employee
        @SerializedName("UserId") val UserId: String,
        @SerializedName("EmployeeName") val EmployeeName: String,
        @SerializedName("TotalCount") val TotalCount: String,
        @SerializedName("TotalAmmount") val TotalAmmount: String,
        @SerializedName("status") val Status: String
)
