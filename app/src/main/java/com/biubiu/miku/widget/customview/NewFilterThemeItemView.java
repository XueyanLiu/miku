package com.biubiu.miku.widget.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.biubiu.miku.R;
import com.biubiu.miku.util.FileUtils;
import com.biubiu.miku.util.FontsUtils;
import com.biubiu.miku.util.ThreadPool;
import com.biubiu.miku.util.camera.FilterTools;
import com.biubiu.miku.util.video.action.filter.FilterTheme;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.cyberagent.android.gpuimage.GPUImage;

public class NewFilterThemeItemView extends RelativeLayout {
  @BindView(R.id.theme_image_view)
  SimpleDraweeView imageView;
  @BindView(R.id.theme_name)
  TextView themeName;
  private FilterTheme filterTheme;

  public NewFilterThemeItemView(Context context) {
    super(context);
    initViewData();
  }

  public NewFilterThemeItemView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initViewData();
  }

  public NewFilterThemeItemView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initViewData();
  }

  private void initViewData() {
    LayoutInflater inflater =
        (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.new_filter_theme_item, this, true);
    ButterKnife.bind(this);
    themeName.setTypeface(FontsUtils.getHuaKangWTypeface());
  }

  public void setFilterTheme(FilterTheme filterTheme) {
    this.filterTheme = filterTheme;
    String parentPath = new File(filterTheme.getThumbFilePath()).getParent();
    String filterName = FilterTools.getFilterName(getContext(), filterTheme.getFilterData()
        .getFilterThemeType());
    String filterThumbPath = parentPath.endsWith(File.separator) ? parentPath
        + filterName : parentPath + File.separator + filterName;
    themeName.setText(filterName);
    if (new File(filterThumbPath).isFile()) {
      showImage("file://" + filterThumbPath, imageView);
    } else {
      ThreadPool
          .getInstance().execute(() -> {
            GPUImage gpuImage = new GPUImage(getContext());
            gpuImage.setFilter(FilterTools.getFilter(filterTheme.getFilterData()
                .getFilterThemeType()));
            Bitmap bitmap =
                gpuImage
                    .getBitmapWithFilterApplied(BitmapFactory.decodeFile(filterTheme
                        .getThumbFilePath()));
            String filterFilePath = FileUtils.saveBitmapToFile(bitmap, parentPath, filterName, 50);
            new Handler(Looper.getMainLooper()).post(() -> {
              showImage("file://" + filterFilePath, imageView);
            });
          });
    }
  }

  private void showImage(String imagePath, SimpleDraweeView simpleDraweeView) {
    simpleDraweeView.setImageURI(Uri.parse(imagePath));
//    ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(imagePath)).build();
//    ImagePipeline imagePipeline = Fresco.getImagePipeline();
//    DataSource<CloseableReference<CloseableImage>> dataSource =
//        imagePipeline.fetchDecodedImage(request, this);
//    dataSource.subscribe(new BaseBitmapDataSubscriber() {
//      @Override
//      protected void onNewResultImpl(Bitmap bitmap) {
//        Paint paint = new Paint();
//        paint.setAntiAlias(true);
//        paint.setDither(true);
//        paint.setFilterBitmap(true);
//        Bitmap shape = BitmapFactory.decodeResource(getResources(), R.drawable.shape)
//            .copy(Bitmap.Config.ARGB_8888, true);
//        Bitmap newBitmap = Bitmap.createBitmap(shape.getWidth(),
//            shape.getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(newBitmap);
//        canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()),
//            new Rect(0, 0, newBitmap.getWidth(), newBitmap.getHeight()), paint);
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
//        canvas.drawBitmap(shape, new Rect(0, 0, shape.getWidth(), shape.getHeight()),
//            new Rect(0, 0, shape.getWidth(), shape.getHeight()), paint);
//        new Handler(Looper.getMainLooper()).post(() -> {
//          simpleDraweeView.setImageBitmap(newBitmap);
//        });
//      }
//
//      @Override
//      protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
//
//      }
//    }, GaiaApplication.getExecutor());
  }

  public FilterTheme getFilterTheme() {
    return filterTheme;
  }
}
