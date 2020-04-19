package com.sungbin.autoreply.bot.three.kakao

import com.kakao.network.ErrorResult
import com.kakao.usermgmt.response.MeV2Response

interface LoginInterface {
    fun onLoginSuccess(result: MeV2Response?)
    fun onLoginFailed(error: ErrorResult?)
}