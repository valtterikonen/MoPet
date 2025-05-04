package com.example.mobilepet.screens

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.graphics.*
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mobilepet.R
import com.example.mobilepet.models.PetModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@Composable
fun PetPicture(navController: NavController) {
    val context = LocalContext.current
    val petModel: PetModel = viewModel()
    val pet = petModel.pet
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var showGallery by remember { mutableStateOf(false) }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        hasCameraPermission = it
    }

    LaunchedEffect(true) {
        if (!hasCameraPermission) permissionLauncher.launch(android.Manifest.permission.CAMERA)
    }

    if (!hasCameraPermission) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Camera permission required")
        }
        return
    }

    val imageCapture = remember { ImageCapture.Builder().build() }
    val previewView = remember { PreviewView(context) }
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {

            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            )

            val petResId = when (pet?.type) {
                "Dog" -> R.drawable.dog_crop
                "Cat" -> R.drawable.kissa_crop
                "Hedgehog" -> R.drawable.siili_crop
                else -> null
            }

            petResId?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    modifier = Modifier
                        .size(240.dp)
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 72.dp)
                )
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(48.dp)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", modifier = Modifier.size(32.dp))
                }
                Text("Take a Picture", fontSize = 18.sp)
                IconButton(onClick = { showGallery = true }, modifier = Modifier.size(48.dp)) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Gallery", modifier = Modifier.size(32.dp))
                }
            }

            Column(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                    val outputDirectory = context.cacheDir
                    val photoFile = File(
                        outputDirectory,
                        "${System.currentTimeMillis()}.jpg"
                    )
                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
                    val executor: Executor = Executors.newSingleThreadExecutor()

                    imageCapture.takePicture(
                        outputOptions,
                        executor,
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                                val finalBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
                                val canvas = Canvas(finalBitmap)
                                canvas.drawBitmap(bitmap, 0f, 0f, null)

                                petResId?.let { resId ->
                                    val overlay = BitmapFactory.decodeResource(context.resources, resId)
                                    val scaledOverlay = Bitmap.createScaledBitmap(overlay, bitmap.width / 3, bitmap.width / 3, false)
                                    val left = (bitmap.width - scaledOverlay.width) / 2f
                                    val top = bitmap.height - scaledOverlay.height - 50f
                                    canvas.drawBitmap(scaledOverlay, left, top, null)
                                }

                                saveBitmapToGallery(context, finalBitmap) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(if (it) "Image saved!" else "Error saving image.")
                                    }
                                }
                            }

                            override fun onError(exception: ImageCaptureException) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Virhe: ${exception.message}")
                                }
                            }
                        }
                    )
                },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    ) {
                    Text("Take Picture")
                }
            }

            if (showGallery) {
                GalleryDialog(context = context, onDismiss = { showGallery = false })
            }
        }
    }
}

fun saveBitmapToGallery(context: Context, bitmap: Bitmap, onResult: (Boolean) -> Unit) {
    val filename = "petshot_${System.currentTimeMillis()}.jpg"
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/VirtualPet")
        }
    }

    val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    val outputStream: OutputStream? = uri?.let { context.contentResolver.openOutputStream(it) }

    outputStream?.use {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        onResult(true)
    } ?: onResult(false)
}

@Composable
fun GalleryDialog(context: Context, onDismiss: () -> Unit) {
    val images = remember { getImagesFromMediaStore(context) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Gallery", Modifier.padding(start = 16.dp), fontSize = 18.sp)
                    Icon(Icons.Default.Close, contentDescription = "Close",
                        modifier = Modifier
                            .clickable { onDismiss() }
                            .padding(8.dp))
                }
                LazyColumn {
                    items(images) { uri ->
                        AsyncImage(
                            model = uri,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

fun getImagesFromMediaStore(context: Context): List<android.net.Uri> {
    val imageUris = mutableListOf<android.net.Uri>()
    val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.RELATIVE_PATH)
    val selection = "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
    val selectionArgs = arrayOf("%Pictures/VirtualPet/%")
    val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

    context.contentResolver.query(collection, projection, selection, selectionArgs, sortOrder)?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val uri = android.content.ContentUris.withAppendedId(collection, id)
            imageUris.add(uri)
        }
    }
    return imageUris
}