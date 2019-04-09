package com.kaidongyuan.app.kdyorder.ui.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.kaidongyuan.app.kdyorder.R;
import com.kaidongyuan.app.kdyorder.ui.print.DeviceReceiver;
import com.kaidongyuan.app.kdyorder.util.ExceptionUtil;

import net.posprinter.posprinterface.IMyBinder;
import net.posprinter.posprinterface.UiExecute;
import net.posprinter.service.PosprinterService;
import net.posprinter.utils.DataForSendToPrinterPos80;
import net.posprinter.utils.PosPrinterDev;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class PrintActivity  extends BaseActivity implements View.OnClickListener {


    EditText showET;//显示
    BluetoothAdapter bluetoothAdapter;
    private View dialogView;
    private ArrayAdapter<String> adapter1//蓝牙已配的的adapter
            ,adapter2//蓝牙为配对的adapter
            ,adapter3;//usb的adapter
    private ListView lv1,lv2,lv_usb;
    private ArrayList<String> deviceList_bonded=new ArrayList<String>();//已绑定过的list
    private ArrayList<String> deviceList_found=new ArrayList<String>();//新找到的list
    private Button btn_scan;//蓝牙设备弹窗的“搜索”
    private LinearLayout LLlayout;
    android.app.AlertDialog dialog;//弹窗
    String mac;
    private DeviceReceiver myDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        try {
//            initData();
//            setTop();
//            initView();
//            initListener();
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    public void setBluetooth(){
        bluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
        //判断时候打开蓝牙设备
        if (!bluetoothAdapter.isEnabled()){
            //请求用户开启
            Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, 1);
        }else {

            showblueboothlist();

        }
    }

    private void showblueboothlist() {
        if (!bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.startDiscovery();
        }
        LayoutInflater inflater=LayoutInflater.from(this);
        dialogView=inflater.inflate(R.layout.printer_list, null);
        adapter1=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, deviceList_bonded);
        lv1=(ListView) dialogView.findViewById(R.id.listView1);
        btn_scan=(Button) dialogView.findViewById(R.id.btn_scan);
        LLlayout=(LinearLayout) dialogView.findViewById(R.id.ll1);
        lv2=(ListView) dialogView.findViewById(R.id.listView2);
        adapter2=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, deviceList_found);
        lv1.setAdapter(adapter1);
        lv2.setAdapter(adapter2);
        dialog=new android.app.AlertDialog.Builder(this).setTitle("BLE").setView(dialogView).create();
        dialog.show();

        myDevice=new DeviceReceiver(deviceList_found,adapter2,lv2);

        //注册蓝牙广播接收者
        IntentFilter filterStart=new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter filterEnd=new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(myDevice, filterStart);
        registerReceiver(myDevice, filterEnd);

        setDlistener();
        findAvalibleDevice();
    }


    private void setDlistener() {
        // TODO Auto-generated method stub
        btn_scan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                LLlayout.setVisibility(View.VISIBLE);
                //btn_scan.setVisibility(View.GONE);
            }
        });
        //已配对的设备的点击连接
        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                try {
                    if(bluetoothAdapter!=null&&bluetoothAdapter.isDiscovering()){
                        bluetoothAdapter.cancelDiscovery();

                    }

                    String msg=deviceList_bonded.get(arg2);
                    mac=msg.substring(msg.length()-17);
                    String name=msg.substring(0, msg.length()-18);
                    //lv1.setSelection(arg2);
                    dialog.cancel();
                    showET.setText(mac);
                    //Log.i("TAG", "mac="+mac);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        //未配对的设备，点击，配对，再连接
        lv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                try {
                    if(bluetoothAdapter!=null&&bluetoothAdapter.isDiscovering()){
                        bluetoothAdapter.cancelDiscovery();

                    }
                    String msg=deviceList_found.get(arg2);
                    mac=msg.substring(msg.length()-17);
                    String name=msg.substring(0, msg.length()-18);
                    //lv2.setSelection(arg2);
                    dialog.cancel();
                    showET.setText(mac);
                    Log.i("TAG", "mac="+mac);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    /*
    找可连接的蓝牙设备
     */
    private void findAvalibleDevice() {
        // TODO Auto-generated method stub
        //获取可配对蓝牙设备
        Set<BluetoothDevice> device=bluetoothAdapter.getBondedDevices();

        deviceList_bonded.clear();
        if(bluetoothAdapter!=null&&bluetoothAdapter.isDiscovering()){
            adapter1.notifyDataSetChanged();
        }
        if(device.size()>0){
            //存在已经配对过的蓝牙设备
            for(Iterator<BluetoothDevice> it = device.iterator(); it.hasNext();){
                BluetoothDevice btd=it.next();
                deviceList_bonded.add(btd.getName()+'\n'+btd.getAddress());
                adapter1.notifyDataSetChanged();
            }
        }else{  //不存在已经配对过的蓝牙设备
            deviceList_bonded.add("No can be matched to use bluetooth");
            adapter1.notifyDataSetChanged();
        }

    }

    View dialogView3;
    private TextView tv_usb;
    private List<String> usbList,usblist;

    /*
    uSB连接
     */
    private void setUSB(){
        LayoutInflater inflater=LayoutInflater.from(this);
        dialogView3=inflater.inflate(R.layout.usb_link,null);
        tv_usb= (TextView) dialogView3.findViewById(R.id.textView1);
        lv_usb= (ListView) dialogView3.findViewById(R.id.listView1);


        usbList= PosPrinterDev.GetUsbPathNames(this);
        if (usbList==null){
            usbList=new ArrayList<>();
        }
        usblist=usbList;
        tv_usb.setText(getString(R.string.usb_pre_con)+usbList.size());
        adapter3=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,usbList);
        lv_usb.setAdapter(adapter3);


        android.support.v7.app.AlertDialog dialog=new android.support.v7.app.AlertDialog.Builder(this)
                .setView(dialogView3).create();
        dialog.show();

        setUsbLisener(dialog);

    }
    String usbDev="";
    public void setUsbLisener(final android.support.v7.app.AlertDialog dialog) {

        lv_usb.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                usbDev=usbList.get(i);
                showET.setText(usbDev);
                dialog.cancel();
                Log.e("usbDev: ",usbDev);
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    public void fds(View v) {

        Log.d("LM", "fds: ");
        setBluetooth();
    }
}
