package com.one.cbsl.ui.complain.model
data class PendingInstallResponse(
    val BranchCode: String,
    val Clientid: String,
    val BranchName: String,
    val DeliveryDate: String,
    val DispatchDate: String,
    val MachineModelName: String,
    val MachineNumber: String? = null,
    val MachineStageName: String,
    val PONumber: String,
    val ProjectName: String,
    val ScheduleDate: String,
    val MachineId: String,
    val status: String?=null
){
    override fun toString(): String {
        return MachineNumber.toString()
    }

}