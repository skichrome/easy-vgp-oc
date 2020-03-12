package com.skichrome.oc.easyvgp.view.fragments

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.skichrome.oc.easyvgp.EasyVGPApplication
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.databinding.FragmentVgpListBinding
import com.skichrome.oc.easyvgp.util.AutoClearedValue
import com.skichrome.oc.easyvgp.util.EventObserver
import com.skichrome.oc.easyvgp.util.snackBar
import com.skichrome.oc.easyvgp.view.base.BaseBindingFragment
import com.skichrome.oc.easyvgp.view.fragments.adapters.VgpListFragmentAdapter
import com.skichrome.oc.easyvgp.viewmodel.VgpListViewModel
import com.skichrome.oc.easyvgp.viewmodel.vmfactory.VgpListViewModelFactory
import kotlinx.android.synthetic.main.fragment_vgp_list.*

class VgpListFragment : BaseBindingFragment<FragmentVgpListBinding>()
{
    // =================================
    //              Fields
    // =================================

    private val args by navArgs<VgpListFragmentArgs>()
    private val viewModel by viewModels<VgpListViewModel> {
        VgpListViewModelFactory((requireActivity().application as EasyVGPApplication).vgpListRepository)
    }

    private var adapter by AutoClearedValue<VgpListFragmentAdapter>()

    // =================================
    //        Superclass Methods
    // =================================

    override fun getFragmentLayout(): Int = R.layout.fragment_vgp_list

    override fun configureFragment()
    {
        configureViewModel()
        configureBinding()
        configureRecyclerView()
        configureBtn()
    }

    // =================================
    //              Methods
    // =================================

    private fun configureViewModel() = viewModel.apply {
        message.observe(viewLifecycleOwner, EventObserver { binding.root.snackBar(getString(it)) })
        loadAllVgpFromMachine(args.machineId)
    }

    private fun configureBinding()
    {
        binding.viewModel = viewModel
    }

    private fun configureRecyclerView()
    {
        adapter = VgpListFragmentAdapter(viewModel)
        binding.vgpListFragmentRecyclerView.setHasFixedSize(true)
        binding.vgpListFragmentRecyclerView.adapter = adapter
    }

    private fun configureBtn()
    {
        vgpListFragmentFab.setOnClickListener { navigateToNewVgpFragment() }
    }

    private fun navigateToNewVgpFragment(machineId: Long = args.machineId, machineTypeId: Long = args.machineTypeId, customerId: Long = -1L)
    {
        val opt = VgpListFragmentDirections.actionVgpListFragmentToVgpFragment(
            machineId = machineId,
            machineTypeId = machineTypeId,
            customerId = customerId
        )
        findNavController().navigate(opt)
    }
}