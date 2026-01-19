package com.example.quotevault.core.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import kotlin.math.min

enum class CardTemplate {
    SIMPLE, GRADIENT, DARK
}

class QuoteCardGenerator {

    fun generateQuoteCard(
        quote: String,
        author: String,
        template: CardTemplate = CardTemplate.SIMPLE,
        width: Int = 1080,
        height: Int = 1080
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        when (template) {
            CardTemplate.SIMPLE -> drawSimpleCard(canvas, quote, author, width, height)
            CardTemplate.GRADIENT -> drawGradientCard(canvas, quote, author, width, height)
            CardTemplate.DARK -> drawDarkCard(canvas, quote, author, width, height)
        }

        return bitmap
    }

    private fun drawSimpleCard(
        canvas: Canvas,
        quote: String,
        author: String,
        width: Int,
        height: Int
    ) {
        // White background
        canvas.drawColor(Color.WHITE)

        // Quote text
        val quotePaint = Paint().apply {
            color = Color.BLACK
            textSize = 56f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            isAntiAlias = true
        }

        val padding = 80
        val availableWidth = width - 2 * padding

        val wrappedQuote = wrapText(quotePaint, "\"$quote\"", availableWidth)
        val quoteY = height / 2 - 150

        drawWrappedText(canvas, quotePaint, wrappedQuote, padding.toFloat(), quoteY.toFloat(), availableWidth)

        // Author
        val authorPaint = Paint().apply {
            color = Color.GRAY
            textSize = 32f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }

        canvas.drawText("— $author", padding.toFloat(), (height - 200).toFloat(), authorPaint)
    }

    private fun drawGradientCard(
        canvas: Canvas,
        quote: String,
        author: String,
        width: Int,
        height: Int
    ) {
        // Gradient background (blue to purple)
        val topColor = Color.parseColor("#6200EE")
        val bottomColor = Color.parseColor("#BB86FC")

        for (y in 0 until height) {
            val ratio = y.toFloat() / height
            val color = interpolateColor(topColor, bottomColor, ratio)
            canvas.drawLine(0f, y.toFloat(), width.toFloat(), y.toFloat(), Paint().apply { this.color = color })
        }

        // Quote text (white)
        val quotePaint = Paint().apply {
            color = Color.WHITE
            textSize = 56f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val padding = 80
        val availableWidth = width - 2 * padding

        val wrappedQuote = wrapText(quotePaint, "\"$quote\"", availableWidth)
        val quoteY = height / 2 - 150

        drawWrappedText(canvas, quotePaint, wrappedQuote, padding.toFloat(), quoteY.toFloat(), availableWidth)

        // Author (white)
        val authorPaint = Paint().apply {
            color = Color.WHITE
            textSize = 32f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }

        canvas.drawText("— $author", padding.toFloat(), (height - 200).toFloat(), authorPaint)
    }

    private fun drawDarkCard(
        canvas: Canvas,
        quote: String,
        author: String,
        width: Int,
        height: Int
    ) {
        // Dark background
        canvas.drawColor(Color.parseColor("#1F1B24"))

        // Accent line
        val accentPaint = Paint().apply {
            color = Color.parseColor("#BB86FC")
            strokeWidth = 4f
        }
        canvas.drawLine(0f, 150f, width.toFloat(), 150f, accentPaint)

        // Quote text (light text)
        val quotePaint = Paint().apply {
            color = Color.WHITE
            textSize = 56f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }

        val padding = 80
        val availableWidth = width - 2 * padding

        val wrappedQuote = wrapText(quotePaint, "\"$quote\"", availableWidth)
        val quoteY = height / 2 - 100

        drawWrappedText(canvas, quotePaint, wrappedQuote, padding.toFloat(), quoteY.toFloat(), availableWidth)

        // Author
        val authorPaint = Paint().apply {
            color = Color.parseColor("#BFBFBF")
            textSize = 32f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }

        canvas.drawText("— $author", padding.toFloat(), (height - 200).toFloat(), authorPaint)
    }

    private fun wrapText(paint: Paint, text: String, maxWidth: Int): List<String> {
        val result = mutableListOf<String>()
        val words = text.split(" ")
        var currentLine = StringBuilder()

        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            val testWidth = paint.measureText(testLine)

            if (testWidth > maxWidth) {
                if (currentLine.isNotEmpty()) {
                    result.add(currentLine.toString())
                    currentLine = StringBuilder(word)
                } else {
                    result.add(word)
                    currentLine = StringBuilder()
                }
            } else {
                currentLine = StringBuilder(testLine)
            }
        }

        if (currentLine.isNotEmpty()) {
            result.add(currentLine.toString())
        }

        return result
    }

    private fun drawWrappedText(
        canvas: Canvas,
        paint: Paint,
        lines: List<String>,
        x: Float,
        startY: Float,
        maxWidth: Int
    ) {
        var y = startY
        val lineHeight = paint.textSize * 1.5f

        for (line in lines) {
            canvas.drawText(line, x, y, paint)
            y += lineHeight
        }
    }

    private fun interpolateColor(colorA: Int, colorB: Int, ratio: Float): Int {
        val aA = (colorA shr 24) and 0xFF
        val aR = (colorA shr 16) and 0xFF
        val aG = (colorA shr 8) and 0xFF
        val aB = colorA and 0xFF

        val bA = (colorB shr 24) and 0xFF
        val bR = (colorB shr 16) and 0xFF
        val bG = (colorB shr 8) and 0xFF
        val bB = colorB and 0xFF

        val resA = (aA + (bA - aA) * ratio).toInt()
        val resR = (aR + (bR - aR) * ratio).toInt()
        val resG = (aG + (bG - aG) * ratio).toInt()
        val resB = (aB + (bB - aB) * ratio).toInt()

        return (resA shl 24) or (resR shl 16) or (resG shl 8) or resB
    }
}

