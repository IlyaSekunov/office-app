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
    id = 1,
    email = "dktinkoff@yandex.ru",
    name = "Дмитрий",
    surname = "Комарницкий",
    job = "Сотрудник Tinkoff",
    office = Offices[2],
    photo = "https://img2.akspic.ru/previews/2/7/7/4/7/174772/174772-skelet-18650-past-rebro-kost-500x.jpg"
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

val Users = mutableListOf(
    ideaAuthor,
    ideaAuthor.copy(id = 2),
    ideaAuthor.copy(id = 3)
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
    commentsCount = 342,
    isInProgress = false,
    isImplemented = false,
    isSuggestedToMyOffice = false
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

val Posts = mutableListOf(
    ideaPost,
    ideaPost.copy(
        id = 1,
        attachedImages = listOf(
            "https://i.pinimg.com/236x/93/ed/3a/93ed3af6411e1e8b997038c74c287a8a.jpg",
            "https://bigpicture.ru/wp-content/uploads/2009/12/wave01-800x565.jpg"
        )
    ),
    ideaPost.copy(
        id = 2,
        attachedImages = listOf(
            "https://love.romanticcollection.ru/wp-content/uploads/priznaniya-44.jpg",
            "https://fantiky.ru/wp-content/uploads/2023/03/tiger-130323-1-576x1024-1-450x800.jpg"
        )
    ),
    ideaPost.copy(id = 3),
    ideaPost.copy(id = 4),
    ideaPost.copy(
        id = 5,
        attachedImages = listOf(
            "https://avatars.dzeninfra.ru/get-zen-vh/5413359/2a00000185b7739bc7fa55e2e588dfcb8707/1080x1920"
        )
    ),
    ideaPost.copy(id = 6),
    ideaPost.copy(
        id = 7,
        attachedImages = listOf(
            "https://img.stablecog.com/insecure/1920w/aHR0cHM6Ly9iLnN0YWJsZWNvZy5jb20vMjAyZDNmYmItNDNlYi00MjU1LWEzNjYtYTM3MWIzM2E4NWFlLmpwZWc.webp",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQVJS04WndV6zhVK60zhRM17bS9aCEmH3LagJ_7VhFIFA&s",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRsGkKR5KYFXlcf45zzk_xcizhzGY3AX-iG3yGjFWqK4A&s"
        )
    ),
    ideaPost.copy(id = 8),
    ideaPost.copy(id = 9),
    ideaPost.copy(
        id = 10,
        attachedImages = listOf(
            "https://masterpiecer-images.s3.yandex.net/5dccb025621011ee9210222e7fa838a6:upscaled"
        )
    ),
)