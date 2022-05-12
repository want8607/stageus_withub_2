package com.example.withub.loginFragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.withub.*
import retrofit2.Call
import retrofit2.Response

class PwCertifyFragment:Fragment() {
    lateinit var select: String
    lateinit var userEmail: String
    lateinit var count: CountDownTimer
    lateinit var token: String
    lateinit var id: String
    private var running = false
    private var confirmBtnBoolean = false
    val retrofit = RetrofitClient.initRetrofit()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        var view: View = inflater.inflate(R.layout.pwcertify_fragment, container, false)
        val findPwActivity = activity as FindPwActivity
        val backBtn = findPwActivity.findViewById<Button>(R.id.back_btn_find_pw)
        val spinner = view.findViewById<Spinner>(R.id.email_spinner_find_pw)
        val emailText = view.findViewById<EditText>(R.id.email_edittext_find_pw)
        val certificationBtn = view.findViewById<Button>(R.id.email_certification_btn_find_pw)
        val timerTime = view.findViewById<TextView>(R.id.timer_text_find_pw)
        val idText = view.findViewById<EditText>(R.id.id_edittext_find_pw)
        val confirmBtn = view.findViewById<Button>(R.id.certi_num_confirm_btn_find_pw)
        val certiNumText = view.findViewById<EditText>(R.id.certi_num_Edittext_find_pw)
        val nextBtn = view.findViewById<Button>(R.id.next_btn_find_pw)

        backBtn.setOnClickListener{
            val intent = Intent(findPwActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        certificationBtn.setOnClickListener {
            confirmBtnBoolean = true
            if (idText.text.toString().isEmpty()) {
                dialogMessage("아이디를 적어주세요.")
            } else if (select == "--선택--") {
                dialogMessage("도메인을 선택해주세요.")
            } else {
                id = idText.text.toString()
                certificationBtn.setBackgroundResource(R.drawable.stroke_disabled_btn)
                certificationBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.thick_gray))
                certificationBtn.setEnabled(false)
                sendMailApi(idText.text.toString())  //api로 메일 보내기
                if (running) {
                    count.cancel()
                }
                timerStart(timerTime)
            }
        }

        nextBtn.setOnClickListener{
            findPwActivity.tokenInform(token)
        }

        confirmBtn.setOnClickListener{
            if(confirmBtnBoolean == false)  {
                dialogMessage("이메일 인증을 해주세요.")
            } else if (running == false) {
                dialogMessage("시간이 초과되었습니다. 이메일을 다시 인증해주세요.")
            } else if (certiNumText.text.isEmpty()){
                dialogMessage("인증번호를 입력해주세요.")
            } else{
                EmailCertifyApi(certiNumText,nextBtn,certificationBtn)
            }
        }

        idRegEx(idText,emailText,certificationBtn)
        emailRegEx(emailText, certificationBtn)
        emailSpinnerSelect(spinner, emailText)
        return view
    }

    fun emailSpinnerSelect(
        spinner: Spinner,
        emailText: EditText,
    ) {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.email_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                select = spinner.selectedItem.toString()
                userEmail = emailText.getText().toString() + "@" + select
            }
        }
    }

    fun emailRegEx(
        emailText: EditText,
        certificationBtn: Button
    ) {
        emailText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (emailText.getText().toString().length >= 1) {
                    certificationBtn.setBackgroundResource(R.drawable.stroke_btn)
                    certificationBtn.setTextColor(ContextCompat.getColor(context!!, R.color.black))
                    certificationBtn.setEnabled(true)
                } else {
                    certificationBtn.setBackgroundResource(R.drawable.stroke_disabled_btn)
                    certificationBtn.setTextColor(
                        ContextCompat.getColor(
                            context!!,
                            R.color.thick_gray
                        )
                    )
                    certificationBtn.setEnabled(false)
                }
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }

    fun idRegEx(idText: EditText,emailText: EditText,certificationBtn: Button) {
        idText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (emailText.getText().toString().length >= 1) {
                    certificationBtn.setBackgroundResource(R.drawable.stroke_btn)
                    certificationBtn.setTextColor(ContextCompat.getColor(context!!, R.color.black))
                    certificationBtn.setEnabled(true)
                } else {
                    certificationBtn.setBackgroundResource(R.drawable.stroke_disabled_btn)
                    certificationBtn.setTextColor(
                        ContextCompat.getColor(
                            context!!,
                            R.color.thick_gray
                        )
                    )
                    certificationBtn.setEnabled(false)
                }
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }

    fun sendMailApi(idText: String) {
        var inform = FindPwIdEmailValue(idText,userEmail)
        val requestSendEmailApi = retrofit.create(FindPwSendEmailApi::class.java)
        Log.d("message","sdfsdfsdf")

        requestSendEmailApi.idEmailCheck(inform).enqueue(object : retrofit2.Callback<FindPwIdEmailCheckData> {
            override fun onFailure(
                call: Call<FindPwIdEmailCheckData>,
                t: Throwable
            ) {
                Log.d("message","sdfsdfsdf")

            }
            override fun onResponse(call: Call<FindPwIdEmailCheckData>, response: Response<FindPwIdEmailCheckData>) {
                Log.d("message","${response.body()!!.success}")
                Log.d("message","${response.body()!!.token}")

                token = response.body()!!.token
            }
        })
    }

    fun EmailCertifyApi(certiNumText:EditText,nextBtn:Button,certificationBtn: Button) {
        var inform = FindPwTokenAuthEmailIdValue(id, userEmail, certiNumText.text.toString(),token)
        val requestCertiNumConfirmApi = retrofit.create(FindPwCertiNumConfirmApi::class.java)
        requestCertiNumConfirmApi.certiNumCheck(inform).enqueue(object : retrofit2.Callback<FindPwCertiNumCheckData> {
            override fun onFailure(
                call: Call<FindPwCertiNumCheckData>,
                t: Throwable
            ) {
            }
            override fun onResponse(call: Call<FindPwCertiNumCheckData>, response: Response<FindPwCertiNumCheckData>) {
                if (response.body()!!.success == true) {
                    count.cancel()
                    dialogMessage("인증번호가 확인되었습니다.")
                    nextBtn.setBackgroundResource(R.drawable.login_btn)
                    nextBtn.setEnabled(true)
                } else {
                    count.cancel()
                    dialogMessage("인증번호가 틀렸습니다. 다시 인증해주세요.")
                    certificationBtn.setBackgroundResource(R.drawable.stroke_btn)
                    certificationBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
                    certificationBtn.setEnabled(true)
                }
            }
        })
    }

    fun timerStart(timerTime: TextView) {
        running = true
        count = object : CountDownTimer(1000 * 300, 1000) {
            override fun onTick(p0: Long) {
                Log.d("ss", "$p0")
                var minuteTime = (p0 / 1000 / 60).toString()
                var secondsTime = (p0 / 1000 % 60).toString()
                if (secondsTime.length == 1) {
                    timerTime.text = "$minuteTime:0$secondsTime"
                } else {
                    timerTime.text = "$minuteTime:$secondsTime"
                }
            }
            override fun onFinish() {
                running = false
            }
        }
        count.start()
    }

    fun dialogMessage(message:String) {
        var builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setMessage(message)
        builder.setPositiveButton("확인", null)
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }
}