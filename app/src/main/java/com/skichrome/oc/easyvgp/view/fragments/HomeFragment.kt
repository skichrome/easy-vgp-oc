package com.skichrome.oc.easyvgp.view.fragments

import android.content.Intent
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.firebase.auth.FirebaseAuth
import com.skichrome.oc.easyvgp.EasyVGPApplication
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.databinding.FragmentHomeBinding
import com.skichrome.oc.easyvgp.model.local.database.HomeEndValidityReportItem
import com.skichrome.oc.easyvgp.util.*
import com.skichrome.oc.easyvgp.view.base.BaseBindingFragment
import com.skichrome.oc.easyvgp.view.fragments.adapters.HomeReportFragmentAdapter
import com.skichrome.oc.easyvgp.viewmodel.HomeViewModel
import com.skichrome.oc.easyvgp.viewmodel.vmfactory.HomeViewModelFactory
import java.text.SimpleDateFormat

class HomeFragment : BaseBindingFragment<FragmentHomeBinding>()
{
    // =================================
    //              Fields
    // =================================

    private var isMailClientOpened = false
    private var adapterClickedExtraId = -1L
    private var adapter: HomeReportFragmentAdapter by AutoClearedValue()

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
        configureRecyclerView()
        configureUI()
    }

    override fun onResume()
    {
        super.onResume()
        isMailClientOpened = false
    }

    override fun onStop()
    {
        isMailClientOpened = true
        super.onStop()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        when
        {
            requestCode == RC_SEND_EMAIL_INTENT && isMailClientOpened ->
            {
                if (adapterClickedExtraId != -1L)
                    viewModel.updateReportEmailSendStatus(adapterClickedExtraId)
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    // =================================
    //              Methods
    // =================================

    private fun configureBtn()
    {
        binding.homeFragmentFab.setOnClickListener { navigateToCustomersFragment() }
    }

    private fun configureViewModel() = viewModel.apply {
        onEmailClickEvent.observe(viewLifecycleOwner, EventObserver { sendEmailIntent(it) })
        controlList.observe(viewLifecycleOwner, Observer { it?.let { reports -> configureChart(reports) } })
    }

    private fun configureRecyclerView()
    {
        adapter = HomeReportFragmentAdapter(viewModel)
        binding.homeFragmentRecyclerView.adapter = adapter
    }

    private fun configureUI()
    {
        binding.viewModel = viewModel
    }

    // --- Navigation --- //

    private fun navigateToCustomersFragment() = findNavController().navigate(R.id.action_homeFragment_to_customerFragment)

    private fun configureChart(reports: List<HomeEndValidityReportItem>)
    {
        val deadlineOver = reports.filter { it.reportDeltaDay?.let { delta -> delta <= 0 } ?: false }.size.toFloat()
        val fiveDaysCount = reports.filter { it.reportDeltaDay?.let { delta -> delta in 1..5 } ?: false }.size.toFloat()
        val fifteenDaysCount = reports.filter { it.reportDeltaDay?.let { delta -> delta in 6..15 } ?: false }.size.toFloat()
        val noCloseDeadline = if (reports.isEmpty()) 1f else reports.filter { it.reportDeltaDay?.let { delta -> delta > 15 } ?: false }.size.toFloat()

        val entries = arrayListOf<PieEntry>()

        entries.add(PieEntry(deadlineOver, getString(R.string.home_fragment_chart_deadline_over)))
        entries.add(PieEntry(fiveDaysCount, getString(R.string.home_fragment_chart_deadline_five_days)))
        entries.add(PieEntry(fifteenDaysCount, getString(R.string.home_fragment_chart_deadline_fithteen_days)))
        entries.add(PieEntry(noCloseDeadline, getString(R.string.home_fragment_chart_no_close_deadline)))

        val pieDataSet = PieDataSet(entries, "").apply {
            val colors = IntArray(4)
            colors[0] = R.color.deadlineOverColor
            colors[1] = R.color.deadline5DaysColor
            colors[2] = R.color.deadline15DaysColor
            colors[3] = R.color.noDeadlineColor
            setColors(colors, context)
        }
        val pieData = PieData(pieDataSet).apply {
            setValueFormatter(PercentFormatter(binding.homeFragmentChart))
        }

        binding.homeFragmentChart.apply {
            setEntryLabelTextSize(25f)
            setDrawEntryLabels(false)
            setUsePercentValues(true)
            centerText = getString(R.string.home_fragment_chart_center_text)
            description = null
            data = pieData
            invalidate()
        }
    }

    private fun sendEmailIntent(report: HomeEndValidityReportItem)
    {
        if (report.reportLocalPath == null)
        {
            binding.root.snackBar(getString(R.string.home_fragment_error_download_report_before))
            return
        }

        Intent(Intent.ACTION_SEND).apply {
            val subject = getString(R.string.share_report_remind_email_subject)
            val userName = FirebaseAuth.getInstance().currentUser?.displayName
            val reportDate = SimpleDateFormat.getDateInstance().format(report.reportEndDate)
            val content = getString(R.string.share_report_remind_email_content, reportDate, report.reportDeltaDay, userName)

            if (canReadExternalStorage())
            {
                requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.createOrGetFile(PDF_FOLDER_NAME, report.reportLocalPath)
                    ?.let { file ->
                        if (file.exists())
                        {
                            val attachment = FileProvider.getUriForFile(requireContext(), getString(R.string.file_provider_authority), file)

                            putExtra(Intent.EXTRA_EMAIL, arrayOf(report.customerEmail))
                            putExtra(Intent.EXTRA_SUBJECT, subject)
                            putExtra(Intent.EXTRA_TEXT, content)
                            putExtra(Intent.EXTRA_STREAM, attachment)

                            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                            type = "message/rfc822"

                            resolveActivity(requireActivity().packageManager)?.let {
                                adapterClickedExtraId = report.extraId
                                startActivityForResult(
                                    Intent.createChooser(this, getString(R.string.share_report_intent_chooser)),
                                    RC_SEND_EMAIL_INTENT
                                )
                            } ?: binding.root.snackBar(getString(R.string.share_report_no_mail_app_found))
                        }
                    }
            }

        }
    }
}