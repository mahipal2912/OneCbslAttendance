package com.one.cbsl.utils

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfWriter
import com.one.cbsl.utils.Utils.getBitmap
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.net.MalformedURLException
import java.text.SimpleDateFormat
import java.util.*

class Constants {
    companion object {
        const val AUTH_HEADER = "MJKjkKJHbZzTLxenX2E5xVbEN/wDpMiIsruYCKbsR5TytsDvQ75dncMJxXb3mnDo"
        const val TYPE_HOD = "3"
        const val TYPE_ALL = "2"
        const val TYPE_ADMIN = "1"
        const val Attendance = "Attendance"
        const val Movement = "Movement"
        const val KEY_FOREGROUND_ENABLED = "KEY_FOREGROUND_ENABLED"
        const val Conveyance = "Conveyance"
        const val LeavePlan = "Leave Plan"
        const val Complaint = "Complaint"
        const val Voucher = "Voucher"
        const val ApprovalHod = "Conveyance HOD"
        const val ApprovalHead = "Conveyance HEAD"
        const val FacilityId = "FacilityId"
        const val Status = "Status"
        const val FromDate = "FromDate"
        const val ToDate = "ToDate"
        const val RegisterComplaint = "New Complaint"
        const val PendingComplaint = "Complaint Pending"
        const val PendingPmr = "Pending PMR"
        const val PendingInstallation = "Install Pending"
        const val CloseComplaint = "Close Complaint"
        const val COMPLAINT_USERID = "COMPLAINT_USERID"
        const val COMPANY = "COMPANY"
        const val DEVICE_ID = "DEVICE_ID"
        const val GROUP_ID = "GROUP_ID"
        const val IsTourActive = "IsTourActive"
        const val isPunchIn = "isPunchIn"
        const val UserName = "UserName"
        const val IMAGE = "iamge"
        const val UserTypeID = "UserTypeId"
        const val UserId = "UserId"
        const val EmpCode = "EmpCode"
        const val Email = "Email"
        const val Mobile = "Mobile"
        const val isLogin = "isLogin"
        const val isLoginByDevice = "isLoginByDevice"


        val headList = arrayOf("CBSPL", "CBMPL", "CRCPL", "CSSPL")
        val monthList = arrayOf(
            "January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December"
        )
        val voucherList = arrayOf(
            "Mobile Expenses", "Printout & Zerox",
            "Postage & Courier Expenses", "Machines repair & Maintenance Expenses", "Fright Charges"
        )

        fun getTodayData(): String {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            return "" + (month + 1) + "-" + day + "-" + year

        }

        fun getCurrentDate(): String {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            return "$year/${month + 1}/$day"


        }

        fun getOneMonthBeforeDate(): String {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            return "$year/$month/$day"


        }


        fun getDateWithTime(): String {
            val currentDateTime = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("MM-dd-yyyy HH:mm", Locale.getDefault())
            return dateFormat.format(currentDateTime.time)

        }

        fun getDateSelection(context: Context, callback: (String) -> Unit) {
            try {
                val cal = Calendar.getInstance()
                val dpd = DatePickerDialog(
                    context,
                    { _, year, monthOfYear, dayOfMonth ->
                        val selectedDate = "" + (monthOfYear + 1) + "-" + dayOfMonth + "-" + year
                        callback(selectedDate)
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                )
                dpd.datePicker.maxDate = System.currentTimeMillis()
                dpd.show()
            } catch (e: Exception) {
                e.printStackTrace()
                callback("")
            }
        }

        fun getPostDateSelection(context: Context, callback: (String) -> Unit) {
            try {
                val cal = Calendar.getInstance()
                val dpd = DatePickerDialog(
                    context,
                    { _, year, monthOfYear, dayOfMonth ->
                        val selectedDate = "" + (monthOfYear + 1) + "-" + dayOfMonth + "-" + year
                        callback(selectedDate)
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                )
                dpd.datePicker.minDate = System.currentTimeMillis()
                dpd.show()
            } catch (e: Exception) {
                e.printStackTrace()
                callback("")
            }
        }

        internal fun generatePdf(path: ArrayList<Uri>): String {
            val document = Document()

            val directoryPath = Cbsl.getInstance().externalCacheDir!!.path
            val pathPdf = "$directoryPath/${System.currentTimeMillis()}.pdf"
            try {
                PdfWriter.getInstance(
                    document,
                    FileOutputStream(pathPdf)
                )
            } catch (e: DocumentException) {
                e.printStackTrace()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

            document.open()

            var image: Image? = null

            try {
                for (i in path.indices) {
                    val reducedSelfieSizeBitmap = getBitmap(path[i])
                    val stream = ByteArrayOutputStream()
                    reducedSelfieSizeBitmap!!.compress(Bitmap.CompressFormat.JPEG, 50, stream)
                    val byteFormat = stream.toByteArray()
                    image = Image.getInstance(byteFormat)
                    image!!.scaleToFit(PageSize.A4.width, PageSize.A4.height)
                    image.alignment = Element.ALIGN_CENTER
                    document.add(image)
                }
            } catch (e: DocumentException) {
                e.printStackTrace()
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            document.close()


            return pathPdf

        }

    }
}