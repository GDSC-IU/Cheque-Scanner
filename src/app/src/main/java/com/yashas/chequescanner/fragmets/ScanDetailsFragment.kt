package com.yashas.chequescanner.fragmets

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.yashas.chequescanner.R
import com.yashas.chequescanner.database.ScanHistoryEntity
import com.yashas.chequescanner.utils.ScanHistoryDBUtils
import com.yashas.chequescanner.utils.WordsToNumbersUtil


class ScanDetailsFragment : Fragment() {

    private lateinit var payee: AppCompatEditText
    private lateinit var amount: AppCompatEditText
    private lateinit var name: AppCompatEditText
    private lateinit var accountNumber: AppCompatEditText
    private lateinit var date: AppCompatEditText
    private lateinit var image: AppCompatImageView
    private lateinit var save: AppCompatButton
    private lateinit var dataLayout: ConstraintLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_scan_details, container, false)
        setup(view)
        return view
    }

    private fun setup(view: View) {
        initUI(view)
        getBundle()
        listener()
    }

    private fun getBundle() {
        val arguments = arguments
        if (arguments != null) {
            val imageUri = arguments.getString("imageUri")!!
            val gallery = arguments.getBoolean("gallery")
            if (gallery) {
                Glide.with(context!!)
                    .load(imageUri)
                    .error(R.drawable.ic_error)
                    .into(image)
            } else {
                Glide.with(context!!)
                    .load(imageUri)
                    .error(R.drawable.ic_error)
                    .into(image)
            }
            progressBar.visibility = View.VISIBLE
            analyseImageText(imageUri)
        } else {
            Toast.makeText(context!!, "Sorry Some Error Occurred in image processing", Toast.LENGTH_LONG).show()
            (context as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.frame, ScanFragment())
                .commit()
        }
    }

    private fun initUI(view: View) {
        payee = view.findViewById(R.id.payee)
        name = view.findViewById(R.id.name)
        date = view.findViewById(R.id.date)
        accountNumber = view.findViewById(R.id.accountNumber)
        name = view.findViewById(R.id.name)
        save = view.findViewById(R.id.save)
        image = view.findViewById(R.id.image)
        amount = view.findViewById(R.id.amount)
        dataLayout = view.findViewById(R.id.dataLayout)
        progressBar = view.findViewById(R.id.progress)
        sharedPreferences = context!!.getSharedPreferences("saved", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("saved", false).apply()
    }

    private fun listener() {
        save.setOnClickListener {
            when {
                payee.text.isNullOrEmpty() -> {
                    payee.error = "Enter payee name"
                }
                else -> {
                    val data = ScanHistoryEntity(
                        payeeName = payee.text.toString(),
                        name = name.text.toString(),
                        amount = amount.text.toString(),
                        accountNumber = accountNumber.text.toString(),
                        date = date.text.toString()
                    )
                    ScanHistoryDBUtils(context!!, 1, data).execute()
                    sharedPreferences.edit().putBoolean("saved", true).apply()
                    Toast.makeText(context, "Saved Successfully", Toast.LENGTH_LONG).show()
                    (context as FragmentActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, HomeFragment())
                        .commit()
                }
            }
        }
    }

    private fun analyseImageText(imageUri: String) {
        var inputImage: InputImage? = null
        try {
            inputImage = InputImage.fromFilePath(context!!, Uri.parse(imageUri))
        } catch (e: Exception) {
            Toast.makeText(context, "Some error occurred", Toast.LENGTH_LONG).show()
        }
        var previous = ""
        val recognizer = TextRecognition.getClient()
        if (inputImage != null) {
            recognizer.process(inputImage).addOnSuccessListener {
                if (it != null) {
                    for (textBlock in it.textBlocks) {
                        for (line in textBlock.lines) {
                            println(line.text)
                            if ((line.text.contains(
                                    "rupees",
                                    ignoreCase = true
                                ) || line.text.contains(
                                    "pees",
                                    ignoreCase = true
                                ) || line.text.contains(
                                    "upoes",
                                    ignoreCase = true
                                ) || line.text.contains(
                                    "aupees",
                                    ignoreCase = true
                                )|| line.text.contains(
                                    "bupee",
                                    ignoreCase = true
                                )||line.text.contains(
                                        "rupes",
                            ignoreCase = true
                            )) && line.text.contains("only", ignoreCase = true)
                            ) {
                                val list: ArrayList<String> =
                                    line.text.split(" ").map { s -> s.trim() } as ArrayList<String>
                                list.removeAt(0)
                                list.removeAt(list.lastIndex)
                                amount.setText(
                                    WordsToNumbersUtil.convertTextualNumbersInDocument(
                                        list.joinToString(" ")
                                    )
                                )
                            } else if (((line.text.take(4)
                                    .contains("Cate ", ignoreCase = true) || line.text.take(4)
                                    .contains(
                                        "Date ",
                                        ignoreCase = true
                                    )|| line.text.take(4)
                                    .contains(
                                        "ate ",
                                        ignoreCase = true
                                    )||line.text.take(4)
                                    .contains(
                                        "ae ",
                                        ignoreCase = true
                                    )) && !(line.text.contains("valid", ignoreCase = true)))
                            ) {
                                val list: ArrayList<String> =
                                    line.text.split(" ").map { s -> s.trim() } as ArrayList<String>
                                list.removeAt(0)
                                try {
                                    date.setText(list.joinToString(""))
                                } catch (e: java.lang.Exception) {
                                }
                            } else if (previous != "" && (line.text.contains(
                                    "D D",
                                    ignoreCase = true
                                ))
                            ) {
                                val list: ArrayList<String> =
                                    previous.split(" ").map { s -> s.trim() } as ArrayList<String>
                                if(list[0].contains("ate", ignoreCase = true)){
                                    list.removeAt(0)
                                }
                                try {
                                    date.setText(list.joinToString(""))
                                } catch (e: java.lang.Exception) {
                                }
                            } else if ((line.text.take(3).contains(
                                    "pax ",
                                    ignoreCase = true
                                ) || line.text.contains(
                                    "pay ",
                                    ignoreCase = true
                                ) || line.text.equals(
                                    "ay ",
                                    ignoreCase = true
                                ) || (line.text.take(3).contains(
                                    "pa ",
                                    ignoreCase = true
                                )) && !(line.text.contains("payable", ignoreCase = true))
                                        )
                            ) {
                                val list: ArrayList<String> =
                                    line.text.split(" ").map { s -> s.trim() } as ArrayList<String>
                                list.removeAt(0)
                                name.setText(list.joinToString(" "))
                            } else {
                                var acno: Int = -1
                                try {
                                    acno = line.text.toInt()
                                } catch (e: NumberFormatException) {
                                }
                                if (acno != -1) {
                                    accountNumber.setText(acno.toString())
                                }
                            }
                            previous = line.text
                        }
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(context, "some error occurred", Toast.LENGTH_LONG).show()
            }.addOnCompleteListener {
                dataLayout.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            }
        } else {
            Toast.makeText(context, "some error occurred", Toast.LENGTH_LONG).show()
        }
    }
}
