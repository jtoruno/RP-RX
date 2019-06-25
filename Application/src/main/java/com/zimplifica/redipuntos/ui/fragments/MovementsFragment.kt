package com.zimplifica.redipuntos.ui.fragments


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.ui.activities.MovementDetailActivity
import kotlinx.android.synthetic.main.fragment_movements.view.*


class MovementsFragment : Fragment() {
    lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =inflater.inflate(R.layout.fragment_movements, container, false)
        recyclerView = view.mov_fragment_recycler_view
        // Inflate the layout for this fragment
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }


}
