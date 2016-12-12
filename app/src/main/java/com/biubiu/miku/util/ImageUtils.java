package com.biubiu.miku.util;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;

import com.biubiu.miku.MikuApplication;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;


public class ImageUtils {

    public static final int CHOOSE_PHOTO_TAG = 1;
    public static final int PHOTO_RESULT = 2;
    private static final int CROP_PHOTO_OUTPUT = 180;
    private static final String IMAGE_UNSPECIFIED = "image/*";
    private static final String MEDIA_CONTENT_PATH = "content://media/external/images/media/";

    public static void getPicture(Fragment fragment) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.putExtra("return-data", true);
        fragment.startActivityForResult(intent, CHOOSE_PHOTO_TAG);
    }

    public static void startPhotoZoomActivity(Fragment fragment, Intent data) {
        //    Uri uri = data.getData();
        //    String path = getPath(fragment.getActivity(), uri);
        //    uri = Uri.parse(path);
        //    Intent intent = new Intent(fragment.getActivity(), CropActivity.class);
        //    intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
        //    fragment.getActivity().startActivityForResult(intent, PHOTO_RESULT);

        // Intent intent = new Intent("com.android.camera.action.CROP");
        // intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
        // intent.putExtra("crop", "true");
        // intent.putExtra("aspectX", 1);
        // intent.putExtra("aspectY", 1);
        // intent.putExtra("outputX", CROP_PHOTO_OUTPUT);
        // intent.putExtra("outputY", CROP_PHOTO_OUTPUT);
        // intent.putExtra("return-data", true);
        // intent.putExtra("scale", true);
        // intent.putExtra("scaleUpIfNeeded", true);
        // intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        // fragment.startActivityForResult(intent, PHOTO_RESULT);
    }

    public static DraweeController showResGif(int resourceId, SimpleDraweeView simpleDraweeView) {
        ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(
                    String id,
                    @Nullable ImageInfo imageInfo,
                    @Nullable Animatable anim) {
                if (anim != null) {
                    anim.start();
                }
            }
        };

        Uri uri = Uri.parse("res://com.blinnnk.gaia/" + resourceId);
        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setControllerListener(controllerListener)
                .build();
        simpleDraweeView.setController(draweeController);
        return draweeController;
    }

    public static String getPath(Context context, Uri uri) {
        if (uri.toString().startsWith("file://")) {
            String id = "";
            String[] column = {MediaStore.Images.Media._ID};
            String sel = MediaStore.Images.Media.DATA + "=?";
            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel,
                    new String[]{uri.toString()}, null);
            int columnIndex = cursor.getColumnIndex(column[0]);
            if (cursor.moveToFirst()) {
                id = cursor.getString(columnIndex);
            }
            cursor.close();
            return MEDIA_CONTENT_PATH + id;
        }
        if (DocumentsContract.isDocumentUri(uri)) {
            String wholeID = DocumentsContract.getDocumentId(uri);
            String id = wholeID.split(":")[1];
            return MEDIA_CONTENT_PATH + id;
        } else {
            return uri.toString();
        }
    }

    public static Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree) {
        if (orientationDegree == 0) {
            return bm;
        }
        Matrix m = new Matrix();
        m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        try {
            Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
            return bm1;
        } catch (OutOfMemoryError ex) {
        }
        return null;
    }

    public static Bitmap drawableToBitmap(Drawable drawable)// drawable 转换成bitmap
    {
        int width = drawable.getIntrinsicWidth(); // 取drawable的长宽
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE
                ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565; // 取drawable的颜色格式
        Bitmap bitmap = Bitmap.createBitmap(width, height, config); // 建立对应bitmap
        Canvas canvas = new Canvas(bitmap); // 建立对应bitmap的画布
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas); // 把drawable内容画到画布中
        return bitmap;
    }

    public static Drawable zoomDrawable(Drawable drawable, int w, int h) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap oldbmp = drawableToBitmap(drawable);// drawable转换成bitmap
        Matrix matrix = new Matrix(); // 创建操作图片用的Matrix对象
        float scaleWidth = ((float) w / width); // 计算缩放比例
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidth, scaleHeight); // 设置缩放比例
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true); // 建立新的bitmap，其内容是对原bitmap的缩放后的图
        return new BitmapDrawable(newbmp); // 把bitmap转换成drawable并返回
    }

    public static Bitmap dstInBitmapFromRes(Bitmap originBitmap, int dstBitmapRes) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setFilterBitmap(true);
        Bitmap shape =
                BitmapFactory.decodeResource(MikuApplication.context.getResources(), dstBitmapRes)
                        .copy(Bitmap.Config.ARGB_8888, true);
        Bitmap newBitmap = Bitmap.createBitmap(shape.getWidth(),
                shape.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawBitmap(originBitmap,
                new Rect(0, 0, originBitmap.getWidth(), originBitmap.getHeight()),
                new Rect(0, 0, newBitmap.getWidth(), newBitmap.getHeight()), paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawBitmap(shape, new Rect(0, 0, shape.getWidth(), shape.getHeight()), new Rect(0, 0, shape.getWidth(), shape.getHeight()), paint);
        return newBitmap;
    }

}
