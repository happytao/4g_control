package com.xt.wateringapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.IpSecManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.chinamobile.iot.onenet.OneNetApi;
import com.chinamobile.iot.onenet.OneNetApiCallback;
import com.chinamobile.iot.onenet.http.Config;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends BaseActivity implements View.OnClickListener {
    private boolean isOrderMode = false;
    private Switch mSwitch;
    private Group mOrderGroup;
    private TextView mTiming,mOrderDate,mOrderTime,mCompleteTime,mTimingTip,mOrderTip,mRelayStatus,mTemp,mWaterCheck,mLocation,mStatusMode,mIsOnline,mLog,mBtnManualStart,mBtnManualStop;
    private long timing;
    private List<String> hourList = new ArrayList<>();
    private List<String> minList = new ArrayList<>();
    private List<String> secondList = new ArrayList<>();
    private String timingStr,orderDateStr,orderTimeStr;
    private boolean isWatering = false;
    private Button mBtnWatering,mBtnSaveOrderTime;
    private Date orderDate,orderTime;
    private boolean isOrderTimeSaved = false;
    private CountDownTimer countDownTimer,countDownOrderTimer;
    private List<GetStatusBean.DataDTO> allStatusBeanList = new ArrayList<GetStatusBean.DataDTO>();
    private String longitude,latitude;
    private boolean isOrderStarted = false;
    private String openRelayCmdId;
    private String completeTimeStr;
    private View mBackground2;
    private BackgroundService.CountDownTimerBinder mBinder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (BackgroundService.CountDownTimerBinder) service;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("TAG",name.toString());

        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Intent intent = new Intent(this,BackgroundService.class);
        startService(intent);
        bindService(intent,connection,BIND_AUTO_CREATE);
        super.onCreate(savedInstanceState);

    }



    @Override
    protected int initLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        mSwitch = findViewById(R.id.switch_order_watering);
        mOrderGroup = findViewById(R.id.order_group);
        mTiming = findViewById(R.id.timing);
        mOrderDate = findViewById(R.id.order_date);
        mOrderTime = findViewById(R.id.order_time);
        mCompleteTime = findViewById(R.id.complete_time);
        mBtnWatering = findViewById(R.id.btn_start_watering);
        mBtnSaveOrderTime = findViewById(R.id.btn_save_order_time);
        mTimingTip = findViewById(R.id.timing_tip);
        mOrderTip = findViewById(R.id.order_tip);
        mTemp = findViewById(R.id.temperature);
        mRelayStatus = findViewById(R.id.relay_status);
        mWaterCheck = findViewById(R.id.water_check);
        mLocation = findViewById(R.id.location);
        mStatusMode = findViewById(R.id.status_mode);
        mIsOnline = findViewById(R.id.hardware_online_status);
        mLog = findViewById(R.id.my_log);
        mBtnManualStart = findViewById(R.id.btn_manual_start);
        mBtnManualStop = findViewById(R.id.btn_manual_stop);
        mBackground2 = findViewById(R.id.view_background2);
        MainActivityPermissionsDispatcher.initStatusWithPermissionCheck(this);
        mTiming.setOnClickListener(this);
        mBtnWatering.setOnClickListener(this);
        mOrderDate.setOnClickListener(this);
        mOrderTime.setOnClickListener(this);
        mBtnSaveOrderTime.setOnClickListener(this);
        mBtnManualStart.setOnClickListener(this);
        mBtnManualStop.setOnClickListener(this);


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void initData() {
        //???????????????????????????????????????List
        hourList = IntStream.range(0, 11).mapToObj(String::valueOf).collect(Collectors.toList());
        minList = IntStream.range(0, 60).mapToObj(String::valueOf).collect(Collectors.toList());
        secondList = IntStream.range(0, 60).mapToObj(String::valueOf).collect(Collectors.toList());
        refreshAllStatus();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //????????????????????????????????????1????????????ServiceConnection????????????
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //ServiceConnection????????????????????????binder????????????????????????
                            initWateringMode();
                            switchWateringMode();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * ???SharedPreferences?????????????????????????????????
     */
    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE})
    protected void initStatus() {
        isOrderMode = (boolean) SPUtils.get(context,"isOrderMode",false);
        isOrderStarted = (boolean) SPUtils.get(context,"isOrderStarted",false);
        isOrderTimeSaved = (boolean) SPUtils.get(context,"isOrderTimeSaved",false);
        isWatering = (boolean) SPUtils.get(context,"isWatering",false);
        if(isWatering) {
            completeTimeStr = (String) SPUtils.get(context,"wateringCompleteTime","");
            timing = stringToDate(completeTimeStr).getTime() - (new Date().getTime());
            if(timing < 3000) {
                isWatering = false;
                timing = 0L;
                isOrderTimeSaved = false;
                orderTimeStr = null;
            }
        }
        else if(isOrderStarted) {
            orderTimeStr = (String) SPUtils.get(context,"orderTime","");
            orderDateStr = orderTimeStr;
            timing = (long) SPUtils.get(context,"orderTiming",0L);
            if(stringToDate(orderTimeStr).getTime() <= (new Date().getTime())) {
                isOrderStarted = false;
                timing = timing - ((new Date().getTime()) - stringToDate(orderTimeStr).getTime());
                if(timing <= 3000) {
                    isWatering = false;
                    timing = 0L;
                }
                else {
                    isWatering = true;
                }
                isOrderTimeSaved = false;
                orderTimeStr = null;
            }
        }
        else if(isOrderTimeSaved) {
            orderTimeStr = (String) SPUtils.get(context,"orderTime","");
            orderDateStr = orderTimeStr;
            if(stringToDate(orderTimeStr).getTime() <= (new Date().getTime())) {
                isOrderTimeSaved = false;
                orderTimeStr = null;
                orderDateStr = null;
            }
        }
    }

    /**
     * ???????????????????????????????????????????????????????????????????????????
     * ???????????????????????????????????????????????????
     * ?????????????????????????????????????????????????????????????????????
     */
    private void initWateringMode() {
        if(isOrderMode) {
            mSwitch.setChecked(true);
            mOrderGroup.setVisibility(View.VISIBLE);
            mBackground2.setLayoutParams(setMarginTopOfBackground(40f));

        }
        else {
            mSwitch.setChecked(false);
            mOrderGroup.setVisibility(View.GONE);
            mBackground2.setLayoutParams(setMarginTopOfBackground(20f));
        }
        if(isWatering) {
            startWatering();
        }
        else if(isOrderStarted) {
            startOrderTiming();
            holdOrderTime();
        }
        else if(isOrderTimeSaved) {
            holdOrderTime();
        }
        setModeView();
    }

    /**
     * switch????????????/????????????????????????
     */
    private void switchWateringMode() {
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    mOrderGroup.setVisibility(View.VISIBLE);
                    mBackground2.setLayoutParams(setMarginTopOfBackground(40f));
                    isOrderMode = true;
                }
                else {
                    mOrderGroup.setVisibility(View.GONE);
                    mBackground2.setLayoutParams(setMarginTopOfBackground(20f));
                    isOrderMode = false;
                    if (isOrderTimeSaved || isOrderStarted) {
                        if (countDownOrderTimer != null) {
                            countDownOrderTimer.cancel();
                        }
                        timingWateringFinished();
                        changeOrderTime();
                        isOrderStarted = false;
                        isOrderTimeSaved = false;
                    }
                }
            }
        });
    }

    /**
     * ???????????????????????????????????????????????????????????????View???MarginTop?????????????????????
     * @param marginTop ?????????MarginTop
     * @return
     */
    private ViewGroup.LayoutParams setMarginTopOfBackground(Float marginTop) {
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        layoutParams.setMargins(0,DimensionUtils.dip2px(context,marginTop),0,0);
        layoutParams.startToStart = R.id.parent;
        layoutParams.endToEnd = R.id.parent;
        layoutParams.topToBottom = R.id.view_background;
        layoutParams.bottomToBottom = R.id.btn_save_order_time;
        layoutParams.verticalChainStyle = R.id.packed;
        return layoutParams;

    }

    /**
     * ????????????????????????
     */
    private void pickTiming() {
        OptionsPickerView pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                timingStr = hourList.get(options1) + "???" + minList.get(options2) + "???" + secondList.get(options3) + "???";
                timing = Long.valueOf(hourList.get(options1)) * 60 * 60 * 1000 + Long.valueOf(minList.get(options2)) * 60 * 1000 + Long.valueOf(secondList.get(options3)) * 1000;
                mTiming.setText(timingStr);
                mTiming.setBackgroundColor(getResources().getColor(R.color.design_default_color_background));
            }
        }).setTitleText("??????????????????")
                .setLabels("???","???","???")
                .build();
        pvOptions.setNPicker(hourList,minList,secondList);
        pvOptions.show();

    }

    /**
     * PickerVIew??????????????????
     */
    private void pickOrderDate() {
        Calendar endDate =  Calendar.getInstance();
        endDate.set(2030,1,1);
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(new Date());
        TimePickerView pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                orderDate = date;
                orderDateStr = dateToString(orderDate);
                mOrderDate.setText(orderDateStr.substring(0,13));
                mOrderDate.setBackgroundColor(getResources().getColor(R.color.design_default_color_background));

            }
        })
                .setRangDate(startDate,endDate)
                .build();
        pvTime.show();
    }

    /**
     * PickerView??????????????????
     */
    private void pickOrderTime() {
        TimePickerView pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                orderTime = date;
                orderTimeStr = dateToString(orderTime);
                updateOrderDateStr();
                mOrderTime.setText(orderTimeStr.substring(14,orderTimeStr.length()));
                mOrderTime.setBackgroundColor(getResources().getColor(R.color.design_default_color_background));




            }
        }).setType(new boolean[]{false,false,false,true,true,true})
                .build();
        pvTime.show();
    }

    /**
     * ?????????????????????????????????????????????????????????
     */
    private void updateOrderDateStr() {
        StringBuilder sb = new StringBuilder(orderDateStr);
        sb.replace(14,sb.length(),orderTimeStr.substring(14));
        orderTimeStr = sb.toString();
        mOrderDate.setText(orderDateStr.substring(0,13));
        mOrderDate.setBackgroundColor(getResources().getColor(R.color.design_default_color_background));
        mOrderTime.setText(orderTimeStr.substring(14,orderTimeStr.length()));
        mOrderTime.setBackgroundColor(getResources().getColor(R.color.design_default_color_background));
    }

    /**
     * Date?????????String
     * @param date
     * @return
     */
    private String dateToString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy???-MM???-dd??? HH:mm:ss");
        String dateString =  dateFormat.format(date);
        return dateString;

    }

    /**
     * ??????????????????????????????????????????Date?????????????????????String
     * @param date
     * @return
     */
    private String orderDateToString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
        String dateString = dateFormat.format(date);
        return dateString;
    }

    /**
     * String???Date
     * @param dateStr
     * @return
     */
    private Date stringToDate(String dateStr) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy???-MM???-dd??? HH:mm:ss");
        try {
            Date date = dateFormat.parse(dateStr);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * ????????????????????????????????????????????????
     * @return ?????????????????????????????????
     */
    private long betweenOrderToNowTime() {
       Date nowTime = new Date();
       Date orderTime = stringToDate(orderTimeStr);
       return orderTime.getTime() - nowTime.getTime();
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.timing:
                pickTiming();
                break;
            case R.id.order_date:
                pickOrderDate();
                break;
            case R.id.order_time:
                pickOrderTime();
                break;
            case R.id.btn_start_watering:
                if (timing > 3000) {
                    if (!isOrderTimeSaved) {
                        if(isOrderStarted) {
                            timingWateringFinished();
                            return;
                        }
                        if(!isWatering) {
                            startWatering();
                            openRelay();

                        }
                        else {
                            timingWateringFinished();
                        }
                    }
                    else {
                        if (!isOrderStarted) {
                            startOrderTiming();
                            relayOrderMode();
                        } else {
                            timingWateringFinished();

                        }
                    }
                } else {
                    showToast("???????????????????????????");
                }
                break;
            case R.id.btn_save_order_time:
                if(!isOrderTimeSaved) {
                    if(orderTimeStr != null && stringToDate(orderTimeStr).getTime() - (new Date().getTime()) > 5000) {
                        holdOrderTime();
                        isOrderTimeSaved = true;
                    }
                    else {
                        showToast("???????????????????????????");
                    }
                }
                else {
                    changeOrderTime();
                    isOrderTimeSaved = false;
                }
                break;
            case R.id.btn_manual_start :
                openRelayManual();
                break;
            case R.id.btn_manual_stop :
                timingWateringFinished();
                break;
            default:
                break;

        }

    }

    /**
     * ??????????????????????????????
     */
    private void setModeView() {
        if(isOrderStarted) {
            mStatusMode.setText("??????????????????: ??????????????????");
        }
        else if(isWatering) {
            mStatusMode.setText("??????????????????: ??????????????????");
        }
        else {
            mStatusMode.setText("??????????????????: ?????????");
        }
    }



    /**
     * ???????????????????????????????????????
     */
    private void startWatering() {
        isWatering = true;
        isOrderStarted = false;
        mBtnWatering.setText("????????????");
        mBtnWatering.setBackground(getResources().getDrawable(R.drawable.switch_button_stop));
        mTiming.setClickable(false);
        mTimingTip.setText("???????????????????????????????????????");
        mTimingTip.setTextColor(getResources().getColor(R.color.red));
        mTiming.setBackgroundColor(getResources().getColor(R.color.design_default_color_background));
        mBinder.startTiming(mTiming, timing, 1000, false, new OnCountDownTimerService() {
            @Override
            public void onTick(long mill) {
                timing = mill;
            }

            @Override
            public void onFinish() {
                timingWateringFinished();
            }
        });

//        countDownTimer = new CountDownTimerUtils(mTiming, timing, 1000, new OnCountDownTimerFinished() {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                timing = millisUntilFinished;
//            }
//
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            @Override
//            public void onFinished() {
//                timingWateringFinished();
//                NotificationUtil.sendNotificationMessage(context,"???????????????");
//            }
//
//        }).start();
        setModeView();
        wateringCompleteTime();

    }

    /**
     * ???????????????????????????
     */
    private void timingWateringFinished() {
        isWatering = false;
        isOrderStarted = false;
        mBtnWatering.setText("????????????");
        mBtnWatering.setBackground(getResources().getDrawable(R.drawable.switch_button));
        mTiming.setClickable(true);
        mTiming.setBackgroundColor(Color.parseColor("#87CEEB"));
        mTimingTip.setText("????????????????????????????????????");
        mTimingTip.setTextColor(getResources().getColor(R.color.green));
        if(mBinder != null) {
            mBinder.cancelTiming();
        }
        isWatering = false;
        closeRelay();
        cancelRelayOrder();
        setModeView();
        mCompleteTime.setText("?????????????????? ???");

    }

    /**
     * ?????????????????????????????????????????????
     */
    private void startOrderTiming() {
        isOrderStarted = true;
        mBtnWatering.setText("????????????");
        mBtnWatering.setBackground(getResources().getDrawable(R.drawable.switch_button_change_order_time));
        mTiming.setClickable(false);
        mTimingTip.setText("????????????????????????");
        mTimingTip.setTextColor(getResources().getColor(R.color.red));
        mTiming.setBackgroundColor(getResources().getColor(R.color.design_default_color_background));
        mBinder.startTiming(mTiming, betweenOrderToNowTime(), 1000, true, new OnCountDownTimerService() {
            @Override
            public void onTick(long mill) {
            }

            @Override
            public void onFinish() {
                startWatering();
                isWatering = true;
                isOrderTimeSaved = false;
                changeOrderTime();
            }
        });
//        countDownOrderTimer = new CountDownTimerUtils(mTiming, betweenOrderToNowTime(), 1000, new OnCountDownTimerFinished() {
//            @Override
//            public void onTick(long millisUntilFinished) {
//
//            }
//
//            @Override
//            public void onFinished() {
//                startWatering();
//                isWatering = true;
//                isOrderTimeSaved = false;
//                changeOrderTime();
//
//            }
//        }).start();
        setModeView();
        wateringCompleteTime();
    }

    /**
     * ?????????????????????????????????
     */
    private void wateringCompleteTime() {
        Date completeTime = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy???-MM???-dd??? HH:mm:ss");
        if (isOrderTimeSaved) {
            completeTime.setTime(stringToDate(orderTimeStr).getTime() + timing);
        } else {
            completeTime.setTime(new Date().getTime() + timing);
        }
        completeTimeStr = dateFormat.format(completeTime);
        mCompleteTime.setText("????????????????????? " + completeTimeStr);


    }

    /**
     * ???????????????????????????
     */
    private void openRelay() {
        StringBuilder sb = new StringBuilder("rs,0,");
        sb.append((((double) timing) / 1000));
        OneNetApi.sendCmdToDevice("707674497", sb.toString(), new OneNetApiCallback() {
            @Override
            public void onSuccess(String response) {
                CmdResponseBean cmdResponseBean = GsonUtil.fromJson(response,CmdResponseBean.class);
                if (cmdResponseBean.getErrno() == 0) {
                    openRelayCmdId = cmdResponseBean.getData().getCmd_uuid();
                    selectHistory();
                    showToast("?????????????????????");
                }
                else if(cmdResponseBean.getErrno() == 10) {
                    showToast("???????????????");
                }
                else {
                    showToast("??????????????? " + cmdResponseBean.getError());
                }

            }

            @Override
            public void onFailed(Exception e) {
                showToast("??????????????????");
                Log.e(TAG,Log.getStackTraceString(e));

            }
        });

    }

    /**
     * ?????????????????????
     */
    private void closeRelay() {
        OneNetApi.sendCmdToDevice("707674497", "rs,0,0", new OneNetApiCallback() {
            @Override
            public void onSuccess(String response) {
                CmdResponseBean cmdResponseBean = GsonUtil.fromJson(response,CmdResponseBean.class);
                if (cmdResponseBean.getErrno() == 0) {
                    openRelayCmdId = cmdResponseBean.getData().getCmd_uuid();
                    selectHistory();
                    showToast("?????????????????????");
                }
                else if(cmdResponseBean.getErrno() == 10) {
                    showToast("???????????????");
                }
                else {
                    showToast("??????????????? " + cmdResponseBean.getError());
                }
            }

            @Override
            public void onFailed(Exception e) {
                showToast("??????????????????");
                Log.e(TAG,Log.getStackTraceString(e));

            }
        });
    }

    /**
     * ?????????????????????
     */
    private void openRelayManual() {
        OneNetApi.sendCmdToDevice("707674497", "rs,0,-1", new OneNetApiCallback() {
            @Override
            public void onSuccess(String response) {
                CmdResponseBean cmdResponseBean = GsonUtil.fromJson(response,CmdResponseBean.class);
                if (cmdResponseBean.getErrno() == 0) {
                    openRelayCmdId = cmdResponseBean.getData().getCmd_uuid();
                    selectHistory();
                    showToast("?????????????????????");
                }
                else if(cmdResponseBean.getErrno() == 10) {
                    showToast("???????????????");
                }
                else {
                    showToast("??????????????? " + cmdResponseBean.getError());
                }
            }

            @Override
            public void onFailed(Exception e) {
                showToast("??????????????????");
                Log.e(TAG,Log.getStackTraceString(e));
            }
        });
    }

    /**
     * ?????????????????????
     * ?????????????????????rt,0,????????????,???????????????
     */
    private void relayOrderMode() {
        StringBuilder sb = new StringBuilder("rt,0,");
        sb.append(orderDateToString(stringToDate(orderTimeStr)) + ",");
        sb.append(orderDateToString(stringToDate(completeTimeStr)));
        OneNetApi.sendCmdToDevice("707674497", sb.toString(), new OneNetApiCallback() {
            @Override
            public void onSuccess(String response) {
                CmdResponseBean cmdResponseBean = GsonUtil.fromJson(response,CmdResponseBean.class);
                if (cmdResponseBean.getErrno() == 0) {
                    openRelayCmdId = cmdResponseBean.getData().getCmd_uuid();
                    selectHistory();
                    showToast("????????????");
                }
                else if(cmdResponseBean.getErrno() == 10) {
                    showToast("???????????????");
                }
                else {
                    showToast("??????????????? " + cmdResponseBean.getError());
                }
            }

            @Override
            public void onFailed(Exception e) {

            }
        });
    }

    private void cancelRelayOrder() {
        OneNetApi.sendCmdToDevice("707674497", "rt,0,00/01/01 00:00:00,00/01/01 00:00:01", new OneNetApiCallback() {
            @Override
            public void onSuccess(String response) {
                CmdResponseBean cmdResponseBean = GsonUtil.fromJson(response,CmdResponseBean.class);
                if (cmdResponseBean.getErrno() == 0) {
                    openRelayCmdId = cmdResponseBean.getData().getCmd_uuid();
                    selectHistory();
                    showToast("??????????????????");
                }
                else if(cmdResponseBean.getErrno() == 10) {
                    showToast("???????????????");
                }
                else {
                    showToast("??????????????? " + cmdResponseBean.getError());
                }
            }

            @Override
            public void onFailed(Exception e) {

            }
        });
    }

    /**
     * ????????????????????????
     */
    private void holdOrderTime() {
        mBtnSaveOrderTime.setText("??????????????????");
        mBtnSaveOrderTime.setBackground(getResources().getDrawable(R.drawable.switch_button_change_order_time));
        mOrderDate.setClickable(false);
        mOrderTime.setClickable(false);
        mOrderTip.setText("??????????????????????????????");
        mOrderTip.setTextColor(getResources().getColor(R.color.red));
        mOrderDate.setBackgroundColor(getResources().getColor(R.color.design_default_color_background));
        mOrderTime.setBackgroundColor(getResources().getColor(R.color.design_default_color_background));
        updateOrderDateStr();
    }

    /**
     * ????????????????????????
     */
    private void changeOrderTime() {
        mBtnSaveOrderTime.setText("??????????????????");
        mBtnSaveOrderTime.setBackground(getResources().getDrawable(R.drawable.switch_button));
        mOrderDate.setClickable(true);
        mOrderTime.setClickable(true);
        mOrderTime.setBackgroundColor(Color.parseColor("#87CEEB"));
        mOrderDate.setBackgroundColor(Color.parseColor("#87CEEB"));
        mOrderTip.setText("???????????????????????????????????????");
        mOrderTip.setTextColor(getResources().getColor(R.color.green));

    }


    /**
     * ??????RxJava ??????????????????2???????????????????????????
     */
    private void refreshAllStatus() {
        RxJavaUtil.getInstance().refreshEverySecond(new OnRefresh() {
            @Override
            public void onRefresh(GetStatusBean getStatusBean) {
                allStatusBeanList = getStatusBean.getData();
                setAllStatusView();

            }

            @Override
            public void onError(String errorMsg) {
                showToast(errorMsg);

            }
        });
    }

    /**
     * ????????????????????????????????????app???
     */
    private void setAllStatusView() {
        for (GetStatusBean.DataDTO dataDTO : allStatusBeanList) {
            switch (dataDTO.getId()) {
                case "relay" :
                    String s = dataDTO.getCurrent_value().toString().substring(3,4);
                    if("0".equals(s)) {
                        mRelayStatus.setText("?????????????????????: ??????");
                    }
                    else {
                        mRelayStatus.setText("?????????????????????: ??????");
                    }
                    break;
                case "csq":
                    mTemp.setText("??????????????? " + dataDTO.getCurrent_value() + "???");
                    break;
                case "longitude":
                    longitude = dataDTO.getCurrent_value().toString();
                    break;
                case "latitude" :
                    latitude = dataDTO.getCurrent_value().toString();
                    break;
                case "DIs" :
                    if("1".equals(dataDTO.getCurrent_value().toString().substring(3,4))) {
                        mWaterCheck.setText("????????????????????????????????????");
                    }
                    else {
                        mWaterCheck.setText("????????????????????????????????????");
                    }
                    if(calculateTimeDiff(dataDTO.getUpdate_at()) > 5000) {
                        mIsOnline.setText("??????????????????????????????: ?????????");
                    }
                    else{
                        mIsOnline.setText("??????????????????????????????: ??????");
                    }
                    break;
                default:
                    break;
            }
        }
        mLocation.setText("??????????????????" + "?????????" + longitude + "," + "??????: " + latitude);
    }

    /**
     * ????????????????????????????????????????????????????????????
     * @param updateTimeStr  ????????????????????????
     * @return ???????????????????????????
     */
    private long calculateTimeDiff(String updateTimeStr) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date updateTime = dateFormat.parse(updateTimeStr);
            Date nowTime = new Date();
            long diff = nowTime.getTime() - updateTime.getTime();
            return diff;
        } catch (ParseException e) {
            e.printStackTrace();
            return 10000;
        }


    }

    /**
     * ????????????????????????????????????uuid???????????????????????????????????????????????????Log???????????????
     */
    private void selectHistory() {
        OneNetApi.queryCmdStatus(openRelayCmdId, new OneNetApiCallback() {
            @Override
            public void onSuccess(String response) {
                mLog.setText("Log: " + response);
            }

            @Override
            public void onFailed(Exception e) {

            }
        });
    }

    /**
     * ??????App?????????????????????SharedPreferences???
     */
    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE})
    protected void saveData() {
        try {
            SPUtils.put(context,"isOrderMode",isOrderMode);
            SPUtils.put(context,"isOrderStarted",isOrderStarted);
            SPUtils.put(context,"isOrderTimeSaved",isOrderTimeSaved);
            SPUtils.put(context,"isWatering",isWatering);
            if(isWatering) {
                SPUtils.put(context,"wateringCompleteTime",completeTimeStr);
            }
            else if(isOrderStarted) {
                SPUtils.put(context,"orderTime",orderTimeStr);
                SPUtils.put(context,"orderTiming",timing);
            }
            else if(isOrderTimeSaved) {
                SPUtils.put(context,"orderTime",orderTimeStr);
            }
        } catch (Exception e) {
            Log.e("TAG",Log.getStackTraceString(e));
        }
    }






    @Override
    protected void onDestroy() {
        MainActivityPermissionsDispatcher.saveDataWithPermissionCheck(this);
        if(mBinder != null) {
            mBinder.cancelTiming();
            mBinder = null;
        }
        RxJavaUtil.close();
        super.onDestroy();
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults);
    }
}