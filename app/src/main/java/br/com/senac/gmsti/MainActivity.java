package br.com.senac.gmsti;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import br.com.senac.gmsti.modelo.GmsTi;
import br.com.senac.gmsti.webservice.Api;

public class MainActivity extends AppCompatActivity {

    private static final int CODE_GET_REQUEST =1024;
    private static final int CODE_POST_REQUEST =1025;

    TextView EditTextIdAtendimento;
    TextView EditTextServico;
    TextView EditTextTipo;
    TextView EditTextData;
    TextView EditTextStatus;
    TextView EditTextIdCliente;
    TextView EditTextIdFuncionario;
    Button   buttonExecutado;
    Button   buttonFinalizado;
    ListView listview;
    Boolean isUpdating  = false;
    List<GmsTi> GmsTiList;
    ProgressBar progressBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        progressBar =findViewById(R.id.barraProgresso);
        listview =findViewById(R.id.listaservicos);

        EditTextIdAtendimento= findViewById(R.id.editTextIdAtendimento);
        EditTextServico = findViewById(R.id.editTextServico);
        EditTextTipo = findViewById(R.id.editTextTipo);
        EditTextData = findViewById(R.id.editTextData);
        EditTextStatus =findViewById(R.id.editTextStatus);
        EditTextIdCliente =findViewById(R.id.editTextIdCliente);
        EditTextIdFuncionario=findViewById(R.id.editTextIdFuncionario);

        buttonExecutado = findViewById(R.id.buttonExecutado);
        buttonFinalizado = findViewById(R.id.buttonFinalizado);


    }

    private void updateGmsti(){

        String idcliente= EditTextIdCliente.getText().toString().trim();
        String idfuncionario= EditTextIdFuncionario.getText().toString().trim();
        String atendimento=EditTextIdAtendimento.getText().toString().trim();
        String servico  = EditTextServico.getText().toString().trim();
        String tipo =   EditTextTipo.getText().toString().trim();
        String data=   EditTextData.getText().toString().trim();
        String status = EditTextStatus.getText().toString().trim();

        HashMap<String,String> params = new HashMap<>();
        params.put("atendimento", atendimento);
        params.put("servico",servico);
        params.put("tipo",tipo);
        params.put("data",data);
        params.put("status",status);
        params.put("idcliente",idcliente);
        params.put("idfuncionario",idfuncionario);

        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_UPDATE_GMSTI, params, CODE_POST_REQUEST);
        request.execute();

        isUpdating = false;
    }

    private void readGmsTi() {



        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_READ_GMSTI, null, CODE_GET_REQUEST);
        request.execute();


    }

    private void refreshAppCarList(JSONArray gmsti)  throws JSONException {
        GmsTiList.clear();
        for(int i   = 0; i< gmsti.length();i++){
            JSONObject obj = gmsti.getJSONObject(i);
            GmsTiList.add(new GmsTi(
                    obj.getInt("idcliente"),
                    obj.getInt("idfuncionario"),
                    obj.getString("atendimento"),



           ));
        }
        AppCarAdapter adapter = new AppCarAdapter(appCarList);
        listview.setAdapter(adapter);
    }

    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;

        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);
            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    Toast.makeText(MainActivity.this,object.getString("message"),Toast.LENGTH_SHORT).show();
                    refreshGmsTiList(object.getJSONArray("gmsti"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
