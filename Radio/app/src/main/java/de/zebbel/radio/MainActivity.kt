package de.zebbel.radio

import android.microntek.CarManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    private val context = this

    // init CarManager
    private var carManager: CarManager? = CarManager()

    // sqlite database acess
    var databaseAccess: DatabaseAccess? = null

    // UI Views
    private var freqSeekbar: SeekBar? = null
    private var scanPrevButton: ImageView? = null
    private var scanNextButton: ImageView? = null
    var freqTextView: TextView? = null
    var stationLogoImageView: ImageView? = null
    var stationNameTextView: TextView? = null
    var radiotextTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // sqlite database (Radio Logos)
        databaseAccess = DatabaseAccess.getInstance(this)
        databaseAccess!!.open()

        // init carManager handler
        carManager!!.attach(CarManagerHandler(context), "Radio,KeyDown")

        initFreqSeekbar()
        initRadioSettings()

        startRadio()
    }

    // on app destroy
    override fun onDestroy() {
        super.onDestroy()
        databaseAccess!!.close()
        pauseRadio()
    }

    // init frequency seekbar
    private fun initFreqSeekbar(){
        freqSeekbar = findViewById(R.id.freqSeekbar)

        freqSeekbar!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            var value = 0
            val stepSize = 5
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) { value = (i / stepSize).toDouble().roundToInt() * stepSize }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) { setFrequency(value * 10) }
        })

        freqTextView = findViewById(R.id.freqTextView)
        stationLogoImageView = findViewById(R.id.stationLogoimageView)
        stationNameTextView = findViewById(R.id.stationNameTextView)
        radiotextTextView = findViewById(R.id.radiotextTextView)
        scanPrevButton = findViewById(R.id.scanPrevButton)
        scanNextButton = findViewById(R.id.scanNextButton)

        stationLogoImageView!!.setImageBitmap(databaseAccess!!.getLogo(100))

        scanPrevButton!!.setOnClickListener { scanDown()}
        scanNextButton!!.setOnClickListener { scanUp()}
    }

    // init radio settings
    private fun initRadioSettings(){
        carManager!!.setParameters("ctl_radio_rds=1")               // enable rds
        carManager!!.setParameters("ctl_radio_st=1")                // enable stereo
        // carManager!!.setParameters("ctl_radio_af=1")             // enable alternative frequency
        // carManager!!.setParameters("ctl_radio_ta=1")             // enable traffic announcement
        // carManager!!.setParameters("ctl_radio_pty=1")            // enable program type (classic, rock, pop, ...)
        // carManager!!.setParameters("ctl_radio_loc=1")            // enable local ???
        // carManager!!.setParameters("ctl_radio_tune=up")          // fine tune
        // carManager!!.setParameters("ctl_radio_tune=down")        // fine tune
        // carManager!!.setParameters("ctl_radio_seek=up")          // seek signal upwards
        // carManager!!.setParameters("ctl_radio_seek=down")        // seek signal downwards
        // carManager!!.setParameters("ctl_radio_seek=auto")        // seek signal automatic ???
        // carManager!!.setParameters("ctl_radio_frequency=99900")  // set frequency to 99,9Mhz
    }

    // start Radio
    private fun startRadio(){
        carManager!!.setParameters("av_focus_gain=fm")
        carManager!!.setParameters("av_channel_enter=fm")
    }

    // pause radio
    private fun pauseRadio(){
        carManager!!.setParameters("av_focus_loss=fm")
        carManager!!.setParameters("av_channel_exit=fm")
    }

    // seek frequency upwards
    private fun scanUp(){
        stationLogoImageView!!.setImageBitmap(databaseAccess!!.getLogo(100))
        carManager!!.setParameters("ctl_radio_seek=up")
    }

    // seek frequency downwards
    private fun scanDown(){
        stationLogoImageView!!.setImageBitmap(databaseAccess!!.getLogo(100))
        carManager!!.setParameters("ctl_radio_seek=down")
    }

    // set frequency on radio module
    fun setFrequency(frequency: Int){
        stationLogoImageView!!.setImageBitmap(databaseAccess!!.getLogo(100))
        carManager!!.setParameters("ctl_radio_frequency=$frequency")
    }

    // set update frequency views
    fun setFrequencyView(value: String){
        freqSeekbar!!.progress = value.toInt()
        freqTextView!!.text = StringBuilder(value).insert(value.length - 2, ".").toString()
    }
}