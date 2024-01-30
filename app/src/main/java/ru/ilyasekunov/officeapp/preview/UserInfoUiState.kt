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
    photo = "https://mir-s3-cdn-cf.behance.net/project_modules/2800_opt_1/2a58a155982241.599bb53fcaf9f.jpg",
    office = officeList[2]
)