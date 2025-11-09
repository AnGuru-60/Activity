package otus.gpb.homework.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.content.DialogInterface
import android.widget.Button
import androidx.core.content.res.ResourcesCompat
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.registerForActivityResult


class EditProfileActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var editProfileButton: Button
    private var repeatCameraAccessAttempt: Boolean = false

    private val ImageMethods by lazy {
        arrayOf(
            resources.getString(R.string.alert_dialog_create_foto),
            resources.getString(R.string.alert_dialog_choose_foto)
        )
    }

    private var selectedImageMethod = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        imageView = findViewById(R.id.imageview_photo)
        imageView.setOnClickListener {
            Toast.makeText(this, "ФОТО!!!", Toast.LENGTH_SHORT).show()
            dialogActionWithFoto.show()
        }

        editProfileButton = findViewById(R.id.editProfileButton)
        editProfileButton.setOnClickListener {
            launcherFillUserInfo.launch()
        }


        findViewById<Toolbar>(R.id.toolbar).apply {
            inflateMenu(R.menu.menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.send_item -> {
                        openSenderApp()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private val launcherFillUserInfo = registerForActivityResult(UserProfileContract()){ userInfo ->
        userInfo?.name?.let { findViewById<TextView>(R.id.textview_name).text = it }
        userInfo?.surName?.let { findViewById<TextView>(R.id.textview_surname).text = it }
        userInfo?.age?.let { findViewById<TextView>(R.id.textview_age).text = it.toString() }
    }
    private val launcherPermissionCamera = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        when {
            granted -> {
                Toast.makeText(this, "Доступ к камере получен! Снимаем фото!", Toast.LENGTH_SHORT).show()
                setImageToImageView()
            }
            !shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                if( !repeatCameraAccessAttempt ){
                    Toast.makeText(this, "Очень нужен доступ к камере, чтобы сделать фото!", Toast.LENGTH_SHORT).show()
                    openSettingsDialog.show()
                }
            }
            else -> {
                Toast.makeText(this, "Потом попробуешь еще раз...", Toast.LENGTH_SHORT).show()
                repeatCameraAccessAttempt = true
            }
        }
    }

    private val launcherGetPicture = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { result ->
        findViewById<ImageView>(R.id.imageview_photo).setImageBitmap(result)
    }

    private fun setImageToImageView(){
        imageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.cat, null))
    }

    private val dialogActionWithFoto
        get()= MaterialAlertDialogBuilder(this)
            .setTitle(R.string.alert_dialog_title)
            .setSingleChoiceItems(ImageMethods, -1) { _, which -> selectedImageMethod = which}
            .setPositiveButton(R.string.alert_dialog_ok, ::onPositiveButtonActionWithFotoClickListener)
            .setNegativeButton(R.string.alert_dialog_canceled) { dialog, _ ->
                dialog.dismiss()
                selectedImageMethod = -1
            }
            .create()

    fun onPositiveButtonActionWithFotoClickListener(dialog: DialogInterface?, which: Int) {
        when(selectedImageMethod){
            0 -> {
                val isGrantedCamera = ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
                if(isGrantedCamera){
                    Toast.makeText(this, "Доступ к камере есть! Снимаем фото!", Toast.LENGTH_SHORT).show()
                    launcherGetPicture.launch()
                }
                else{
                    Toast.makeText(this, "Доступа к камере нет! Запрашиваем!", Toast.LENGTH_SHORT).show()
                    if( repeatCameraAccessAttempt ){
                        repeatCameraPermissionDialog.show()
                    }
                    else{
                        launcherPermissionCamera.launch(Manifest.permission.CAMERA)
                    }
                }
            }
            1 -> {
                selectPictureLauncher.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    ))
            }
        }
        dialog?.dismiss()
    }

    private val repeatCameraPermissionDialog
        get() = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.repeat_alert_dialog_title)
            .setMessage("Необходим доступ к камере для создания аватара!!!")
            .setPositiveButton(R.string.repeat_alert_dialog_get_access, ::onPositiveButtonRepeatCameraPermissionDialog)
            .setNegativeButton(R.string.repeat_alert_dialog_canceled) { dialog, _ ->
                dialog.dismiss()
            }
            .create()

    fun onPositiveButtonRepeatCameraPermissionDialog(dialog: DialogInterface?, which: Int) {
        launcherPermissionCamera.launch(Manifest.permission.CAMERA)
        repeatCameraAccessAttempt = false
    }

    private val openSettingsDialog
        get() = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.open_settings_dialog_title)
            .setPositiveButton(R.string.open_settings_dialog_button, ::onPositiveButtonOpenSettingsDialog)
            .create()

    fun onPositiveButtonOpenSettingsDialog(dialog: DialogInterface?, which: Int) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }

    private val selectPictureLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { result ->
        result?.let { populateImage(it) }
    }

    /**
     * Используйте этот метод чтобы отобразить картинку полученную из медиатеки в ImageView
     */
    private fun populateImage(uri: Uri) {
        val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
        imageView.setImageBitmap(bitmap)
        imageView.tag = uri
    }

    private fun openSenderApp() {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "image/*"
            setPackage("org.telegram.messenger")

            val imgUri = imageView.tag as? Uri
            if (imgUri != null) putExtra(Intent.EXTRA_STREAM, imgUri)
            putExtra(Intent.EXTRA_TEXT,
                     "Имя : ${ findViewById<TextView>(R.id.textview_name).text}\nФамилия: ${ findViewById<TextView>(R.id.textview_surname).text}\nВозраст: ${ findViewById<TextView>(R.id.textview_age).text}")
        }

        runCatching {
            startActivity(intent)
        }.getOrElse {
            Toast.makeText(this, "Ну удалось запустить Telegram!!!", Toast.LENGTH_SHORT).show()
        }
    }
}