package ru.ilyasekunov.officeapp.preview

import ru.ilyasekunov.officeapp.data.model.IdeaAuthor
import ru.ilyasekunov.officeapp.data.model.IdeaPost
import ru.ilyasekunov.officeapp.data.officeList
import java.time.LocalDateTime

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
    office = officeList[1],
    attachedImages = emptyList(),
    likesCount = 1123,
    isLikePressed = true,
    dislikesCount = 101,
    isDislikePressed = false,
    commentsCount = 342
)

