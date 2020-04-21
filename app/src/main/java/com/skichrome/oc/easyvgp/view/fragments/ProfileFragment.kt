package com.skichrome.oc.easyvgp.view.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.skichrome.oc.easyvgp.EasyVGPApplication
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.databinding.FragmentProfileBinding
import com.skichrome.oc.easyvgp.model.local.database.Company
import com.skichrome.oc.easyvgp.model.local.database.User
import com.skichrome.oc.easyvgp.model.local.database.UserAndCompany
import com.skichrome.oc.easyvgp.util.*
import com.skichrome.oc.easyvgp.view.base.BaseBindingFragment
import com.skichrome.oc.easyvgp.viewmodel.HomeViewModel
import com.skichrome.oc.easyvgp.viewmodel.vmfactory.HomeViewModelFactory

class ProfileFragment : BaseBindingFragment<FragmentProfileBinding>()
{
    // =================================
    //              Fields
    // =================================

    private val viewModel by viewModels<HomeViewModel> {
        HomeViewModelFactory((requireActivity().application as EasyVGPApplication).homeRepository)
    }

    private var signatureImagePath: Uri? = null
    private var remoteSignatureImagePath: Uri? = null

    private var logoImagePath: Uri? = null
    private var remoteLogoImagePath: Uri? = null

    // =================================
    //        Superclass Methods
    // =================================

    override fun getFragmentLayout(): Int = R.layout.fragment_profile

    override fun configureFragment()
    {
        configureViewModel()
        configureUI()
        configureBtn()
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        outState.putString(FRAGMENT_STATE_REMOTE_SIGNATURE_LOCATION, remoteSignatureImagePath?.toString())
        outState.putString(FRAGMENT_STATE_REMOTE_LOGO_LOCATION, remoteLogoImagePath?.toString())
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?)
    {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.getString(FRAGMENT_STATE_REMOTE_SIGNATURE_LOCATION)?.let { remoteSignatureImagePath = Uri.parse(it) }
        savedInstanceState?.getString(FRAGMENT_STATE_REMOTE_LOGO_LOCATION)?.let { remoteLogoImagePath = Uri.parse(it) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if (resultCode == RESULT_OK)
        {
            when (requestCode)
            {
                RC_PICK_LOGO_INTENT ->
                {
                    logoImagePath = data?.data
                    logoImagePath?.path?.split("/")?.last()?.let { binding.profileFragmentCompanyLogoLocationTextView.text = it }
                }
                RC_PICK_SIGNATURE_INTENT ->
                {
                    signatureImagePath = data?.data
                    signatureImagePath?.path?.split("/")?.last()?.let { binding.profileFragmentSignatureLocationTextView.text = it }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    // =================================
    //              Methods
    // =================================

    private fun configureViewModel() = viewModel.apply {
        FirebaseAuth.getInstance().uid?.let {
            getCurrentFirebaseUser(it)
        }
        onSaveEvent.observe(viewLifecycleOwner, EventObserver { getUserValues(it) })
        onSaveSuccessEvent.observe(viewLifecycleOwner, EventObserver { if (it) findNavController().navigateUp() })
        message.observe(viewLifecycleOwner, EventObserver { toast(getString(it)) })
        currentUser.observe(viewLifecycleOwner, Observer {
            it?.let { user ->
                signatureImagePath = user.user.signaturePath
                remoteSignatureImagePath = user.user.remoteSignaturePath

                logoImagePath = user.company.localCompanyLogo
                remoteLogoImagePath = user.company.remoteCompanyLogo

                user.user.signaturePath?.path?.split("/")?.last()
                    ?.let { signature -> binding.profileFragmentSignatureLocationTextView.text = signature }
                user.company.localCompanyLogo?.path?.split("/")?.last()
                    ?.let { signature -> binding.profileFragmentCompanyLogoLocationTextView.text = signature }
            }
        })
    }

    private fun configureUI()
    {
        binding.viewModel = viewModel
    }

    private fun configureBtn()
    {
        binding.profileFragmentCompanyLogoBtn.setOnClickListener { openFileUsingSAF(RC_PICK_LOGO_INTENT) }
        binding.profileFragmentSignatureBtn.setOnClickListener { openFileUsingSAF(RC_PICK_SIGNATURE_INTENT) }
    }

    private fun openFileUsingSAF(origin: Int)
    {
        Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
            resolveActivity(requireActivity().packageManager)?.let {
                startActivityForResult(this, origin)
            } ?: binding.root.snackBar(getString(R.string.profile_fragment_no_app_take_picture_intent))
        }
    }

    private fun getUserValues(userAndCompany: UserAndCompany)
    {
        if (binding.profileFragmentEnableSignatureSwitch.isChecked && signatureImagePath == null)
        {
            binding.root.snackBar("Please set a signature or uncheck enable signature parameter")
            return
        }

        val company = Company(
            id = userAndCompany.company.id,
            name = binding.profileFragmentCompanyEditText.text.toString(),
            siret = binding.profileFragmentCompanySiretEditText.text.toString(),
            localCompanyLogo = logoImagePath,
            remoteCompanyLogo = remoteLogoImagePath
        )
        val user = User(
            id = userAndCompany.user.id,
            firebaseUid = userAndCompany.user.firebaseUid,
            name = userAndCompany.user.name,
            email = userAndCompany.user.email,
            approval = binding.profileFragmentApprovalEditText.text.toString(),
            vatNumber = binding.profileFragmentVATEditText.text.toString(),
            companyId = userAndCompany.company.id,
            signaturePath = signatureImagePath,
            remoteSignaturePath = remoteSignatureImagePath,
            isSignatureEnabled = binding.profileFragmentEnableSignatureSwitch.isChecked
        )
        viewModel.updateUserAndCompany(UserAndCompany(user = user, company = company))
    }
}