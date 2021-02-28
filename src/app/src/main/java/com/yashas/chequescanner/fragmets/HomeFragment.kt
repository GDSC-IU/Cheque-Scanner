package com.yashas.chequescanner.fragmets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.FragmentActivity
import com.yashas.chequescanner.R

class HomeFragment : Fragment() {

    private lateinit var button: AppCompatButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_home, container, false)
        setup(view)
        return view
    }

    private fun setup(view: View){
        initUI(view)
        listeners()
    }

    private fun initUI(view: View){
        button = view.findViewById(R.id.scanBtn)
    }

    private fun listeners(){
        button.setOnClickListener {
            (context as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.frame, ScanFragment())
                .commit()
        }
    }

}