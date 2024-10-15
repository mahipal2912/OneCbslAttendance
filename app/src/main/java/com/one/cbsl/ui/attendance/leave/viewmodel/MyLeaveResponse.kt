import com.google.gson.annotations.SerializedName

data class MyLeaveResponse(

    @SerializedName("Id") val id: String?,
    @SerializedName("EmpId") val UserId: String?,
    @SerializedName("EmployeeName") val EmployeeName: String,
    @SerializedName("FromDate") val FromDate: String,
    @SerializedName("ToDate") val ToDate: String,
    @SerializedName("Reason") val Reason: String,
    @SerializedName("status") val status: String?=null,
    @SerializedName("ApprovedBy") val ApprovedBy: String?=null,
    @SerializedName("LeaveApprovalStatus") val ApprovedStatus: String?=null,
    @SerializedName("leaveType") val LeaveType: String?=null,
    @SerializedName("Leavemarkby") val Leavemarkby: String?=null


)