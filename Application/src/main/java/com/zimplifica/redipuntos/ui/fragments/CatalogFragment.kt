package com.zimplifica.redipuntos.ui.fragments


import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.ui.adapters.CommerceAdapter
import kotlinx.android.synthetic.main.fragment_catalog.view.*

class CatalogFragment : Fragment() {
    lateinit var recyclerView : RecyclerView
    lateinit var adapter : CommerceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_catalog, container, false)
        // Inflate the layout for this fragment
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val searchView = view.commerce_search_view
        searchView.isIconified = false
        searchView.clearFocus()
        searchView.queryHint = "Buscar comercio..."
        val autoComplete = searchView.findViewById<SearchView.SearchAutoComplete>(R.id.search_src_text)
        autoComplete.setHintTextColor(activity!!.getColor(R.color.grayIconTint))
        autoComplete.setTextColor(activity!!.getColor(android.R.color.white))

        val icon = searchView.findViewById<ImageView>(R.id.search_button)
        icon.setColorFilter(activity!!.getColor(R.color.grayIconTint))
        val iconClose = searchView.findViewById<ImageView>(R.id.search_close_btn)
        iconClose.setColorFilter(activity!!.getColor(R.color.grayIconTint))
        val icon2 = searchView.findViewById<ImageView>(R.id.search_mag_icon)
        icon2.setColorFilter(activity!!.getColor(R.color.grayIconTint))
        val plate = searchView.findViewById<View>(R.id.search_plate)
        //plate.setBackgroundColor(activity!!.getColor(R.color.searchPlateColor))

        val frame = searchView.findViewById<View>(R.id.search_edit_frame)
        frame.setBackgroundColor(activity!!.getColor(R.color.searchPlateColor))

        recyclerView = view.commerce_recycler_view
        val manager = GridLayoutManager(activity,2)
        recyclerView.layoutManager = manager
        adapter = CommerceAdapter()
        recyclerView.adapter = adapter
        val list = mutableListOf<String>()
        list.add("Hello")
        list.add("Hello")
        list.add("Hello")
        adapter.setCommerces(list)

    }


}
