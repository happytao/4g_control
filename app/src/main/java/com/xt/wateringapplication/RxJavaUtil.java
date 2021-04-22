package com.xt.wateringapplication;

import android.app.Application;

import com.chinamobile.iot.onenet.OneNetApi;
import com.chinamobile.iot.onenet.OneNetApiCallback;
import com.chinamobile.iot.onenet.http.Config;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static io.reactivex.rxjava3.core.Observable.create;

/**
 * @author:DIY
 * @date: 2021/4/16
 */
public class RxJavaUtil {
    private static RxJavaUtil rxJavaUtil;
    private String responseStr = "0";
    private boolean isHttpError = false;
    private int errorCode;
    private String errorMessage;

    public void refreshEverySecond(OnRefresh onRefresh) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> emitter) throws Throwable {
                while (true) {
                    updateDIStatus();
                    getAllStatus();
                    Thread.sleep(2000);
                    emitter.onNext(responseStr);
                }
            }
        })
        .map(new Function<String, GetStatusBean>() {
            @Override
            public GetStatusBean apply(String s) throws Throwable {
                GetStatusBean getStatusBean = GsonUtil.fromJson(s,GetStatusBean.class);
                return getStatusBean;
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<GetStatusBean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull GetStatusBean getStatusBean) {
                        if(isHttpError) {
                            onRefresh.onError("网络请求错误，请检查网络");
                            return;
                        }
                        if(getStatusBean != null) {
                            if(getStatusBean.getErrno() == 0) {
                                onRefresh.onRefresh(getStatusBean);
                            }
                            else {
                                onRefresh.onError(getStatusBean.getError());
                            }
                        }

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });



    }

    public static RxJavaUtil getInstance() {
        rxJavaUtil = new RxJavaUtil();
        return rxJavaUtil;
    }





    public static void close() {
        if (rxJavaUtil != null) {
            rxJavaUtil = null;
        }
    }

    private void getAllStatus() {
        OneNetApi.queryMultiDataStreams("707674497", new OneNetApiCallback() {
            @Override
            public void onSuccess(String response) {
                responseStr = response;
            }

            @Override
            public void onFailed(Exception e) {
                isHttpError = true;

            }
        });
    }

    private void updateDIStatus() {
        OneNetApi.sendCmdToDevice("707674497", "di", new OneNetApiCallback() {
            @Override
            public void onSuccess(String response) {
            }

            @Override
            public void onFailed(Exception e) {
                isHttpError = true;
            }
        });
    }
}
