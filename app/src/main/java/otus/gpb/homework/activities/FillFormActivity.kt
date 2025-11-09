package otus.gpb.homework.activities

import android.os.Bundle
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.content.Intent

class FillFormActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_fill_form)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.button)?.setOnClickListener {
            val firstName = findViewById<EditText>(R.id.editFirstName).text.toString()
            val lastName = findViewById<EditText>(R.id.editLastName).text.toString()
            val age = findViewById<EditText>(R.id.editAge).text.toString().toIntOrNull() ?: 0

            if (firstName.isNotBlank() && lastName.isNotBlank() && age > 0) {
                val user = UserInfo(firstName, lastName, age)
                val intent = Intent().putExtra(USER_PROFILE_RESULT_KEY, user)
                setResult(RESULT_OK, intent)
            } else {
                setResult(RESULT_CANCELED)
            }
            finish()
        }
    }

    companion object {
        const val USER_PROFILE_RESULT_KEY = "UserProfileResultKey"
    }
}