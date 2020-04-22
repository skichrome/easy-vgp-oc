package com.skichrome.oc.easyvgp.view.fragments

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.skichrome.oc.easyvgp.EasyVGPApplication
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.databinding.FragmentMachineBinding
import com.skichrome.oc.easyvgp.model.local.database.Machine
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

    private var machineAdapter by AutoClearedValue<MachineFragmentAdapter>()

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
        machineLongClicked.observe(viewLifecycleOwner, EventObserver { goToAddEditMachineFragment(it) })
        machineClicked.observe(viewLifecycleOwner, EventObserver { goToVgpFragment(it) })
        errorMessage.observe(viewLifecycleOwner, EventObserver { binding.root.snackBar(getString(it)) })
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
        machineAdapter = MachineFragmentAdapter(viewModel)

        binding.fragMachineRecyclerView.apply {
            adapter = machineAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun goToAddEditMachineFragment(machineId: Long = -1L)
    {
        val opt = MachineFragmentDirections.actionMachineFragmentToAddEditMachineFragment(machineId = machineId, customerId = args.customerId)
        findNavController().navigate(opt)
    }

    private fun goToVgpFragment(machine: Machine? = null)
    {
        val opt = MachineFragmentDirections.actionMachineFragmentToVgpListFragment(
            machineId = machine?.machineId ?: -1L,
            machineTypeId = machine?.type ?: -1L,
            customerId = args.customerId
        )
        findNavController().navigate(opt)
    }
}