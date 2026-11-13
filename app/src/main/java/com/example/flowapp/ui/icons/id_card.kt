package com.example.flowapp.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Suppress("CheckReturnValue")
public val id_card: ImageVector
  get() {
    if (_id_card != null) {
      return _id_card!!
    }
    _id_card =
      ImageVector.Builder(
          name = "id_card",
          defaultWidth = 24.dp,
          defaultHeight = 24.dp,
          viewportWidth = 24f,
          viewportHeight = 24f,
        )
        .apply {
          path(
            fill = SolidColor(Color.Black),
            fillAlpha = 1f,
            stroke = null,
            strokeAlpha = 1f,
            strokeLineWidth = 1f,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Bevel,
            strokeLineMiter = 1f,
            pathFillType = PathFillType.Companion.NonZero,
          ) {
            moveTo(14f, 13f)
            horizontalLineToRelative(5f)
            verticalLineTo(11f)
            horizontalLineTo(14f)
            verticalLineToRelative(2f)
            close()
            moveToRelative(0f, -3f)
            horizontalLineToRelative(5f)
            verticalLineTo(8f)
            horizontalLineTo(14f)
            verticalLineToRelative(2f)
            close()
            moveTo(5f, 16f)
            horizontalLineToRelative(8f)
            verticalLineTo(15.45f)
            quadToRelative(0f, -1.13f, -1.1f, -1.79f)
            reflectiveQuadTo(9f, 13f)
            reflectiveQuadTo(6.1f, 13.66f)
            reflectiveQuadTo(5f, 15.45f)
            verticalLineTo(16f)
            close()
            moveToRelative(5.41f, -4.59f)
            quadTo(11f, 10.83f, 11f, 10f)
            quadTo(11f, 9.17f, 10.41f, 8.59f)
            reflectiveQuadTo(9f, 8f)
            quadTo(8.18f, 8f, 7.59f, 8.59f)
            reflectiveQuadTo(7f, 10f)
            reflectiveQuadToRelative(0.59f, 1.41f)
            reflectiveQuadTo(9f, 12f)
            quadToRelative(0.83f, 0f, 1.41f, -0.59f)
            close()
            moveTo(4f, 20f)
            quadTo(3.18f, 20f, 2.59f, 19.41f)
            reflectiveQuadTo(2f, 18f)
            verticalLineTo(6f)
            quadTo(2f, 5.18f, 2.59f, 4.59f)
            reflectiveQuadTo(4f, 4f)
            horizontalLineTo(20f)
            quadToRelative(0.83f, 0f, 1.41f, 0.59f)
            quadTo(22f, 5.18f, 22f, 6f)
            verticalLineTo(18f)
            quadToRelative(0f, 0.82f, -0.59f, 1.41f)
            reflectiveQuadTo(20f, 20f)
            horizontalLineTo(4f)
            close()
            moveTo(4f, 18f)
            horizontalLineTo(20f)
            verticalLineTo(6f)
            horizontalLineTo(4f)
            verticalLineTo(18f)
            close()
            moveToRelative(0f, 0f)
            verticalLineTo(6f)
            verticalLineTo(18f)
            close()
          }
        }
        .build()
    return _id_card!!
  }

private var _id_card: ImageVector? = null
