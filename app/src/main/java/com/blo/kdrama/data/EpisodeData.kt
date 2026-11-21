package com.blo.kdrama.data

/**
 * Полные серии, которые телеканал KBS официально и бесплатно выложил на своём YouTube-канале KBS World TV.
 * Формат: "серия.часть:videoId:длительность_в_секундах" через «;». Сгенерировано по данным канала.
 */
object EpisodeData {
    private val raw: Map<String, String> = mapOf(
        "school2013" to "1.1:NEg7pGXq2_g:1915;1.2:2iGjmiJEnGc:1842;2.1:qJrZr7ajCR4:1948;2.2:vSxUhhuO5_A:1864;3.1:EQLvCAj9F1s:1952;3.2:JuOSErzpiO0:1841;4.1:lr-rkFtrAkc:1881;4.2:axnCuuDVQhM:1902;5.1:bK_GTHyZiIU:1988;5.2:vQbAJOUGewg:1842;6.1:z2eMCs6uVTE:2182;6.2:nhFfAekGfUA:1628;7.1:rXA9nHcS-Hs:1842;7.2:I_bpegVP8oA:2019;8.1:FV7h6kBkfwU:1832;8.2:JDVfRvDNuHw:2103;9.1:VPGnvvgGZtQ:1932;9.2:dDsAjJlcvHU:2121;10.1:v45HAws9sPA:2167;10.2:_VzPBujN2iU:1908;11.1:EHWroNoGArs:2233;11.2:lc4uwxZHDOk:1742;12.1:A5fwbKG7HqU:2016;12.2:xLjPoYjXRlU:2023;13.1:D_5rm_lQN7c:2246;13.2:_wmKRC3EmV4:1732;14.1:yIlwGQ0d8S4:2060;14.2:ovW_lyQQM1A:1886;15.1:QyM66ly7ITw:2150;15.2:oAgJosqkets:1815;16.1:2GUPc_kzntg:2227;16.2:78KWraNASyk:1810",
        "school2015" to "1.0:NkXw0NMJ3Jk:3559;2.0:WwnX1Ni9qQQ:3495;3.0:HfSCxAgtwlE:3583;4.0:sjurQD5X2tI:3538;5.0:oukZNsh1bx0:3575;6.0:8z0pA8Hot7Y:3540;7.0:LIlbcGw-OMk:3553;8.0:kgwFJ6ymTd4:3571;9.0:qz1_h7BjmLM:3571;10.0:1fOtJ0aVIjc:3572;11.0:XYSQa7dsA3s:3568;12.0:fGSl_8Bto8U:3655;13.0:c4xFWBZUdIc:3566;14.0:FGM8rs2MTz4:3536;15.0:knEALo1R-bk:3527;16.0:66IWEpzAuPQ:3565",
        "masterofstudy" to "1.0:IcBdmOAAs4Y:3922;2.0:FDNET2wsGF0:3941;3.0:3RgeBw395IE:3898;4.0:oY-JGxVVt2Q:3991;5.0:53VTS6HrRA4:3914;6.0:b4zUvS8yJCQ:3967;7.0:QxrITLmS3ss:3937;8.0:lq3c-ZZZ-rM:3930;9.0:8mKUzZgqZB0:3970;10.0:FWVI38DHAG8:3970;11.0:bHebAX3iVG0:3991;12.0:CESileDiDXw:4026;13.0:s2xlvYuuTC4:4006;14.0:Egh9oUoMcj4:3979;15.0:VEMxjfk_L6E:3991;16.0:CyjrJ0Y3pg0:4003",
        "dreamhigh2" to "1.0:NlhDvpT7kOM:3723;2.0:3g8hdGUOBPE:3644;3.0:kYi1iZ2g5zE:3976;4.0:XBnx2oD8R9U:3914;5.0:Es5Z_9JdROI:4159;6.0:jMXx8Kjusyk:4002;7.0:Ogu3T-XuJm4:3977;8.0:zrkls3CfHQU:3986;9.0:jEevoPhzCzs:3969;10.0:VpnpK4q0G4g:3933;11.0:KDjOgtAVzIo:3986;12.0:sSZt2mSnvfE:3714;13.0:3pm4dYTcwog:3919;14.0:4CPfM0uUtJ8:3650;15.0:6X0Hqsg351g:4008;16.0:3h2cia3SfDA:3881",
        "gunman" to "1.0:xgsam4vD5a8:3513;2.0:_1lL-nKq_Ao:3532;3.0:7wEYyjISRE0:3566;4.0:iTuR4SJdYqU:3565;5.0:PqYohOsqs8k:3701;6.0:ltW6kHRbbiE:3744;7.0:-HfdbRjuCNg:3619;8.0:8wT484hypqk:3636;9.0:qTNNWWnFeq8:3684;10.0:cnjS0mi8jdo:3636;11.0:CyI6rm7eGyA:3638;12.0:Cy8OwiBLbBE:3627;13.0:JqFHW5mNRrw:3748;14.0:jl1RgyNG9SU:3755;15.0:yy9Kivpn76g:3718;16.0:LX4Mzn5lH6Q:3593;17.0:l6s5Ayb346M:3700;18.0:c9rulQ8Mo_0:3631;19.0:7IOe0NqxNGw:3696;20.0:CpP5vo_42uA:3636;21.0:5VIcFkYpTvY:3711;22.0:38U_BpMBcnY:3727",
        "breadlove" to "1.0:ovnNwODtkMw:3893;2.0:R-MdxXEzQVI:3844;3.0:Y7VMr4_ActM:3837;4.0:jIe9RZNSWEs:3785;5.0:MXvI9TlSwRs:3989;6.0:VEWYZ7Msx-s:4024;7.0:dPxgYXwxPV0:3955;8.0:zKyCY9MfDYY:4004;9.0:AmhD8q9rbGM:3988;10.0:V4bkBBJfEt0:3924;11.0:pxef5Drgubo:3999;12.0:Y50fldE5I3w:3978;13.0:nvjCW7OZDX8:3968;14.0:yeBlTCg5CW8:3952;15.0:fUtpsGBrfo0:3975;16.0:Y-VLUe_AIQo:3987;17.0:hfyL4vUvQqk:4038;18.0:yS0TnDGrZ4o:3978;19.0:BAaNSup4ybM:4036;20.0:Suoe8ifDp3I:4007;21.0:wqvX9QGyuaA:4025;22.0:U9ZBBPAib3M:4060;23.0:vyeRdnaEMxc:4007;24.0:PVkhWMd1OLs:4015;25.0:3sdPOn8-odE:3911;26.0:ukxA_aNo9po:3904;27.0:wbzk30zxawg:3942;28.0:ZKymyOjvF8c:3904;29.0:4898zE7_1SU:3924;30.0:Cc_ykIStqbc:4021",
        "jangyoungsil" to "1.0:TOqMGpiR8RQ:3733;2.0:PHxc-k72l6Q:3631;3.0:Q8l9rb2eu00:3083;4.0:dH6vY3kSACA:3121;5.0:1ESMGIf6FPM:3165;6.0:KyJ8dQGPmM4:2918;7.0:npGaLD1isVk:3063;8.0:ppjAbNY5IZ4:3057;9.0:WZscNsdwKdI:3284;10.0:_v3i3FOf26I:3151;11.0:oTUyUNO2Tnk:3261;12.0:4UvnffTDk3c:3228;13.0:YpETTeIsKNA:3180;14.0:ynhPtY4lRzI:3224;15.0:oLXTSpXSpo8:3215;16.0:p41sNDyt6Eg:3247;17.0:NzHRZINY6KM:3241;18.0:lrbJgvzDYb0:3161;19.0:ZcOp0f45WAg:2954;20.0:hv6PYDeekmM:2976;21.0:GS66wWZPm9c:3167;22.0:Gzoq4pE2FRk:2936;23.0:vglVOGLaEMY:3197;24.0:HdUhpkW-cVM:3476",
        "youarethebest" to "1.0:CPzdIHf-EQM:3843;2.0:PF6DcRUSaug:3831;3.0:TUB0P7CY-3o:3839;4.0:zFLPtp1jnx0:3862;5.0:5ArAh6-tPrI:3796;6.0:dx3Lh6Bf0j4:3817;7.0:1mlZGmBs9oY:3832;8.0:tXQDq5fgJKc:3816;9.0:0rFZW5EIpbw:3832;10.0:zMvsu5UlMhc:3840;11.0:V2NwWt622oI:3840;12.0:Y1p4t5A7ahw:3802;13.0:upMacF5Gpic:3830;14.0:7q1tn9AK5GQ:3801;15.0:JI6fOwoFb08:3935;16.0:VT_7LYBg66E:3851;17.0:x6cK3uZTBqM:3775;18.0:RRhjY1VNw5Y:3819;19.0:FXlh3KgFZVA:3782;20.0:QOt4axZSXDA:3825;21.0:O1d3Dv0voJA:3866;22.0:Lh0i23jQHVU:3842;23.0:0iUEkMy-gyI:3827;24.0:IpugQAry3jk:3844;25.0:gwKLBY7RApg:3868;26.0:YRsd2IyWEbY:3816;27.0:iGqLyNhvgXA:3745;28.0:APr1oR4hXMw:3763;29.0:YeBiOkGXH7E:3847;30.0:5s0aOkYjw_I:3857;31.0:Vn91mqhKESM:3829;32.0:Cs7sAIqeT0g:3843;33.0:4uHDqC-into:3842;34.0:cs1toQMZwmE:3832;35.0:W_kygEQBBh4:3844;36.0:22WcLP5EPbs:3835;37.0:vq_AfOtAf_0:3827;38.0:4rcH4cb48NQ:3823;39.0:8Nr0EDs3Bu8:3860;40.0:Pksidceb1X0:3867;41.0:NbfSiJ9M9as:3826;42.0:Ru7FtOsRUFM:3795;43.0:cfArMvM4Iyc:3840;44.0:xr38Z_d19kE:3845;45.0:Ah140Uo5jvs:3870;46.0:lDiqaNan9kU:3855;47.0:yJuZMF91gdI:3827;48.0:xotFHf_W7Ck:3800;49.0:zgFU6CuZtMk:3813;50.0:YwAxrAy4EBk:3839",
        "goldenlife" to "1.0:AespNs0-l1g:3957;2.0:CncVazPN0b0:4034;3.0:HmqoC0oual8:4021;4.0:zgoNBOEYt5E:4047;5.0:QVewtpLXY-Q:3990;6.0:s_mrj88F4EQ:4041;7.0:YHfXqoDYzVw:4036;8.0:q9hS_PmJ6n4:3989;9.0:LKc7dxrUBRU:3993;10.0:t1r4DCJRKNI:4006;11.0:74WwXHvqHCA:3994;12.0:aMo45n-gA6s:4032;13.0:I6A_M4rnSzQ:3995;14.0:08xhjgPPzSA:3990;15.0:nqBQGmkhGws:3992;16.0:TkS37GKiKnY:3914;17.0:GMzIbLxtGNA:4059;18.0:b-Hfl6_Vc5Y:4045;19.0:8N6Qr4R6SkI:4049;20.0:eJeDSMZrbus:4038;21.0:08Q7bTuKjmQ:4032;22.0:NYMk4M7KlBM:4037;23.0:qdxZJj-tmuw:4048;24.0:kGN9nYlbuns:4040;25.0:XM-3C6RbImY:4016;26.0:gn8VIT_c0DU:4017;27.0:drRi5aLEvLw:4019;28.0:USi2_qBKQTw:4018;29.0:o4Avwo0kJo0:4047;30.0:s9KAIT1zAIk:4047;31.0:D8y8xflDVKg:4043;32.0:3blmpFBEkaI:4043;33.0:Es_mUrPX6Gk:4028;34.0:O9wZV7s-LA4:4037;35.0:sR4Mbsvdnj0:4035;36.0:XlQGK_1uLTY:4045;37.0:-5nhwVZ4oHo:4038;38.0:MHHWGeOP0Ns:4053;39.0:jmWlXGmV57Q:4015;40.0:GPKiisQQCKg:4016;41.0:iVoLf7J9Vbw:4008;42.0:F3HquwARHMg:4009;43.0:N0gqRRaaEiY:4043;44.0:AOImQNN_IF4:4047;45.0:0XYCYnPraWw:4045;46.0:tX31GLA2OOE:4027;47.0:JRtCAJDJyhU:4049;48.0:7T1zDEzM224:4044;49.0:BjsaYB0RglY:4038;50.0:CyQXuCzxofQ:4292;51.0:6Jvg1p9TE0w:4069;52.0:U9yyxPFoHOA:4504",
        "maninmask" to "1.0:QMdNmSg384Y:3472;2.0:1onD7Ymm56Y:3561;3.0:xwQBEee4jjs:3558;4.0:dzZvH6nCxqk:3553;5.0:2B0YbAsYVfs:3556;6.0:xv_u_qevrXc:3528;7.0:B-xNf-5jYtY:3577;8.0:ENgsbc61Ejs:3565;9.0:euKhkfkmw30:3576;10.0:_Qb_8X0KuOQ:3576;11.0:iLBK4lOLvxU:3533;12.0:eyFg-YgVtpg:3537;13.0:S_E8nXaNKe8:3536;14.0:S2oOnavq0nw:3533;15.0:lxJPwLh5la0:3727;16.0:QNXmO4vESwo:3696",
    )

