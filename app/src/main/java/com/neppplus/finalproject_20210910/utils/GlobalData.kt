package com.neppplus.finalproject_20210910.utils

import com.neppplus.finalproject_20210910.datas.UserData

class GlobalData {

    companion object {

//        로그인한 사람은 없을 수도 있다. => null로 로그인한 사람이 없다는걸 표현.
//        UserData?  로 null 허용.
//        기본값 : 로그인한 사람이 없다. null로 미리 대입.

        var loginUser : UserData? = null

    }
}