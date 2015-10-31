package neurotech.com.br.wantit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class CadastroActivity extends AppCompatActivity {

    private TextView txt_nome;
    private TextView txt_cpf;
    private TextView txt_fone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        txt_nome = (TextView) findViewById(R.id.editText);
        txt_cpf = (TextView) findViewById(R.id.editText2);
        txt_fone = (TextView) findViewById(R.id.editText3);
    }


    public void cadastrar(View view){

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("WANTIT_PREF", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.saved_user_name), txt_nome.getText().toString());
        editor.commit();

        Intent intent = new Intent();
        intent.setClass(CadastroActivity.this, MainActivity.class);
        startActivity(intent);
    }

}
