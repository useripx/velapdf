package com.njagakneai.velapdf.data.repository

import android.content.Context
import androidx.core.content.FileProvider
import com.njagakneai.velapdf.data.model.SelectedImage
import com.njagakneai.velapdf.utils.PdfGenerator
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import java.io.File

class PdfRepository(private val context: Context) {

    /**
     * Generates a PDF from a list of selected images.
     * Emits [PdfGenerationState] for each progress step.
     *
     * Uses [channelFlow] instead of [flow] because progress callbacks
     * originate from Dispatchers.IO inside PdfGenerator.
     */
    fun generatePdf(
        images: List<SelectedImage>,
        outputFileName: String
    ): Flow<PdfGenerationState> = channelFlow {
        send(PdfGenerationState.Loading(0))
        try {
            val outputDir = File(context.cacheDir, "pdf_exports")
            if (!outputDir.exists()) {
                outputDir.mkdirs()
            }

            val outputFile = File(outputDir, "$outputFileName.pdf")

            val file = PdfGenerator.generatePdfFromImages(
                context = context,
                images = images,
                outputFile = outputFile,
                compressionLevel = com.njagakneai.velapdf.data.model.CompressionLevel.BIASA,
                onProgress = { progress ->
                    trySend(PdfGenerationState.Loading(progress))
                }
            )

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            send(PdfGenerationState.Success(uri))
        } catch (e: Exception) {
            e.printStackTrace()
            send(PdfGenerationState.Error(e))
        }
    }
}
