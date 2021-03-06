package com.kaidongyuan.app.kdyorder.ui.activity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kaidongyuan.app.kdyorder.R;
import com.kaidongyuan.app.kdyorder.app.MyApplication;
import com.kaidongyuan.app.kdyorder.bean.OrderDetails;
import com.kaidongyuan.app.kdyorder.bean.OutPutOrderProduct;
import com.kaidongyuan.app.kdyorder.constants.EXTRAConstants;
import com.kaidongyuan.app.kdyorder.constants.SharedPreferenceConstants;
import com.kaidongyuan.app.kdyorder.ui.print.DeviceReceiver;
import com.kaidongyuan.app.kdyorder.util.DateUtil;
import com.kaidongyuan.app.kdyorder.util.ExceptionUtil;
import com.kaidongyuan.app.kdyorder.util.SharedPreferencesUtil;
import com.kaidongyuan.app.kdyorder.util.StringUtils;
import com.kaidongyuan.app.kdyorder.util.ToastUtil;
import com.kaidongyuan.app.kdyorder.util.Tools;

import net.posprinter.posprinterface.IMyBinder;
import net.posprinter.posprinterface.ProcessData;
import net.posprinter.posprinterface.UiExecute;
import net.posprinter.service.PosprinterService;
import net.posprinter.utils.DataForSendToPrinterPos80;
import net.posprinter.utils.PosPrinterDev;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class PrintActivity extends BaseActivity implements View.OnClickListener {

    /**
     * 返回上一界面按钮
     */
    private ImageView mImageViewGoBack;

    //IMyBinder接口，所有可供调用的连接和发送数据的方法都封装在这个接口内
    public static IMyBinder binder;

    //bindService的参数connection
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //绑定成功
            binder = (IMyBinder) iBinder;
            Log.e("binder", "connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e("disbinder", "disconnected");
        }
    };

    public static boolean ISCONNECT;//判断是否连接成功
    Button BTCon,//连接按钮
            BTDisconnect,
            BtSb,
            btPrintCustom,   //打印客户联
            btPrintReceipt;  //打印回单联
    EditText showET;//显示
    BluetoothAdapter bluetoothAdapter;
    private View dialogView;
    private ArrayAdapter<String> adapter1//蓝牙已配的的adapter
            , adapter2//蓝牙为配对的adapter
            , adapter3;//usb的adapter
    private ListView lv1, lv2, lv_usb;
    private ArrayList<String> deviceList_bonded = new ArrayList<String>();//已绑定过的list
    private ArrayList<String> deviceList_found = new ArrayList<String>();//新找到的list
    private Button btn_scan;//蓝牙设备弹窗的“搜索”
    private LinearLayout LLlayout;
    android.app.AlertDialog dialog;//弹窗
    String mac;
    private DeviceReceiver myDevice;

    // 总金额
    private float outPutTotalPrice = (float) 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        try {
            //绑定service，获取ImyBinder对象
            Intent intent = new Intent(this, PosprinterService.class);
            bindService(intent, conn, BIND_AUTO_CREATE);
//            initData();
//            setTop();
            initView();
            setlistener();
            // 自动填充蓝牙打机机并连接（上一次成功连接）
            autoConBlue();
        } catch (Exception e) {
            ExceptionUtil.handlerException(e);
        }
    }

    //初始化控件
    private void initView() {

        mImageViewGoBack = (ImageView) this.findViewById(R.id.button_goback);
        BtSb = (Button) findViewById(R.id.buttonSB);
        showET = (EditText) findViewById(R.id.showET);
        BTCon = (Button) findViewById(R.id.buttonConnect);
        BTDisconnect = (Button) findViewById(R.id.buttonDisconnect);
        btPrintCustom = (Button) findViewById(R.id.btPrintCustom);
    }

    //给按钮添加监听事件
    private void setlistener() {

        mImageViewGoBack.setOnClickListener(this);
        BtSb.setOnClickListener(this);
        BTCon.setOnClickListener(this);
        BTDisconnect.setOnClickListener(this);
        btPrintCustom.setOnClickListener(this);

//        conPort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                pos=i;
//                switch (i){
//                    case 0:
//                        //spiner是网络连接时的处理
//                        showET.setText("");
//                        showET.setEnabled(true);
//                        BtSb.setVisibility(View.GONE);
//                        showET.setHint(getString(R.string.hint));
//                        break;
//                    case 1:
//                        //spiner是蓝牙连接时的处理
//                        showET.setText("");
//                        BtSb.setVisibility(View.VISIBLE);
//                        showET.setHint(getString(R.string.bleselect));
//                        showET.setEnabled(false);
//                        break;
//                    case 2:
//                        //spiner是USB时的处理
//                        showET.setText("");
//                        BtSb.setVisibility(View.VISIBLE);
//                        showET.setHint(getString(R.string.usbselect));
//                        showET.setEnabled(false);
//                        break;
//                    default:break;
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

    }

    private void autoConBlue() {

        String mac = SharedPreferencesUtil.getValueByName(SharedPreferenceConstants.BUSSINESS_CODE,
                SharedPreferenceConstants.LAST_BLUETOOTH_CONNECTION, SharedPreferencesUtil.STRING);
        if (!mac.equals("")) {

            Log.d("LM", "有");

            showET.setText(mac);
            ToastUtil.showToastBottom("正在连接打印机...", Toast.LENGTH_LONG);

            new Thread() {
                public void run() {

                    try {
                        sleep(400);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            connetBle();
                        }
                    });
                }
            }.start();
        } else {

            Log.d("LM", "没有 ");
        }
    }

    public void setBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //判断时候打开蓝牙设备
        if (!bluetoothAdapter.isEnabled()) {
            //请求用户开启
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, 1);
        } else {

            showblueboothlist();

        }
    }

    private void showblueboothlist() {
        if (!bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.startDiscovery();
        }
        LayoutInflater inflater = LayoutInflater.from(this);
        dialogView = inflater.inflate(R.layout.printer_list, null);
        adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, deviceList_bonded);
        lv1 = (ListView) dialogView.findViewById(R.id.listView1);
        btn_scan = (Button) dialogView.findViewById(R.id.btn_scan);
        LLlayout = (LinearLayout) dialogView.findViewById(R.id.ll1);
        lv2 = (ListView) dialogView.findViewById(R.id.listView2);
        adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, deviceList_found);
        lv1.setAdapter(adapter1);
        lv2.setAdapter(adapter2);
        dialog = new android.app.AlertDialog.Builder(this).setTitle("BLE").setView(dialogView).create();
        dialog.show();

        myDevice = new DeviceReceiver(deviceList_found, adapter2, lv2);

        //注册蓝牙广播接收者
        IntentFilter filterStart = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter filterEnd = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
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
                    if (bluetoothAdapter != null && bluetoothAdapter.isDiscovering()) {
                        bluetoothAdapter.cancelDiscovery();

                    }

                    String msg = deviceList_bonded.get(arg2);
                    mac = msg.substring(msg.length() - 17);

                    // 存储mac，下次自动连接
                    SharedPreferencesUtil.WriteSharedPreferences(SharedPreferenceConstants.BUSSINESS_CODE,
                            SharedPreferenceConstants.LAST_BLUETOOTH_CONNECTION, mac);

                    String name = msg.substring(0, msg.length() - 18);
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
                    if (bluetoothAdapter != null && bluetoothAdapter.isDiscovering()) {
                        bluetoothAdapter.cancelDiscovery();

                    }
                    String msg = deviceList_found.get(arg2);
                    mac = msg.substring(msg.length() - 17);

                    // 存储mac，下次自动连接
                    SharedPreferencesUtil.WriteSharedPreferences(SharedPreferenceConstants.BUSSINESS_CODE,
                            SharedPreferenceConstants.LAST_BLUETOOTH_CONNECTION, mac);

                    String name = msg.substring(0, msg.length() - 18);
                    //lv2.setSelection(arg2);
                    dialog.cancel();
                    showET.setText(mac);
                    Log.i("TAG", "mac=" + mac);
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
        Set<BluetoothDevice> device = bluetoothAdapter.getBondedDevices();

        deviceList_bonded.clear();
        if (bluetoothAdapter != null && bluetoothAdapter.isDiscovering()) {
            adapter1.notifyDataSetChanged();
        }
        if (device.size() > 0) {
            //存在已经配对过的蓝牙设备
            for (Iterator<BluetoothDevice> it = device.iterator(); it.hasNext(); ) {
                BluetoothDevice btd = it.next();
                deviceList_bonded.add(btd.getName() + '\n' + btd.getAddress());
                adapter1.notifyDataSetChanged();
            }
        } else {  //不存在已经配对过的蓝牙设备
            deviceList_bonded.add("No can be matched to use bluetooth");
            adapter1.notifyDataSetChanged();
        }

    }

    View dialogView3;
    private TextView tv_usb;
    private List<String> usbList, usblist;

    /*
    uSB连接
     */
    private void setUSB() {
        LayoutInflater inflater = LayoutInflater.from(this);
        dialogView3 = inflater.inflate(R.layout.usb_link, null);
        tv_usb = (TextView) dialogView3.findViewById(R.id.textView1);
        lv_usb = (ListView) dialogView3.findViewById(R.id.listView1);


        usbList = PosPrinterDev.GetUsbPathNames(this);
        if (usbList == null) {
            usbList = new ArrayList<>();
        }
        usblist = usbList;
        tv_usb.setText(getString(R.string.usb_pre_con) + usbList.size());
        adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, usbList);
        lv_usb.setAdapter(adapter3);


        android.support.v7.app.AlertDialog dialog = new android.support.v7.app.AlertDialog.Builder(this)
                .setView(dialogView3).create();
        dialog.show();

        setUsbLisener(dialog);

    }

    String usbDev = "";

    public void setUsbLisener(final android.support.v7.app.AlertDialog dialog) {

        lv_usb.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                usbDev = usbList.get(i);
                showET.setText(usbDev);
                dialog.cancel();
                Log.e("usbDev: ", usbDev);
            }
        });
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        // 返回
        if (id == R.id.button_goback) {

            this.finish();
        }

        // 设备按钮
        if (id == R.id.buttonSB) {

            setBluetooth();
            BTCon.setText(getString(R.string.connect));
        }
        // 蓝牙连接
        if (id == R.id.buttonConnect) {

            connetBle();
        }
        // 断开按钮
        if (id == R.id.buttonDisconnect) {
            if (ISCONNECT) {
                binder.disconnectCurrentPort(new UiExecute() {
                    @Override
                    public void onsucess() {
                        ToastUtil.showToastBottom(getString(R.string.toast_discon_success), Toast.LENGTH_SHORT);
                        showET.setText("");
                        BTCon.setText(getString(R.string.connect));
                    }

                    @Override
                    public void onfailed() {
                        ToastUtil.showToastBottom(getString(R.string.toast_discon_faile), Toast.LENGTH_SHORT);

                    }
                });
            } else {
                ToastUtil.showToastBottom(getString(R.string.toast_present_con), Toast.LENGTH_SHORT);
            }
        }

        // 打印客户联
        if (id == R.id.btPrintCustom) {

            printText("客户联");

            printText("虚线");

            printText("回单联");
        }
    }

    /*
        打印文本
        pos指令中并没有专门的打印文本的指令
        但是，你发送过去的数据，如果不是打印机能识别的指令，满一行后，就可以自动打印了，或者加上OA换行，也能打印
         */
    private void printText(final String CUSTOM_OR_RECEIPT) {

        if (!ISCONNECT) {
            ToastUtil.showToastBottom(getString(R.string.connect_first), Toast.LENGTH_SHORT);
            return;
        }

        try {
            binder.writeDataByYouself(
                    new UiExecute() {
                        @Override
                        public void onsucess() {

                        }

                        @Override
                        public void onfailed() {

                        }
                    }, new ProcessData() {
                        @Override
                        public List<byte[]> processDataBeforeSend() {

                            List<byte[]> list = new ArrayList<byte[]>();
                            //创建一段我们想打印的文本,转换为byte[]类型，并添加到要发送的数据的集合list中

                            //初始化打印机，清除缓存
                            list.add(DataForSendToPrinterPos80.initializePrinter());

                            if (CUSTOM_OR_RECEIPT.equals("虚线")) {

                                list.add(DataForSendToPrinterPos80.printAndFeedLine());
                                list.add(StringUtils.strTobytes("- - - - - - - - - - - - - - - - - - - - - - - -"));
                                list.add(DataForSendToPrinterPos80.printAndFeedLine());
                                list.add(DataForSendToPrinterPos80.printAndFeedLine());
                                return list;
                            }

                            // 客户联|回单联
                            list.add(DataForSendToPrinterPos80.setAbsolutePrintPosition(200, 01));
                            if (CUSTOM_OR_RECEIPT.equals("客户联")) {
                                list.add(StringUtils.strTobytes("【客户联】"));
                            } else if (CUSTOM_OR_RECEIPT.equals("回单联")) {
                                list.add(StringUtils.strTobytes("【回单联】"));
                            } list.add(DataForSendToPrinterPos80.printAndFeedLine());

                            // 供应商
                            String supplier = "";
                            // 客户代码
                            String patyCode = "";
                            // 客户名称
                            String patyName = "";
                            // 客户地址
                            String patyAddress = "";
                            // 客户电话
                            String patyTel = "";
                            Intent intent = getIntent();
                            List<OutPutOrderProduct> OPGoods = null;
                            List<OrderDetails> IPGoods = null;
                            outPutTotalPrice = 0;
                            try {
                                supplier = intent.getStringExtra(EXTRAConstants.EXTRA_OUTPUT_SUPPLIER);
                                patyCode = intent.getStringExtra(EXTRAConstants.EXTRA_OUTPUT_PARTY_CODE);
                                patyTel = intent.getStringExtra(EXTRAConstants.EXTRA_OUTPUT_PARTY_TEL);
                                patyName = intent.getStringExtra(EXTRAConstants.EXTRA_OUTPUT_PARTY_NAME);
                                patyAddress = intent.getStringExtra(EXTRAConstants.EXTRA_OUTPUT_PARTY_ADDRESS);
                                OPGoods = (List<OutPutOrderProduct>) getIntent().getSerializableExtra(EXTRAConstants.EXTRA_OUTPUT_GOODS);
                                IPGoods = (List<OrderDetails>) getIntent().getSerializableExtra(EXTRAConstants.EXTRA_PRINT_INPUT_GOODS);
                            } catch (Exception e) {
                            }


                            // 头部
                            // 抬头 居中
                            list.add(DataForSendToPrinterPos80.selectAlignment(1));
                            list.add(StringUtils.strTobytes(supplier));
                            list.add(DataForSendToPrinterPos80.printAndFeedLine());
                            list.add(DataForSendToPrinterPos80.selectAlignment(0));
                            list.add(StringUtils.strTobytes("---------------------------------------------"));
                            list.add(DataForSendToPrinterPos80.printAndFeedLine());


                            list.add(DataForSendToPrinterPos80.setAbsolutePrintPosition(00, 00));
                            // 客户代码/电话/ 居左
                            String partyCode = "客户代码：" + patyCode + "   [" + patyTel + "]";
                            list.add(StringUtils.strTobytes(partyCode));
                            list.add(DataForSendToPrinterPos80.printAndFeedLine());

                            // 客户名称 居左
                            String partyName = "客户名称：" + patyName;
                            list.add(StringUtils.strTobytes(partyName));
                            list.add(DataForSendToPrinterPos80.printAndFeedLine());

                            // 客户名称 居左
                            String partyAddress = "客户地址：" + patyAddress;
                            list.add(StringUtils.strTobytes(partyAddress));
                            list.add(DataForSendToPrinterPos80.printAndFeedLine());

                            list.add(StringUtils.strTobytes("---------------------------------------------"));
                            list.add(DataForSendToPrinterPos80.printAndFeedLine());
                            // 商品格式说明 居左
                            list.add(StringUtils.strTobytes("商品/数量/单价"));
                            list.add(DataForSendToPrinterPos80.printAndFeedLine());
                            list.add(StringUtils.strTobytes("---------------------------------------------"));
                            list.add(DataForSendToPrinterPos80.printAndFeedLine());
                            list.add(DataForSendToPrinterPos80.selectAlignment(0));
                            // 销售出库单
                            if (OPGoods != null) {

                                for (int i = 0; i < OPGoods.size(); i++) {

                                    OutPutOrderProduct op = OPGoods.get(i);
                                    String name = (i + 1) + "." + op.getPRODUCT_NAME();
                                    // 产品名称太长，分两行
                                    String namePadPreix = name;
                                    String nameSuffix = "";

                                    // 数量占位符
                                    String qtyLoc = "abcdefgheijklnmopqrstuv";
                                    int nameLenght = Tools.textLength(namePadPreix);
                                    int pad = Tools.textLength(qtyLoc) - nameLenght;
                                    if (pad > 0) {
                                        for (int j = 0; j < pad; j++) {
                                            namePadPreix = namePadPreix + " ";
                                        }
                                    }

                                    // 产品名称超过设置长度，自动换行
                                    if(pad < 0) {
                                        int padPreix = 1;
                                        for (int j = 0; j <= name.length(); j++) {
                                            if(padPreix > 0) {
                                                namePadPreix = name.substring(0,j);
                                                int namePadPreixLenght = Tools.textLength(namePadPreix);
                                                padPreix = Tools.textLength(qtyLoc) - namePadPreixLenght;
                                            }else {
                                                nameSuffix = name.substring(j - 1, name.length());
                                                break;
                                            }
                                        }
                                    }

                                    // 名称
                                    list.add(DataForSendToPrinterPos80.setAbsolutePrintPosition(00, 00));
                                    list.add(StringUtils.strTobytes(namePadPreix));

                                    // 数量
                                    String qty = "   " + Tools.oneDecimal(op.getOUTPUT_QTY()) + "[" + op.getOUTPUT_UOM() + "]";
                                    list.add(StringUtils.strTobytes(qty));

                                    // 金额
                                    String price = "￥" + Tools.twoDecimal(Tools.convertToFloat(op.getORG_PRICE(), 0) - Tools.convertToFloat(op.getMJ_PRICE(), 0));
                                    list.add(DataForSendToPrinterPos80.setAbsolutePrintPosition(200, 01));
                                    list.add(StringUtils.strTobytes(price));
                                    list.add(DataForSendToPrinterPos80.printAndFeedLine());

                                    if(pad < 0) {
                                        // 名称(第二行)
                                        list.add(DataForSendToPrinterPos80.setAbsolutePrintPosition(25, 00));
                                        list.add(StringUtils.strTobytes(nameSuffix));
                                        list.add(DataForSendToPrinterPos80.printAndFeedLine());
                                    }

                                    // 总金额
                                    outPutTotalPrice += ((Tools.convertToFloat(op.getORG_PRICE(), 0) - Tools.convertToFloat(op.getMJ_PRICE(), 0)) * op.getOUTPUT_QTY());
                                    Log.d("LM", "processDataBeforeSend: ");
                                }
                            }else if(IPGoods != null) {

                                for (int i = 0; i < IPGoods.size(); i++) {

                                    OrderDetails ip = IPGoods.get(i);
                                    String name = (i + 1) + "." + ip.getPRODUCT_NAME();
                                    // 产品名称太长，分两行
                                    String namePadPreix = name;
                                    String nameSuffix = "";

                                    // 数量占位符
                                    String qtyLoc = "abcdefgheijklnmopqrstuv";
                                    int nameLenght = Tools.textLength(namePadPreix);
                                    int pad = Tools.textLength(qtyLoc) - nameLenght;
                                    if (pad > 0) {
                                        for (int j = 0; j < pad; j++) {
                                            namePadPreix = namePadPreix + " ";
                                        }
                                    }

                                    // 产品名称超过设置长度，自动换行
                                    if(pad < 0) {
                                        int padPreix = 1;
                                        for (int j = 0; j <= name.length(); j++) {
                                            if(padPreix > 0) {
                                                namePadPreix = name.substring(0,j);
                                                int namePadPreixLenght = Tools.textLength(namePadPreix);
                                                padPreix = Tools.textLength(qtyLoc) - namePadPreixLenght;
                                            }else {
                                                nameSuffix = name.substring(j - 1, name.length());
                                                break;
                                            }
                                        }
                                    }

                                    // 名称
                                    list.add(DataForSendToPrinterPos80.setAbsolutePrintPosition(00, 00));
                                    list.add(StringUtils.strTobytes(namePadPreix));

                                    // 数量
                                    String qty = "   " + Tools.oneDecimal(ip.getORDER_QTY()) + "[" + ip.getORDER_UOM() + "]";
                                    list.add(StringUtils.strTobytes(qty));

                                    // 金额
                                    String price = "￥" + Tools.twoDecimal(ip.getACT_PRICE());
                                    list.add(DataForSendToPrinterPos80.setAbsolutePrintPosition(200, 01));
                                    list.add(StringUtils.strTobytes(price));
                                    list.add(DataForSendToPrinterPos80.printAndFeedLine());

                                    if(pad < 0) {
                                        // 名称(第二行)
                                        list.add(DataForSendToPrinterPos80.setAbsolutePrintPosition(25, 00));
                                        list.add(StringUtils.strTobytes(nameSuffix));
                                        list.add(DataForSendToPrinterPos80.printAndFeedLine());
                                    }

                                    // 总金额
                                    outPutTotalPrice += (ip.getACT_PRICE() * ip.getORDER_QTY());
                                    Log.d("LM", "processDataBeforeSend: ");
                                }
                            }

                            // 尾部
                            // 总数量、总金额
                            String totalQTY = "";
                            float totalPrice = outPutTotalPrice;
                            String orderNO = "";

                            try {
                                totalQTY = intent.getStringExtra(EXTRAConstants.EXTRA_OUTPUT_GOODS_QTY);
                                orderNO = intent.getStringExtra(EXTRAConstants.EXTRA_OUTPUT_NO);
                            } catch (Exception e) {
                            }
                            list.add(DataForSendToPrinterPos80.selectAlignment(0));
                            list.add(StringUtils.strTobytes("---------------------------------------------"));
                            list.add(DataForSendToPrinterPos80.printAndFeedLine());
                            String total = "总数量：" + Tools.oneDecimal(totalQTY) + "     总金额：" + Tools.twoDecimal(totalPrice);
                            list.add(StringUtils.strTobytes(total));
                            list.add(DataForSendToPrinterPos80.printAndFeedLine());

                            // 打印时间、开单人、帐号
                            String userName = MyApplication.userName;
                            String time = Tools.getCurrDate() + "  [" + MyApplication.getInstance().getUser().getUSER_NAME() + "  " + userName + "]";
                            list.add(StringUtils.strTobytes(time));
                            list.add(DataForSendToPrinterPos80.printAndFeedLine());

                            // 订单号
                            String ordNo = "订单号：" + orderNO;
                            list.add(StringUtils.strTobytes(ordNo));
                            list.add(DataForSendToPrinterPos80.printAndFeedLine());

                            if (CUSTOM_OR_RECEIPT.equals("回单联")) {
                                list.add(DataForSendToPrinterPos80.printAndFeedLine());
                                list.add(StringUtils.strTobytes("客户签名："));
                                list.add(DataForSendToPrinterPos80.printAndFeedLine());
                            }
                            return list;
                        }
                    });
        } catch (Exception e) {

            ToastUtil.showToastBottom("打印异常：" + e.getMessage(), Toast.LENGTH_SHORT);
        }

    }

    /*
    蓝牙连接
    */
    private void connetBle() {
        String bleAdrress = showET.getText().toString();
        if (bleAdrress.equals(null) || bleAdrress.equals("")) {

            ToastUtil.showToastBottom(getString(R.string.bleselect), Toast.LENGTH_SHORT);
        } else {
            binder.connectBtPort(bleAdrress, new UiExecute() {
                @Override
                public void onsucess() {
                    ISCONNECT = true;
                    ToastUtil.showToastBottom(getString(R.string.con_success), Toast.LENGTH_SHORT);
                    BTCon.setText(getString(R.string.con_success));
                    //此处也可以开启读取打印机的数据
                    //参数同样是一个实现的UiExecute接口对象
                    //如果读的过程重出现异常，可以判断连接也发生异常，已经断开
                    //这个读取的方法中，会一直在一条子线程中执行读取打印机发生的数据，
                    //直到连接断开或异常才结束，并执行onfailed
                    binder.write(DataForSendToPrinterPos80.openOrCloseAutoReturnPrintState(0x1f), new UiExecute() {
                        @Override
                        public void onsucess() {
                            binder.acceptdatafromprinter(new UiExecute() {
                                @Override
                                public void onsucess() {

                                }

                                @Override
                                public void onfailed() {
                                    ISCONNECT = false;
                                    ToastUtil.showToastBottom(getString(R.string.con_has_discon), Toast.LENGTH_SHORT);
                                }
                            });
                        }

                        @Override
                        public void onfailed() {

                        }
                    });


                }

                @Override
                public void onfailed() {
                    //连接失败后在UI线程中的执行
                    ISCONNECT = false;
                    ToastUtil.showToastBottom(getString(R.string.con_failed), Toast.LENGTH_SHORT);
                }
            });
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binder.disconnectCurrentPort(new UiExecute() {
            @Override
            public void onsucess() {

            }

            @Override
            public void onfailed() {

            }
        });
        unbindService(conn);
    }
}
