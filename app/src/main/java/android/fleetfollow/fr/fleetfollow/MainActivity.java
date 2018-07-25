package android.fleetfollow.fr.fleetfollow;

import android.content.Intent;
import android.fleetfollow.android.FleetFollow;
import android.fleetfollow.android.User;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button getUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getUser = (Button) findViewById(R.id.getUser);
        // FleetFollow fleetFollow = new FleetFollow();
        // fleetFollow.Init(this, "sqdqs", new User("Test", "Testounet", "pds", "qsd", ""));



        getUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, new_page.class));
            }
        });

    }

}
