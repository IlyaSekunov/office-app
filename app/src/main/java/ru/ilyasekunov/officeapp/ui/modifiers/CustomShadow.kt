package ru.ilyasekunov.officeapp.ui.modifiers

import android.graphics.BlurMaskFilter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.shadow(
    color: Color = Color.Black.copy(alpha = 0.6f),
    leftSideWidth: Dp = 0.dp,
    topSideWidth: Dp = 0.dp,
    rightSideWidth: Dp = 0.dp,
    bottomSideWidth: Dp = 0.dp,
    blurRadius: Dp = 0.dp
) = this.then(
    Modifier.drawBehind {
        drawIntoCanvas {
            val paint = Paint()
            val frameworkPaint = paint.asFrameworkPaint()
            val leftPixel = 0f - leftSideWidth.toPx()
            val topPixel = 0f - topSideWidth.toPx()
            val rightPixel = size.width + rightSideWidth.toPx()
            val bottomPixel = size.height + bottomSideWidth.toPx()

            if (blurRadius != 0.dp) {
                frameworkPaint.maskFilter =
                    BlurMaskFilter(blurRadius.toPx(), BlurMaskFilter.Blur.NORMAL)
            }

            frameworkPaint.color = color.toArgb()
            it.drawRect(
                left = leftPixel,
                top = topPixel,
                right = rightPixel,
                bottom = bottomPixel,
                paint = paint
            )
        }
    }
)