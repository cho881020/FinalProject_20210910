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
import com.neppplus.finalproject_20210910.adapters.AppointmentRecyclerAdapter
import com.neppplus.finalproject_20210910.adapters.InvitedAppointmentRecyclerAdapter
import com.neppplus.finalproject_20210910.adapters.MyFriendsRecyclerAdapter
import com.neppplus.finalproject_20210910.databinding.FragmentInvitedAppointmentsListBinding
import com.neppplus.finalproject_20210910.databinding.FragmentMyAppointmentsListBinding
import com.neppplus.finalproject_20210910.databinding.FragmentMyFriendsListBinding
import com.neppplus.finalproject_20210910.datas.AppointmentData
import com.neppplus.finalproject_20210910.datas.BasicResponse
import com.neppplus.finalproject_20210910.datas.UserData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InvitedAppointmentsListFragment : BaseFragment() {

    companion object {
        private var frag : InvitedAppointmentsListFragment? = null
        fun getFrag() : InvitedAppointmentsListFragment {
            if (frag == null) {
                frag = InvitedAppointmentsListFragment()
            }

            return frag!!
        }
    }

    lateinit var binding: FragmentInvitedAppointmentsListBinding

    val mAppointmentList = ArrayList<AppointmentData>()
//    lateinit var mAdapter : AppointmentAdapter

    lateinit var mRecyclerAdapter : InvitedAppointmentRecyclerAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_invited_appointments_list, container, false)
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
        mRecyclerAdapter = InvitedAppointmentRecyclerAdapter(mContext, mAppointmentList)
        binding.invitedAppointmentsRecyclerView.adapter = mRecyclerAdapter

        binding.invitedAppointmentsRecyclerView.layoutManager = LinearLayoutManager(mContext)

    }


    override fun onResume() {
        super.onResume()
        getAppointmentListFromServer()
    }

    fun getAppointmentListFromServer() {

        apiService.getRequestAppointmentList().enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {

                val basicResponse = response.body()!!

                mAppointmentList.clear()

//                약속목록변수에 => 서버가 알려준 약속목록을 전부 추가.
                mAppointmentList.addAll( basicResponse.data.invitedAppointments )

//                어댑터 새로고침
                mRecyclerAdapter.notifyDataSetChanged()

            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

            }

        })

    }

}