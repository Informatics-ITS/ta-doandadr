package com.github.doandadr.petualanganhijaiyah.lwjgl3.ml

import com.github.doandadr.petualanganhijaiyah.ml.Recognition
import org.tensorflow.SavedModelBundle
import org.tensorflow.ndarray.NdArrays
import org.tensorflow.ndarray.Shape
import org.tensorflow.types.TFloat32

class DesktopRecognition(private val model: SavedModelBundle) : Recognition {

    override fun predict(input: Array<FloatArray>): FloatArray {
        // Create the NdArray with the desired shape (1, 64, 64, 1)
        val inputShape = Shape.of(1, 64, 64, 1)
        val inputBuffer = NdArrays.ofFloats(inputShape)

        // Fill the input tensor with the normalized pixel values from the 2D array
        for (row in 0 until 64) {
            for (col in 0 until 64) {
                inputBuffer.setFloat(input[row][col], 0, row.toLong(), col.toLong(), 0)
            }
        }
        val inputTensor = TFloat32.tensorOf(inputBuffer)

        // Predict
        val imagePredictions = run {
            val result: TFloat32 = model.session().runner()
                .feed("serving_default_conv2d_5_input:0", inputTensor)
                .fetch("StatefulPartitionedCall:0")
                .run().get(0) as TFloat32
            result.copyTo(NdArrays.ofFloats(result.shape()))
        }
        val predictionSize = imagePredictions.shape().size(1).toInt()

        val predictions = FloatArray(predictionSize)
        // Fill the predictions array with the output values
        for (i in 0 until predictionSize) {
            predictions[i] = imagePredictions.getFloat(0, i.toLong())
        }

        for (i in 0 until predictionSize) {
            print("${"%2d".format(i)}   ")
        }
        println()
        for (j in 0 until imagePredictions.shape().size(1)) {
            val value = imagePredictions.getFloat(0, j)
            print("${"%.2f".format(value)} ")
        }
        println()

        return predictions
    }

}
