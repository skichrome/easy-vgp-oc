package com.skichrome.oc.easyvgp.view.fragments

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputEditText
import com.skichrome.oc.easyvgp.EasyVGPApplication
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.databinding.FragmentAddEditCustomerBinding
import com.skichrome.oc.easyvgp.model.local.database.Customer
import com.skichrome.oc.easyvgp.util.EventObserver
import com.skichrome.oc.easyvgp.util.snackBar
import com.skichrome.oc.easyvgp.view.base.BaseBindingFragment
import com.skichrome.oc.easyvgp.viewmodel.CustomerViewModel
import com.skichrome.oc.easyvgp.viewmodel.vmfactory.CustomerViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*

class AddEditCustomerFragment : BaseBindingFragment<FragmentAddEditCustomerBinding>()
{
    // =================================
    //              Fields
    // =================================

    private val args: AddEditCustomerFragmentArgs by navArgs()
    private val viewModel by viewModels<CustomerViewModel> {
        CustomerViewModelFactory((requireActivity().application as EasyVGPApplication).customerRepository)
    }

    private lateinit var inputList: List<TextInputEditText>

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
            binding.addEditCustomerFragmentFirstNameEditText,
            binding.addEditCustomerFragmentLastNameEditText,
            binding.addEditCustomerFragmentCompanyNameEditText,
            binding.addEditCustomerFragmentSiretEditText,
            binding.addEditCustomerFragmentEmailEditText,
            binding.addEditCustomerFragmentAddressEditText,
            binding.addEditCustomerFragmentPostCodeEditText,
            binding.addEditCustomerFragmentCityEditText
        )

        binding.viewModel = viewModel

        if (args.customerId != -1L)
        {
            activity?.apply { toolbar?.title = getString(R.string.title_fragment_edit_customer) }
            viewModel.loadCustomerById(args.customerId)
        }
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

        inputList.forEach { editText ->

            if (editText.text.toString() == "")
            {
                canRegisterCustomer = false
                editText.error = getString(R.string.frag_add_edit_customer_error_input)
                return@forEach
            }
        }

        if (canRegisterCustomer)
        {
            val customer = Customer(
                id = if (args.customerId != -1L) args.customerId else 0,
                firstName = binding.addEditCustomerFragmentFirstNameEditText.text.toString(),
                lastName = binding.addEditCustomerFragmentLastNameEditText.text.toString(),
                siret = binding.addEditCustomerFragmentSiretEditText.text.toString(),
                postCode = binding.addEditCustomerFragmentPostCodeEditText.text.toString().toInt(),
                address = binding.addEditCustomerFragmentAddressEditText.text.toString(),
                city = binding.addEditCustomerFragmentCityEditText.text.toString(),
                email = binding.addEditCustomerFragmentEmailEditText.text.toString(),
                mobilePhone = binding.addEditCustomerFragmentMobilePhoneEditText.text.toString().toIntOrNull(),
                notes = binding.addEditCustomerFragmentNotesEditText.text.toString(),
                phone = binding.addEditCustomerFragmentPhoneEditText.text.toString().toIntOrNull(),
                companyName = binding.addEditCustomerFragmentCompanyNameEditText.text.toString()
            )

            if (args.customerId != -1L)
                viewModel.updateCustomer(customer)
            else
                viewModel.saveCustomer(customer)
        }
        else
            binding.root.snackBar(getString(R.string.frag_add_edit_customer_error_input_snack_bar_msg))
    }
}