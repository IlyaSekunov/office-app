package ru.ilyasekunov.officeapp.ui

import ru.ilyasekunov.officeapp.data.model.Comment

fun Comment.updateLike(): Comment {
    val newIsPressed = !isLikePressed
    val newLikesCount = if (newIsPressed) likesCount + 1 else likesCount - 1
    return if (isDislikePressed) {
        this.copy(
            isDislikePressed = false,
            dislikesCount = dislikesCount - 1,
            isLikePressed = newIsPressed,
            likesCount = newLikesCount
        )
    } else {
        this.copy(
            isLikePressed = newIsPressed,
            likesCount = newLikesCount
        )
    }
}

fun Comment.updateDislike(): Comment {
    val newIsPressed = !isDislikePressed
    val newDislikesCount = if (newIsPressed) dislikesCount + 1 else dislikesCount - 1
    return if (isLikePressed) {
        this.copy(
            isLikePressed = false,
            likesCount = likesCount - 1,
            isDislikePressed = newIsPressed,
            dislikesCount = newDislikesCount
        )
    } else {
        this.copy(
            isDislikePressed = newIsPressed,
            dislikesCount = newDislikesCount
        )
    }
}