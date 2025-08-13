package ca.pkay.rcloneexplorer;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

public class RcloneProgressActivity extends AppCompatActivity {
    private TextView commandView;
    private TextView outputView;
    private NestedScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rclone_progress);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Rclone Progress");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        commandView = findViewById(R.id.rclone_command_view);
        outputView = findViewById(R.id.rclone_output_view);
        scrollView = findViewById(R.id.rclone_output_scroll);

        // Build rclone command from preferences
        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        int transfers = prefs.getInt(getString(R.string.pref_key_rclone_transfers), 1);
        int bwlimit = prefs.getInt(getString(R.string.pref_key_rclone_bwlimit), 0);
        int checkers = prefs.getInt(getString(R.string.pref_key_rclone_checkers), 8);

        // Example: rclone copy source: dest --transfers N --bwlimit XM --checkers N -P
        String source = "source"; // TODO: Replace with actual source
        String dest = "dest";     // TODO: Replace with actual destination
        StringBuilder cmdBuilder = new StringBuilder();
        cmdBuilder.append("rclone copy ").append(source).append(" ").append(dest)
            .append(" --transfers ").append(transfers)
            .append(" --checkers ").append(checkers)
            .append(" --stats=1s --stats-log-level NOTICE --use-json-log -P");
        if (bwlimit > 0) {
            cmdBuilder.append(" --bwlimit ").append(bwlimit).append("M");
        }
        String rcloneCmd = cmdBuilder.toString();
        commandView.setText(rcloneCmd);

        // Run rclone and stream output
        new Thread(() -> {
            try {
                // Build command array for ProcessBuilder
                java.util.List<String> cmdList = new java.util.ArrayList<>();
                cmdList.add("rclone");
                cmdList.add("copy");
                cmdList.add(source);
                cmdList.add(dest);
                cmdList.add("--transfers");
                cmdList.add(String.valueOf(transfers));
                cmdList.add("--checkers");
                cmdList.add(String.valueOf(checkers));
                cmdList.add("--stats=1s");
                cmdList.add("--stats-log-level");
                cmdList.add("NOTICE");
                cmdList.add("--use-json-log");
                cmdList.add("-P");
                if (bwlimit > 0) {
                    cmdList.add("--bwlimit");
                    cmdList.add(bwlimit + "M");
                }

                ProcessBuilder pb = new ProcessBuilder(cmdList);
                pb.redirectErrorStream(true);
                Process process = pb.start();
                java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    runOnUiThread(() -> {
                        outputView.append(line + "\n");
                        scrollView.post(() -> scrollView.fullScroll(android.view.View.FOCUS_DOWN));
                    });
                }
                process.waitFor();
            } catch (Exception e) {
                runOnUiThread(() -> outputView.append("Error running rclone: " + e.getMessage() + "\n"));
            }
        }).start();
    }
}
