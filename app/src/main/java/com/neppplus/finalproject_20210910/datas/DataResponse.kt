package com.neppplus.finalproject_20210910.datas

class DataResponse(
//    로그인 성공시 파싱용 변수.
    var user: UserData,
    var token: String,
//    이 밑으로는 약속 목록파싱용 변수.
    var appointments: List<AppointmentData>,
//    장소 목록
    var places: List<PlaceData>,
//    친구목록
    var friends: List<UserData>,
//    검색된 사용자 목록
    var users: List<UserData>
    ) {
}