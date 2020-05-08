package com.sungbin.autoreply.bot.three.view.activity.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.savvyapps.togglebuttonlayout.ToggleButtonLayout
import com.sungbin.autoreply.bot.three.R


class SettingFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)
        view.findViewById<ToggleButtonLayout>(R.id.tbl_favorite_langauge).setToggled(R.id.simple, true)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

}