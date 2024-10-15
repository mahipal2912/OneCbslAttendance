package com.one.cbsl.ui.attendance.conveyance.model

data class GetTourResponse(
    val Approved_amount: String,
    val BoardingCharge: String,
    val FromDate: String,
    val HOD_Approval_Status: String,
    val HOD_Approved_Date: String,
    val HODremarks: String,
    val LoadingCharge: String,
    val NEFTNo: String,
    val OtherCharge: String,
    val PaidStatus: String,
    val ToDate: String,
    val Total_amount: String,
    val TourId: String,
    val TransportCharge: String,
    val bankname: String,
    val projectname: String
)
