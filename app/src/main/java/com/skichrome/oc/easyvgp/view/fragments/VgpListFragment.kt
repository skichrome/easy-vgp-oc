package com.skichrome.oc.easyvgp.view.fragments

import android.util.Log
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import com.skichrome.oc.easyvgp.EasyVGPApplication
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.databinding.FragmentVgpListBinding
import com.skichrome.oc.easyvgp.util.AutoClearedValue
import com.skichrome.oc.easyvgp.util.CURRENT_LOCAL_PROFILE
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
        reportDateEvent.observe(viewLifecycleOwner, EventObserver { navigateToNewVgpFragment(reportDateToEdit = it) })
        pdfClickEvent.observe(viewLifecycleOwner, EventObserver { generateReport(it) })
        pdfDataReadyEvent.observe(viewLifecycleOwner, EventObserver { Log.e("VgpListFrag", "Report to save : $it") })
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

    private fun generateReport(reportDate: Long)
    {
        val userId = PreferenceManager.getDefaultSharedPreferences(context)
            .getLong(CURRENT_LOCAL_PROFILE, -1L)

        viewModel.loadReport(
            userId = userId,
            machineId = args.machineId,
            reportDate = reportDate,
            machineTypeId = args.machineTypeId,
            customerId = args.customerId
        )
    }

    private fun navigateToNewVgpFragment(customerId: Long = -1L, reportDateToEdit: Long = -1L)
    {
        val opt = VgpListFragmentDirections.actionVgpListFragmentToVgpFragment(
            machineId = args.machineId,
            machineTypeId = args.machineTypeId,
            customerId = customerId,
            reportDateToEdit = reportDateToEdit
        )
        findNavController().navigate(opt)
    }
}