package com.example.curso.minhaaplicao;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Com_Parametro extends AppCompatActivity {

    Button Pesquisar;
    ListView ListadePessoas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_com__parametro);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
ListadePessoas = (ListView)findViewById(R.id.listar);
        Pesquisar = (Button)findViewById(R.id.button2);
        Pesquisar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DownloadJsonAsyncTask().execute("http://192.168.181.134/apicliente/api/cliente/retornaclientes?tipo=json");
                //"http://10.0.2.2/apicliente/api/cliente/retornaclientes?tipo=json");
                //"http://10.0.2.2:3630/api/cliente/retornaclientes?tipo=json");

            }
        }); //teste2

ListadePessoas.setOnItemClickListener(new ItemClickedListener());

    }
    private class ItemClickedListener implements android.widget.AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0, View arg1, int
                position, long id) {
            Pessoas pessoa = (Pessoas) arg0.getItemAtPosition(position);
            mensagem("Dados do cliente",pessoa.getNome()+" "+pessoa.getCpf());
        }

        public void mensagem(String titulo, String mensagem) {
            android.app.AlertDialog.Builder alertateste = new android.app.AlertDialog.Builder(Com_Parametro.this);
            alertateste.setMessage(mensagem);
            alertateste.setTitle(titulo);
            alertateste.setNeutralButton("OK", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub

                }
            });
            alertateste.show();
        }
    }
    class DownloadJsonAsyncTask extends AsyncTask<String, Void, List<Pessoas>> {
        ProgressDialog dialog;

        @Override
        protected List<Pessoas> doInBackground(String... params) {
            String urlString = params[0];
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(urlString);
            try {
                HttpResponse response = httpclient.execute(httpget);
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    InputStream instream = entity.getContent();
                    String json = getStringFromInputStream(instream);
                    instream.close();
                    List<Pessoas> pessoas = getPessoas(json);
                    return pessoas;
                }
            } catch (Exception e) {
                Log.e("Erro", "Falha ao acessar Web service", e);
            }
            return null;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(Com_Parametro.this, "Aguarde",
                    "Fazendo download do JSON");
        }
        //Depois de executada a chamada do serviço
        @Override
        protected void onPostExecute(List<Pessoas> result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if (result.size() > 0) {

                ArrayAdapter<Pessoas> adapter = new ArrayAdapter<Pessoas>(
                        Com_Parametro.this,
                        android.R.layout.simple_list_item_1, result);
               ListadePessoas.setAdapter(adapter);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        Com_Parametro.this)
                        .setTitle("Erro")
                        .setMessage("Não foi possível acessar as informações!!")
                        .setPositiveButton("OK", null);
                builder.create().show();
            }
        }
    }
    //Converte objeto InputStream para String
    private String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }
    //Retorna uma lista de pessoas com as informações retornadas do JSON
    private List<Pessoas> getPessoas(String jsonString) {
        List<Pessoas> pessoas = new ArrayList<Pessoas>();
        try {
            JSONArray pessoasJson = new JSONArray(jsonString);
            JSONObject pessoa;

            for (int i = 0; i < pessoasJson.length(); i++) {
                pessoa = new JSONObject(pessoasJson.getString(i));
                Log.i("PESSOA ENCONTRADA: ",
                        "nome=" + pessoa.getString("nome"));

                Pessoas objetoPessoa = new Pessoas();
                objetoPessoa.setNome(pessoa.getString("nome"));
                objetoPessoa.setCpf(pessoa.getString("cpf"));
                pessoas.add(objetoPessoa);
            }

        } catch (JSONException e) {
            Log.e("Erro", "Erro no parsing do JSON", e);
        }
        return pessoas;
    }

}
