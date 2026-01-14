package com.one.cbsl.ui.attendance.conveyance.model

import com.google.gson.annotations.SerializedName

data class HodConveyanceResponse(
    @SerializedName("TotalApproved") val Total_Approved: String?,
    @SerializedName("TotalApproval Pending") val TotalApprovalPending: String,
    @SerializedName("TotalHold") val TotalHold: String,
    @SerializedName("TotalRejected") val TotalRejected: String,
    @SerializedName("ApprovedAmount") val ApprovedAmount: String,
    @SerializedName("ApprovalAmount") val PendingApprovalAmount: String,
    @SerializedName("HoldAmount") val HoldAmount: String,
    @SerializedName("RejectedAmount") val RejectedAmount: String,
    @SerializedName("TotalPending") val TotalPending: String,
    //facility
    @SerializedName("FacilityId") val FacilityId: String,
    @SerializedName("FacilityName") val FacilityName: String,
    @SerializedName("Facility Name") val fName: String,
    @SerializedName("Approved") val Approved: String,
    @SerializedName("ApprovalPending") val ApprovalPending: String,
    @SerializedName("Hold") var Hold: String? = null,
    @SerializedName("Rejected") val Rejected: String? = null,
    @SerializedName("fromDate") val fromDate: String? = null,
    @SerializedName("toDate") val toDate: String? = null,

    //Employee
    @SerializedName("UserId") val UserId: String,
    @SerializedName("EmployeeName") val EmployeeName: String,
    @SerializedName("TotalCount") val TotalCount: String,
    @SerializedName("TotalAmmount") val TotalAmmount: String,

    //Employee
    @SerializedName("Total") val total: String,
    @SerializedName("Absent") val Absent: String,
    @SerializedName("Present") val Present: String,


    //conveyanceDetail
    @SerializedName("status") val Status: String? = null


)
