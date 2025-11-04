package otus.gpb.homework.activities

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity.RESULT_CANCELED
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.content.IntentCompat

class UserProfileContract : ActivityResultContract<Unit, UserInfo?>() {

    override fun createIntent(
        context: Context,
        input: Unit
    ): Intent {
        return Intent(context, FillFormActivity::class.java)
    }

    override fun parseResult(
        resultCode: Int,
        intent: Intent?
    ): UserInfo? {
        if (
            resultCode == RESULT_CANCELED
            || intent == null
            || resultCode != RESULT_OK
        ) return null

        return IntentCompat.getParcelableExtra(
            intent,
            FillFormActivity.USER_PROFILE_RESULT_KEY,
            UserInfo::class.java
        )
    }
}