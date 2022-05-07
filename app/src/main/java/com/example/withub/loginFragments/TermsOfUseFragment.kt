package com.example.withub.loginFragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.withub.LoginActivity
import com.example.withub.R
import com.example.withub.SignupActivity

class TermsOfUseFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.termsofuse_fragment, container, false)
        val signupActivity = activity as SignupActivity
        val signupBackBtn = signupActivity.findViewById<Button>(R.id.signup_back_btn)
        val checkBoxAllCheck = view.findViewById<CheckBox>(R.id.checkbox_all_check)
        val checkBox1 = view.findViewById<CheckBox>(R.id.checkbox_terms_of_use_1)
        val checkBox2 = view.findViewById<CheckBox>(R.id.checkbox_terms_of_use_2)
        val nextBtn = view.findViewById<Button>(R.id.next_btn_terms_of_use)
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

        signupBackBtn.setOnClickListener{
            val intent = Intent(signupActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        nextBtn.setOnClickListener{
            if ( checkBox1.isChecked && checkBox2.isChecked) {
                parentFragmentManager.beginTransaction().replace(R.id.fragmentArea, IdPwInputFragment()).commit()
            }
        }

        checkBoxAllCheck.setOnClickListener { onCheckChanged(checkBoxAllCheck,checkBoxAllCheck,checkBox1,checkBox2,nextBtn) }
        checkBox1.setOnClickListener { onCheckChanged(checkBox1,checkBoxAllCheck,checkBox1,checkBox2,nextBtn) }
        checkBox2.setOnClickListener { onCheckChanged(checkBox2,checkBoxAllCheck,checkBox1,checkBox2,nextBtn) }

        return view
    }

    private fun onCheckChanged(compoundButton: CompoundButton,checkBoxAllCheck:CheckBox,checkBox1:CheckBox,checkBox2:CheckBox,nextBtn:Button) {
        when(compoundButton.id) {
            R.id.checkbox_all_check -> {
                if (checkBoxAllCheck.isChecked) {
                    checkBox1.isChecked = true
                    checkBox2.isChecked = true
                    nextBtn.setBackgroundResource(R.drawable.login_btn)
                }else {
                    checkBox1.isChecked = false
                    checkBox2.isChecked = false
                    nextBtn.setBackgroundResource(R.drawable.disabled_button)
                }
            }
            else -> {
                checkBoxAllCheck.isChecked = (
                        checkBox1.isChecked
                                && checkBox2.isChecked)
                if (checkBox1.isChecked && checkBox2.isChecked) {
                    nextBtn.setBackgroundResource(R.drawable.login_btn)
                } else{
                    nextBtn.setBackgroundResource(R.drawable.disabled_button)
                }
            }
        }
    }

}


