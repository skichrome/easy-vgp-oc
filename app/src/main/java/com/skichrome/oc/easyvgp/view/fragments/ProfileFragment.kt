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
import kotlinx.android.synthetic.main.fragment_profile.*

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
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?)
    {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.getString(FRAGMENT_STATE_REMOTE_SIGNATURE_LOCATION)?.let {
            remoteSignatureImagePath = Uri.parse(it)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if (requestCode == RC_PICK_PICTURE_INTENT && resultCode == RESULT_OK)
        {
            data?.data?.let {
                signatureImagePath = it
                binding.profileFragmentSignatureLocationTextView.text = signatureImagePath?.path?.split("/")?.last()
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
                binding.profileFragmentSignatureLocationTextView.text = user.user.signaturePath?.path?.split("/")?.last()
            }
        })
    }

    private fun configureUI()
    {
        binding.viewModel = viewModel
    }

    private fun configureBtn()
    {
        binding.profileFragmentSignatureBtn.setOnClickListener { openFileUsingSAF() }
    }

    private fun openFileUsingSAF()
    {
        Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"

            resolveActivity(requireActivity().packageManager)?.let {
                startActivityForResult(this, RC_PICK_PICTURE_INTENT)
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
            name = profileFragmentCompanyNameEditText.text.toString(),
            siret = profileFragmentCompanySiretEditText.text.toString()
        )
        val user = User(
            id = userAndCompany.user.id,
            firebaseUid = userAndCompany.user.firebaseUid,
            name = userAndCompany.user.name,
            email = userAndCompany.user.email,
            approval = profileFragmentApprovalEditText.text.toString(),
            vatNumber = profileFragmentVatEditText.text.toString(),
            companyId = userAndCompany.company.id,
            signaturePath = signatureImagePath,
            remoteSignaturePath = remoteSignatureImagePath,
            isSignatureEnabled = binding.profileFragmentEnableSignatureSwitch.isChecked
        )
        viewModel.updateUserAndCompany(UserAndCompany(user = user, company = company))
    }
}