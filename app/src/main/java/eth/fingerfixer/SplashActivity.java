package eth.fingerfixer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        //setContentContentView는 하지 않습니다.
        startActivity(new Intent(this, NoteList.class));
        finish();
    }
}