    /** KBS Drama Special — законченные фильмы по часу: videoId, длительность, название. */
    private val specials: List<Triple<String, Int, String>> = listOf(
        Triple("T2l-llbL2lk", 3948, "Забытое время года"),
        Triple("Kc0uDCglq8k", 3828, "Тайна мисс Ким"),
        Triple("W-1IMejVMxw", 3841, "Такое долгое прощание"),
        Triple("uX8Ez500P9w", 4177, "Сон в разгар лета"),
        Triple("vZ7EMIH0oHw", 4009, "Если бы мы были временами года"),
        Triple("yOd7Brk1cd8", 4102, "Тетрадь моих позорных ошибок"),
        Triple("65WYszK8A78", 3922, "Тунец и дельфин"),
        Triple("AqHDfUAZqzU", 3859, "Нос Пиноккио"),
        Triple("W81-kqZVljk", 3954, "Роман средь бела дня"),
        Triple("-wCBrOILa4M", 3872, "Так близко и так далеко"),
        Triple("5Fp34PsN-3Q", 3793, "Третий брак моей мамы"),
        Triple("CpSi7w78h6g", 3941, "Дай нам встретиться"),
        Triple("k1uc7jKBZ-A", 4342, "Девушка из лапшичной"),
        Triple("fdPn5ddBFaU", 3651, "Беглецы"),
    )

    private val cache = HashMap<String, List<Episode>>()

    fun forDrama(id: String): List<Episode> = cache.getOrPut(id) {
        if (id == "specials") {
            specials.mapIndexed { i, (vid, sec, title) -> Episode(i + 1, 0, vid, sec, title) }
        } else {
            raw[id].orEmpty().split(';').filter { it.isNotBlank() }.map { item ->
                val (num, vid, sec) = item.split(':')
                val (ep, part) = num.split('.').map { it.toInt() }
                Episode(ep, part, vid, sec.toInt())
            }
        }
    }
}
