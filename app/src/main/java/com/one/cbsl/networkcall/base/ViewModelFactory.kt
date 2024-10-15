package com.one.cbsl.networkcall.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.one.cbsl.networkcall.MainRepository
import com.one.cbsl.networkcall.NetworkApiHelper
import com.one.cbsl.ui.attendance.conveyance.viewmodel.ConveyanceViewModel
import com.one.cbsl.ui.attendance.home.viewmodel.HomeViewModel
import com.one.cbsl.ui.attendance.leave.viewmodel.LeaveViewModel
import com.one.cbsl.ui.attendance.movement.viewmodel.MovementViewModel
import com.one.cbsl.ui.attendance.other.viewmodel.LoginViewModel
import com.one.cbsl.ui.attendance.profile.MyProfileViewModel
import com.one.cbsl.ui.attendance.punchattendance.viewmodel.AttendanceViewModel
import com.one.cbsl.ui.complain.viewmodel.ComplaintViewModel

class ViewModelFactory(private val apiHelper: NetworkApiHelper) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                return LoginViewModel(
                    MainRepository(
                        apiHelper
                    )
                ) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                return HomeViewModel(
                    MainRepository(
                        apiHelper
                    )
                ) as T
            }
            modelClass.isAssignableFrom(AttendanceViewModel::class.java) -> {
                return AttendanceViewModel(
                    MainRepository(
                        apiHelper
                    )
                ) as T
            }
            modelClass.isAssignableFrom(MovementViewModel::class.java) -> {
                return MovementViewModel(
                    MainRepository(
                        apiHelper
                    )
                ) as T
            }
            modelClass.isAssignableFrom(ConveyanceViewModel::class.java) -> {
                return ConveyanceViewModel(
                    MainRepository(
                        apiHelper
                    )
                ) as T
            }
            modelClass.isAssignableFrom(LeaveViewModel::class.java) -> {
                return LeaveViewModel(
                    MainRepository(
                        apiHelper
                    )
                ) as T
            }
            modelClass.isAssignableFrom(ComplaintViewModel::class.java) -> {
                return ComplaintViewModel(
                    MainRepository(
                        apiHelper
                    )
                ) as T
            }
            modelClass.isAssignableFrom(MyProfileViewModel::class.java) -> {
                return MyProfileViewModel(
                    MainRepository(
                        apiHelper
                    )
                ) as T
            }

            else -> throw IllegalArgumentException("Unknown class name")
        }
    }
}