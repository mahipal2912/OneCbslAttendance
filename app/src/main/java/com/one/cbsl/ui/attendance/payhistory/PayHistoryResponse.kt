package com.one.cbsl.ui.attendance.payhistory

import com.google.gson.annotations.SerializedName

data class PayHistoryResponse(
    @SerializedName("EmployeeCode") val employeeCode: String,
    @SerializedName("EmployeeName") val employeeName: String,
    @SerializedName("A_C_NAME") val accountName: String,
    @SerializedName("A_C_NO") val accountNumber: String,
    @SerializedName("TransactionID") val transactionId: String,
    @SerializedName("TransactionDate") val transactionDate: String, // We use String here, can be converted to LocalDateTime
    @SerializedName("CreditedAmount") val creditedAmount: String,
    @SerializedName("Remarks") val remarks: String,
    @SerializedName("Status") val status: String
)
