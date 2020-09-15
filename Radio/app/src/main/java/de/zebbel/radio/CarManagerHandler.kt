package de.zebbel.radio

import android.os.Handler
import android.os.Looper
import android.os.Message

class CarManagerHandler(private var mainActivity: MainActivity) : Handler(Looper.myLooper()!!) {

    override fun handleMessage(message: Message) {
        super.handleMessage(message)

        if(message.obj == "Radio"){
            if(message.data.getString("type") == "freq"){
                val freq = (message.data.getInt("value") / 10000).toString()
                mainActivity.stationNameTextView?.text = StringBuilder(freq).insert(freq.length - 2, ".").toString()
                mainActivity.freqTextView?.text = StringBuilder(freq).insert(freq.length - 2, ".").toString()

                mainActivity.setFrequencyView(freq)
            }
            else if(message.data.getString("type") == "seek_found"){
                //Log.d("seek found", "")
            }
            else if(message.data.getString("type") == "tp"){
                //Log.d("radio: tp - traffic program", message.data.getInt("value").toString())
            }
            else if(message.data.getString("type") == "rt"){
                var str = message.data.getByteArray("value")?.toString(Charsets.UTF_8) as String
                str = str.replace("   ", "")
                if(mainActivity.radiotextTextView?.text != str) mainActivity.radiotextTextView?.text = str
                //Log.d("radio: rt - radio text", str)
            }
            else if(message.data.getString("type") == "stereo"){
                //Log.d("radio: stereo", message.data.getInt("value").toString())
            }
            else if(message.data.getString("type") == "pi"){
                mainActivity.stationNameTextView?.text = mainActivity.databaseAccess!!.getLongStationName(message.data.getInt("value"))
                mainActivity.stationLogoImageView!!.setImageBitmap(mainActivity.databaseAccess!!.getLogo(message.data.getInt("value")))

                //Log.d("radio: pi - programm identification", "%X".format(message.data.getInt("value")))
            }
            else if(message.data.getString("type") == "pty"){
                //Log.d("radio: pty - program type", message.data.getInt("value").toString())
            }
            else if(message.data.getString("type") == "psn"){
                //val charset = Charsets.ISO_8859_1
                //val str = message.data.getByteArray("value")?.toString(charset)
                //Log.d("radio: psn - program service name", str.toString())
            }
            else {
                return
                //Log.d("radio", message.data.getString("type").toString())
            }
        }
    }
}