package com.mina.janus.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mina.janus.R
import com.mina.janus.utilities.Constants.showToast

class ReservationFragment : Fragment() {
private var array :IntArray?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        arguments?.let { array = it.getIntArray("gatesID") }
        showToast(array!![0].toString(),requireContext())
        return inflater.inflate(R.layout.fragment_reservation, container, false)
    }

}