package otus.gpb.homework.activities.sender

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.payload.Payload

class SenderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sender)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.button1).setOnClickListener {
            val intent = openMap()
            startActivity(intent)
        }

        findViewById<Button>(R.id.button2).setOnClickListener {
            val intent = openMail()
            startActivity(intent)
        }

        findViewById<Button>(R.id.button3).setOnClickListener {
            val intent = openIntent()
            startActivity(intent)
        }
    }

    fun openMap() = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("geo:0,0?z=30&q=Restaurants near Moscow, Russia")
    ).setPackage("com.google.android.apps.maps")

    fun openMail() = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf("android@otus.ru"))
        putExtra(Intent.EXTRA_SUBJECT, "Тестовое письмо в OTUS")
        putExtra(Intent.EXTRA_TEXT, "Привет, OTUS! Как дела?")
    }

    fun openIntent(): Intent {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            addCategory(Intent.CATEGORY_DEFAULT)
            putExtra("intentPayload", Payload("Славные парни", "2016", "Что бывает, когда напарником брутального костолома становится субтильный лопух? Наемный охранник Джексон Хили и частный детектив Холланд Марч вынуждены работать в паре, чтобы распутать плевое дело о пропавшей девушке, которое оборачивается преступлением века. Смогут ли парни разгадать сложный ребус, если у каждого из них – свои, весьма индивидуальные методы."))
        }
        return intent
    }
}