import com.google.gson.annotations.SerializedName

data class MovementResponse(

    @SerializedName("MovementId") val movementId: String,
    @SerializedName("MovementCode") val movementCode: String,
    @SerializedName("MovementDate") val movementDate: String,
    @SerializedName("FromLocation") val fromLocation: String,
    @SerializedName("fooding") val fooding: String,
    @SerializedName("TotalExpense") val TotalExpense: String,
    @SerializedName("taskname") val taskname: String,
    @SerializedName("ToLocation") val toLocation: String,
    @SerializedName("LB_Claim") val LB_Clain: String,
    @SerializedName("Remarks") val remark: String,
    @SerializedName("ComplaintNumber") val ComplaintNumber: String,
    @SerializedName("MachineNumber") val MachineNumber: String,
    @SerializedName("ClientName") val ClientName: String,
    @SerializedName("ClientPlaceName") val ClientPlaceName: String,
    @SerializedName("complainttype") val complainttype: String,


) {
    override fun toString(): String {
        return movementCode
    }
}