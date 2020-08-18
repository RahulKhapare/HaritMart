package grocery.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import grocery.app.util.WindowBarColor;

public class EditProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowBarColor.setColor(this);
        setContentView(R.layout.activity_edit_profile);
    }

    public void onBackClick(View view) {
        finish();
    }
}