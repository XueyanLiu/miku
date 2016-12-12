package com.biubiu.miku;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.biubiu.miku.constant.VideoQuality;
import com.biubiu.miku.event.PostProgressCallbackEvent;
import com.biubiu.miku.event.PostStartCallbackEvent;
import com.biubiu.miku.model.Post;
import com.biubiu.miku.model.PostVideo;
import com.biubiu.miku.util.FileUtils;
import com.biubiu.miku.util.FontsUtils;
import com.biubiu.miku.util.ThreadPool;
import com.biubiu.miku.util.video.VideoMetaData;
import com.biubiu.miku.util.video.VideoUtils;
import com.biubiu.miku.util.video.action.sticker.DefaultStickerManager;
import com.biubiu.miku.util.video.generator.VideoProcessCallback;
import com.biubiu.miku.util.video.generator.VideoProcessControler;
import com.biubiu.miku.util.video.task.VideoContentTask;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.internal.Supplier;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.github.hiteshsondhi88.libffmpeg.CustomFFmpeg;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

import static android.R.attr.id;

/**
 * Created by luis on 2016/11/21.
 */

public class MikuApplication extends MultiDexApplication {

    public static Context context;
    private Handler handler = new Handler(Looper.getMainLooper());
    public static final double CROP_PERCENT = .1f;
    public static final double ACTION_PERCENT = .6f;

    @Override
    public void onCreate() {
        super.onCreate();
        MikuApplication.context = this;
        if (isMainProcess()) {
            FontsUtils.init();
            CustomFFmpeg.getInstance().init();
            Fresco.initialize(context, configureCaches());
            initStickers();
            EventBus.getDefault().register(this);
        }
    }

    private boolean isMainProcess() {
        String packageName = getProcessName(context);
        return !TextUtils.isEmpty(packageName) && packageName.equals(context.getPackageName());
    }

