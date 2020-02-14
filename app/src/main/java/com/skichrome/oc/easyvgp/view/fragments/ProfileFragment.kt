package com.skichrome.oc.easyvgp.view.fragments

import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.skichrome.oc.easyvgp.EasyVGPApplication
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.databinding.FragmentProfileBinding
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
    }

    private fun configureUI()
    {
        binding.viewModel = viewModel
    }
}