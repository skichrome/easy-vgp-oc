package com.skichrome.oc.easyvgp.view.fragments

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.skichrome.oc.easyvgp.EasyVGPApplication
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.databinding.FragmentProfileBinding
import com.skichrome.oc.easyvgp.model.local.database.Company
import com.skichrome.oc.easyvgp.model.local.database.User
import com.skichrome.oc.easyvgp.model.local.database.UserAndCompany
import com.skichrome.oc.easyvgp.util.EventObserver
import com.skichrome.oc.easyvgp.util.toast
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

    // =================================
    //        Superclass Methods
    // =================================

    override fun getFragmentLayout(): Int = R.layout.fragment_profile

    override fun configureFragment()
    {
        configureViewModel()
        configureUI()
    }

    // =================================
    //              Methods
    // =================================

    private fun configureViewModel() = viewModel.apply {
        FirebaseAuth.getInstance().uid?.let {
            getCurrentFirebaseUser(it)
        }
        onSaveEvent.observe(this@ProfileFragment, EventObserver { getUserValues(it) })
        onSaveSuccessEvent.observe(this@ProfileFragment, EventObserver { if (it) findNavController().navigateUp() })
        message.observe(this@ProfileFragment, EventObserver { toast(getString(it)) })
    }

    private fun configureUI()
    {
        binding.viewModel = viewModel
    }

    private fun getUserValues(userAndCompany: UserAndCompany)
    {
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
            companyId = userAndCompany.company.id
        )
        viewModel.updateUserAndCompany(UserAndCompany(user = user, company = company))
    }
}