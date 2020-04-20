package com.skichrome.oc.easyvgp.view.fragments

import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.skichrome.oc.easyvgp.EasyVGPApplication
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.databinding.FragmentAddEditCustomerBinding
import com.skichrome.oc.easyvgp.model.local.database.Customer
import com.skichrome.oc.easyvgp.util.EventObserver
import com.skichrome.oc.easyvgp.util.snackBar
import com.skichrome.oc.easyvgp.view.base.BaseBindingFragment
import com.skichrome.oc.easyvgp.viewmodel.CustomerViewModel
import com.skichrome.oc.easyvgp.viewmodel.vmfactory.CustomerViewModelFactory

class AddEditCustomerFragment : BaseBindingFragment<FragmentAddEditCustomerBinding>()
{
    // =================================
    //              Fields
    // =================================

    private val args: AddEditCustomerFragmentArgs by navArgs()
    private val viewModel by viewModels<CustomerViewModel> {
        CustomerViewModelFactory((requireActivity().application as EasyVGPApplication).customerRepository)
    }

    private lateinit var inputList: List<TextView>

    // =================================
    //        Superclass Methods
    // =================================

    override fun getFragmentLayout(): Int = R.layout.fragment_add_edit_customer
    override fun configureFragment()
    {
        configureUI()
        configureViewModel()
        configureFab()
    }

    // =================================
    //              Methods
    // =================================

    private fun configureUI()
    {
        inputList = listOf(
            binding.addEditCustomerFragFirstNameText,
            binding.addEditCustomerFragLastNameText,
            binding.addEditCustomerFragCompanyNameText,
            binding.addEditCustomerFragSiretText,
            binding.addEditCustomerFragAddressText,
            binding.addEditCustomerFragPostCodeText,
            binding.addEditCustomerFragCityText
        )

        binding.viewModel = viewModel

        if (args.customerId != -1L)
            viewModel.loadCustomerById(args.customerId)
    }

    private fun configureViewModel() = viewModel.apply {
        errorMessage.observe(viewLifecycleOwner, EventObserver { binding.root.snackBar(getString(it)) })
        customersSaved.observe(viewLifecycleOwner, EventObserver { findNavController().navigateUp() })
    }

    private fun configureFab()
    {
        binding.addEditCustomerFragFab.setOnClickListener { getUserEnteredValues() }
    }

    // --- Actions methods --- //

    private fun getUserEnteredValues()
    {
        var canRegisterCustomer = true

        inputList.forEach { textView ->

            if (textView.text.toString() == "")
            {
                canRegisterCustomer = false
                textView.error = getString(R.string.frag_add_edit_customer_error_input)
                view?.snackBar(getString(R.string.frag_add_edit_customer_error_input_snack_bar_msg))
                return@forEach
            }
        }

        if (canRegisterCustomer)
        {
            val customer = Customer(
                id = if (args.customerId != -1L) args.customerId else 0,
                firstName = binding.addEditCustomerFragFirstNameText.text.toString(),
                lastName = binding.addEditCustomerFragLastNameText.text.toString(),
                siret = binding.addEditCustomerFragSiretText.text.toString(),
                postCode = binding.addEditCustomerFragPostCodeText.text.toString().toInt(),
                address = binding.addEditCustomerFragAddressText.text.toString(),
                city = binding.addEditCustomerFragCityText.text.toString(),
                email = binding.addEditCustomerFragEmailText.text.toString(),
                mobilePhone = binding.addEditCustomerFragMobilePhoneText.text.toString().toIntOrNull(),
                notes = binding.addEditCustomerFragNotesText.text.toString(),
                phone = binding.addEditCustomerFragPhoneText.text.toString().toIntOrNull(),
                companyName = binding.addEditCustomerFragCompanyNameText.text.toString()
            )

            if (args.customerId != -1L)
                viewModel.updateCustomer(customer)
            else
                viewModel.saveCustomer(customer)
        }
    }
}