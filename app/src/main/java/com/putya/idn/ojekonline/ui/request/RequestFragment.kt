package com.putya.idn.ojekonline.ui.request

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.putya.idn.ojekonline.R
import com.putya.idn.ojekonline.ui.request.fragment.CompleteBookingFragment
import com.putya.idn.ojekonline.ui.request.fragment.ProcessBookingFragment
import com.putya.idn.ojekonline.ui.request.fragment.RequestBookingFragment
import kotlinx.android.synthetic.main.fragment_request.*

class RequestFragment : Fragment() {

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {

                R.id.request -> {
                    setFragment(RequestFragment())
                    return@OnNavigationItemSelectedListener true
                }

                R.id.handle -> {
                    setFragment(ProcessBookingFragment())
                    return@OnNavigationItemSelectedListener true
                }

                R.id.complete -> {
                    setFragment(CompleteBookingFragment())
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    private fun setFragment(fragment: Fragment) {
        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.pager, fragment)
            ?.commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_request, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragment(RequestBookingFragment())
        navigation2.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }
}