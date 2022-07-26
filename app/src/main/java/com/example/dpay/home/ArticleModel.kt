package com.example.dpay.home

data class ArticleModel (
    val articleId: String, //목록 고유 id
    val writerId: String, //작성자 아이디
    val title: String, //제목
    val createAt: Long, //글작성시간
    val price: String, //배달 가격
    val imageUrl: String //이미지 url
){
    constructor(): this("","", "", 0, "", "")
}