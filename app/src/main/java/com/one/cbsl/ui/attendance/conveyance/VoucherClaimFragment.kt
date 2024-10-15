package com.one.cbsl.ui.attendance.conveyance

import SaveResponse
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.util.Base64
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.one.cbsl.CbslMain
import com.one.cbsl.MainActivityListener
import com.one.cbsl.R
import com.one.cbsl.adapter.SelectImageAdapter
import com.one.cbsl.databinding.FragmentClaimVoucherBinding
import com.one.cbsl.databinding.RawImageSelectionDialogBinding
import com.one.cbsl.networkcall.NetworkApiHelper
import com.one.cbsl.networkcall.RetrofitBuilder
import com.one.cbsl.networkcall.base.ViewModelFactory
import com.one.cbsl.ui.attendance.conveyance.viewmodel.ConveyanceViewModel
import com.one.cbsl.utils.Constants
import com.one.cbsl.utils.DialogUtils
import com.one.cbsl.utils.SessionManager
import com.one.cbsl.utils.Utils
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.*
import java.util.*


class VoucherClaimFragment : Fragment() {

    companion object {
        fun newInstance() = VoucherClaimFragment()
    }

    var voucheType: String? = ""
    var purposeExpense: String? = ""
    var headConveyance: String? = ""
    private var _binding: FragmentClaimVoucherBinding? = null
    private lateinit var viewModel: ConveyanceViewModel

