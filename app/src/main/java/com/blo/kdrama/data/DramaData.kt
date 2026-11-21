package com.blo.kdrama.data

/**
 * Каталог: только дорамы, которые телеканал KBS сам выложил целиком и бесплатно
 * на официальном YouTube-канале KBS World TV. Видео воспроизводятся встроенным плеером YouTube.
 */
object DramaData {

    val dramas: List<Drama> = listOf(
        Drama(
            id = "school2015", title = "Школа 2015: Кто ты?", titleKo = "후아유 - 학교 2015", titleEn = "Who Are You: School 2015",
            year = 2015, network = "KBS2", genres = listOf("Школа", "Детектив", "Романтика"), difficulty = "Новичок", emoji = "🏊",
            description = "Сирота Ынби после несчастного случая теряет память, и её принимают за пропавшую сестру-близнеца из элитной школы.",
            learn = "Школьный банмаль, дружба и ссоры подростков, простые бытовые фразы.",
            phrases = listOf(
                w("쌍둥이", "ssangdungi", "близнецы"),
                w("기억 상실", "gieok sangsil", "потеря памяти"),
                w("전학생", "jeonhaksaeng", "новенький (переведённый ученик)"),
                w("수영부", "suyeongbu", "школьная команда пловцов"),
                w("짝", "jjak", "сосед по парте; пара"),
                w("친구잖아", "chingujana", "мы же друзья"),
            ),
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
        ),
        Drama(
            id = "masterofstudy", title = "Бог учёбы", titleKo = "공부의 신", titleEn = "Master of Study",
            year = 2010, network = "KBS2", genres = listOf("Школа", "Комедия"), difficulty = "Новичок", emoji = "📚",
            description = "Бывший хулиган, а теперь адвокат, берётся за худшую школу страны и обещает отправить двоечников в лучший университет.",
            learn = "Лексика учёбы и экзаменов, мотивационные фразы — идеально для тех, кто сам учит язык.",
            phrases = listOf(
                w("공부", "gongbu", "учёба"),
                w("대학", "daehak", "университет"),
                w("수능", "suneung", "вступительный экзамен в вуз"),
                w("꼴찌", "kkoljji", "последний по успеваемости"),
                w("특별반", "teukbyeolban", "спецкласс"),
                w("할 수 있다!", "hal su itda!", "я смогу!"),
            ),
        ),
        Drama(
            id = "dreamhigh2", title = "Одержимые мечтой 2", titleKo = "드림하이 2", titleEn = "Dream High 2",
            year = 2012, network = "KBS2", genres = listOf("Музыка", "Школа", "Романтика"), difficulty = "Новичок", emoji = "🎤",
            description = "Школа искусств «Кирин»: ученики мечтают о большой сцене, а к ним переводят участников популярной айдол-группы.",
            learn = "Мир K-pop: стажёры, прослушивания, дебют. Живая речь ровесников.",
            phrases = listOf(
                w("꿈", "kkum", "мечта"),
                w("아이돌", "aidol", "айдол"),
                w("연습생", "yeonseupsaeng", "стажёр (трейни)"),
                w("오디션", "odisyeon", "прослушивание"),
                w("무대", "mudae", "сцена"),
                w("데뷔", "debwi", "дебют"),
            ),
        ),
        Drama(
            id = "breadlove", title = "Хлеб, любовь и мечты", titleKo = "제빵왕 김탁구", titleEn = "Bread, Love and Dreams",
            year = 2010, network = "KBS2", genres = listOf("Драма", "Семейный"), difficulty = "Средний", emoji = "🥐",
            description = "Внебрачный сын владельца хлебозавода с невероятным нюхом идёт к мечте стать лучшим пекарем Кореи.",
            learn = "Семейная и деловая лексика, уважительная речь к старшим.",
            phrases = listOf(
                w("빵", "ppang", "хлеб"),
                w("제빵사", "jeppangsa", "пекарь"),
                w("반죽", "banjuk", "тесто"),
                w("냄새", "naemsae", "запах"),
                w("회장님", "hoejangnim", "господин председатель"),
                w("어머니", "eomeoni", "мать (вежливо)"),
            ),
        ),
        Drama(
            id = "youarethebest", title = "Ты лучшая, Ли Сун Щин!", titleKo = "최고다 이순신", titleEn = "You Are The Best",
            year = 2013, network = "KBS2", genres = listOf("Семейный", "Романтика"), difficulty = "Средний", emoji = "🌟",
            description = "Младшая дочь большой семьи, у которой ничего не получается, неожиданно пробует себя в актёрстве.",
            learn = "Семейные разговоры, обращения к родственникам, слова поддержки.",
            phrases = listOf(
                w("막내", "mangnae", "младший в семье"),
                w("배우", "baeu", "актёр, актриса"),
                w("실패", "silpae", "неудача"),
                w("포기하다", "pogihada", "сдаваться"),
                w("엄마", "eomma", "мама"),
                w("최고다!", "choegoda!", "ты лучше всех!"),
            ),
        ),
        Drama(
            id = "goldenlife", title = "Моя золотая жизнь", titleKo = "황금빛 내인생", titleEn = "My Golden Life",
            year = 2017, network = "KBS2", genres = listOf("Семейный", "Мелодрама"), difficulty = "Средний", emoji = "✨",
            description = "Девушке, которая тянет на себе семью, говорят, что она — потерянная дочь богатой семьи. Её жизнь резко меняется.",
            learn = "Речь богатых и простых семей, работа, отношения с родителями.",
            phrases = listOf(
                w("재벌", "jaebeol", "чеболь, олигарх"),
                w("딸", "ttal", "дочь"),
                w("아버지", "abeoji", "отец"),
                w("진실", "jinsil", "правда"),
                w("인생", "insaeng", "жизнь (судьба)"),
                w("비정규직", "bijeonggyujik", "временный работник"),
            ),
        ),
        Drama(
            id = "maninmask", title = "Прокурор в маске", titleKo = "복면검사", titleEn = "The Man in the Mask",
            year = 2015, network = "KBS2", genres = listOf("Детектив", "Экшен", "Комедия"), difficulty = "Средний", emoji = "🎭",
            description = "Днём он — прокурор, а по ночам надевает маску и вершит правосудие кулаками.",
            learn = "Юридическая и полицейская лексика, разговорные угрозы и шутки.",
            phrases = listOf(
                w("검사", "geomsa", "прокурор"),
                w("복면", "bongmyeon", "маска (закрывающая лицо)"),
                w("정의", "jeongui", "справедливость"),
                w("범인", "beomin", "преступник"),
                w("증거", "jeunggeo", "улика"),
                w("형사", "hyeongsa", "детектив (полицейский)"),
            ),
        ),
        Drama(
            id = "gunman", title = "Стрелок из Чосона", titleKo = "조선총잡이", titleEn = "Gunman in Joseon",
            year = 2014, network = "KBS2", genres = listOf("Исторический", "Экшен", "Романтика"), difficulty = "Продвинутый", emoji = "🔫",
            description = "Конец эпохи Чосон: лучший мечник страны берёт в руки револьвер, чтобы отомстить за отца.",
            learn = "Речь эпохи Чосон: обращения к господам, вежливые архаичные формы.",
            phrases = listOf(
                w("총", "chong", "ружьё, пистолет"),
                w("칼", "kal", "меч, нож"),
                w("복수", "boksu", "месть"),
                w("나리", "nari", "господин"),
                w("개화", "gaehwa", "модернизация, «просвещение»"),
                w("조선", "joseon", "Чосон (династия)"),
            ),
        ),
        Drama(
            id = "jangyoungsil", title = "Чан Ён Силь", titleKo = "장영실", titleEn = "Jang Youngsil",
            year = 2016, network = "KBS1", genres = listOf("Исторический", "Наука"), difficulty = "Продвинутый", emoji = "🔭",
            description = "Реальная история: сын рабыни становится великим учёным при короле Седжоне и изобретает водяные часы.",
            learn = "Придворная речь, научная лексика, обращения к королю.",
            phrases = listOf(
                w("노비", "nobi", "раб, слуга"),
                w("세종대왕", "sejongdaewang", "король Седжон Великий"),
                w("하늘", "haneul", "небо"),
                w("별", "byeol", "звезда"),
                w("발명", "balmyeong", "изобретение"),
                w("전하", "jeonha", "Ваше Величество"),
            ),
        ),
        Drama(
            id = "specials", title = "KBS Драма-спешл", titleKo = "KBS 드라마 스페셜", titleEn = "KBS Drama Special",
            year = 2018, network = "KBS2", genres = listOf("Антология", "Романтика"), difficulty = "Средний", emoji = "🎞️",
            description = "Короткие законченные истории по часу: романтика, семейная драма, мистика. Можно посмотреть целиком за вечер.",
            learn = "Разные жанры и стили речи в одном месте — хорошо, чтобы проверить себя.",
            phrases = listOf(
                w("단막극", "danmakgeuk", "драма в одной серии"),
                w("이야기", "iyagi", "история"),
                w("결말", "gyeolmal", "концовка"),
                w("주인공", "juingong", "главный герой"),
                w("감독", "gamdok", "режиссёр"),
                w("짧다", "jjalda", "короткий"),
            ),
        ),
    )

    val genres: List<String> get() = dramas.flatMap { it.genres }.distinct()

    fun drama(id: String): Drama? = dramas.find { it.id == id }
}
