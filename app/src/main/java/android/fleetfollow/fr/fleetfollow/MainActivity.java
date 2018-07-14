package android.fleetfollow.fr.fleetfollow;


import android.fleetfollow.android.FleetFollow;
import android.fleetfollow.android.User;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private FleetFollow fleetFollow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fleetFollow = new FleetFollow();
        fleetFollow.Init(this, "sqd", new User("Marc", "Jaroule", "", "762665Z", ""));

    }

}
