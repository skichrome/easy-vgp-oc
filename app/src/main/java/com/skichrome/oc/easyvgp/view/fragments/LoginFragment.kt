package com.skichrome.oc.easyvgp.view.fragments

import com.firebase.ui.auth.AuthUI
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.util.FRAGMENTS_INT_ARGUMENTS
import com.skichrome.oc.easyvgp.util.RC_SIGN_IN_CODE
import com.skichrome.oc.easyvgp.view.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : BaseFragment()
{
    // =================================
    //              Fields
    // =================================

    companion object
    {
        @JvmStatic
        fun newInstance(): LoginFragment = LoginFragment()
    }

    // =================================
    //        Superclass Methods
    // =================================

    override fun getFragmentLayout(): Int = R.layout.fragment_login

    override fun configureFragment()
    {
        getArgumentsFromBundle()
    }

    // =================================
    //              Methods
    // =================================

    private fun getArgumentsFromBundle()
    {
        val textRes = arguments?.getInt(FRAGMENTS_INT_ARGUMENTS) ?: R.string.frag_login_btn
        configureBtn(getString(textRes))
    }

    private fun configureBtn(text: String)
    {
        fragLoginBtn.text = text

        fragLoginBtn.setOnClickListener { configureAppLogin() }
    }

    // --- Login configuration --- //

    private fun configureAppLogin()
    {
        val loginProviders = listOf(AuthUI.IdpConfig.EmailBuilder().build())
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.LoginTheme)
                .setAvailableProviders(loginProviders)
                .setIsSmartLockEnabled(false)
                .build(),
            RC_SIGN_IN_CODE
        )
    }
}