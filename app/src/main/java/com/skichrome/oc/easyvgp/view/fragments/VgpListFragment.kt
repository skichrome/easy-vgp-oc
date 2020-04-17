package com.skichrome.oc.easyvgp.view.fragments

import android.content.Intent
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import com.skichrome.oc.easyvgp.EasyVGPApplication
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.databinding.FragmentVgpListBinding
import com.skichrome.oc.easyvgp.model.local.database.VgpListItem
import com.skichrome.oc.easyvgp.util.*
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
        reportDateEvent.observe(viewLifecycleOwner, EventObserver { navigateToNewVgpSetupFragment(reportDateToEdit = it) })
        pdfClickEvent.observe(viewLifecycleOwner, EventObserver { generateReport(it) })
        pdfValidClickEvent.observe(viewLifecycleOwner, EventObserver { openPdfViewer(it) })
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
        vgpListFragmentFab.setOnClickListener { navigateToNewVgpSetupFragment() }
    }

    private fun generateReport(report: VgpListItem)
    {
        val userId = PreferenceManager.getDefaultSharedPreferences(context)
            .getLong(CURRENT_LOCAL_PROFILE, -1L)

        viewModel.loadReport(
            userId = userId,
            machineId = args.machineId,
            report = report,
            machineTypeId = args.machineTypeId,
            customerId = args.customerId
        )
    }

    private fun navigateToNewVgpSetupFragment(customerId: Long = -1L, reportDateToEdit: Long = -1L)
    {
        val opt = VgpListFragmentDirections.actionVgpListFragmentToNewVgpSetupFragment(
            machineId = args.machineId,
            machineTypeId = args.machineTypeId,
            customerId = customerId,
            reportDateToEdit = reportDateToEdit
        )
        findNavController().navigate(opt)
    }

    private fun openPdfViewer(report: VgpListItem)
    {
        report.reportLocalPath?.let { fileName ->
            Intent(Intent.ACTION_VIEW).apply {
                Log.e("VgpListFrag", "Filename : $fileName")

                if (canReadExternalStorage())
                {
                    requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.getFile(PDF_FOLDER_NAME, fileName)
                        ?.let { file ->
                            Log.e("VgpListFrag", "File path : ${file.path}")
                            val uri = FileProvider.getUriForFile(requireContext(), AUTHORITY, file)
                            Log.e("VgpListFrag", "Uri : $uri")
                            setDataAndType(uri, "application/pdf")
                            flags = Intent.FLAG_ACTIVITY_NO_HISTORY

                            resolveActivity(requireActivity().packageManager)?.let {
                                startActivity(this)
                            } ?: binding.root.snackBar("No application found to open a PDF document")
                        }
                }

            }
        }
    }
}