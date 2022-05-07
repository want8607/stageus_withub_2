package com.example.withub

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.withub.loginFragments.EmailCertifyFragment
import com.example.withub.loginFragments.GitHubRepositoryAddFragment
import com.example.withub.loginFragments.NickNameAreaSelectFragment
import com.example.withub.loginFragments.TermsOfUseFragment

class SignupActivity: AppCompatActivity() {
    lateinit var email: String
    lateinit var id: String
    lateinit var nickName: String
    lateinit var area: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.fragmentArea, GitHubRepositoryAddFragment()).commit()
        }
    }

    fun idInform(idValue:String){
        id = idValue
        var fragment = EmailCertifyFragment()
        var myBundle = Bundle()
        myBundle.putString("id", idValue)
        fragment.arguments = myBundle
        supportFragmentManager.beginTransaction().replace(R.id.fragmentArea, fragment).commit()
    }

    fun emailInform(emailValue:String) {
        email = emailValue
        var fragment = NickNameAreaSelectFragment()
        supportFragmentManager.beginTransaction().replace(R.id.fragmentArea, fragment).commit()
    }

    fun nickNameAreaInform(nickNameValue:String,areaValue: String){
        nickName = nickNameValue
        area = areaValue
        var fragment = GitHubRepositoryAddFragment()
        supportFragmentManager.beginTransaction().replace(R.id.fragmentArea, fragment).commit()
    }
}