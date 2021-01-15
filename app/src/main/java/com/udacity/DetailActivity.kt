package com.udacity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    companion object{
        const val DOWNLOAD_FILE = "dFile"
        fun getIntent(context: Context,fileDownloaded:String):Intent{
            return Intent(context,DetailActivity::class.java).apply {
                putExtra(DOWNLOAD_FILE,fileDownloaded)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        if(intent.hasExtra(DOWNLOAD_FILE)){
            tv_file_name.text = intent.getStringExtra(DOWNLOAD_FILE)
            tv_status.text ="Success"
        }

        btn_ok.setOnClickListener {

            val i = Intent(this,MainActivity::class.java)
            startActivity(i)
            finish()
        }


    }

}
