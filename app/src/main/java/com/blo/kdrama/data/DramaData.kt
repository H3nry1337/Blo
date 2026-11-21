package com.blo.kdrama.data

import java.net.URLEncoder

/**
 * Каталог дорам. Приложение НЕ хранит и не раздаёт видео: оно открывает только
 * официальные бесплатные площадки (официальный YouTube-канал KBS World, Rakuten Viki,
 * Tubi), где сериалы доступны легально — бесплатно с рекламой. Набор серий зависит от страны.
 */
object DramaData {

    val dramas: List<Drama> = listOf(
        Drama(
            id = "dots", title = "Потомки солнца", titleKo = "태양의 후예", titleEn = "Descendants of the Sun",
            year = 2016, network = "KBS2", genres = listOf("Романтика", "Экшен"), difficulty = "Средний", emoji = "🪖",
            description = "Капитан спецназа и хирург знакомятся в Сеуле и снова встречаются в зоне миротворческой миссии.",
            learn = "Армейская и медицинская лексика, вежливые формы -요 и -습니다.",
            phrases = listOf(
                w("사과할까요? 고백할까요?", "sagwahalkkayo? gobaekalkkayo?", "Мне извиниться или признаться?"),
                w("군인", "gunin", "военный"),
                w("의사", "uisa", "врач"),
                w("대위", "daewi", "капитан"),
                w("작전", "jakjeon", "операция (военная)"),
                w("살려 주세요", "sallyeo juseyo", "спасите!"),
            ),
            onKbsWorld = true,
        ),
        Drama(
            id = "fightmyway", title = "Иду своей дорогой", titleKo = "쌈, 마이웨이", titleEn = "Fight for My Way",
            year = 2017, network = "KBS2", genres = listOf("Романтика", "Комедия"), difficulty = "Новичок", emoji = "🥊",
            description = "Четверо друзей, которым говорят «вам не по статусу мечтать», упрямо идут к своей цели.",
            learn = "Живой банмаль между друзьями, пусанский акцент, шутки.",
            phrases = listOf(
                w("꿈", "kkum", "мечта; сон"),
                w("포기하지 마", "pogihaji ma", "не сдавайся"),
                w("남사친", "namsachin", "друг-парень (просто друг)"),
                w("내 편", "nae pyeon", "на моей стороне"),
                w("격투기", "gyeoktugi", "смешанные единоборства"),
                w("우리 사귈래?", "uri sagwillae?", "давай встречаться?"),
            ),
            onKbsWorld = true,
        ),
        Drama(
            id = "camellia", title = "Когда цветёт камелия", titleKo = "동백꽃 필 무렵", titleEn = "When the Camellia Blooms",
            year = 2019, network = "KBS2", genres = listOf("Мелодрама", "Детектив", "Комедия"), difficulty = "Средний", emoji = "🌺",
            description = "Мать-одиночка открывает бар в маленьком городке, а простодушный полицейский влюбляется в неё с первого взгляда.",
            learn = "Провинциальный диалект (사투리), речь маленького городка, слова поддержки.",
            phrases = listOf(
                w("동백꽃", "dongbaekkot", "камелия"),
                w("경찰", "gyeongchal", "полиция, полицейский"),
                w("술집", "suljip", "бар"),
                w("동네", "dongne", "район, родной квартал"),
                w("사투리", "saturi", "диалект"),
                w("힘내요", "himnaeyo", "держитесь! не сдавайтесь!"),
            ),
            onKbsWorld = true,
        ),
        Drama(
            id = "producers", title = "Продюсеры", titleKo = "프로듀사", titleEn = "The Producers",
            year = 2015, network = "KBS2", genres = listOf("Комедия", "Романтика", "Офис"), difficulty = "Средний", emoji = "🎥",
            description = "Закулисье развлекательного отдела телеканала: съёмки шоу, капризные звёзды и офисная романтика.",
            learn = "Офисная иерархия, слова из мира ТВ, обращения 선배 / 후배.",
            phrases = listOf(
                w("방송국", "bangsongguk", "телеканал (здание)"),
                w("피디", "pidi", "продюсер-режиссёр (PD)"),
                w("예능", "yeneung", "развлекательное шоу"),
                w("촬영", "chwaryeong", "съёмки"),
                w("시청률", "sicheongnyul", "телерейтинг"),
                w("선배", "seonbae", "старший коллега"),
            ),
            onKbsWorld = true,
        ),
        Drama(
            id = "bof", title = "Мальчики краше цветов", titleKo = "꽃보다 남자", titleEn = "Boys Over Flowers",
            year = 2009, network = "KBS2", genres = listOf("Романтика", "Школа"), difficulty = "Новичок", emoji = "🌸",
            description = "Девушка из простой семьи попадает в элитную школу и бросает вызов компании богатых парней F4.",
            learn = "Простые школьные диалоги, эмоциональные восклицания, банмаль.",
            phrases = listOf(
                w("학교", "hakgyo", "школа"),
                w("부자", "buja", "богач"),
                w("서민", "seomin", "простой человек (не богач)"),
                w("괴롭히다", "goerophida", "издеваться, травить"),
                w("잡초", "japcho", "сорняк (символ стойкости героини)"),
                w("데이트", "deiteu", "свидание"),
            ),
            onKbsWorld = true,
        ),
        Drama(
            id = "moonlight", title = "Озарённые луной", titleKo = "구르미 그린 달빛", titleEn = "Love in the Moonlight",
            year = 2016, network = "KBS2", genres = listOf("Исторический", "Романтика"), difficulty = "Продвинутый", emoji = "🌙",
            description = "Эпоха Чосон: девушка, переодетая евнухом, попадает на службу к наследному принцу.",
            learn = "Придворная речь сагыков: 저하, -옵니다, обращения к королевской семье.",
            phrases = listOf(
                w("저하", "jeoha", "Ваше Высочество (наследному принцу)"),
                w("세자", "seja", "наследный принц"),
                w("내관", "naegwan", "евнух при дворе"),
                w("궁", "gung", "дворец"),
                w("소인", "soin", "я, ничтожный слуга"),
                w("아니 되옵니다", "ani doeomnida", "нельзя (почтительно)"),
            ),
            onKbsWorld = true,
        ),
        Drama(
            id = "school2013", title = "Школа 2013", titleKo = "학교 2013", titleEn = "School 2013",
            year = 2012, network = "KBS2", genres = listOf("Школа", "Драма"), difficulty = "Новичок", emoji = "🏫",
            description = "Будни одного класса старшей школы глазами молодой учительницы и её непростых учеников.",
            learn = "Школьная лексика, простой разговорный язык подростков.",
            phrases = listOf(
                w("반", "ban", "класс (группа учеников)"),
                w("담임 선생님", "damim seonsaengnim", "классный руководитель"),
                w("수업", "sueop", "урок"),
                w("시험", "siheom", "экзамен"),
                w("왕따", "wangtta", "изгой, травля"),
                w("졸업", "joreop", "выпуск, окончание школы"),
            ),
            onKbsWorld = true,
        ),
        Drama(
            id = "goblin", title = "Токкэби (Гоблин)", titleKo = "도깨비", titleEn = "Guardian: The Lonely and Great God",
            year = 2016, network = "tvN", genres = listOf("Фэнтези", "Романтика"), difficulty = "Средний", emoji = "🗡️",
            description = "Бессмертный токкэби ищет невесту, которая сможет прервать его вечную жизнь.",
            learn = "Поэтичные фразы, лексика судьбы и загробного мира.",
            phrases = listOf(
                w("날이 좋아서, 날이 좋지 않아서, 날이 적당해서, 모든 날이 좋았다", "nari joaseo, nari jochi anaseo, nari jeokdanghaeseo, modeun nari joatda", "Потому что был хороший день, потому что был плохой день, потому что был обычный день — все дни были прекрасны"),
                w("도깨비", "dokkaebi", "токкэби, гоблин"),
                w("신부", "sinbu", "невеста"),
                w("저승사자", "jeoseungsaja", "жнец, проводник душ"),
                w("첫눈", "cheonnun", "первый снег"),
                w("운명", "unmyeong", "судьба"),
            ),
        ),
        Drama(
            id = "cloy", title = "Аварийная посадка любви", titleKo = "사랑의 불시착", titleEn = "Crash Landing on You",
            year = 2019, network = "tvN", genres = listOf("Романтика", "Комедия"), difficulty = "Средний", emoji = "🪂",
            description = "Южнокорейская наследница после аварии на параплане оказывается в Северной Корее.",
            learn = "Разница между южным и северным корейским, бизнес-лексика.",
            phrases = listOf(
                w("불시착", "bulsichak", "аварийная посадка"),
                w("북한", "bukan", "Северная Корея"),
                w("남한", "namhan", "Южная Корея"),
                w("동무", "dongmu", "товарищ (северное)"),
                w("재벌", "jaebeol", "чеболь, олигарх"),
                w("보고 싶었어", "bogo sipeosseo", "я скучал по тебе"),
            ),
        ),
        Drama(
            id = "secretarykim", title = "Что случилось с секретарём Ким?", titleKo = "김비서가 왜 그럴까", titleEn = "What's Wrong with Secretary Kim",
            year = 2018, network = "tvN", genres = listOf("Романтика", "Комедия", "Офис"), difficulty = "Новичок", emoji = "💼",
            description = "Самовлюблённый вице-председатель в шоке: его идеальный секретарь решила уволиться.",
            learn = "Офисная вежливость, официальные формы -습니다, романтическая комедия.",
            phrases = listOf(
                w("비서", "biseo", "секретарь"),
                w("부회장님", "buhoejangnim", "господин вице-председатель"),
                w("퇴사하다", "toesahada", "уволиться"),
                w("완벽하다", "wanbyeokada", "идеальный"),
                w("왜 그럴까?", "wae geureolkka?", "почему же так?"),
                w("수고하셨습니다", "sugohasyeotseumnida", "спасибо за работу"),
            ),
        ),
        Drama(
            id = "reply1988", title = "Ответ 1988", titleKo = "응답하라 1988", titleEn = "Reply 1988",
            year = 2015, network = "tvN", genres = listOf("Семейный", "Комедия"), difficulty = "Средний", emoji = "📼",
            description = "Пять семей из одного сеульского переулка в конце 80-х: дружба, первая любовь и семейное тепло.",
            learn = "Семейная лексика, обращение по имени с -아/-야, тёплая бытовая речь.",
            phrases = listOf(
                w("골목", "golmok", "переулок"),
                w("이웃", "iut", "сосед"),
                w("덕선아!", "deokseona!", "Доксон! (зов по имени с -아)"),
                w("첫사랑", "cheotsarang", "первая любовь"),
                w("밥 먹자", "bap meokja", "давай поедим"),
                w("가족", "gajok", "семья"),
            ),
        ),
        Drama(
            id = "hospital", title = "Мудрая жизнь в больнице", titleKo = "슬기로운 의사생활", titleEn = "Hospital Playlist",
            year = 2020, network = "tvN", genres = listOf("Медицина", "Дружба"), difficulty = "Средний", emoji = "🩺",
            description = "Пятеро друзей-врачей со студенческих времён работают в одной больнице и играют в своей группе.",
            learn = "Медицинская лексика, речь близких друзей, вежливость в больнице.",
            phrases = listOf(
                w("병원", "byeongwon", "больница"),
                w("수술", "susul", "операция"),
                w("환자", "hwanja", "пациент"),
                w("교수님", "gyosunim", "профессор"),
                w("동기", "donggi", "однокурсник"),
                w("밴드", "baendeu", "музыкальная группа"),
            ),
        ),
        Drama(
            id = "itaewon", title = "Итэвон класс", titleKo = "이태원 클라쓰", titleEn = "Itaewon Class",
            year = 2020, network = "JTBC", genres = listOf("Драма", "Бизнес"), difficulty = "Средний", emoji = "🍻",
            description = "Парень, потерявший всё, открывает бар в Итэвоне и бросает вызов крупной корпорации.",
            learn = "Бизнес-лексика, мотивационные монологи, речь молодёжи.",
            phrases = listOf(
                w("복수", "boksu", "месть"),
                w("가게", "gage", "заведение, магазин"),
                w("사장", "sajang", "хозяин, владелец"),
                w("포차", "pocha", "уличный бар"),
                w("소신", "sosin", "убеждения"),
                w("무릎 꿇어", "mureup kkureo", "встань на колени"),
            ),
        ),
        Drama(
            id = "star", title = "Любовь со звезды", titleKo = "별에서 온 그대", titleEn = "My Love from the Star",
            year = 2013, network = "SBS", genres = listOf("Фэнтези", "Романтика"), difficulty = "Средний", emoji = "⭐",
            description = "Пришелец живёт на Земле уже 400 лет и неожиданно влюбляется в капризную топ-актрису.",
            learn = "Речь звёзд шоу-бизнеса, ирония, романтические фразы.",
            phrases = listOf(
                w("별", "byeol", "звезда"),
                w("외계인", "oegyein", "инопланетянин"),
                w("배우", "baeu", "актёр, актриса"),
                w("치맥", "chimaek", "курица и пиво"),
                w("첫눈 오는 날", "cheonnun oneun nal", "день первого снега"),
                w("기억", "gieok", "память, воспоминание"),
            ),
        ),
        Drama(
            id = "woo", title = "Необычный адвокат У Ён У", titleKo = "이상한 변호사 우영우", titleEn = "Extraordinary Attorney Woo",
            year = 2022, network = "ENA", genres = listOf("Юридический", "Драма"), difficulty = "Средний", emoji = "🐋",
            description = "Блестящая молодая адвокатесса с аутизмом начинает работать в крупной юридической фирме.",
            learn = "Юридические термины, вежливая речь на работе, палиндромы.",
            phrases = listOf(
                w("변호사", "byeonhosa", "адвокат"),
                w("고래", "gorae", "кит"),
                w("법정", "beopjeong", "зал суда"),
                w("의뢰인", "uiroein", "клиент (адвоката)"),
                w("기러기, 토마토, 스위스, 인도인, 별똥별, 우영우", "gireogi, tomato, seuwiseu, indoin, byeolttongbyeol, u yeong-u", "палиндромы: гусь, помидор, Швейцария, индиец, падающая звезда, У Ён У"),
                w("이상하다", "isanghada", "странный"),
            ),
        ),
        Drama(
            id = "janggeum", title = "Жемчужина дворца", titleKo = "대장금", titleEn = "Jewel in the Palace",
            year = 2003, network = "MBC", genres = listOf("Исторический", "Кулинария"), difficulty = "Продвинутый", emoji = "🍲",
            description = "Сирота становится придворной поварихой, а затем первой женщиной — личным лекарем короля.",
            learn = "Придворная иерархия, кулинарная и медицинская лексика сагыков.",
            phrases = listOf(
                w("수라간", "suragan", "королевская кухня"),
                w("상궁", "sanggung", "придворная дама"),
                w("의녀", "uinyeo", "женщина-лекарь"),
                w("전하", "jeonha", "Ваше Величество"),
                w("음식", "eumsik", "еда, блюдо"),
                w("약초", "yakcho", "лекарственные травы"),
            ),
        ),
    )

