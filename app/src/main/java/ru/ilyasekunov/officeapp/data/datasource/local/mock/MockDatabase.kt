package ru.ilyasekunov.officeapp.data.datasource.local.mock

import ru.ilyasekunov.officeapp.data.model.Comment
import ru.ilyasekunov.officeapp.data.model.IdeaAuthor
import ru.ilyasekunov.officeapp.data.model.IdeaPost
import ru.ilyasekunov.officeapp.data.model.Office
import ru.ilyasekunov.officeapp.data.model.SortingCategory
import ru.ilyasekunov.officeapp.data.model.User
import java.time.LocalDateTime

val SortingCategories = listOf(
    SortingCategory(id = 1, name = "Лайкам"),
    SortingCategory(id = 2, name = "Дизлайкам"),
    SortingCategory(id = 3, name = "Комментариям")
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
    name = "Дмитрий",
    surname = "Комарницкий",
    job = "Сотрудник Tinkoff",
    office = Offices[2],
    photo = ""
)

var User: User? = userInfoPreview

val ideaAuthor = IdeaAuthor(
    id = 1,
    name = "Дмитрий",
    surname = "Комарницкий",
    job = "Сотрудник Tinkoff",
    photo = "https://i.pinimg.com/474x/9f/e4/e4/9fe4e42a2f83f78caef84579c1f1980b.jpg",
    office = Offices[2]
)

val Users = mutableListOf(ideaAuthor, User!!.toIdeaAuthor())

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

val Comments = mutableListOf(
    Comment(
        id = 0,
        author = ideaAuthor,
        content = "Ну да, ну да",
        date = LocalDateTime.now(),
        isLikePressed = true,
        likesCount = 234,
        isDislikePressed = false,
        dislikesCount = 23
    ),
    Comment(
        id = 1,
        author = ideaAuthor,
        content = "Ну да, ну да",
        date = LocalDateTime.now(),
        isLikePressed = true,
        likesCount = 234,
        attachedImage = "https://img.freepik.com/free-photo/painting-mountain-lake-with-mountain-background_188544-9126.jpg",
        isDislikePressed = false,
        dislikesCount = 23
    ),
    Comment(
        id = 2,
        author = ideaAuthor,
        content = "Ну да, ну да",
        date = LocalDateTime.now(),
        isLikePressed = true,
        likesCount = 234,
        isDislikePressed = false,
        dislikesCount = 23
    ),
    Comment(
        id = 3,
        author = ideaAuthor,
        content = "Ну да, ну да",
        date = LocalDateTime.now(),
        isLikePressed = true,
        likesCount = 234,
        isDislikePressed = false,
        dislikesCount = 23
    ),
    Comment(
        id = 4,
        author = ideaAuthor,
        content = "Ну да, ну да",
        date = LocalDateTime.now(),
        isLikePressed = true,
        likesCount = 234,
        isDislikePressed = false,
        dislikesCount = 23
    ),
    Comment(
        id = 5,
        author = ideaAuthor,
        content = "Ну да, ну да",
        date = LocalDateTime.now(),
        isLikePressed = true,
        likesCount = 234,
        isDislikePressed = false,
        dislikesCount = 23
    ),
    Comment(
        id = 6,
        author = ideaAuthor,
        content = "Ну да, ну да",
        date = LocalDateTime.now(),
        isLikePressed = true,
        likesCount = 234,
        isDislikePressed = false,
        dislikesCount = 23
    )
)

val Posts = mutableListOf(ideaPost)