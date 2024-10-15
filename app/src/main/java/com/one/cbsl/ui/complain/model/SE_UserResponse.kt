package com.one.cbsl.ui.complain.model

import com.google.gson.annotations.SerializedName

class SE_UserResponse(

    @SerializedName("NickName") val NickName: String? = null,
    @SerializedName("CRMUsersId") val CRMUsersId: String? = null,
    @SerializedName("Email") val Email: String? = null
)
{
    override fun toString(): String {
        return NickName!!
    }
}