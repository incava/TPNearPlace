package com.incava.tpnearplace.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.incava.tpnearplace.databinding.RecyclerItemListFragmentBinding
import com.incava.tpnearplace.model.Place

class PlaceListRecyclerAdapter(var documents: MutableList<Place>) : Adapter<PlaceListRecyclerAdapter.VH>() {

    inner class VH(val binding : RecyclerItemListFragmentBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = RecyclerItemListFragmentBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return VH(binding)
    }

    override fun getItemCount() = documents.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val place = documents[position]
        holder.binding.apply {
            tvPlaceName.text = if(place.road_address_name =="") place.address_name else place.road_address_name
            tvDistance.text = "${place.distance}m"
        }

    }


}