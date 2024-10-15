package com.one.cbsl.ui.complain.model

import com.google.gson.annotations.SerializedName

class BranchResponse(

    @SerializedName("ClientCode") val BranchId: String? = null,
    @SerializedName("ClientName") val branch_name: String? = null,
    @SerializedName("status") val Status: String? = null
) {
    override fun toString(): String {
        return branch_name!!
    }

}