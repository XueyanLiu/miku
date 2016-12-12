package com.github.hiteshsondhi88.libffmpeg;

import android.os.AsyncTask;

import com.biubiu.miku.MikuApplication;
import com.biubiu.miku.util.FileUtils;
import com.biubiu.miku.util.ThreadPool;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

import java.io.File;
import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * FFmpeg command tools.
 */
public class CustomFFmpeg {
  private static final long RETRY_INTERVAL = 5000L;
  private long timeout = Long.MAX_VALUE;
  private final Set<String> runningTasks = new HashSet<>();
  private String ffmpegFilePath = new File(
      MikuApplication.context.getFilesDir().getAbsolutePath(), "ffmpeg")
          .getAbsolutePath();

  private static CustomFFmpeg instance = null;

  public CustomFFmpeg() {}

  public static CustomFFmpeg getInstance() {
    if (instance == null) {
      instance = new CustomFFmpeg();
    }
    return instance;
  }

  public void init() {
    if (new File(ffmpegFilePath).isFile()) {
      boolean isEcecutable = new File(ffmpegFilePath).setExecutable(true);
    } else {
      ThreadPool.getInstance().execute(() -> {
        if (FileUtils.copyAssetFile("ffmpeg", ffmpegFilePath)) {
          boolean isEcecutable = new File(ffmpegFilePath).setExecutable(true);
        }
      });
    }
  }

  public boolean isAvailable() {
    File ffmpegFile = new File(ffmpegFilePath);
    return ffmpegFile.canExecute();
  }

  public boolean isRunning(final String originVideoFilePath) {
    return runningTasks.contains(originVideoFilePath);
  }

  public boolean execute(final String originVideoFilePath,
      Map<String, String> environmentVars,
      String[] cmd,
      final FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) {
    android.util.Log.e("cpu", "cpu:" + CpuArchHelper.getCpuArch().toString());
    if (!isAvailable()) {
      ffmpegExecuteResponseHandler.onFailure("FFmpeg is not available.");
      return false;
    }
    if (isRunning(originVideoFilePath)) {
      ffmpegExecuteResponseHandler.onFailure(originVideoFilePath + " is already processing.");
      return false;
    }
    if (cmd.length != 0) {
      runningTasks.add(originVideoFilePath);
      StringBuilder ffmpegCommandBuilder = new StringBuilder();
      if (environmentVars != null) {
        for (Map.Entry<String, String> var : environmentVars.entrySet()) {
          ffmpegCommandBuilder.append(var.getKey() + "=" + var.getValue() + " ");
        }
      }
      ffmpegCommandBuilder.append(ffmpegFilePath);
      String[] ffmpegBinary = new String[] {ffmpegCommandBuilder.toString()};
      String[] command = concatenate(ffmpegBinary, cmd);
      FFmpegExecuteAsyncTask ffmpegExecuteAsyncTask =
          new FFmpegExecuteAsyncTask(command, timeout, new FFmpegExecuteResponseHandler() {
            @Override
            public void onSuccess(String message) {
              if (ffmpegExecuteResponseHandler != null) {
                ffmpegExecuteResponseHandler.onSuccess(message);
              }
            }

            @Override
            public void onProgress(String message) {
              if (ffmpegExecuteResponseHandler != null) {
                ffmpegExecuteResponseHandler.onProgress(message);
              }
            }

            @Override
            public void onFailure(String message) {
              if (ffmpegExecuteResponseHandler != null) {
                ffmpegExecuteResponseHandler.onFailure(message);
              }
            }

            @Override
            public void onStart() {
              if (ffmpegExecuteResponseHandler != null) {
                ffmpegExecuteResponseHandler.onStart();
              }
            }

            @Override
            public void onFinish() {
              runningTasks.remove(originVideoFilePath);
              if (ffmpegExecuteResponseHandler != null) {
                ffmpegExecuteResponseHandler.onFinish();
              }
            }
          });
      ffmpegExecuteAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    } else {
      throw new IllegalArgumentException("shell command cannot be empty");
    }
    return true;
  }

  private static <T> T[] concatenate(T[] a, T[] b) {
    int aLen = a.length;
    int bLen = b.length;

    @SuppressWarnings("unchecked")
    T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
    System.arraycopy(a, 0, c, 0, aLen);
    System.arraycopy(b, 0, c, aLen, bLen);

    return c;
  }

  public void execute(final String originVideoFilePath,
      String[] cmd,
      FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler)
          throws FFmpegCommandAlreadyRunningException {
    execute(originVideoFilePath, null, cmd, ffmpegExecuteResponseHandler);
  }

}
