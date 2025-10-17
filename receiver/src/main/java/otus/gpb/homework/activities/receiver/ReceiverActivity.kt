package otus.gpb.homework.activities.receiver

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.IntentCompat
import android.widget.ImageView
import android.widget.TextView
import com.example.payload.Payload

class ReceiverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receiver)

        val payload: Payload? = IntentCompat.getParcelableExtra(intent, "intentPayload", Payload::class.java)

        val imageViewer = findViewById<ImageView>(R.id.posterImageView)
        val imageId = when (payload?.title) {
                "Славные парни" -> R.drawable.niceguys
                "Интерстеллар" -> R.drawable.interstellar
                else -> 0
            }

        if( imageId > 0 ) {
            imageViewer.setImageDrawable(AppCompatResources.getDrawable(this, imageId))
        }

        findViewById<TextView>(R.id.titleTextView).apply { text = payload?.title }
        findViewById<TextView>(R.id.descriptionTextView).apply { text = payload?.description }
        findViewById<TextView>(R.id.yearTextView).apply { text = payload?.year }
    }
}
