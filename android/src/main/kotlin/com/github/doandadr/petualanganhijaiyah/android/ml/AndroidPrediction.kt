package com.github.doandadr.petualanganhijaiyah.android.ml

import com.github.doandadr.petualanganhijaiyah.ml.Model
import com.github.doandadr.petualanganhijaiyah.ml.Recognition
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class AndroidRecognition(private val model: Model) : Recognition {
    override fun predict(input: Array<FloatArray>): FloatArray {
        // Define the input shape
        val inputShape = intArrayOf(1, 64, 64, 1)
        val inputBuffer = TensorBuffer.createFixedSize(inputShape, DataType.FLOAT32)

        // Flatten and normalize the input array
        val inputArray = FloatArray(64 * 64)
        for (i in input.indices) {
            for (j in input[i].indices) {
                inputArray[i * 64 + j] = input[i][j]
            }
        }
        inputBuffer.loadArray(inputArray)

        // Run the model
        val outputs = model.process(inputBuffer)
        val outputBuffer = outputs.outputFeature0AsTensorBuffer

        // Extract the predictions
        return outputBuffer.floatArray
    }
}
