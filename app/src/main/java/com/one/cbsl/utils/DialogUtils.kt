package com.one.cbsl.utils

import android.content.Context
import android.content.Intent
import com.marsad.stylishdialogs.StylishAlertDialog

class DialogUtils {
    companion object {
        @Volatile
        private var dialogInstance: StylishAlertDialog? = null

        private fun getInstance(context: Context, type: Int): StylishAlertDialog {
            dialogInstance?.dismissWithAnimation()  // Dismiss existing dialog instance if it exists
            return StylishAlertDialog(context, type).also {
                dialogInstance = it
            }
        }
        public fun showProgressDialog(context: Context, message: String): StylishAlertDialog {
            val pDialog = getInstance(context, StylishAlertDialog.PROGRESS)
            pDialog.setContentText(message)
                .show()
            pDialog.cancelledOnTouchOutside = false
            pDialog.setDismissOnClick(false)
            return pDialog
        }

        fun showSuccessDialog(context: Context, message: String, activityToStart: Class<*>) {
            val pDialog = getInstance(context, StylishAlertDialog.SUCCESS)
            pDialog.contentText = message
            pDialog.setConfirmButton("OK") { dialog ->
                dialog.dismissWithAnimation()
                dialogInstance = null // Reset the instance after dismissing
                context.startActivity(Intent(context, activityToStart))

            }
            pDialog.show()
        }

        fun showFailedDialog(context: Context, message: String) {
            val pDialog = getInstance(context, StylishAlertDialog.ERROR)
            pDialog.contentText = message
            pDialog.setConfirmButton("OK") { dialog ->
                dialog.dismissWithAnimation()
                dialogInstance = null // Reset the instance after dismissing
            }
            pDialog.show()
        }

        fun dismissDialog() {

            dialogInstance?.dismissWithAnimation()
            dialogInstance = null
        }
    }
}