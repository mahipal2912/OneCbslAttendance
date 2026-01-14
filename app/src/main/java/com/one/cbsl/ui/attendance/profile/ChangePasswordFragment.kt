package com.one.cbsl.ui.attendance.profile

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.one.cbsl.R
import com.one.cbsl.networkcall.NetworkApiHelper
import com.one.cbsl.networkcall.RetrofitBuilder
import com.one.cbsl.networkcall.base.ViewModelFactory
import com.one.cbsl.utils.Constants
import com.one.cbsl.utils.SessionManager
import com.one.cbsl.databinding.PasswordChangeFragmentBinding // Import your binding class
import com.one.cbsl.utils.DialogUtils
import com.one.cbsl.utils.Resource
import java.util.regex.Pattern

class ChangePasswordFragment : Fragment() {

    private var _binding: PasswordChangeFragmentBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = ChangePasswordFragment()
    }

    var regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\$%\\^&\\*]).{8,}\$"
    private lateinit var viewModel: MyProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = PasswordChangeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear the binding reference
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            requireActivity(),
            ViewModelFactory(NetworkApiHelper(RetrofitBuilder.getApi()))
        )[MyProfileViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvTitle.text = "Password Change"
        binding.llBack.setOnClickListener { requireActivity().onBackPressed() }

        binding.tvSave.setOnClickListener {
            when {
                TextUtils.isEmpty(binding.etCurrent.text.toString()) -> {
                    Toast.makeText(
                        requireActivity(),
                        "Invalid Current Password",
                        Toast.LENGTH_LONG
                    ).show()
                }
                !checkPassWord(binding.etNewPassword.text.toString()) -> {
                    Toast.makeText(
                        requireActivity(),
                        "Invalid New Password",
                        Toast.LENGTH_LONG
                    ).show()
                }
                binding.etNewPassword.text.toString() != binding.etConfirmPassword.text.toString() -> {
                    Toast.makeText(
                        requireActivity(),
                        "Password Does not Matched",
                        Toast.LENGTH_LONG
                    ).show()
                }
                binding.etCurrent.text.toString() == binding.etNewPassword.text.toString() -> {
                    Toast.makeText(
                        requireActivity(),
                        "New Password Can't be same as Old Password",
                        Toast.LENGTH_LONG
                    ).show()
                }
                else -> {
                    setupObserver()
                }
            }
        }
    }

    private fun setupObserver() {
        viewModel.changePassword(
            SessionManager.getInstance().getString(Constants.UserId),
            binding.etCurrent.text.toString(),
            binding.etConfirmPassword.text.toString()
        ).observe(requireActivity(), Observer { resource ->
            when (resource) {
                is Resource.Loading -> {
                    DialogUtils.showProgressDialog(requireActivity(), "Loading...")

                }

                is Resource.Success -> {
                    try {
                        try {
                            val response = resource.data
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(
                                activity,
                                response?.get(0)?.MarkStatus.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                            if ( response?.get(0)?.MarkStatus.toString().contains("uccess")) {
                                findNavController().navigate(R.id.password_to_login)
                                SessionManager.getInstance().resetData()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        DialogUtils.dismissDialog()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                is Resource.Error -> {
                    DialogUtils.showFailedDialog(requireActivity(), resource.message.toString())
                }
            }
        }
        )
    }

    private fun checkPassWord(etText: String): Boolean {
        return Pattern.matches(regex, etText)
    }
}
