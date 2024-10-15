package com.one.cbsl.ui.attendance.movement.model

import com.google.gson.annotations.SerializedName

data class TaskResponse(
    var TaskId: String? = null,
    var TaskName: String? = null,
    var status: String? = null,
    val LocationAddress: String, val tourid: String


) {
    override fun toString(): String {
        return TaskName!!
    }

}