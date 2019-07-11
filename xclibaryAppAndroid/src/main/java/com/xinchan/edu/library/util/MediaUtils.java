package com.xinchan.edu.library.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by wanbo on 2017/1/18.
 */

public class MediaUtils implements SurfaceHolder.Callback {
    private static final String TAG = "MediaUtils";
    public static final int MEDIA_AUDIO = 0;
    public static final int MEDIA_VIDEO = 1;
    private Activity activity;
    private MediaRecorder mMediaRecorder;
    private CamcorderProfile profile;
    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private File targetDir;
    private String targetName;
    private File targetFile;
    private int previewWidth, previewHeight;
    private int recorderType;
    private boolean isRecording;
    private GestureDetector mDetector;
    private boolean isZoomIn = false;
    private int or = 90;
    private int cameraPosition = Camera.CameraInfo.CAMERA_FACING_BACK;//默认为后置摄像

    private boolean flashLight = false;

    private MediaPlayer mMediaPlayer;
    private boolean isPlaying = false;

    public MediaUtils(Activity activity) {
        this.activity = activity;
    }

    public void setRecorderType(int type) {
        this.recorderType = type;
    }

    public void setTargetDir(File file) {

        this.targetDir = file;
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public void setTargetName(String name) {
        this.targetName = name;
    }

    public String getTargetFilePath() {
        return targetFile.getPath();
    }

    public boolean deleteTargetFile() {
        if (targetFile.exists()) {
            return targetFile.delete();
        } else {
            return false;
        }
    }

    public void setSurfaceView(SurfaceView view) {
        this.mSurfaceView = view;
        mSurfaceHolder = mSurfaceView.getHolder();
        Log.e("mSurfaceHolder", previewWidth + " * " + previewHeight);

//        mSurfaceHolder.setFixedSize(previewWidth, previewHeight);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.addCallback(this);
        mDetector = new GestureDetector(activity, new ZoomGestureListener());
        mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mDetector.onTouchEvent(event);
                return true;
            }
        });
    }


    public int getPreviewWidth() {
        return previewWidth;
    }

    public int getPreviewHeight() {
        return previewHeight;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void record() {
        if (isRecording) {
            try {
                mMediaRecorder.stop();  // stop the recording
            } catch (RuntimeException e) {
                // RuntimeException is thrown when stop() is called immediately after start().
                // In this case the output file is not properly constructed ans should be deleted.
                Log.d(TAG, "RuntimeException: stop() is called immediately after start()");
                //noinspection ResultOfMethodCallIgnored
                targetFile.delete();
            }
            releaseMediaRecorder(); // release the MediaRecorder object
            mCamera.lock();         // take camera access back from MediaRecorder
            isRecording = false;
        } else {
            startRecordThread();
        }
    }

    private boolean prepareRecord() {
        try {

            mMediaRecorder = new MediaRecorder();
            if (recorderType == MEDIA_VIDEO) {
                mCamera.unlock();
                mMediaRecorder.setCamera(mCamera);
                mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
                mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                mMediaRecorder.setProfile(profile);
                // 实际视屏录制后的方向
                if (cameraPosition == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    mMediaRecorder.setOrientationHint(270);
                } else {
                    mMediaRecorder.setOrientationHint(or);
                }

            } else if (recorderType == MEDIA_AUDIO) {
                mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            }
            targetFile = new File(targetDir, targetName);
            mMediaRecorder.setOutputFile(targetFile.getPath());

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("MediaRecorder", "Exception prepareRecord: ");
            releaseMediaRecorder();
            return false;
        }
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d("MediaRecorder", "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d("MediaRecorder", "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    public void stopRecordSave() {
        Log.d("Recorder", "stopRecordSave");
        if (isRecording) {
            isRecording = false;
            try {
                mMediaRecorder.stop();
                Log.d("Recorder", targetFile.getPath());
            } catch (RuntimeException r) {
                Log.d("Recorder", "RuntimeException: stop() is called immediately after start()");
            } finally {
                releaseMediaRecorder();
            }
        }
    }

    public void stopRecordUnSave() {
        Log.d("Recorder", "stopRecordUnSave");
        if (isRecording) {
            isRecording = false;
            try {
                mMediaRecorder.stop();
            } catch (RuntimeException r) {
                Log.d("Recorder", "RuntimeException: stop() is called immediately after start()");
                if (targetFile.exists()) {
                    //不保存直接删掉
                    targetFile.delete();
                }
            } finally {
                releaseMediaRecorder();
            }
            if (targetFile.exists()) {
                //不保存直接删掉
                targetFile.delete();
            }
        }
    }

    private void startPreView(SurfaceHolder holder) {
        if (mCamera == null) {
            mCamera = Camera.open(cameraPosition);
        }
        if (mCamera != null) {
            mCamera.setDisplayOrientation(or);
            try {
                mCamera.setPreviewDisplay(holder);
                Camera.Parameters parameters = mCamera.getParameters();
                List<Camera.Size> mSupportedPreviewSizes = parameters.getSupportedPreviewSizes();
                List<Camera.Size> mSupportedVideoSizes = parameters.getSupportedVideoSizes();
//                for (Camera.Size size :
//                        mSupportedVideoSizes) {
//                    Log.e("mSupportedVideoSizes",size.width + "-" + size.height);
//                }for (Camera.Size size :
//                        mSupportedPreviewSizes) {
//                    Log.e("mSupportedPreviewSizes",size.width + "-" + size.height);
//                }
                Camera.Size optimalSize = CameraHelper.getCloselyPreSize(true,  mSurfaceView.getWidth(),mSurfaceView.getHeight(), mSupportedPreviewSizes);
                // Use the same size for recording profile.
                previewWidth = optimalSize.width;
                previewHeight = optimalSize.height;
                Log.e("optimalSize", optimalSize.width + "-" + optimalSize.height);
                parameters.setPreviewSize(previewWidth, previewHeight);
                profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
                // 这里是重点，分辨率和比特率
                // 分辨率越大视频大小越大，比特率越大视频越清晰
                // 清晰度由比特率决定，视频尺寸和像素量由分辨率决定
                // 比特率越高越清晰（前提是分辨率保持不变），分辨率越大视频尺寸越大。
                profile.videoFrameWidth = optimalSize.width;
                profile.videoFrameHeight = optimalSize.height;
                // 这样设置 1080p的视频 大小在5M , 可根据自己需求调节
                profile.videoBitRate = 2 * optimalSize.width * optimalSize.height;
                List<String> focusModes = parameters.getSupportedFocusModes();
                if (focusModes != null) {
                    for (String mode : focusModes) {
                        mode.contains("continuous-video");
                        parameters.setFocusMode("continuous-video");
                    }
                }
                if (flashLight) {
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                } else {
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                }
                if (Build.VERSION.SDK_INT >= 17)
                    mCamera.enableShutterSound(false);
                try {
                    mCamera.setParameters(parameters);
                } catch (Exception e) {
                    //非常罕见的情况
                    //个别机型在SupportPreviewSizes里汇报了支持某种预览尺寸，但实际是不支持的，设置进去就会抛出RuntimeException.
                    e.printStackTrace();
                    try {
                        //遇到上面所说的情况，只能设置一个最小的预览尺寸
                        parameters.setPreviewSize(1920, 1080);
                        Log.e("setPreviewSize", "1  1920 * 1080");
                        mCamera.setParameters(parameters);
                    } catch (Exception e1) {
                        //到这里还有问题，就是拍照尺寸的锅了，同样只能设置一个最小的拍照尺寸
                        e1.printStackTrace();
                        try {
                            parameters.setPictureSize(1920, 1080);
                            Log.e("setPreviewSize", "2  1920 * 1080");
                            mCamera.setParameters(parameters);
                        } catch (Exception ignored) {
                        }
                    }
                }
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            // clear recorder configuration
            mMediaRecorder.reset();
            // release the recorder object
            mMediaRecorder.release();
            mMediaRecorder = null;
            // Lock camera for later use i.e taking it back from MediaRecorder.
            // MediaRecorder doesn't need it anymore and we will release it if the activity pauses.
            Log.d("Recorder", "release Recorder");
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            // release the camera for other applications
            mCamera.release();
            mCamera = null;
            Log.d("Recorder", "release Camera");
        }
    }

    private void releaseMediaPlayer() {
        Log.e(TAG, "releaseMediaPlayer: releaseMediaPlayer");
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying())
                mMediaPlayer.stop();
//            mSurfaceHolder.addCallback(null);
            mMediaPlayer.reset();
            Log.e(TAG, "releaseMediaPlayer: reset called");
            mMediaPlayer.release();
            Log.e(TAG, "releaseMediaPlayer: release called");
            mMediaPlayer = null;
        }
    }


    public void releaseAll() {
        releaseMediaRecorder();
        releaseCamera();
        releaseSurface();
        releaseMediaPlayer();
    }

    private void releaseSurface() {
        if(mSurfaceHolder != null){
            mSurfaceHolder.addCallback(null);
            mSurfaceHolder = null;
        }
        if(mSurfaceView != null)
            mSurfaceView.setVisibility(View.GONE);
        mSurfaceView = null;
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSurfaceHolder = holder;
        if (!isPlaying) {
            startPreView(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            Log.d(TAG, "surfaceDestroyed: ");
            releaseCamera();
        }
        if (mMediaRecorder != null) {
            releaseMediaRecorder();
        }
    }

    private void startRecordThread() {
        if (prepareRecord()) {
            try {
                mMediaRecorder.start();
                isRecording = true;
                Log.d("Recorder", "Start Record");
            } catch (RuntimeException r) {
                releaseMediaRecorder();
                Log.d("Recorder", "RuntimeException: start() is called immediately after stop()");
            }
        }
    }

    public void startPlay(String targetFilePath) {
        releaseMediaRecorder();
        releaseCamera();
        isPlaying = true;
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        }
        try {
            Log.e(TAG, "startPlay: targetFilePath = " + targetFilePath);
            mMediaPlayer.setDataSource(targetFilePath);
            mMediaPlayer.prepare();
            mSurfaceView.setVisibility(View.GONE);

            mSurfaceView.setVisibility(View.VISIBLE);
            SurfaceHolder holder = mSurfaceView.getHolder();
            holder.removeCallback(this);

            holder.addCallback(playHolder);
            mMediaPlayer.setDisplay(holder);
            mMediaPlayer.start();
            mMediaPlayer.setLooping(true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopPlaying(boolean isFinish) {
        if (isPlaying) {
            isPlaying = false;
            mSurfaceView.setVisibility(View.GONE);
            releaseMediaPlayer();
            if (!isFinish) {
                mSurfaceView.setVisibility(View.VISIBLE);
                mSurfaceView.getHolder().removeCallback(playHolder);
                mSurfaceView.getHolder().addCallback(this);
                startPreView(mSurfaceHolder);
            }
        } else {
            releaseMediaPlayer();
        }
    }

    private SurfaceHolder.Callback playHolder = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
//                    mMediaPlayer.setDisplay(holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            releaseMediaPlayer();
            isPlaying = false;

        }
    };


    private class ZoomGestureListener extends GestureDetector.SimpleOnGestureListener {
        //双击手势事件
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            super.onDoubleTap(e);
            Log.d(TAG, "onDoubleTap: 双击事件");
            if (!isZoomIn) {
                setZoom(20);
                isZoomIn = true;
            } else {
                setZoom(0);
                isZoomIn = false;
            }
            return true;
        }
    }

    private void setZoom(int zoomValue) {
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            if (parameters.isZoomSupported()) {
                int maxZoom = parameters.getMaxZoom();
                if (maxZoom == 0) {
                    return;
                }
                if (zoomValue > maxZoom) {
                    zoomValue = maxZoom;
                }
                parameters.setZoom(zoomValue);
                mCamera.setParameters(parameters);
            }
        }
    }

    private String getVideoThumb(String path) {
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(path);
        return bitmap2File(media.getFrameAtTime());
    }

    private String bitmap2File(Bitmap bitmap) {
        File thumbFile = new File(targetDir,
                targetName);
        if (thumbFile.exists()) thumbFile.delete();
        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(thumbFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            return null;
        }
        return thumbFile.getAbsolutePath();
    }

    public void switchFlashLight() {
        flashLight = !flashLight;
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
        mCamera = Camera.open(cameraPosition);
        Log.e(TAG, "switchFlashLight: cameraPosition = " + cameraPosition);
        startPreView(mSurfaceHolder);
    }

    public void switchCamera() {
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数
        if (cameraCount >= 2) {
            mCamera.stopPreview();//停掉原来摄像头的预览
            mCamera.release();//释放资源
            mCamera = null;//取消原来摄像头
            if (cameraPosition == Camera.CameraInfo.CAMERA_FACING_BACK) {
                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);//打开当前选中的摄像头
                cameraPosition = Camera.CameraInfo.CAMERA_FACING_FRONT;
            } else {
                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);//打开当前选中的摄像头
                cameraPosition = Camera.CameraInfo.CAMERA_FACING_BACK;
            }
            startPreView(mSurfaceHolder);
        }
    }

}
