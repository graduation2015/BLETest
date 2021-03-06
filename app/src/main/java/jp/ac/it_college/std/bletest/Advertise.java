package jp.ac.it_college.std.bletest;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattServerCallback;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.ParcelUuid;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.UUID;

import android.content.Context;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Vibrator;
import android.util.Log;
import android.widget.EditText;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class Advertise extends AdvertiseCallback {

    //UUID
    public static final String SERVICE_UUID_YOU_CAN_CHANGE = "0000180a-0000-1000-8000-00805f9b34fb";
    public static final String CHAR_UUID_YOU_CAN_CHANGE = "00002a29-0000-1000-8000-00805f9b34fb";

    //アドバタイズの設定
    private static final boolean CONNECTABLE = true;
    private static final int TIMEOUT = 0;

    //BLE
    private BluetoothLeAdvertiser advertiser;
    private BluetoothGattServer bluetoothGattServer;

    //Server Message
    private String messageRead = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String SERVER_MESSAGE_WRITE = "Write request";
    private static final String NO_MESSAGE = "No messages";


    //Vibrator
    private Vibrator vibrator;

    private Context context;
    private EditText inputText;
    private static final String TAG = "AdvertiseClass";

    public Advertise(Context context, EditText inputText) {
        this.context = context;
        this.inputText = inputText;
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    private BluetoothGattServerCallback mCallback = new BluetoothGattServerCallback() {
        //セントラル（クライアント）からReadRequestが来ると呼ばれる
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId,
                                                int offset, BluetoothGattCharacteristic characteristic) {
            //EditTextに入力された値を送信
            byte[] messages = inputText.getText().toString().getBytes();
            if (isEmpty(messages)) {
                setCharacteristicValue(characteristic, offset, messages);
            } else {
                // サーバーメッセージの値がnull又は空の場合
                setCharacteristicValue(characteristic, offset, NO_MESSAGE.getBytes());
            }

            bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset,
                    characteristic.getValue());

            Log.d(TAG, String.valueOf(offset));
        }

        //セントラル（クライアント）からWriteRequestが来ると呼ばれる
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId,
                                                 BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded,
                                                 int offset, byte[] value) {
            //セントラルにnullを返信する
            bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, null);
        }
    };

    //アドバタイズを開始
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void startAdvertise(Context context) {

        //BLE各種を取得
        BluetoothManager manager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter adapter = manager.getAdapter();
        advertiser = getAdvertiser(adapter);
        bluetoothGattServer = getGattServer(context, manager);

        //UUIDを設定
        setUuid();

        //アドバタイズを開始
        advertiser.startAdvertising(makeAdvertiseSetting(), makeAdvertiseData(), this);
    }

    //アドバタイズを停止
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void stopAdvertise() {

        //サーバーを閉じる
        if (bluetoothGattServer != null) {
            bluetoothGattServer.clearServices();
            bluetoothGattServer.close();
            bluetoothGattServer = null;
        }

        //アドバタイズを停止
        if (advertiser != null) {
            advertiser.stopAdvertising(this);
            advertiser = null;
        }
    }

    //Advertiserを取得
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BluetoothLeAdvertiser getAdvertiser(BluetoothAdapter adapter) {
        return adapter.getBluetoothLeAdvertiser();
    }

    //GattServerを取得
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public BluetoothGattServer getGattServer(Context context, BluetoothManager manager) {
        return manager.openGattServer(context, mCallback);
    }

    //UUIDを設定
    private void setUuid() {

        //serviceUUIDを設定
        BluetoothGattService service = new BluetoothGattService(
                UUID.fromString(SERVICE_UUID_YOU_CAN_CHANGE),
                BluetoothGattService.SERVICE_TYPE_PRIMARY);

        //characteristicUUIDを設定
        BluetoothGattCharacteristic characteristic = new BluetoothGattCharacteristic(
                UUID.fromString(CHAR_UUID_YOU_CAN_CHANGE),
                BluetoothGattCharacteristic.PROPERTY_READ |
                        BluetoothGattCharacteristic.PROPERTY_WRITE,
                BluetoothGattCharacteristic.PERMISSION_READ |
                        BluetoothGattCharacteristic.PERMISSION_WRITE);

        //characteristicUUIDをserviceUUIDにのせる
        service.addCharacteristic(characteristic);


        //serviceUUIDをサーバーにのせる
        bluetoothGattServer.addService(service);
    }

    //アドバタイズを設定
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private AdvertiseSettings makeAdvertiseSetting() {

        AdvertiseSettings.Builder builder = new AdvertiseSettings.Builder();

        //アドバタイズモード
        builder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);
        //アドバタイズパワー
        builder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_ULTRA_LOW);
        //ペリフェラルへの接続を許可する
        builder.setConnectable(CONNECTABLE);
        //調査中。。
        builder.setTimeout(TIMEOUT);

        return builder.build();
    }

    //アドバタイズデータを作成
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private AdvertiseData makeAdvertiseData() {

        AdvertiseData.Builder builder = new AdvertiseData.Builder();
        builder.addServiceUuid(new ParcelUuid(UUID.fromString(SERVICE_UUID_YOU_CAN_CHANGE)));

        return builder.build();
    }

    private byte[] encodeBytes(Resources r,int resourceId) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Bitmap bitmap = BitmapFactory.decodeResource(r, resourceId);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

        return outputStream.toByteArray();
    }

    private byte[] extractAry(byte[] original, int offset) {
        return Arrays.copyOfRange(original, offset, original.length);
    }

    //Characteristicに値をセットする
    private void setCharacteristicValue(BluetoothGattCharacteristic characteristic,
                                        int offset, byte[] values) {
        //セントラルに任意の文字を返信する
        characteristic.setValue(extractAry(values, offset));

        //画像のbyte配列を送信
/*
        byte[] bytes = encodeBytes(context.getResources(), R.drawable.test2);
        characteristic.setValue(extractAry(bytes, offset));
*/
    }

    private boolean isEmpty(byte[] objects) {
        return !(objects == null || objects.length == 0);
    }
}