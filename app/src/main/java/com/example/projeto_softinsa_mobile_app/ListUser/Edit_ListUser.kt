package com.example.projeto_softinsa_mobile_app.ListUser


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.projeto_softinsa_mobile_app.R
import org.json.JSONObject

class Edit_ListUser : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_list_user)
        requestQueue = Volley.newRequestQueue(this)

        // Obtém os valores transferidos da atividade anterior
        val userId = intent.getStringExtra("userId")
        val givenName = intent.getStringExtra("Lista_user_Nome")
        val givenSubName = intent.getStringExtra("Lista_user_Sobrenome")
        val givenNumFunctionario = intent.getStringExtra("Lista_user_Num_Func")
        val givenEmail = intent.getStringExtra("Lista_user_Email")
        val givenTelemovel = intent.getStringExtra("Lista_user_Telemovel")
        val givenEstado = intent.getStringExtra("Lista_user_Estado")
        val givenisCargo = intent.getStringExtra("Lista_user_Cargo")
        val givenisDepartamento = intent.getStringExtra("Lista_user_Departamento")
        val givenisFilial = intent.getStringExtra("Lista_user_Filial")
        val givenisMorada = intent.getStringExtra("Lista_user_Morada")

        // Define os valores transferidos nas EditTexts
        val nomeEditText = findViewById<EditText>(R.id.editTextNome)
        nomeEditText.setText(givenName)

        val sobrenomeEditText = findViewById<EditText>(R.id.editTextSobrenome)
        sobrenomeEditText.setText(givenSubName)

        val numFuncEditText = findViewById<EditText>(R.id.editTextNum_Func)
        numFuncEditText.setText(givenNumFunctionario)

        val emailEditText = findViewById<EditText>(R.id.editTextEmail)
        emailEditText.setText(givenEmail)

        val telemovelEditText = findViewById<EditText>(R.id.editTextTelemovel)
        telemovelEditText.setText(givenTelemovel)

        val estadoCheckBox = findViewById<CheckBox>(R.id.checkBoxEstado)
        estadoCheckBox.isChecked = givenEstado?.toBoolean() ?: false

        val cargoEditText = findViewById<EditText>(R.id.editTextCargo)
        cargoEditText.setText(givenisCargo)

        val departamentoEditText = findViewById<EditText>(R.id.editTextDepartamento)
        departamentoEditText.setText(givenisDepartamento)

        val filialEditText = findViewById<EditText>(R.id.editTextFilial)
        filialEditText.setText(givenisFilial)

        val moradaEditText = findViewById<EditText>(R.id.editTextMorada)
        moradaEditText.setText(givenisMorada)

        // Inicializa a RequestQueue
        requestQueue = Volley.newRequestQueue(this)

        // Configura o botão de atualizar
        val atualizarButton = findViewById<Button>(R.id.Confirmar)
        atualizarButton.setOnClickListener {
            // Obtém os novos valores dos campos
            val novoNome = nomeEditText.text.toString()
            val novoSobrenome = sobrenomeEditText.text.toString()
            val novoNumFuncionario = numFuncEditText.text.toString()
            val novoEmail = emailEditText.text.toString()
            val novoTelemovel = telemovelEditText.text.toString()
            val novoEstado = estadoCheckBox.isChecked
            val novoCargo = cargoEditText.text.toString()
            val novoDepartamento = departamentoEditText.text.toString()
            val novoFilial = filialEditText.text.toString()
            val novoMorada = moradaEditText.text.toString()

            // Adicione este log antes da alteração do estado
            Log.d("EstadoAntes", "Estado antes da alteração: $givenEstado")
            // Altere o estado aqui
            estadoCheckBox.isChecked = novoEstado
            val textoEstado = if (novoEstado) "Ativo" else "Inativo"
            // Adicione este log após a alteração do estado
            Log.d("EstadoDepois", "Estado depois da alteração: $textoEstado")
            val novoNumeroFuncionario = novoNumFuncionario.toIntOrNull() ?: 0 // Converte para Int ou usa 0 como valor padrão em caso de falha
            val novoCargoId = novoCargo.toIntOrNull() ?: 0
            val novoDepartamentoId = novoDepartamento.toIntOrNull() ?: 0
            val novoFilialId = novoFilial.toIntOrNull() ?: 0

            Log.d("TRUE OR FALSE", novoEstado.toString())

            // Constrói o objeto JSON com os novos valores
            val requestBody = JSONObject().apply {
                put("primeiroNome", novoNome)
                put("ultimoNome", novoSobrenome)
                put("numeroFuncionario", novoNumeroFuncionario)
                put("email", novoEmail)
                put("telemovel", novoTelemovel)
                put("morada", novoMorada)
                put("isAtivo", novoEstado )
                put("cargoId", novoCargoId)
                put("departamentoId", novoDepartamentoId)
                put("filialId", novoFilialId)

            }

            // Define a URL da API de atualização
            val url = "https://softinsa-web-app-carreiras01.onrender.com/user/update/$userId"

            // Cria a requisição PUT para atualizar os dados na API
            // Cria o AlertDialogBuilder
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setTitle("Confirmar atualização")
            alertDialogBuilder.setMessage("Deseja confirmar a atualização?")
            alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
                // Cria a requisição PUT para atualizar os dados na API
                val request = JsonObjectRequest(Request.Method.PUT, url, requestBody,
                    { response ->
                        Toast.makeText(this, "Dados atualizados com sucesso", Toast.LENGTH_SHORT).show()
                        // Você também pode encerrar a atividade ou realizar outras ações
                        Log.d("EstadoDepois", "Estado depois da alteração: $novoEstado")

                        finish()
                    },
                    { error ->
                        val errorMessage = "Erro ao atualizar os dados: ${error.message}"
                        Log.e("UpdateError", errorMessage) // Exibe a mensagem de erro no console (logcat)
                        runOnUiThread {
                            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show() // Exibe a mensagem de erro em um Toast
                        }
                    })

                // Adiciona a requisição à RequestQueue para ser enviada
                requestQueue.add(request)
            }
            alertDialogBuilder.setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            // Exibe o AlertDialog
            alertDialogBuilder.create().show()
        }
        val CancelarButton = findViewById<Button>(R.id.Cancelar)
        CancelarButton.setOnClickListener {
            finish()
        }
    }
}