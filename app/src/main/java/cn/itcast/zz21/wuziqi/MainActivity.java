package cn.itcast.zz21.wuziqi;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private WuZiQiPanel mPanel;
    private Button mRestart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPanel = (WuZiQiPanel) findViewById(R.id.id_panel);
        mRestart = (Button) findViewById(R.id.id_restart);
        mPanel.setListener(new ResultListener() {
            @Override
            public void showResult(int result) {
                String text = (result == WuZiQiPanel.DRAW) ? ("和棋!") : (result == WuZiQiPanel.WHITE_WON ? "白棋胜利!" : "黑棋胜利!");
//                Toast.makeText(MainActivity.this,text,Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setIcon(android.R.drawable.ic_dialog_info);
                builder.setTitle("对战结果:");
                builder.setMessage(text);
                builder.setNegativeButton("重新开始", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPanel.restart();
                    }
                });
                builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.show();
            }
        });
        mRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPanel.restart();
            }
        });
       
    }
}
