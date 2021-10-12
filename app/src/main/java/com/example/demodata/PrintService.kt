package com.example.demodata

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.graphics.*
import android.os.Handler
import android.os.IBinder
import android.util.LruCache
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anggastudio.printama.DeviceListFragment
import com.anggastudio.printama.Printama
import java.util.*

class PrintService : Service(), View.OnClickListener {
    private var mBluetoothAdapter: BluetoothAdapter? = null

    private var row_item: RecyclerView? = null
    private var btn: Button? = null
    private var topRcv: LinearLayout? = null
    private var btmRcv: LinearLayout? = null
    private var mFloatingView: View? = null
    var params: WindowManager.LayoutParams? = null
    private var mWindowManager: WindowManager? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        init()
        return START_NOT_STICKY
    }


    private fun init() {
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_list_product, null)
        initFloatingProperty()
        btn = mFloatingView?.findViewById(R.id.btn)
        topRcv = mFloatingView?.findViewById(R.id.topRcv)
        row_item = mFloatingView?.findViewById(R.id.row_item)
        btmRcv = mFloatingView?.findViewById(R.id.btmRcv)

        row_item?.let { setRecyclerView(it) }
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter == null) {
            Toast.makeText(
                applicationContext,
                "Bluetooth not supported!!", Toast.LENGTH_LONG
            ).show()
            return
        }
        btn?.setOnClickListener {
            if (isBluetoothEnabled()) {
                getConnectedDevice()
            }
            else
            {
                Toast.makeText(baseContext, "Bluetooth Disabled", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun isBluetoothEnabled(): Boolean {
        return mBluetoothAdapter?.isEnabled == true
    }

    private fun showToast(message: String?) {
        Toast.makeText(baseContext, message, Toast.LENGTH_LONG).show()
    }

    fun getBitmapFromView(view: View): Bitmap? {
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) bgDrawable.draw(canvas) else canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return returnedBitmap
    }

    fun getConnectedDevice()
    {

        if (mBluetoothAdapter != null && mBluetoothAdapter?.bondedDevices?.isNotEmpty() == true) {
            Printama.with(baseContext, mBluetoothAdapter?.bondedDevices?.first()?.name).connect({ printama ->
                printama.printImage(topRcv?.let { it1 -> getBitmapFromView(it1) })
                printama.printImage(row_item?.let { getScreenshotFromRecyclerView(it) }, 380)
                printama.printImage(btmRcv?.let { it1 -> getBitmapFromView(it1) })
                printama.close()
            }) { message: String? -> showToast(message) }
        } else {
            showToast("Error")
        }
    }

    fun getScreenshotFromRecyclerView(view: RecyclerView): Bitmap? {
        val adapter = view.adapter
        var bigBitmap: Bitmap? = null
        if (adapter != null) {
            val size = adapter.itemCount
            var height = 0
            val paint = Paint()
            var iHeight = 0
            val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()

            // Use 1/8th of the available memory for this memory cache.
            val cacheSize = maxMemory / 8
            val bitmaCache = LruCache<String, Bitmap>(cacheSize)
            for (i in 0 until size) {
                val holder = adapter.createViewHolder(view, adapter.getItemViewType(i))
                adapter.onBindViewHolder(holder, i)
                holder.itemView.measure(
                    View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )
                holder.itemView.layout(
                    0,
                    0,
                    holder.itemView.measuredWidth,
                    holder.itemView.measuredHeight
                )
                holder.itemView.isDrawingCacheEnabled = true
                holder.itemView.buildDrawingCache()
                val drawingCache = holder.itemView.drawingCache
                if (drawingCache != null) {
                    bitmaCache.put(i.toString(), drawingCache)
                }
                //                holder.itemView.setDrawingCacheEnabled(false);
//                holder.itemView.destroyDrawingCache();
                height += holder.itemView.measuredHeight
            }
            bigBitmap = Bitmap.createBitmap(view.measuredWidth, height, Bitmap.Config.ARGB_8888)
            val bigCanvas = Canvas(bigBitmap)
            bigCanvas.drawColor(Color.WHITE)
            for (i in 0 until size) {
                val bitmap = bitmaCache[i.toString()]
                bigCanvas.drawBitmap(bitmap, 0f, iHeight.toFloat(), paint)
                iHeight += bitmap.height
                bitmap.recycle()
            }
        }
        return bigBitmap
    }

    private fun initFloatingProperty() {
        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,//new
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params!!.gravity = Gravity.BOTTOM
        params!!.x = 0
//        params!!.y = 100

        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        mWindowManager!!.addView(mFloatingView, params)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onClick(p0: View?) {

    }

    override fun stopService(name: Intent?): Boolean {
        return super.stopService(name)
    }

    private fun setRecyclerView(rvItem: RecyclerView) {
        val listdata: ArrayList<ItemsItem> = ArrayList<ItemsItem>()
        listdata.add(ItemsItem("Bonn Brown Bread", "40", "2*48", "76"))
        listdata.add(ItemsItem("Maggie Desi Cheesy 60gm", "15", "2*14", "14"))
        listdata.add(ItemsItem("Amul Gold Milk 500 ml", "30", "3*30", "30"))
        listdata.add(ItemsItem("Amul Butter 100 gm", "18", "1*17", "17"))
        rvItem.adapter = MyListAdapter(listdata)
        rvItem.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

    }

    override fun onDestroy() {
        super.onDestroy()
        if (mFloatingView != null) mWindowManager!!.removeView(mFloatingView)
    }
}