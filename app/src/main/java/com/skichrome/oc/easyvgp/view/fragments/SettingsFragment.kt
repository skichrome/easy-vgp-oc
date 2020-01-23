package com.skichrome.oc.easyvgp.view.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.skichrome.oc.easyvgp.R

class SettingsFragment : PreferenceFragmentCompat()
{
    // =================================
    //              Fields
    // =================================

    companion object
    {
        @JvmStatic
        fun newInstance() = SettingsFragment()
    }

    // =================================
    //        Superclass Methods
    // =================================

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) =
        setPreferencesFromResource(R.xml.preferences_fragment, rootKey)
}