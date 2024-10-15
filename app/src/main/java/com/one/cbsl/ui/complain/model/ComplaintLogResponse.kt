package com.one.cbsl.ui.complain.model

data class ComplaintLogResponse(
    val AssignTo: String,
    val CBy: String,
    val CDate: String,
    val CourierName: String,
    val MachineNumber: String,
    val PendingReason: String,
    val PodNumber: String,
    val Remarks: String
)