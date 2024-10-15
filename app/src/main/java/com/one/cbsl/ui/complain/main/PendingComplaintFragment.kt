package com.one.cbsl.ui.complain.main

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.one.cbsl.CbslMain
import com.one.cbsl.adapter.SelectImageAdapter
import com.one.cbsl.databinding.FragmentPendingComplaintBinding
import com.one.cbsl.databinding.RawImageSelectionDialogBinding
import com.one.cbsl.networkcall.NetworkApiHelper
import com.one.cbsl.networkcall.RetrofitBuilder
import com.one.cbsl.networkcall.base.ViewModelFactory
import com.one.cbsl.ui.complain.adapter.AssignComplainAdapter
import com.one.cbsl.ui.complain.adapter.ComplaintLogAdapter
import com.one.cbsl.ui.complain.model.*
import com.one.cbsl.ui.complain.viewmodel.ComplaintViewModel
import com.one.cbsl.utils.*
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class PendingComplaintFragment : Fragment(), AssignComplainAdapter.OptionListener,
    AdapterView.OnItemSelectedListener {

    private var _binding: FragmentPendingComplaintBinding? = null
    private var viewModel: ComplaintViewModel? = null
    private val binding get() = _binding!!
    private var clientId = "0"
    private var complaint_Id = "0"
    var pendingOwnerId: Int = 0
    var pendingReasonId: String? = "0"
    var statusId: Int? = null
    var assignUserId: String? = null
    var check: String? = null
    var itemId: String = "0"
    var complainChangeStatus: String = "0"
    var emptyValue: String = ""
    var complainTypeId: String? = "0"


    var imagesPathList: ArrayList<Uri> = arrayListOf()
    private var pdfName = ""
    private var photoUri: Uri? = null
    val currentDateTime = Calendar.getInstance()

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
                    binding.complainDetails.rvImage.adapter = SelectImageAdapter(imagesPathList)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

    private var cameraActivityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback {
                if (it.resultCode === Activity.RESULT_OK) {
                    pdfName = System.currentTimeMillis().toString() + ".pdf"
                    imagesPathList.add(photoUri!!)
                    binding.complainDetails.rvImage.adapter = SelectImageAdapter(imagesPathList)

                }
            })


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidNetworking.initialize(Cbsl.getInstance());

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        bindView()
        _binding = FragmentPendingComplaintBinding.inflate(inflater, container, false)


        return binding.root
    }

    @SuppressLint("HardwareIds")
    private fun bindView() {
        viewModel = if (SessionManager.getInstance().getString(Constants.COMPANY) == "SOLAR") {
            ViewModelProvider(
                requireActivity(),
                ViewModelFactory(NetworkApiHelper(RetrofitBuilder.solarApiService))
            )[ComplaintViewModel::class.java]
        } else {

            ViewModelProvider(
                requireActivity(), ViewModelFactory(NetworkApiHelper(RetrofitBuilder.bmdApiService))
            )[ComplaintViewModel::class.java]
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadCustomerName()
        getPendingComplaint()
        if (SessionManager.getInstance().getString(Constants.GROUP_ID) == "19") {
            getComplaintStatus("SE_STATUS")
        } else {
            getComplaintStatus("C_STATUS")
        }
        binding.searchOption.spinClientName.onItemSelectedListener = this
        binding.complainDetails.spinPendingOwner.onItemSelectedListener = this
        binding.complainDetails.spinPending.onItemSelectedListener = this
        binding.complainDetails.spinStatus.onItemSelectedListener = this
        binding.complainDetails.spinAssignto.onItemSelectedListener = this
        binding.complainDetails.spinComplainType.onItemSelectedListener = this
        binding.complainDetails.spinComplainItem.onItemSelectedListener = this
        binding.complainDetails.checkType.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.complainDetails.llComplainItemType.visibility = View.VISIBLE
                complainChangeStatus = "1"
            } else {
                complainChangeStatus = "0"
                itemId = "0"
                binding.complainDetails.llComplainItemType.visibility = View.GONE
            }
        }

        binding.btnSearch.setOnClickListener {
            getPendingComplaint()
        }
        binding.complainDetails.btnDocumentUpload.setOnClickListener {
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
        binding.complainDetails.icCloseDialog.setOnClickListener {
            binding.complainDetails.rlComplainDialog.visibility = View.GONE
            binding.llMainHeader.visibility = View.VISIBLE
            imagesPathList.clear()
            binding.complainDetails.rvImage.adapter = SelectImageAdapter(imagesPathList)
        }
        binding.logDialog.icCloseLogDialog.setOnClickListener {
            binding.logDialog.rlComplainLogDialog.visibility = View.GONE
            binding.llMainHeader.visibility = View.VISIBLE
        }
        binding.complainDetails.tvAssingDate.text = Constants.getDateWithTime()
        binding.complainDetails.tvAssingDate.setOnClickListener {
            pickDateTime()
        }
        binding.complainDetails.btnAssign.setOnClickListener {
            when (check) {
                "R" -> {
                    if (imagesPathList.size > 0) {
                        closeComplaintNew(
                            Base64.encodeToString(
                                Utils.convertPDFToByteArray(
                                    Constants.generatePdf(
                                        imagesPathList
                                    )
                                ), Base64.DEFAULT
                            )
                        )
                    } else {
                        DialogUtils.showFailedDialog(requireActivity(), "Please upload document")
                    }
                }

                "P" -> {
                    if (imagesPathList.size > 0) {
                        pendingComplaintNew(
                            Base64.encodeToString(
                                Utils.convertPDFToByteArray(
                                    Constants.generatePdf(
                                        imagesPathList
                                    )
                                ), Base64.DEFAULT
                            )
                        )
                    } else {
                        DialogUtils.showFailedDialog(requireActivity(), "Please upload document")
                    }
                }

                "A" -> {
                    assignComplaint()
                }

            }
        }
    }

    private fun openCamera() {
        photoUri = null
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "New Picture")
            put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        }
        photoUri = requireActivity().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
        )
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        }
        cameraActivityResultLauncher.launch(cameraIntent)
    }

    private fun getComplaintStatus(s: String) {
        viewModel?.getComplaintStatus(s)?.observe(requireActivity(), Observer { resources ->
            when (resources) {
                is Resource.Success -> {
                    resources.data?.let { response ->
                        binding.complainDetails.spinStatus.adapter = ArrayAdapter(
                            requireActivity(),
                            android.R.layout.simple_spinner_dropdown_item,
                            resources.data
                        )
                    }
                }

                is Resource.Loading -> {
                }

                is Resource.Error -> {

                }
            }
        })


    }

    private fun loadCustomerName() {
        viewModel?.getCustomerName(
            "999", "0"
        )?.observe(requireActivity(), Observer { resource ->
            when (resource) {
                is Resource.Loading -> {
                    DialogUtils.showProgressDialog(requireActivity(), "Fetching Customer")
                }

                is Resource.Success -> {
                    DialogUtils.dismissDialog()
                    binding.searchOption.spinClientName.adapter = ArrayAdapter(
                        requireContext(), android.R.layout.simple_spinner_item, resource.data!!
                    )
                    clientId = resource.data[0].ClientId.toString()
                }

                is Resource.Error -> {
                    DialogUtils.showFailedDialog(requireActivity(), resource.message.toString())
                }
            }
        })

    }

    private fun getPendingComplaint() {
        viewModel?.getComplainDetails(clientId, "0", binding.searchOption.etBranch.text.toString())
            ?.observe(requireActivity(), Observer { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        DialogUtils.showProgressDialog(requireActivity(), "Fetching Complaint")
                    }

                    is Resource.Success -> {
                        if (resource.data?.get(0)?.Status == null) {
                            binding.llNoData.visibility = View.GONE
                            binding.rvMachineList.visibility = View.VISIBLE
                            binding.rvMachineList.adapter =
                                AssignComplainAdapter(resource.data!!, this)
                        } else {
                            binding.rvMachineList.visibility = View.GONE
                            binding.llNoData.visibility = View.VISIBLE
                        }
                        DialogUtils.dismissDialog()

                    }

                    is Resource.Error -> {
                        DialogUtils.showFailedDialog(
                            requireActivity(), resource.message.toString()
                        )
                    }
                }
            })
    }

    private fun getRegisterComplainDetail(complainId: String) {
        viewModel?.getRegisterComplainDetail(
            complainId
        )?.observe(requireActivity(), Observer { resources ->
            when (resources) {
                is Resource.Loading -> {
                    DialogUtils.showProgressDialog(requireActivity(), "Fetching Complaint")
                }

                is Resource.Success -> {
                    DialogUtils.dismissDialog()
                    resources.data?.let { response ->
                        try {
                            binding.complainDetails.rlComplainDialog.visibility = View.VISIBLE
                            binding.complainDetails.apply {
                                binding.complainDetails.tvComplainType.text =
                                    response[0].ComplaintType_Name
                                binding.complainDetails.tvComplainNo.text =
                                    response[0].Complain_tNumber
                                binding.complainDetails.tvMachineNo.text =
                                    response[0].Machine_Number
                                binding.complainDetails.tvItemName.text = response[0].ClientName

                            }
                            binding.llMainHeader.visibility = View.GONE
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                }

                is Resource.Error -> {
                    DialogUtils.showFailedDialog(
                        requireActivity(), resources.message.toString()
                    )
                }
            }
        })
    }

    private fun loadHardwareItem(machine_no: String) {
        viewModel?.getMachineItem(
            machine_no
        )?.observe(requireActivity(), Observer { resource ->
            when (resource) {
                is Resource.Loading -> {
                    DialogUtils.showProgressDialog(requireActivity(), "Fetching Complaint")
                }

                is Resource.Success -> {
                    DialogUtils.dismissDialog()
                    resource.data?.let { response ->
                        try {
                            if (response[0].ItemName != null) {
                                binding.btnSearch.visibility = View.VISIBLE
                                binding.complainDetails.spinComplainItem.adapter = ArrayAdapter(
                                    requireContext(), android.R.layout.simple_spinner_item, response
                                )
                            }
                        } catch (e: Exception) {
                        }
                    }
                }

                is Resource.Error -> {
                    DialogUtils.showFailedDialog(requireActivity(), resource.message.toString())
                }
            }
        })
    }

    private fun getComplainType() {
        viewModel?.getComplainType()?.observe(requireActivity(), Observer { resource ->
            when (resource) {
                is Resource.Loading -> {
                    DialogUtils.showProgressDialog(requireActivity(), "Fetching Complaint")
                }

                is Resource.Success -> {
                    DialogUtils.dismissDialog()
                    resource.data?.let { response ->
                        try {
                            if (response[0].ComplaintTypeName != null) {
                                binding.complainDetails.spinComplainType.adapter = ArrayAdapter(
                                    requireContext(), android.R.layout.simple_spinner_item, response
                                )
                            }
                        } catch (e: Exception) {
                        }
                    }
                }

                is Resource.Error -> {
                    DialogUtils.showFailedDialog(requireActivity(), resource.message.toString())
                }
            }
        })
    }

    private fun getEngineer(workOrderId: String) {
        viewModel?.getEngineer(
            workOrderId
        )?.observe(viewLifecycleOwner, Observer { resources ->
            when (resources) {
                is Resource.Success -> {
                    DialogUtils.dismissDialog()
                    resources.data?.let { response ->
                        try {
                            binding.complainDetails.spinAssignto.adapter = ArrayAdapter(
                                requireActivity(),
                                android.R.layout.simple_spinner_dropdown_item,
                                response
                            )

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                }

                is Resource.Loading -> {
                    DialogUtils.showProgressDialog(requireActivity(), "Fetching Engineer")
                }

                is Resource.Error -> {
                    DialogUtils.showFailedDialog(
                        requireActivity(), resources.message.toString()
                    )
                }
            }
        })
    }

    private fun getPendingOwnerReason() {
        viewModel?.getPendingReason(
            "C_PENDING"

        )?.observe(viewLifecycleOwner, Observer { resources ->
            when (resources) {
                is Resource.Success -> {
                    DialogUtils.dismissDialog()
                    resources.data?.let { response ->
                        try {
                            binding.complainDetails.spinPending.adapter = ArrayAdapter(
                                requireActivity(),
                                android.R.layout.simple_spinner_dropdown_item,
                                response
                            )

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                }

                is Resource.Loading -> {
                    DialogUtils.showProgressDialog(requireActivity(), "Fetching Data")
                }

                is Resource.Error -> {
                    DialogUtils.showFailedDialog(
                        requireActivity(), resources.message.toString()
                    )
                }
            }
        })
        viewModel?.getPendingOwner(
            "PENDING_OWNER"

        )?.observe(viewLifecycleOwner, Observer { resources ->
            when (resources) {
                is Resource.Success -> {
                    DialogUtils.dismissDialog()
                    resources.data?.let { response ->
                        try {
                            binding.complainDetails.spinPendingOwner.adapter = ArrayAdapter(
                                requireActivity(),
                                android.R.layout.simple_spinner_dropdown_item,
                                response
                            )

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                }

                is Resource.Loading -> {
                    DialogUtils.showProgressDialog(requireActivity(), "Fetching Data")
                }

                is Resource.Error -> {
                    DialogUtils.showFailedDialog(
                        requireActivity(), resources.message.toString()
                    )
                }
            }
        })
    }

    override fun showComplaintLog(ComplaintId: String) {
        viewModel?.getComplaintLog(ComplaintId)?.observe(viewLifecycleOwner, Observer { resources ->
            when (resources) {
                is Resource.Success -> {
                    DialogUtils.dismissDialog()
                    resources.data?.let { response ->
                        binding.logDialog.rlComplainLogDialog.visibility = View.VISIBLE
                        binding.logDialog.rvComplaintLog.adapter = ComplaintLogAdapter(response)
                    }
                }

                is Resource.Loading -> {
                    DialogUtils.showProgressDialog(requireActivity(), "Getting Log")
                }

                is Resource.Error -> {
                    DialogUtils.showFailedDialog(requireActivity(), resources.message.toString())
                }
            }
        })

    }

    @SuppressLint("CheckResult")
    private fun closeComplaintNew(pdfData: String) {
        DialogUtils.showProgressDialog(requireActivity(), "Processing...")
        var url =
            "http://crmapi.cbslprojects.com/webmethods/apiwebservice.asmx/closeRejectComplaint"
        if (SessionManager.getInstance().getString(Constants.COMPANY) == "SOLAR") {
            url =
                "https://dms.crconline.in/solarappnew/webmethods/apiwebservice.asmx/closeRejectComplaint"
        }
        AndroidNetworking.initialize(requireContext())
        AndroidNetworking.post(url).addBodyParameter("complainID", complaint_Id)
            .addBodyParameter("ComplaintTypeId", complainTypeId)
            .addBodyParameter("remarks", binding.complainDetails.etComplainDetail.text.toString())
            .addBodyParameter("imageLocation", pdfData)
            .addBodyParameter("ComplaintChangeStatus", complainChangeStatus).addBodyParameter(
                "cby", SessionManager.getInstance().getString(Constants.COMPLAINT_USERID)
            ).addBodyParameter("check", check)
            .addBodyParameter("resolveDate", binding.complainDetails.tvAssingDate.text.toString())
            .build()
            .getAsObjectList(ProjectResponse::class.java, object :
                ParsedRequestListener<List<ProjectResponse>> {
                override fun onResponse(response: List<ProjectResponse>?) {
                    DialogUtils.dismissDialog()
                    DialogUtils.showSuccessDialog(
                        requireActivity(),
                        response?.get(0)?.Status.toString(),
                        CbslMain::class.java
                    )
                }

                override fun onError(anError: ANError?) {
                    DialogUtils.showFailedDialog(requireActivity(), anError?.message.toString())
                }
            })
    }

    @SuppressLint("CheckResult")
    private fun pendingComplaintNew(pdfData: String) {
        var url =
            "http://crmapi.cbslprojects.com/webmethods/apiwebservice.asmx"
        if (SessionManager.getInstance().getString(Constants.COMPANY) == "SOLAR") {
            url =
                "https://dms.crconline.in/solarappnew/webmethods/apiwebservice.asmx"
        }
        AndroidNetworking.initialize(requireContext())
        AndroidNetworking.post("$url/pendingComplaint")
            .addBodyParameter("complainID", complaint_Id)
            .addBodyParameter("complainstatusType", complainTypeId)
            .addBodyParameter("pendingReasonId", pendingReasonId)
            .addBodyParameter("courierName", "").addBodyParameter("podNumber", "")
            .addBodyParameter("expect_retrundate", "")
            .addBodyParameter("ComplaintTypeId", complainTypeId).addBodyParameter("ItemId", itemId)
            .addBodyParameter("ComplaintChangeStatus", complainChangeStatus)
            .addBodyParameter("imageLocation", pdfData).addBodyParameter(
                "cby", SessionManager.getInstance().getString(Constants.COMPLAINT_USERID)
            ).addBodyParameter("remarks", binding.complainDetails.etComplainDetail.text.toString())
            .addBodyParameter("check", check)
            .addBodyParameter("pendingownerid", pendingOwnerId.toString())
            .build()
            .getAsObjectList(ProjectResponse::class.java, object :
                ParsedRequestListener<List<ProjectResponse>> {
                override fun onResponse(response: List<ProjectResponse>?) {
                    DialogUtils.dismissDialog()
                    DialogUtils.showSuccessDialog(
                        requireActivity(),
                        response?.get(0)?.Status.toString(),
                        CbslMain::class.java
                    )
                }

                override fun onError(anError: ANError?) {
                    DialogUtils.showFailedDialog(requireActivity(), anError?.message.toString())
                }
            })

    }

    private fun assignComplaint() {
        viewModel?.assignResolveComplaint(
            complaint_Id,
            complainTypeId!!,
            assignUserId!!,
            binding.complainDetails.tvAssingDate.text.toString(),
            SessionManager.getInstance().getString(Constants.EmpCode),
            binding.complainDetails.etComplainDetail.text.toString(),
            check!!,
            "",
            complainTypeId!!,
            itemId,
            binding.complainDetails.etComplainDetail.text.toString(),
            "0.00",
            complainChangeStatus
        )?.observe(viewLifecycleOwner, Observer { resources ->
            when (resources) {
                is Resource.Success -> {

                    resources.data?.let { response ->
                        try {

                            binding.complainDetails.rlComplainDialog.visibility = View.GONE
                            requireActivity().onBackPressed()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                }

                is Resource.Loading -> {

                }

                is Resource.Error -> {

                }
            }
        })

    }

    override fun onItemClick(ComplaintId: String, workorderId: String) {
        complaint_Id = ComplaintId
        getRegisterComplainDetail(ComplaintId)
        getEngineer(workorderId)
        getComplainType()
        imagesPathList.clear()
        binding.complainDetails.rvImage.adapter = SelectImageAdapter(imagesPathList)
    }


    override fun onItemSelected(parent: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
        when (p1?.parent) {

            binding.searchOption.spinClientName -> {
                try {
                    val selectedItem = parent?.getItemAtPosition(position) as ComplainClientResponse
                    clientId = selectedItem.ClientId!!
                    //loadBranchName(cityId!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            binding.complainDetails.spinStatus -> {
                try {
                    val selectedItem =
                        parent?.getItemAtPosition(position) as ComplaintStatusResponse
                    statusId = selectedItem.ItemId!!.toInt()
                    when (statusId) {
                        3 -> {
                            check = "P"
                            binding.complainDetails.llDocumentLayout.visibility = View.VISIBLE
                            binding.complainDetails.llAssignUserLayout.visibility = View.GONE
                            binding.complainDetails.llPendingLayout.visibility = View.VISIBLE
                            binding.complainDetails.checkType.visibility = View.VISIBLE
                            getPendingOwnerReason();
                        }

                        4 -> {
                            binding.complainDetails.llDocumentLayout.visibility = View.GONE
                            binding.complainDetails.llPendingLayout.visibility = View.GONE
                            binding.complainDetails.llAssignUserLayout.visibility = View.VISIBLE
                            binding.complainDetails.llUserSelection.visibility = View.VISIBLE
                            binding.complainDetails.checkType.visibility = View.VISIBLE
                            check = "A"
                        }

                        else -> {
                            binding.complainDetails.llDocumentLayout.visibility = View.VISIBLE
                            binding.complainDetails.llPendingLayout.visibility = View.GONE
                            binding.complainDetails.llAssignUserLayout.visibility = View.VISIBLE
                            binding.complainDetails.llUserSelection.visibility = View.GONE
                            binding.complainDetails.checkType.visibility = View.GONE
                            check = "R"
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            binding.complainDetails.spinAssignto -> {
                try {
                    val selectedItem = parent?.getItemAtPosition(position) as SE_UserResponse
                    assignUserId = selectedItem.CRMUsersId!!
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            binding.complainDetails.spinPending -> {
                try {
                    val selectedItem = parent?.getItemAtPosition(position) as PendingReasonResponse
                    pendingReasonId = selectedItem.reasonId!!
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            binding.complainDetails.spinPendingOwner -> {
                try {
                    val selectedItem = parent?.getItemAtPosition(position) as PendingOwnerResponse
                    pendingOwnerId = selectedItem.PendingOwnerId!!
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            binding.complainDetails.spinComplainType -> {
                try {
                    val selectedItem = parent?.getItemAtPosition(position) as ComplaintResponse
                    complainTypeId = selectedItem.id
                    if (selectedItem.ComplaintTypeName == "hardware") {
                        loadHardwareItem(binding.complainDetails.tvMachineNo.text.toString())
                        binding.complainDetails.llComplainItemType.visibility = View.VISIBLE
                    } else {
                        binding.complainDetails.llComplainItemType.visibility = View.GONE
                        itemId = "0"
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            binding.complainDetails.spinComplainItem -> {
                try {
                    val selectedItem = parent?.getItemAtPosition(position) as HardwareResponse
                    itemId = selectedItem.ItemId!!
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }

    }

    private fun pickDateTime() {
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)

        DatePickerDialog(
            requireContext(), DatePickerDialog.OnDateSetListener { _, year, month, day ->
                TimePickerDialog(
                    requireContext(), TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                        val pickedDateTime = Calendar.getInstance()
                        pickedDateTime.set(year, month, day, hour, minute)
                        // Format the pickedDateTime
                        val dateFormat = SimpleDateFormat("MM-dd-yyyy HH:mm", Locale.getDefault())
                        val formattedDateTime = dateFormat.format(pickedDateTime.time)

                        binding.complainDetails.tvAssingDate.text = formattedDateTime
                        //  doSomethingWith(pickedDateTime)
                    }, startHour, startMinute, true
                ).show()
            }, startYear, startMonth, startDay
        ).show()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        Log.d("", "Nothing selected")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

