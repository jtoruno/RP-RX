package com.zimplifica.redipuntos.ui.fragments


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.ui.activities.MovementDetailActivity
import kotlinx.android.synthetic.main.fragment_movements.view.*


class MovementsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =inflater.inflate(R.layout.fragment_movements, container, false)
        view.button.setOnClickListener {
            val intent = Intent(activity, MovementDetailActivity::class.java)
            startActivity(intent)
        }
        // Inflate the layout for this fragment
        return view
    }


}
