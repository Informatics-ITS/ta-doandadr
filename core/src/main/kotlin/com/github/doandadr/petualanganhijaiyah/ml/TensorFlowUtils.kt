package com.github.doandadr.petualanganhijaiyah.ml

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object TensorFlowUtils {
    fun convertValuesToImage(imageValues: FloatArray, display: Boolean = false): BufferedImage {
        // Reshape and normalize the image values
        val width = 64
        val height = 64
        val imageArray = Array(height) { FloatArray(width) }
        for (i in imageValues.indices) {
            val row = i / width
            val col = i % width
            imageArray[row][col] = imageValues[i]
        }

        // Convert to BufferedImage
        val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)
        for (y in 0 until height) {
            for (x in 0 until width) {
                val value = (imageArray[y][x] * 255).toInt()
                val rgb = (value shl 16) or (value shl 8) or value
                bufferedImage.setRGB(x, y, rgb)
            }
        }

        // Optionally display the image
        if (display) {
            val file = File("image.png")
            ImageIO.write(bufferedImage, "png", file)
            println("Image saved to ${file.absolutePath}")
        }

        return bufferedImage
    }

    fun sortPredictions(predictions: FloatArray): List<Pair<Int, Float>> {
        return predictions.mapIndexed { index, value -> index to value }.sortedByDescending { it.second }
    }
}
