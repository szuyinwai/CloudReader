package com.example.jingbin.cloudreader.ui.study;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.jingbin.cloudreader.R;
import com.example.jingbin.cloudreader.databinding.ActivityWxLoginBinding;
import com.example.jingbin.cloudreader.utils.ToastUtil;
import com.example.jingbin.cloudreader.viewmodel.study.WXLoginViewModel;

import me.jingbin.bymvvm.base.BaseActivity;
import me.jingbin.bymvvm.utils.StatusBarUtil;

/***
 *  Created by yimwai on 2023/4/7.
 */
public class WXLoginActivity extends BaseActivity<WXLoginViewModel, ActivityWxLoginBinding> {

    /***
     * start logic
     * @param mContext mContext
     */
    public static void start(@NonNull Context mContext) {
        Intent intent = new Intent(mContext, WXLoginActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wx_login);
        StatusBarUtil.setStatusBarLightMode(this, ContextCompat.getColor(this, R.color.color_page_bg));
        initializeUi();
        setNoTitlePage();
        showContentView();
        initEvent();
    }

    private void initializeUi() {
        bindingView.setTextWxNickName("起来睡觉了");
        bindingView.setTextPwdTitle("密码");
        bindingView.setHintTextEditPwd("请填写微信密码");
        bindingView.setTextBtnSMSCaptchaLogin("用短信验证码登录");
        bindingView.setTextBtnLogin("登录");
        bindingView.setTextBtnBackPwd("找回密码");
        bindingView.setTextBtnEmergencyFreeze("紧急冻结");
        bindingView.setTextBtnMore("更多");
    }

    private void initEvent() {
        bindingView.tvSMSCaptcha.setOnClickListener(this::jumpSMSCaptcha);
        bindingView.btnLogin.setOnClickListener(this::performLogin);
        bindingView.tvBackPwd.setOnClickListener(this::jumpBackPwd);
        bindingView.tvEmergencyFreeze.setOnClickListener(this::performFreeze);
        bindingView.tvMore.setOnClickListener(this::jumpMore);
        bindingView.tvPwdLogin.setOnClickListener(this::jumpPwdCaptcha);
        bindingView.atvFetchCaptcha.setOnClickListener(this::obtainSMSCaptcha);
    }

    private void obtainSMSCaptcha(View view) {
        ToastUtil.showToast("获取验证码接口！！！");
    }

    private void jumpPwdCaptcha(View view) {
        bindingView.setIsPwdLogin(false);
        bindingView.setTextWxNickName("起来睡觉了");
        bindingView.setTextPwdTitle("密码");
        bindingView.setHintTextEditPwd("请填写微信密码");
        bindingView.setTextBtnSMSCaptchaLogin("用短信验证码登录");
    }

    private void jumpSMSCaptcha(View view) {
        bindingView.setTextWxNickName("138 0826 1934");
        bindingView.setTextPwdTitle("验证码");
        bindingView.setHintTextEditPwd("请填写验证码");
        bindingView.setIsPwdLogin(true);
        bindingView.setTextFetchCaptcha("获取验证码");
        bindingView.setTextBtnPwdLogin("用密码登录");
    }

    private void jumpBackPwd(View view) {
        ToastUtil.showToast("找回密码页面");
    }

    private void performFreeze(View view) {
        ToastUtil.showToast("紧急冻结页面");
    }

    private void jumpMore(View view) {
        ToastUtil.showToast("更多页面");
    }

    private void performLogin(View view) {
        ToastUtil.showToast("密码不能为空，请输入密码");
    }

}