    val genres: List<String> get() = dramas.flatMap { it.genres }.distinct()

    fun drama(id: String): Drama? = dramas.find { it.id == id }

    private fun enc(s: String) = URLEncoder.encode(s, "UTF-8")

    /** Ссылки только на официальные бесплатные (с рекламой) площадки. */
    fun sourcesFor(d: Drama): List<Source> = buildList {
        if (d.onKbsWorld) {
            add(
                Source(
                    "YouTube · KBS World TV",
                    "https://www.youtube.com/@KBSWorldTV/search?query=${enc(d.titleEn)}",
                    "Официальный канал KBS: многие дорамы выложены целиком, с субтитрами.",
                ),
            )
        }
        add(
            Source(
                "Rakuten Viki",
                "https://www.viki.com/search?q=${enc(d.titleEn)}",
                "Многие серии бесплатно с рекламой, есть субтитры на разных языках.",
            ),
        )
        add(
            Source(
                "Tubi",
                "https://tubitv.com/search/${enc(d.titleEn)}",
                "Бесплатный легальный стриминг с рекламой (не во всех странах).",
            ),
        )
        add(
            Source(
                "YouTube · трейлеры и клипы",
                "https://www.youtube.com/results?search_query=${enc(d.titleKo + " " + d.network + " 하이라이트")}",
                "Короткие официальные сцены удобно разбирать по фразам.",
            ),
        )
    }

    /** Общие легальные площадки с бесплатными корейскими сериалами. */
    val freePlatforms: List<Source> = listOf(
        Source("KBS World TV", "https://www.youtube.com/@KBSWorldTV/playlists", "Официальный YouTube-канал KBS: дорамы, шоу, субтитры"),
        Source("Rakuten Viki", "https://www.viki.com/explore?country=korea", "Огромный каталог, бесплатно с рекламой"),
        Source("Tubi", "https://tubitv.com/search/korean%20drama", "Бесплатно с рекламой, доступность зависит от страны"),
    )
}
