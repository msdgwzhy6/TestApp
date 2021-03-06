package com.dev.kit.testapp.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.LocaleList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dev.kit.basemodule.activity.BaseStateViewActivity;
import com.dev.kit.basemodule.activity.VideoRecordActivity;
import com.dev.kit.basemodule.netRequest.model.BaseController;
import com.dev.kit.basemodule.netRequest.subscribers.NetRequestCallback;
import com.dev.kit.basemodule.netRequest.subscribers.NetRequestSubscriber;
import com.dev.kit.basemodule.netRequest.util.BaseServiceUtil;
import com.dev.kit.basemodule.netRequest.util.CommonInterceptor;
import com.dev.kit.basemodule.util.FileUtil;
import com.dev.kit.basemodule.util.LogUtil;
import com.dev.kit.basemodule.util.PermissionRequestUtil;
import com.dev.kit.basemodule.util.ToastUtil;
import com.dev.kit.testapp.R;
import com.dev.kit.testapp.animation.PropertyAnimationEntryActivity;
import com.dev.kit.testapp.indicator.CustomIndicatorActivity;
import com.dev.kit.testapp.mediaSelectorTest.MediaSelectorTestActivity;
import com.dev.kit.testapp.multiGroupHistogram.MultiGroupHistogramActivity;
import com.dev.kit.testapp.pagerTest.PagerTestActivity;
import com.dev.kit.testapp.recordingAnimation.RecordingAnimationActivity;
import com.dev.kit.testapp.rxJavaAndRetrofitTest.ApiService;
import com.dev.kit.testapp.rxJavaAndRetrofitTest.NetRequestDemoActivity;
import com.dev.kit.testapp.videoRecord.RecordVideoActivity;

import java.io.File;
import java.util.Locale;
import java.util.Random;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MainActivity extends BaseStateViewActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            LogUtil.e("getSavedState: " + savedInstanceState.getString("saveState"));
        }
        init();
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else locale = Locale.getDefault();

        String language = locale.getLanguage() + "-" + locale.getCountry();
        LogUtil.e("language: " + language);
    }

    @Override
    public View createContentView(LayoutInflater inflater, ViewGroup contentRoot) {
        return inflater.inflate(R.layout.activity_main, contentRoot, false);
    }

    private void init() {
        setText(R.id.tv_title, R.string.app_name);
        setOnClickListener(R.id.iv_left, this);
        setOnClickListener(R.id.tv_net_test, this);
        setOnClickListener(R.id.tv_upload_file, this);
        setOnClickListener(R.id.tv_vp_test, this);
        setOnClickListener(R.id.tv_property_animation, this);
        setOnClickListener(R.id.tv_MultiGroupHistogramView, this);
        setOnClickListener(R.id.tv_set_font, this);
        setOnClickListener(R.id.tv_indicator, this);
        setOnClickListener(R.id.tv_audio_animation, this);
        setOnClickListener(R.id.tv_media_selector, this);
        setOnClickListener(R.id.tv_video_record1, this);
        setOnClickListener(R.id.tv_video_record2, this);
        setContentState(STATE_DATA_CONTENT);
    }

    private void uploadFile() {
        CommonInterceptor.updateOrInsertCommonParam("key1", "value1");
        String dirFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "123";
        File file = null;
        if (FileUtil.isDir(dirFilePath)) {
            File dirFile = new File(dirFilePath);
            File[] fileList = dirFile.listFiles();
            if (fileList != null && fileList.length > 0) {
                Random random = new Random();
                int fileIndex = random.nextInt(fileList.length);
                file = fileList[fileIndex];
            }
        } else {
            return;
        }
        if (file == null) {
            return;
        }
        LogUtil.e("fileName: " + file.getName());
        RequestBody userParamBody = RequestBody.create(null, "zhangsan");
        String fileType = FileUtil.getMimeType(file.getAbsolutePath());
        MediaType mediaType = MediaType.parse(fileType);
        RequestBody fileParamBody = RequestBody.create(mediaType, file);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("userAvatar", file.getName(), fileParamBody);
        NetRequestSubscriber<String> subscriber = new NetRequestSubscriber<>(new NetRequestCallback<String>() {
        }, this);
        Observable<String> observable = BaseServiceUtil.createService(ApiService.class).uploadFile(userParamBody, filePart);
        BaseController.sendRequest(this, subscriber, observable);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left: {
                finish();
                break;
            }
            case R.id.tv_net_test: {
                startActivity(new Intent(MainActivity.this, NetRequestDemoActivity.class));
                break;
            }
            case R.id.tv_upload_file: {
                if (PermissionRequestUtil.isPermissionGranted(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    uploadFile();
                } else {
                    PermissionRequestUtil.requestPermission(MainActivity.this, new PermissionRequestUtil.OnPermissionRequestListener() {
                        @Override
                        public void onPermissionsGranted() {
                            uploadFile();
                        }

                        @Override
                        public void onPermissionsDenied(String... deniedPermissions) {
                            showToast("您拒绝了存储读取权限，应用无法访问您的文件");
                        }
                    }, Manifest.permission.READ_EXTERNAL_STORAGE);
                }
                break;
            }
            case R.id.tv_vp_test: {
                startActivity(new Intent(this, PagerTestActivity.class));
                break;
            }
            case R.id.tv_property_animation: {
                startActivity(new Intent(this, PropertyAnimationEntryActivity.class));
                break;
            }
            case R.id.tv_MultiGroupHistogramView: {
                startActivity(new Intent(this, MultiGroupHistogramActivity.class));
                break;
            }
            case R.id.tv_set_font: {
                startActivity(new Intent(this, SettingActivity.class));
                break;
            }
            case R.id.tv_indicator: {
                startActivity(new Intent(this, CustomIndicatorActivity.class));
                break;
            }
            case R.id.tv_audio_animation: {
                startActivity(new Intent(this, RecordingAnimationActivity.class));
                break;
            }
            case R.id.tv_media_selector: {
                startActivity(new Intent(this, MediaSelectorTestActivity.class));
                break;
            }
            case R.id.tv_video_record1: {
                startVideoRecord(1);
                break;
            }
            case R.id.tv_video_record2: {
                startVideoRecord(2);
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("saveState", "saveState");
        super.onSaveInstanceState(outState);
    }

    private void startVideoRecord(final int flag) {
        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (PermissionRequestUtil.isPermissionGranted(this, permissions)) {
            if (flag == 1) {
                startActivityForResult(new Intent(this, RecordVideoActivity.class), 101);
            } else {
                startActivityForResult(new Intent(this, VideoRecordActivity.class), 101);
            }
            return;
        }
        PermissionRequestUtil.requestPermission(this, new PermissionRequestUtil.OnPermissionRequestListener() {
            @Override
            public void onPermissionsGranted() {
                startVideoRecord(flag);
            }

            @Override
            public void onPermissionsDenied(String... deniedPermissions) {
                StringBuilder sb = new StringBuilder();
                for (String permission : deniedPermissions) {
                    sb.append(permission).append("\n");
                }
                sb.deleteCharAt(sb.length() - 1);
                showToast("您拒绝了以下权限:\n" + sb.toString());
                LogUtil.e("deniedPermissions: " + sb.toString());
            }
        }, permissions);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
            String videoPath = data.getStringExtra(VideoRecordActivity.RECORDED_VIDEO_PATH);
            ToastUtil.showToast(this, "视频录制完成: " + videoPath.substring(videoPath.lastIndexOf(File.separator)));
        }
    }
}
