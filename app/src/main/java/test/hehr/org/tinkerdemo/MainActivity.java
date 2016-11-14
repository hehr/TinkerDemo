package test.hehr.org.tinkerdemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tencent.tinker.lib.tinker.TinkerInstaller;

import java.io.File;

public class MainActivity extends Activity implements View.OnClickListener {


    private Button displayName; //显示名字
    private Button addPatch; //打补丁
    private EditText et; //输入框
    private static final String TAG = "Tiker-Demo-MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         et = (EditText) findViewById(R.id.input_name);
         displayName = (Button) findViewById(R.id.display_name);
         addPatch = (Button) findViewById(R.id.add_patch);

         displayName.setOnClickListener(this);
         addPatch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.display_name:

                Toast.makeText(MainActivity.this,et.getText().toString().trim(),Toast.LENGTH_LONG).show();

                break;
            case R.id.add_patch:

                String patchPath  = Environment.getExternalStorageDirectory().getAbsolutePath() + "/patch_signed_7zip.zip";
                File file = new File(patchPath);

                if (file.exists()){

                    Log.v(TAG,"补丁文件存在");
                    TinkerInstaller.onReceiveUpgradePatch(getApplicationContext(), patchPath);

                }else {

                    Toast.makeText(MainActivity.this,"补丁apk不存在!!!!",Toast.LENGTH_LONG).show();

                }

                break;
            default:

                break;
        }
    }
}
