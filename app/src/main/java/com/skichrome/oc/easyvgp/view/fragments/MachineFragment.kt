package com.skichrome.oc.easyvgp.view.fragments

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.skichrome.oc.easyvgp.EasyVGPApplication
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.databinding.FragmentMachineBinding
import com.skichrome.oc.easyvgp.util.AutoClearedValue
import com.skichrome.oc.easyvgp.util.EventObserver
import com.skichrome.oc.easyvgp.util.snackBar
import com.skichrome.oc.easyvgp.view.base.BaseBindingFragment
import com.skichrome.oc.easyvgp.view.fragments.adapters.MachineFragmentAdapter
import com.skichrome.oc.easyvgp.viewmodel.MachineViewModel
import com.skichrome.oc.easyvgp.viewmodel.vmfactory.MachineViewModelFactory

class MachineFragment : BaseBindingFragment<FragmentMachineBinding>()
{
    // =================================
    //              Fields
    // =================================

    private val args by navArgs<MachineFragmentArgs>()
    private val viewModel by viewModels<MachineViewModel> {
        MachineViewModelFactory((requireActivity().application as EasyVGPApplication).machineRepository)
    }

    private var adapter by AutoClearedValue<MachineFragmentAdapter>()

    // =================================
    //        Superclass Methods
    // =================================

    override fun getFragmentLayout(): Int = R.layout.fragment_machine

    override fun configureFragment()
    {
        configureViewModel()
        configureDataBinding()
        configureBtn()
        configureRecyclerView()
    }

    // =================================
    //              Methods
    // =================================

    private fun configureViewModel() = viewModel.apply {
        changeCustomerId(args.customerId)
        machineLongClicked.observe(this@MachineFragment, EventObserver { goToAddEditMachineFragment(it) })
        machineClicked.observe(this@MachineFragment, EventObserver { goToVgpFragment(it) })
        errorMessage.observe(this@MachineFragment, EventObserver { binding.root.snackBar(getString(it)) })
    }

    private fun configureDataBinding()
    {
        binding.viewModel = viewModel
    }

    private fun configureBtn()
    {
        binding.fragMachineFab.setOnClickListener { goToAddEditMachineFragment() }
    }

    private fun configureRecyclerView()
    {
        adapter = MachineFragmentAdapter(viewModel)
        val spanCount = if (resources.getBoolean(R.bool.isTablet)) 3 else 2

        binding.fragMachineRecyclerView.layoutManager = GridLayoutManager(context, spanCount)
        binding.fragMachineRecyclerView.adapter = adapter
    }

    private fun goToAddEditMachineFragment(machineId: Long = -1L)
    {
        val opt = MachineFragmentDirections.actionMachineFragmentToAddEditMachineFragment(machineId = machineId, customerId = args.customerId)
        findNavController().navigate(opt)
    }

    private fun goToVgpFragment(machineId: Long = -1L)
    {
        val opt = MachineFragmentDirections.actionMachineFragmentToVgpFragment(machineId = machineId, customerId = args.customerId)
        findNavController().navigate(opt)
    }
}