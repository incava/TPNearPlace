package com.incava.tpnearplace.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.incava.tpnearplace.R
import com.incava.tpnearplace.activities.MainActivity
import com.incava.tpnearplace.adapters.PlaceListRecyclerAdapter
import com.incava.tpnearplace.databinding.FragmentPlaceListBinding


class PlaceListFragment : Fragment() {
    private lateinit var binding : FragmentPlaceListBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaceListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //MainActivity에 있는 대량의 데이터를 소환
        val ma:MainActivity = (requireActivity() as MainActivity)
//        ma.searchPlaceResponse ?: return
//        binding.recycler.adapter = PlaceListRecyclerAdapter(ma.searchPlaceResponse!!.documents)
        ma.searchPlaceResponse?.apply {
            binding.recycler.adapter = PlaceListRecyclerAdapter(requireActivity(),documents)
        }

    }


}