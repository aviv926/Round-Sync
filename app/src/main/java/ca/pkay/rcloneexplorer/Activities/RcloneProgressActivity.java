package ca.pkay.rcloneexplorer.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import ca.pkay.rcloneexplorer.R;

public class RcloneProgressActivity extends Activity {
    private static TextView staticProgressOutput;
    private TextView progressOutput;
    private Button closeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rclone_progress);
        progressOutput = findViewById(R.id.progressOutput);
        staticProgressOutput = progressOutput;
        closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void appendOutput(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressOutput.append(text + "\n");
            }
        });
    }

    // Static method for demo; in production, use a safer IPC or event bus
    public static void appendOutputStatic(final String text) {
        if (staticProgressOutput != null) {
            staticProgressOutput.post(new Runnable() {
                @Override
                public void run() {
                    staticProgressOutput.append(text + "\n");
                }
            });
        }
    }
}
