package com.igormelo.chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;


import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private EditText InputMessage;
    private Button send;
    private ListView lista;
    private ArrayAdapter<String> adapter;
    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InputMessage = (EditText)findViewById(R.id.input_mensagem);
        send = (Button)findViewById(R.id.btn_enviar);
        lista = (ListView)findViewById(R.id.lista_mensagens);
        Intent intent = getIntent();
       final String nick = intent.getExtras().getString("nick");


        adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1);
        lista.setAdapter(adapter);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!InputMessage.getText().toString().equals("")){
                    socket.emit("mensagem_servidor", nick + InputMessage.getText());
                    InputMessage.setText("");
                }
            }
        });
            try {
                socket = IO.socket("http://192.168.20.160:3130");
            } catch (URISyntaxException e){
                this.finish();
                return;
        }
        socket.on("mensagem_cliente", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                if(args.length > 0) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.add((String) args[0]);
                            adapter.notifyDataSetChanged();
                            lista.smoothScrollToPosition(adapter.getCount() - 1);
                        }
                    });
                }
            }
        });
        socket.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (socket != null)
            socket.disconnect();
    }
}
