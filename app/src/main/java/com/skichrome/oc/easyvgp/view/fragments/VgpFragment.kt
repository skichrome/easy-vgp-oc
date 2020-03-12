package com.skichrome.oc.easyvgp.view.fragments

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.skichrome.oc.easyvgp.EasyVGPApplication
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.databinding.FragmentVgpBinding
import com.skichrome.oc.easyvgp.util.AutoClearedValue
import com.skichrome.oc.easyvgp.util.EventObserver
import com.skichrome.oc.easyvgp.util.snackBar
import com.skichrome.oc.easyvgp.view.base.BaseBindingFragment
import com.skichrome.oc.easyvgp.view.fragments.adapters.ControlPointVgpAdapter
import com.skichrome.oc.easyvgp.viewmodel.VgpViewModel
import com.skichrome.oc.easyvgp.viewmodel.vmfactory.VgpViewModelFactory

class VgpFragment : BaseBindingFragment<FragmentVgpBinding>()
{
    // =================================
    //              Fields
    // =================================

    private val args: VgpFragmentArgs by navArgs()
    private val viewModel by viewModels<VgpViewModel> {
        VgpViewModelFactory((requireActivity().application as EasyVGPApplication).vgpRepository)
    }

    private var adapter by AutoClearedValue<ControlPointVgpAdapter>()

    // =================================
    //        Superclass Methods
    // =================================

    override fun getFragmentLayout(): Int = R.layout.fragment_vgp

    override fun configureFragment()
    {
        configureViewModel()
        configureBinding()
        configureRecyclerView()
    }

    // =================================
    //              Methods
    // =================================

    private fun configureViewModel()
    {
        viewModel.message.observe(viewLifecycleOwner, EventObserver { binding.root.snackBar(getString(it)) })
        viewModel.getMachineTypeWithControlPoints(args.machineTypeId)
    }

    private fun configureBinding()
    {
        binding.viewModel = viewModel
    }

    private fun configureRecyclerView()
    {
        adapter = ControlPointVgpAdapter(viewModel)
        binding.fragVGPRecyclerView.setHasFixedSize(true)
        binding.fragVGPRecyclerView.adapter = adapter
    }
}