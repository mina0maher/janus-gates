package com.mina.janus.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
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
    private lateinit var gatesRecyclerView: RecyclerView
    private lateinit var gatesProgressPar:ProgressBar
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reservation, container, false)
        gatesRecyclerView = view.findViewById(R.id.recycler_gates)
        gatesProgressPar = view.findViewById(R.id.gates_progress_par)
        arguments?.let { array = it.getIntArray("gatesID") }
        apiViewModel.getAllGates()
        gatesLoading(true)
        apiViewModel.gatesBodyLiveData.observe(requireActivity()){
            gatesModel = it
            if(array!=null) {
                for (i in array!!) {
                    for ((k, j) in it.withIndex()) {
                        if (i == j.id) {
                            gatesModel!![k].isChecked = true
                        }
                    }
                }
            }
            gatesRecyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            gatesRecyclerView.adapter = GatesAdapter(gatesModel!!,requireContext())
            gatesLoading(false)
        }
        apiViewModel.codesLiveData.observe(requireActivity()){
            showToast(it.toString(),requireContext())
            gatesLoading(false)
        }
        apiViewModel.errorMessageLiveData.observe(requireActivity()){
            showToast(it,requireContext())
            gatesLoading(false)
        }

        return view
    }
    private fun gatesLoading(isLoading:Boolean){
        if(isLoading){
            gatesRecyclerView.visibility=View.INVISIBLE
            gatesProgressPar.visibility=View.VISIBLE
        }else{
            gatesRecyclerView.visibility=View.VISIBLE
            gatesProgressPar.visibility=View.INVISIBLE
        }
    }

}