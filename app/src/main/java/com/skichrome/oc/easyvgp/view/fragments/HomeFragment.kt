package com.skichrome.oc.easyvgp.view.fragments

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.skichrome.oc.easyvgp.EasyVGPApplication
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.databinding.FragmentHomeBinding
import com.skichrome.oc.easyvgp.util.EventObserver
import com.skichrome.oc.easyvgp.util.toast
import com.skichrome.oc.easyvgp.view.base.BaseBindingFragment
import com.skichrome.oc.easyvgp.viewmodel.HomeViewModel
import com.skichrome.oc.easyvgp.viewmodel.vmfactory.HomeViewModelFactory
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : BaseBindingFragment<FragmentHomeBinding>()
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

    override fun getFragmentLayout(): Int = R.layout.fragment_home
    override fun configureFragment()
    {
        configureBtn()
        configureViewModel()
        configureUI()
    }

    // =================================
    //              Methods
    // =================================

    private fun configureBtn()
    {
        fragHomeBtnNewVGP.setOnClickListener { navigateToCustomersFragment() }
        fragHomeBtnSeeVGP.setOnClickListener { navigateToNewVGPFragment() }
    }

    private fun configureViewModel() = viewModel.apply {
        currentUserId.observe(viewLifecycleOwner, EventObserver { toast("saved : $it") })
    }

    private fun configureUI()
    {
        binding.viewModel = viewModel
    }

    // --- Navigation --- //

    private fun navigateToCustomersFragment() = findNavController().navigate(R.id.action_homeFragment_to_customerFragment)

    private fun navigateToNewVGPFragment() = findNavController().navigate(R.id.action_homeFragment_to_vgpListFragment)
}