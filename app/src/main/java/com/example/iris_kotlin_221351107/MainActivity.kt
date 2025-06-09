package com.example.iris_kotlin_221351107

import android.content.res.AssetManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class MainActivity : AppCompatActivity() {

    private lateinit var interpreter: Interpreter
    private val modelPath = "iris.tflite"

    private lateinit var resultText: TextView
    private lateinit var edtSepalLength: EditText
    private lateinit var edtSepalWidth: EditText
    private lateinit var edtPetalLength: EditText
    private lateinit var edtPetalWidth: EditText
    private lateinit var checkButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inisialisasi view
        resultText       = findViewById(R.id.txtResult)
        edtSepalLength   = findViewById(R.id.edtSepalLengthCm)
        edtSepalWidth    = findViewById(R.id.edtSepalWidthCm)
        edtPetalLength   = findViewById(R.id.edtPetalLengthCm)
        edtPetalWidth    = findViewById(R.id.edtPetalWidthCm)
        checkButton      = findViewById(R.id.btnCheck)

        checkButton.setOnClickListener {
            val result = doInference(
                edtSepalLength.text.toString(),
                edtSepalWidth.text.toString(),
                edtPetalLength.text.toString(),
                edtPetalWidth.text.toString()
            )

            resultText.text = when (result) {
                0 -> "iris-setosa"
                1 -> "iris-versicolor"
                else -> "iris-virginica"
            }
        }

        initInterpreter()
    }

    private fun initInterpreter() {
        val options = Interpreter.Options().apply {
            setNumThreads(5)
            setUseNNAPI(true)
        }
        interpreter = Interpreter(loadModelFile(assets, modelPath), options)
    }

    private fun doInference(
        input1: String, input2: String,
        input3: String, input4: String
    ): Int {
        val input = arrayOf(floatArrayOf(
            input1.toFloat(),
            input2.toFloat(),
            input3.toFloat(),
            input4.toFloat()
        ))

        val output = Array(1) { FloatArray(3) }
        interpreter.run(input, output)

        Log.i("result", output[0].toList().toString())
        return output[0].indexOfFirst { it == output[0].maxOrNull() }
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        return fileChannel.map(
            FileChannel.MapMode.READ_ONLY,
            fileDescriptor.startOffset,
            fileDescriptor.declaredLength
        )
    }
}
