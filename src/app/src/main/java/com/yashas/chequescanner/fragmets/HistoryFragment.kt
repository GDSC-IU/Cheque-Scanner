package com.yashas.chequescanner.fragmets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yashas.chequescanner.R
import com.yashas.chequescanner.ScanHistoryAdapter
import com.yashas.chequescanner.database.ScanHistoryEntity
import com.yashas.chequescanner.utils.ScanHistoryDBUtils

class HistoryFragment : Fragment() {

    lateinit var recycler: RecyclerView
    lateinit var adapter: ScanHistoryAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        initUI(view)
        return view
    }

    private fun initUI(view: View){
        val list = ScanHistoryDBUtils(context!!, 4).execute().get() as ArrayList<ScanHistoryEntity>
        recycler = view.findViewById(R.id.recycler)
        adapter = ScanHistoryAdapter(context!!, list)
        layoutManager = LinearLayoutManager(context!!)
        recycler.layoutManager = layoutManager
        recycler.adapter = adapter
    }

}