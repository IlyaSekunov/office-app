package ru.ilyasekunov.officeapp.preview

import ru.ilyasekunov.officeapp.data.model.User
import ru.ilyasekunov.officeapp.data.officeList

val userInfoPreview = User(
    id = 0,
    email = "dktinkoff@yandex.ru",
    password = "12345",
    name = "Дмитрий",
    surname = "Комарницкий",
    job = "Сотрудник Tinkoff",
    office = officeList[2]
)