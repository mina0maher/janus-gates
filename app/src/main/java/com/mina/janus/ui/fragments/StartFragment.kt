package com.mina.janus.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.mina.janus.R
import com.mina.janus.utilities.Constants
import com.mina.janus.utilities.PreferenceManager


class StartFragment : Fragment(R.layout.fragment_start) {
    //declare views
    private lateinit var signUpButton: Button
    private lateinit var logoImage:ImageView
    private lateinit var linearLayout: LinearLayout
    private lateinit var preferenceManager: PreferenceManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //init views
        preferenceManager = PreferenceManager(requireActivity())
        if(preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)){
            findNavController().navigate(R.id.action_startFragment_to_mapsFragment)
        }
        signUpButton = view.findViewById(R.id.buttonSignUp)
        logoImage = view.findViewById(R.id.ImageView)
        linearLayout = view.findViewById(R.id.linearLayout)

        setListeners()

    }
    private fun setListeners(){
        signUpButton.setOnClickListener{
            val extras = FragmentNavigatorExtras(logoImage to "imageSmall")
            findNavController().navigate(
                R.id.action_startFragment_to_signupFragment,
                null,
                null,
                extras
            )
        }
        linearLayout.setOnClickListener{
            val extras = FragmentNavigatorExtras(logoImage to "image_small")
            findNavController().navigate(
                R.id.action_startFragment_to_loginFragment,
                null,
                null,
                extras
            )
        }
    }
}