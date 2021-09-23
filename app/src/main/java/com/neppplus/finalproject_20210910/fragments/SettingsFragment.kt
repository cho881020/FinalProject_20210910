package com.neppplus.finalproject_20210910.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.neppplus.finalproject_20210910.R
import com.neppplus.finalproject_20210910.adapters.MyFriendsRecyclerAdapter
import com.neppplus.finalproject_20210910.databinding.FragmentMyAppointmentsListBinding
import com.neppplus.finalproject_20210910.databinding.FragmentMyFriendsListBinding
import com.neppplus.finalproject_20210910.databinding.FragmentSettingsBinding
import com.neppplus.finalproject_20210910.datas.BasicResponse
import com.neppplus.finalproject_20210910.datas.UserData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsFragment : BaseFragment() {

    companion object {
        private var frag : SettingsFragment? = null
        fun getFrag() : SettingsFragment {
            if (frag == null) {
                frag = SettingsFragment()
            }

            return frag!!
        }
    }

    lateinit var binding: FragmentSettingsBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        return  binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupEvents()
        setValues()

    }

    override fun setupEvents() {

    }

    override fun setValues() {


    }

    override fun onResume() {
        super.onResume()
    }



}