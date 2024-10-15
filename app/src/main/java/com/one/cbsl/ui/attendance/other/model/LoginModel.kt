package com.one.cbsl.ui.attendance.other.model

import com.google.gson.annotations.SerializedName

data class LoginModel(
    @SerializedName("UserId") val userId : String,
    @SerializedName("EmployeeCode") val employeeCode : String,
    @SerializedName("UserTypeId") val userTypeId : String,
    @SerializedName("Password") val password : String,
    @SerializedName("EmployeeName") val employeeName : String,
    @SerializedName("EmployeeType") val employeeType : String,
    @SerializedName("companyName") val companyName : String,
    @SerializedName("MobileNo") val mobileNo : String,
    @SerializedName("loginStatus") val loginStatus : String,
    @SerializedName("status") val status : String?=null,
    @SerializedName("loginStatus1") val loginStatus1 : String)
