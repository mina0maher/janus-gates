package com.mina.janus.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mina.janus.R
import com.mina.janus.adapters.GatesAdapter
import com.mina.janus.models.GatesModel
import com.mina.janus.utilities.Constants.showToast
import com.mina.janus.viewmodles.ApiViewModel

class ReservationFragment : Fragment() {
private var array :IntArray?=null
    private val apiViewModel: ApiViewModel by viewModels()
    private var gatesModel:GatesModel? = null
    private lateinit var recyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_reservation, container, false)
        recyclerView = view.findViewById(R.id.recycler_gates)
        arguments?.let { array = it.getIntArray("gatesID") }
        showToast(array!![0].toString(),requireContext())
        apiViewModel.getAllGates()
        apiViewModel.gatesBodyLiveData.observe(requireActivity()){
            gatesModel = it
            for(i in array!!){
                for((k, j) in it.withIndex()){
                    if(i==j.id){
                         gatesModel!![k].isChecked=true
                    }
                }
            }
            recyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            recyclerView.adapter = GatesAdapter(it,requireContext());

        }
        apiViewModel.codesLiveData.observe(requireActivity()){
            showToast(it.toString(),requireContext());
        }
        apiViewModel.errorMessageLiveData.observe(requireActivity()){
            showToast(it,requireContext());
        }
        return view;
    }

}