    private static String getProcessName(Context context) {
        int myPid = android.os.Process.myPid();
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processes = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo process : processes) {
            if (process.pid == myPid) {
                return process.processName;
            }
        }
        return "";
    }

    private void initStickers() {
        new Handler().postDelayed(() -> DefaultStickerManager.getInstance().setLoadListener(null), 200);
    }

    private static ImagePipelineConfig configureCaches() {
        // 内存配置
        final MemoryCacheParams bitmapCacheParams = new MemoryCacheParams(
                getMemoryCacheSize(40), // 内存缓存中总图片的最大大小,以字节为单位。
                Integer.MAX_VALUE, // 内存缓存中图片的最大数量。
                getMemoryCacheSize(30), // 内存缓存中准备清除但尚未被删除的总图片的最大大小,以字节为单位。
                Integer.MAX_VALUE, // 内存缓存中准备清除的总图片的最大数量。
                Integer.MAX_VALUE); // 内存缓存中单个图片的最大大小。

        // 修改内存图片缓存数量，空间策略（这个方式有点恶心）
        Supplier<MemoryCacheParams> mSupplierMemoryCacheParams = new Supplier<MemoryCacheParams>() {
            @Override
            public MemoryCacheParams get() {
                return bitmapCacheParams;
            }
        };
        // 小图片的磁盘配置
        DiskCacheConfig diskSmallCacheConfig = DiskCacheConfig.newBuilder(context)
                .setBaseDirectoryPath(new File(FileUtils.getFileCacheDir()))// 缓存图片基路径
                .setBaseDirectoryName(FileUtils.SMALL_IMAGE_CACHE_NAME)// 文件夹名
                // .setCacheErrorLogger(cacheErrorLogger)//日志记录器用于日志错误的缓存。
                // .setCacheEventListener(cacheEventListener)//缓存事件侦听器。
                // .setDiskTrimmableRegistry(diskTrimmableRegistry)//类将包含一个注册表的缓存减少磁盘空间的环境。
                // .setMaxCacheSize(ConfigConstants.MAX_DISK_CACHE_SIZE)// 默认缓存的最大大小。
                // .setMaxCacheSizeOnLowDiskSpace(MAX_SMALL_DISK_LOW_CACHE_SIZE)// 缓存的最大大小,使用设备时低磁盘空间。
                // .setMaxCacheSizeOnVeryLowDiskSpace(MAX_SMALL_DISK_VERYLOW_CACHE_SIZE)// 缓存的最大大小,当设备极低磁盘空间
                // .setVersion(version)
                .build();
        // 默认图片的磁盘配置
        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(context)
                .setBaseDirectoryPath(new File(FileUtils.getFileCacheDir()))// 缓存图片基路径
                .setBaseDirectoryName(FileUtils.NORMAL_IMAGE_CACHE_NAME)
                // .setCacheErrorLogger(cacheErrorLogger)//日志记录器用于日志错误的缓存。
                // .setCacheEventListener(cacheEventListener)//缓存事件侦听器。
                // .setDiskTrimmableRegistry(diskTrimmableRegistry)//类将包含一个注册表的缓存减少磁盘空间的环境。
                // .setMaxCacheSize(ConfigConstants.MAX_DISK_CACHE_SIZE)// 默认缓存的最大大小。
                // .setMaxCacheSizeOnLowDiskSpace(MAX_DISK_CACHE_LOW_SIZE)// 缓存的最大大小,使用设备时低磁盘空间。
                // .setMaxCacheSizeOnVeryLowDiskSpace(MAX_DISK_CACHE_VERYLOW_SIZE)// 缓存的最大大小,当设备极低磁盘空间
                // .setVersion(version)
                .build();
        // 缓存图片配置
        ImagePipelineConfig.Builder configBuilder = ImagePipelineConfig.newBuilder(context)
                // .setAnimatedImageFactory(AnimatedImageFactory animatedImageFactory)//图片加载动画
                .setBitmapMemoryCacheParamsSupplier(mSupplierMemoryCacheParams)// 内存缓存配置（一级缓存，已解码的图片）
                // .setCacheKeyFactory(cacheKeyFactory)//缓存Key工厂
                // .setEncodedMemoryCacheParamsSupplier(encodedCacheParamsSupplier)//内存缓存和未解码的内存缓存的配置（二级缓存）
                // .setExecutorSupplier(new DefaultExecutorSupplier())//线程池配置
                // .setImageCacheStatsTracker(imageCacheStatsTracker)//统计缓存的命中率
                // .setImageDecoder(ImageDecoder imageDecoder) //图片解码器配置
                // .setIsPrefetchEnabledSupplier(Supplier<Boolean>
                // isPrefetchEnabledSupplier)//图片预览（缩略图，预加载图等）预加载到文件缓存
                .setMainDiskCacheConfig(diskCacheConfig)// 磁盘缓存配置（总，三级缓存）
                // .setMemoryTrimmableRegistry(memoryTrimmableRegistry)
                // //内存用量的缩减,有时我们可能会想缩小内存用量。比如应用中有其他数据需要占用内存，不得不把图片缓存清除或者减小 或者我们想检查看看手机是否已经内存不够了。
                // .setNetworkFetchProducer(networkFetchProducer)//自定的网络层配置：如OkHttp，Volley
                // .setPoolFactory(poolFactory)//线程池工厂配置
                // .setProgressiveJpegConfig(progressiveJpegConfig)//渐进式JPEG图
                // .setRequestListeners(requestListeners)//图片请求监听
                // .setResizeAndRotateEnabledForNetwork(boolean
                // resizeAndRotateEnabledForNetwork)//调整和旋转是否支持网络图片
                .setSmallImageDiskCacheConfig(diskSmallCacheConfig);// 磁盘缓存配置（小图片，可选～三级缓存的小图优化缓存）
        return configBuilder.build();
    }

    private static int getMemoryCacheSize(int availableMemoryPercent) {
        if (availableMemoryPercent > 0 && availableMemoryPercent < 100) {
            long availableMemory = Runtime.getRuntime().maxMemory();
            return (int) ((float) availableMemory * ((float) availableMemoryPercent / 100.0F));
        } else {
            throw new IllegalArgumentException("availableMemoryPercent must be in range (0 < % < 100)");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(PostStartCallbackEvent postStartCallbackEvent) {
        // TODO 需要插入数据库，用于记录发送失败, 在增加草稿箱后这里应修改，在这里进行异步发送，存储数据库，并实时修改数据库状态
        VideoContentTask videoContentTask = postStartCallbackEvent.getVideoContentTask();
        Post post = new Post.Builder().setPostType(Post.PostType.SENDING)
                .setCreateTime(System.currentTimeMillis())
                .setDuration(VideoUtils.getMediaDuration(videoContentTask.getVideoPath()))
                .setVideoUrl(videoContentTask.getVideoPath()).setIsMine(true).build();
        post.setVideoRatioWH(videoContentTask.getVideoRatioWH());
        PostVideo postVideo = createPostVideo(videoContentTask);
        postVideo.setState(PostVideo.State.FAILED.ordinal());
        switch (videoContentTask.getVideoCropProcessState()) {
            case SUCCESS:
                if (!TextUtils.isEmpty(videoContentTask.getProcessVideoFilePath())) {
                    post.setVideoUrl(videoContentTask.getProcessVideoFilePath());
                    VideoMetaData videoMetaData =
                            VideoUtils.getVideoMetaData(videoContentTask.getProcessVideoFilePath());
                    videoContentTask.setVideoMetaData(videoMetaData);
                    postVideo.setCropVideoPath(post.getVideoUrl());
                    postVideo.setVideoMetaData(new Gson().toJson(videoMetaData));
                    ThreadPool.getInstance().execute(() -> {
                        //                        long id = DatabaseManager.getInstance().insertPostVideo(postVideo);
                        //                        postVideo.setId(id);
                        post.setId(String.valueOf(id));
                        // todo 延时200,因为立即执行的话进度条不显示
                        handler.postDelayed(() -> {
                            EventBus.getDefault().post(new PostProgressCallbackEvent(post, CROP_PERCENT));
                            processVideo(videoContentTask, videoMetaData, post, postVideo, 0);
                        }, 200L);
                    });
                }
                break;
            case FAILURE:
                ThreadPool.getInstance().execute(() -> {
                    //                    long id = DatabaseManager.getInstance().insertPostVideo(postVideo);
                    //                    postVideo.setId(id);
                });
                break;
            default:
                break;
        }
    }

    private PostVideo createPostVideo(VideoContentTask videoContentTask) {
        if (videoContentTask != null) {
            PostVideo postVideo = new PostVideo();
            postVideo.setTimestamp(System.currentTimeMillis());
            postVideo.setOriginVideoPath(videoContentTask.getVideoPath());
            postVideo.setVideoRatioWH(videoContentTask.getVideoRatioWH());
            postVideo.setSampleSize(videoContentTask.getSampleSize());
            postVideo.setRecordActionLocationTasks(
                    new Gson().toJson(videoContentTask.getRecordActionLocationTasks()));
            return postVideo;
        }
        return null;
    }

    private void processVideo(VideoContentTask videoContentTask, VideoMetaData videoMetaData,
                              Post post, PostVideo postVideo, int position) {
        VideoProcessControler.process(videoContentTask, new VideoProcessCallback() {
            @Override
            public void success(long id, String videoPath) {
                ThreadPool.getInstance().execute(() -> {
                    if (videoMetaData.getRotation() != 0) {
                        VideoUtils.setVideoRotationParam(videoPath, videoMetaData.getRotation());
                    }
//                    if (Config.isSaveVideo()) {
                        FileUtils.saveVideo(videoPath);
//                    }
                    String framePath = VideoUtils.getVideoCover(videoPath, VideoUtils.COVER_NAME);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        post.setLocalCoverPath(framePath);
                        post.setVideoUrl(videoPath);
                        VideoMetaData videoMetaData = VideoUtils.getVideoMetaData(videoPath);
                        if (videoMetaData.getRotation() == 90 || videoMetaData.getRotation() == 270) {
                            post.setHeight(videoMetaData.getWidth());
                            post.setWidth(videoMetaData.getHeight());
                        } else {
                            post.setHeight(videoMetaData.getHeight());
                            post.setWidth(videoMetaData.getWidth());
                        }
                        post.setQuality(VideoQuality.HIGH.getQuality());
                        EventBus.getDefault()
                                .post(new PostProgressCallbackEvent(post, CROP_PERCENT + ACTION_PERCENT));
                        // TODO 上传视频
//                        sendVideo(post, postVideo);
                    });
                });
            }

            @Override
            public void failure(String message) {
            }
        }, postVideo, post, position);
    }

}