    private val binding get() = _binding!!
    var imagesPathList: ArrayList<Uri> = arrayListOf()
    private var pdfName = ""
    private var photoUri: Uri? = null

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { galleryUri ->
            try {
                imagesPathList.clear()
                pdfName = System.currentTimeMillis().toString() + ".pdf"
                galleryUri?.let {
                    for (uri in galleryUri) {
                        val imageUri: Uri = uri
                        imagesPathList.add(imageUri)
                    }
                    binding.rvImage.adapter = SelectImageAdapter(imagesPathList)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }


    //TODO capture the image using camera and display it
    private var cameraActivityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(), ActivityResultCallback {
                if (it.resultCode === Activity.RESULT_OK) {
                    pdfName = System.currentTimeMillis().toString() + ".pdf"
                    imagesPathList.add(photoUri!!)
                    binding.rvImage.adapter = SelectImageAdapter(imagesPathList)

                }
            })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(
                this, ViewModelFactory(NetworkApiHelper(RetrofitBuilder.apiService))
            )[ConveyanceViewModel::class.java]

        _binding = FragmentClaimVoucherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.spinMonth.adapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            Constants.monthList
        )
        binding.spinVoucher.adapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            Constants.voucherList
        )
        binding.spinHead.adapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            Constants.headList
        )

        binding.spinMonth.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    pos: Int,
                    id: Long
                ) {
                    try {
                        purposeExpense = parent.getItemAtPosition(pos).toString()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                } // to close the onItemSelected


                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }
        binding.spinVoucher.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    try {
                        val result = parent.getItemAtPosition(position).toString()
                        voucheType = result
                        if (result.contains("Other") || result.contains("Purchase") || result.contains(
                                "Courier"
                            )
                        ) {
                            binding.etOther.visibility = View.VISIBLE
                            binding.llPeriod.visibility = View.GONE
                            binding.llMonthPeriod.visibility = View.GONE
                        } else if (result.contains("Zerox") || result.contains("Print")) {
                            binding.llPeriod.visibility = View.GONE
                            binding.llMonthPeriod.visibility = View.VISIBLE
                            binding.etOther.visibility = View.GONE
                        } else {
                            binding.llPeriod.visibility = View.VISIBLE
                            binding.llMonthPeriod.visibility = View.GONE
                            binding.etOther.visibility = View.GONE
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                } // to close the onItemSelected

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }
        binding.spinHead.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    try {
                        headConveyance = parent.getItemAtPosition(position).toString()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                } // to close the onItemSelected

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }
        binding.ivImage.setOnClickListener {

            val dialog = Dialog(requireContext())
            val customDialogLayoutBinding: RawImageSelectionDialogBinding =
                RawImageSelectionDialogBinding.inflate(layoutInflater)
            dialog.setContentView(customDialogLayoutBinding.root)
            dialog.show()
            customDialogLayoutBinding.tvCamera.setOnClickListener {
                imagesPathList.clear()
                openCamera()
                dialog.dismiss()
            }
            customDialogLayoutBinding.tvGallery.setOnClickListener {
                galleryLauncher.launch("image/*")
                dialog.dismiss()
            }
            customDialogLayoutBinding.tvCancel.setOnClickListener {
                dialog.dismiss()
            }
        }

        binding.btnSaveVoucher.setOnClickListener {

            when {
                TextUtils.isEmpty(binding.etAmt.text.toString()) -> {
                    showToast("Enter any amount ")
                }
                TextUtils.isEmpty(binding.etRemark.text.toString()) -> {
                    showToast("Enter a Remark")
                }
                (voucheType!!.contains("Other") || voucheType!!.contains("Purchase") || voucheType!!.contains(
                    "Courier"
                )) && TextUtils.isEmpty(binding.etOther.text.toString())
                -> {
                    showToast("Enter Item Name")
                }
                (voucheType!!.contains("Zerox") || voucheType!!.contains("Printout")) && purposeExpense == "Select Month" -> {
                    showToast("Select Month")
                }
                (voucheType!!.contains("Mobile")) && TextUtils.isEmpty(binding.etSelectFrom.text.toString()) && TextUtils.isEmpty(
                    binding.etSelectTo.text.toString()
                ) -> {
                    showToast("Select From to Date")
                }
                else -> {
                    if (imagesPathList.size > 0) {
                        DialogUtils.showProgressDialog(requireActivity(), "Processing..")
                        saveConveyance(
                            Base64.encodeToString(
                                Utils.convertPDFToByteArray(
                                    Constants.generatePdf(
                                        imagesPathList
                                    )
                                ),
                                Base64.DEFAULT
                            )
                        )


                    } else {
                        saveConveyance("")
                    }
                }
            }
        }


        binding.etSelectFrom.setOnClickListener {
            Constants.getDateSelection(requireActivity()) { selectedDate ->
                // Handle the selected date here
                binding.etSelectFrom.text = selectedDate
            }
        }
        binding.etSelectTo.setOnClickListener {
            Constants.getDateSelection(requireActivity()) { selectedDate ->
                // Handle the selected date here
                binding.etSelectTo.text = selectedDate
            }
        }
    }

    private fun openCamera() {
        photoUri=null
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "New Picture")
            put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        }
        photoUri = requireActivity().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        }
        cameraActivityResultLauncher.launch(cameraIntent)    }

    @SuppressLint("CheckResult")
    private fun saveConveyance(pdfByte: String) {
        purposeExpense =
            if (voucheType!!.contains("Other") || voucheType!!.contains("Purchase") || voucheType!!.contains(
                    "Courier"
                )
            ) {
                binding.etOther.text.toString()
            } else if (voucheType!!.contains("Zerox") || voucheType!!.contains("Printout")) {
                purposeExpense
            } else {
                binding.etSelectFrom.text.toString() + " to " + binding.etSelectTo.text.toString()
            }
        DialogUtils.showProgressDialog(requireActivity(), "Processing..")
        AndroidNetworking.initialize(requireContext())
        AndroidNetworking.post("https://dms.crconline.in/cbslattendance/webmethods/apiwebservice.asmx/VoucherSaveNew")
            .addBodyParameter(
                "userId",
                SessionManager.getInstance().getString(Constants.UserId)
            )
            .addBodyParameter("voucherName", voucheType)
            .addBodyParameter("conveyncehead", headConveyance)
            .addBodyParameter("remarks", binding.etRemark.text.toString())
            .addBodyParameter("expanse", binding.etAmt.text.toString())
            .addBodyParameter("imageName", pdfName)
            .addBodyParameter("imageLocation", pdfByte)
            .addBodyParameter("purposeOfExpense", purposeExpense)
            .addBodyParameter("AuthHeader", Constants.AUTH_HEADER)
            .build()
            .getAsObjectList(SaveResponse::class.java, object :
                ParsedRequestListener<List<SaveResponse>> {
                override fun onResponse(response: List<SaveResponse>?) {
                    DialogUtils.dismissDialog()
                    DialogUtils.showSuccessDialog(
                        requireActivity(),
                        response?.get(0)?.status.toString(),
                        CbslMain::class.java
                    )
                }

                override fun onError(anError: ANError?) {
                    Toast.makeText(
                        requireActivity(), anError?.message, Toast.LENGTH_SHORT
                    ).show()
                }
            });
    }


    fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT)
            .show()

    }


}