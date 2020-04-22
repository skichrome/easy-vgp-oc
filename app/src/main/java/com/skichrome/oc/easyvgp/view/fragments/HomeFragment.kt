package com.skichrome.oc.easyvgp.view.fragments

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.skichrome.oc.easyvgp.EasyVGPApplication
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.databinding.FragmentHomeBinding
import com.skichrome.oc.easyvgp.model.local.database.HomeEndValidityReportItem
import com.skichrome.oc.easyvgp.util.AutoClearedValue
import com.skichrome.oc.easyvgp.util.EventObserver
import com.skichrome.oc.easyvgp.util.toast
import com.skichrome.oc.easyvgp.view.base.BaseBindingFragment
import com.skichrome.oc.easyvgp.view.fragments.adapters.HomeReportFragmentAdapter
import com.skichrome.oc.easyvgp.viewmodel.HomeViewModel
import com.skichrome.oc.easyvgp.viewmodel.vmfactory.HomeViewModelFactory

class HomeFragment : BaseBindingFragment<FragmentHomeBinding>()
{
    // =================================
    //              Fields
    // =================================

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

    // =================================
    //              Methods
    // =================================

    private fun configureBtn()
    {
        binding.homeFragmentFab.setOnClickListener { navigateToCustomersFragment() }
    }

    private fun configureViewModel() = viewModel.apply {
        currentUserId.observe(viewLifecycleOwner, EventObserver { toast("saved : $it") })
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
}