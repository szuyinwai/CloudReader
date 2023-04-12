package com.example.jingbin.cloudreader.ui.study;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.jingbin.cloudreader.R;
import com.example.jingbin.cloudreader.databinding.ActivityNavAboutBinding;
import com.example.jingbin.cloudreader.databinding.ActivityStudyMainBinding;
import com.example.jingbin.cloudreader.utils.ToastUtil;
import com.example.jingbin.cloudreader.viewmodel.study.StudyMainViewModel;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.PaymentData;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import me.jingbin.bymvvm.base.BaseActivity;
import me.jingbin.bymvvm.base.NoViewModel;

/***
 *  Created by yimwai on 2023/4/4.
 */
public class StudyMainActivity extends BaseActivity<StudyMainViewModel, ActivityStudyMainBinding> {

    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;

    /***
     * start logic
     * @param mContext mContext
     */
    public static void start(Context mContext) {
        Intent intent = new Intent(mContext, StudyMainActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_main);
        showLoading();
        setTitle("StudyMain");
        initializeUi();
        showContentView();
    }

    private void initializeUi() {
        bindingView.googlePayButton.setVisibility(View.GONE);
        bindingView.textGooglePayButton.setVisibility(View.GONE);
        viewModel.canUseGooglePay.observe(this, this::setGooglePayAvailable);
        bindingView.setIsAvailable(false);
        bindingView.setTextGooglePayButton(getString(R.string.googlepay_status_unavailable));
        bindingView.setTextDetailTitle("Sample Item Title");
        bindingView.setTextDetailPrice("$ Sample price");
        bindingView.setTextDetailDesTitle("Description");
        bindingView.setTextDetailDescription("Sample description text...");
        bindingView.setTextEnterButton("Justing Jiang");
        bindingView.googlePayButton.setOnClickListener(this::requestPayment);
        bindingView.enterButton.setOnClickListener(this::performClickListener);
    }

    private void setGooglePayAvailable(boolean available) {
        bindingView.setIsAvailable(available);
    }

    public void requestPayment(View view) {

        // Disables the button to prevent multiple clicks.
        bindingView.googlePayButton.setClickable(false);

        // The price provided to the API should include taxes and shipping.
        // This price is not displayed to the user.
        long dummyPriceCents = 100;
        long shippingCostCents = 900;
        long totalPriceCents = dummyPriceCents + shippingCostCents;
        final Task<PaymentData> task = viewModel.getLoadPaymentDataTask(totalPriceCents);

        // Shows the payment sheet and forwards the result to the onActivityResult method.
        AutoResolveHelper.resolveTask(task, this, LOAD_PAYMENT_DATA_REQUEST_CODE);
    }

    /**
     * Handle a resolved activity from the Google Pay payment sheet.
     *
     * @param requestCode Request code originally supplied to AutoResolveHelper in requestPayment().
     * @param resultCode  Result code returned by the Google Pay API.
     * @param data        Intent from the Google Pay API containing payment or error data.
     * @see <a href="https://developer.android.com/training/basics/intents/result">Getting a result
     * from an Activity</a>
     */
    @SuppressWarnings("deprecation")
    // Suppressing deprecation until `registerForActivityResult` can be used with the Google Pay API.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // value passed in AutoResolveHelper
            case LOAD_PAYMENT_DATA_REQUEST_CODE:
                switch (resultCode) {

                    case AppCompatActivity.RESULT_OK:
                        PaymentData paymentData = PaymentData.getFromIntent(data);
                        if (null != paymentData) {
                            handlePaymentSuccess(paymentData);
                        }
                        break;

                    case AppCompatActivity.RESULT_CANCELED:
                        // The user cancelled the payment attempt
                        break;

                    case AutoResolveHelper.RESULT_ERROR:
                        Status status = AutoResolveHelper.getStatusFromIntent(data);
                        handleError(status);
                        break;
                }

                // Re-enables the Google Pay payment button.
                bindingView.googlePayButton.setClickable(true);
        }
    }

    /**
     * PaymentData response object contains the payment information, as well as any additional
     * requested information, such as billing and shipping address.
     *
     * @param paymentData A response object returned by Google after a payer approves payment.
     * @see <a href="https://developers.google.com/pay/api/android/reference/
     * object#PaymentData">PaymentData</a>
     */
    private void handlePaymentSuccess(@NonNull PaymentData paymentData) {
        final String paymentInfo = paymentData.toJson();

        try {
            JSONObject paymentMethodData = new JSONObject(paymentInfo).getJSONObject("paymentMethodData");
            // If the gateway is set to "example", no payment information is returned - instead, the
            // token will only consist of "examplePaymentMethodToken".

            final JSONObject tokenizationData = paymentMethodData.getJSONObject("tokenizationData");
            final String token = tokenizationData.getString("token");
            final JSONObject info = paymentMethodData.getJSONObject("info");
            final String billingName = info.getJSONObject("billingAddress").getString("name");
            ToastUtil.showToast(getString(R.string.payments_show_name, billingName));
            // Logging token string.
            Log.d("Google Pay token: ", token);
        } catch (JSONException e) {
            throw new RuntimeException("The selected garment cannot be parsed from the list of elements");
        }
    }

    /**
     * At this stage, the user has already seen a popup informing them an error occurred. Normally,
     * only logging is required.
     *
     * @param status will hold the value of any constant from CommonStatusCode or one of the
     *               WalletConstants.ERROR_CODE_* constants.
     * @see <a href="https://developers.google.com/android/reference/com/google/android/gms/wallet/
     * WalletConstants#constant-summary">Wallet Constants Library</a>
     */
    private void handleError(@Nullable Status status) {
        String errorString = "Unknown error.";
        if (status != null) {
            int statusCode = status.getStatusCode();
            errorString = String.format(Locale.getDefault(), "Error code: %d", statusCode);
        }
        Log.e("loadPaymentData failed", errorString);
    }

    private void performClickListener (@NonNull View view) {
        ToastUtil.showToast("java.net.SocketException: Connection reset");
        WXLoginActivity.start(this);
    }

}
