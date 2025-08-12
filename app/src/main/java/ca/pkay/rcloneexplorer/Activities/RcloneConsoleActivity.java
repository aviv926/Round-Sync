package ca.pkay.rcloneexplorer.Activities;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import ca.pkay.rcloneexplorer.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class RcloneConsoleActivity extends AppCompatActivity {
    private TextView consoleOutput;
    public static final String ACTION_RCLONE_CONSOLE_OUTPUT = "ca.pkay.rcloneexplorer.RCLONE_CONSOLE_OUTPUT";
    public static final String EXTRA_OUTPUT_LINE = "output_line";

    private final BroadcastReceiver outputReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_RCLONE_CONSOLE_OUTPUT.equals(intent.getAction())) {
                String line = intent.getStringExtra(EXTRA_OUTPUT_LINE);
                if (line != null) appendOutput(line);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rclone_console);
        consoleOutput = findViewById(R.id.console_output);
        LocalBroadcastManager.getInstance(this).registerReceiver(outputReceiver, new IntentFilter(ACTION_RCLONE_CONSOLE_OUTPUT));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(outputReceiver);
    }

    public static void launch(Context context) {
        Intent intent = new Intent(context, RcloneConsoleActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void appendOutput(final String text) {
        runOnUiThread(() -> {
            consoleOutput.append(text + "\n");
            int scrollAmount = consoleOutput.getLayout().getLineTop(consoleOutput.getLineCount()) - consoleOutput.getHeight();
            if (scrollAmount > 0)
                consoleOutput.scrollTo(0, scrollAmount);
            else
                consoleOutput.scrollTo(0, 0);
        });
    }

    public static void sendOutputLine(Context context, String line) {
        Intent intent = new Intent(ACTION_RCLONE_CONSOLE_OUTPUT);
        intent.putExtra(EXTRA_OUTPUT_LINE, line);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
