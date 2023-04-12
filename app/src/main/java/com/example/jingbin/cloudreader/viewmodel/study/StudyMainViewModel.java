package com.example.jingbin.cloudreader.viewmodel.study;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.jingbin.cloudreader.utils.helper.PaymentsHelper;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;

import org.json.JSONObject;

import me.jingbin.bymvvm.base.BaseViewModel;

/***
 *  Created by yimwai on 2023/4/4.
 */
public class StudyMainViewModel extends BaseViewModel {

    private final PaymentsClient paymentsClient;

    private final MutableLiveData<Boolean> _canUseGooglePay = new MutableLiveData<>();
    public final LiveData<Boolean> canUseGooglePay = _canUseGooglePay;

    public StudyMainViewModel(@NonNull Application application) {
        super(application);
        paymentsClient = PaymentsHelper.createPaymentsClient(application);
        fetchCanUseGooglePay();
    }

    private void fetchCanUseGooglePay() {
        final JSONObject isReadyToPayJson = PaymentsHelper.getIsReadyToPayRequest();
        if (null == isReadyToPayJson) {
            _canUseGooglePay.setValue(false);
            return;
        }

        // The call to isReadyToPay is asynchronous and returns a Task. We need to provide an
        // OnCompleteListener to be triggered when the result of the call is known.
        IsReadyToPayRequest request = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString());
        Task<Boolean> task = paymentsClient.isReadyToPay(request);
        task.addOnCompleteListener(
                completedTask -> {
                    if (completedTask.isSuccessful()) {
                        _canUseGooglePay.setValue(completedTask.getResult());
                    } else {
                        Log.w("isReadyToPay failed", completedTask.getException());
                        _canUseGooglePay.setValue(false);
                    }
                });
    }

    public Task<PaymentData> getLoadPaymentDataTask(final long priceCents) {
        JSONObject paymentDataRequestJson = PaymentsHelper.getPaymentDataRequest(priceCents);
        if (paymentDataRequestJson == null) {
            return null;
        }

        PaymentDataRequest request =
                PaymentDataRequest.fromJson(paymentDataRequestJson.toString());
        return paymentsClient.loadPaymentData(request);
    }

}