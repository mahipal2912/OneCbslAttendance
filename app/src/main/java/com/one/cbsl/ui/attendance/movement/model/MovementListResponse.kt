package com.one.cbsl.ui.attendance.movement.model

import com.google.gson.annotations.SerializedName

data class MovementListResponse(
    @SerializedName("MovementId") val movementId: String,
    @SerializedName("MovementCode") val movementCode: String,
    @SerializedName("MovementDate") val movementDate: String,
    @SerializedName("FromLocation") val fromLocation: String,
    @SerializedName("ToLocation") val toLocation: String,
    @SerializedName("MovementTime") val MovementTime: String,
    @SerializedName("TaskId") val TaskId: String,
    @SerializedName("TaskName") val TaskName: String,
    @SerializedName("Reason") val Reason: String,
    @SerializedName("Estimate_KM") val Estimate_KM: String = "",
    @SerializedName("CheckoutTime") val CheckoutTime: String,
    @SerializedName("Tourid") val Tourid: String,
    @SerializedName("status") val status: String

) {
    override fun toString(): String {
        return movementCode
    }
}