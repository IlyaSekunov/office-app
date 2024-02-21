package ru.ilyasekunov.officeapp.data.datasource.local.mock

import ru.ilyasekunov.officeapp.data.model.IdeaAuthor
import ru.ilyasekunov.officeapp.data.model.IdeaPost
import ru.ilyasekunov.officeapp.data.model.Office
import ru.ilyasekunov.officeapp.data.model.SortingCategory
import ru.ilyasekunov.officeapp.data.model.User
import java.time.LocalDateTime

var Token: String? = "user"

val SortingCategories = listOf(
    SortingCategory(id = 0, name = "Лайкам"),
    SortingCategory(id = 1, name = "Дизлайкам"),
    SortingCategory(id = 2, name = "Комментариям")
)

val Offices = listOf(
    Office(
        id = 0,
        imageUrl = "https://arearent.ru/upload/resize_cache/iblock/7e7/1250_600_1/ed79s3ywxiqn6myw26f70ndbkw23hc85.jpg",
        address = "ул. Гагарина 6"
    ),
    Office(
        id = 1,
        imageUrl = "https://cdn.inmyroom.ru/uploads/photo/file/a8/a8e4/base_a8e4096a-9249-4fbe-870d-b08d03a78f24.jpg",
        address ="ул.Коминтерна, д.164"
    ),
    Office(
        id = 2,
        imageUrl = "https://interiorizm.com/wp-content/uploads/2021/02/rabochee-mesto-s-vidom-na-more-interiorizm-05.jpg",
        address = "ул.Большая Печерская, 5/9"
    ),
    Office(
        id = 3,
        imageUrl = "https://cdn.inmyroom.ru/uploads/photo/file/d5/d5c9/photos_show_big_d5c9a014-519c-4f3d-bd44-bfb7a8d0e52a.jpg",
        address = "ул.Ковалихинская, д.8"
    ),
    Office(
        id = 4,
        imageUrl = "https://archello.s3.eu-central-1.amazonaws.com/images/2018/02/03/DFBridgesIISHOT07G20-0.1517653034.5896.jpg",
        address = "ул.Горького, д.146"
    )
)

val userInfoPreview = User(
    id = 0,
    email = "dktinkoff@yandex.ru",
    password = "12345",
    name = "Дмитрий",
    surname = "Комарницкий",
    job = "Сотрудник Tinkoff",
    office = Offices[2]
)

var User: User? = userInfoPreview

val ideaAuthor = IdeaAuthor(
    id = 1,
    name = "Дмитрий",
    surname = "Комарницкий",
    job = "Сотрудник Tinkoff"
)

val ideaPost = IdeaPost(
    id = 0,
    title = "Растительные вставки для стола",
    content = "Нас окружает живая природа. Она помогает человечеству выжить. К примеру, без растений мы не смогли бы прожить и дня.\n" +
            "\n" +
            "Они дарят нам кислород, из них мы не смогли б прожить и дня.\n" +
            "\n" +
            "Также растения незаменимы для производства лекарств.\n" +
            "\n" +
            "Многие растения могут излечить даже самых больных людей. Я очень люблю изучать специальную литературу, в которой рассказывается о пользе растений. ",
    date = LocalDateTime.now(),
    ideaAuthor = ideaAuthor,
    office = Offices[1],
    attachedImages = emptyList(),
    likesCount = 1123,
    isLikePressed = true,
    dislikesCount = 101,
    isDislikePressed = false,
    commentsCount = 342
)

val Posts = mutableListOf(ideaPost)