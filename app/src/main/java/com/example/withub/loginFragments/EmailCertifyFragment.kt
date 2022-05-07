package com.example.withub.loginFragments

import android.app.AlertDialog
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
import com.example.withub.R
import com.example.withub.SignupActivity
import com.example.withub.mainFragments.*
import retrofit2.Call
import retrofit2.Response

class EmailCertifyFragment:Fragment() {
    lateinit var userEmail: String
    lateinit var count: CountDownTimer
    lateinit var select: String
    lateinit var token: String
    private var running = false
    private var confirmBtnBoolean = false
    lateinit var id :String
    val retrofit = RetrofitClient.initRetrofit()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.emailcertify_fragment, container, false)
        val spinner: Spinner = view.findViewById(R.id.email_spinner_signup)
        val emailText = view.findViewById<EditText>(R.id.email_edittext_signup)
        val certificationBtn = view.findViewById<Button>(R.id.email_certification_btn_signup)
        val timerTime = view.findViewById<TextView>(R.id.timer_text_signup)
        val confirmBtn = view.findViewById<Button>(R.id.certi_num_confirm_btn_signup)
        val certiNumText = view.findViewById<EditText>(R.id.certi_num_Edittext_signup)
        val signupActivity = activity as SignupActivity
        val signupBackBtn = signupActivity.findViewById<Button>(R.id.signup_back_btn)
        val nextBtn = view.findViewById<Button>(R.id.next_btn_emailcertify)
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        val signupText = signupActivity.findViewById<TextView>(R.id.signup_text)
        val warningInform1 = signupActivity.findViewById<TextView>(R.id.warning_inform_signup_1)
        val warningInform2 = signupActivity.findViewById<TextView>(R.id.warning_inform_signup_2)
        val warningInform3 = signupActivity.findViewById<TextView>(R.id.warning_inform_signup_3)
        val warningInform4 = signupActivity.findViewById<TextView>(R.id.warning_inform_signup_4)
        signupText.visibility = View.VISIBLE
        warningInform1.visibility = View.GONE
        warningInform2.visibility = View.GONE
        warningInform3.visibility = View.GONE
        warningInform4.visibility = View.GONE

        val requestId = arguments?.getString("id")
        if (requestId != null) {
            id = requestId
        }

        signupBackBtn.setOnClickListener {
            builder.setMessage("이전으로 돌아갈시 아이디와 비밀번호 정보를 다시 입력해야 합니다.")
            builder.setPositiveButton("확인") { p0, p1 ->
                if (running == true) {
                    count.cancel()
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentArea, IdPwInputFragment())
                    .commit()
            }
            builder.setNegativeButton("취소", null)
            val alertDialog: AlertDialog = builder.create()
            alertDialog.show()
        }

        nextBtn.setOnClickListener{
            signupActivity.emailInform(userEmail)
        }

        certificationBtn.setOnClickListener {
            confirmBtnBoolean = true
            if (select == "--선택--") {
                dialogMessage("도메인을 선택해주세요.")
            } else {
                certificationBtn.setBackgroundResource(R.drawable.stroke_disabled_btn)
                certificationBtn.setTextColor(ContextCompat.getColor(requireContext(),R.color.thick_gray))
                certificationBtn.setEnabled(false)
                sendMailApi()  //api로 메일 보내기
                if (running) {
                    count.cancel()
                }
                timerStart(timerTime)
            }
        }

        confirmBtn.setOnClickListener{
           if(confirmBtnBoolean == false)  {
               dialogMessage("이메일을 인증해주세요.")
            } else if (running == false) {
               dialogMessage("시간이 초과되었습니다. 이메일을 다시 인증해주세요.")
            } else if (certiNumText.text.isEmpty()){
               dialogMessage("인증번호를 입력해주세요.")
           } else{
                EmailCertifyApi(certiNumText,nextBtn,certificationBtn)
            }
        }

        emailRegEx(emailText, certificationBtn)
        emailSpinnerSelect(spinner, emailText)
        return view
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

    fun sendMailApi() {
        var inform = EmailValue(id,userEmail)
        val requestSendEmailApi = retrofit.create(SendEmailApi::class.java)
        requestSendEmailApi.emailCheck(inform).enqueue(object : retrofit2.Callback<EmailCheckData> {
            override fun onFailure(
                call: Call<EmailCheckData>,
                t: Throwable
            ) {
            }
            override fun onResponse(call: Call<EmailCheckData>, response: Response<EmailCheckData>) {
                token = response.body()!!.token
            }
        })
    }

    fun EmailCertifyApi(certiNumText:EditText,nextBtn:Button,certificationBtn: Button) {
        var inform = tokenAuthEmailValue(token,certiNumText.text.toString(),id)
        val requestCertiNumConfirmApi = retrofit.create(CertiNumConfirmApi::class.java)
        requestCertiNumConfirmApi.certiNumCheck(inform).enqueue(object : retrofit2.Callback<CertiNumCheckData> {
            override fun onFailure(
                call: Call<CertiNumCheckData>,
                t: Throwable
            ) {
            }
            override fun onResponse(call: Call<CertiNumCheckData>, response: Response<CertiNumCheckData>) {
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

    fun dialogMessage(message:String) {
        var builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setMessage(message)
        builder.setPositiveButton("확인", null)
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

}