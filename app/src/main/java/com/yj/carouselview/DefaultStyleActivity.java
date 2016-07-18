package com.yj.carouselview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.carouselview.yj.carouselviewlibrary.CarouselView;

import java.util.ArrayList;
import java.util.List;

public class DefaultStyleActivity extends AppCompatActivity implements
        View.OnClickListener
        ,CarouselView.OnItemClickListener
        ,CarouselView.OnCarouselViewPageChangeListener{
    private CarouselView carouselView;
    private Button btnStartCarousel,btnStopCarousel;
   private final int[] imgId={R.mipmap.img0,R.mipmap.img1,R.mipmap.img2,R.mipmap.img3,R.mipmap.img4};
   private final String[] content={"Page One","Page Two","Page Three"};
    private List<View> views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_style_test);
        init();
    }

    private void init(){
        initView();
        initData();

        carouselView.setItemData(views);
        carouselView.setDescriptionContent(content);
        carouselView.setOnItemClickListener(this);
        carouselView.addmOnCarouselViewPageChangeListener(this);
    }
    private void initView(){
        carouselView=(CarouselView)findViewById(R.id.cvPager);
        btnStartCarousel=(Button)findViewById(R.id.btnStartCarousel);
        btnStopCarousel=(Button)findViewById(R.id.btnStopCarousel);
        btnStartCarousel.setOnClickListener(this);
        btnStopCarousel.setOnClickListener(this);
    }
    private void initData(){
        views=new ArrayList<>();
        ImageView imageView;
        for(int i=0;i<imgId.length;i++){
            imageView=new ImageView(this);
            imageView.setImageResource(imgId[i]);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setId(i);
            views.add(imageView);
        }
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnStartCarousel:
                carouselView.startCarousel();
                break;
            case R.id.btnStopCarousel:
                carouselView.stopCarousel();
                break;
        }
    }

    @Override
    public void onItemClick(int absolutelyPosition, int relativePosition, View view) {
        Toast.makeText(this, "absolutelyPosition:" + absolutelyPosition + "  relativePosition:" + relativePosition + " ID:" + view.getId(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCarouselPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Log.d("----", "onCarouselPageScrolled--position:"+position);
    }

    @Override
    public void onCarouselPageSelected(int position) {
        Log.d("----","onCarouselPageSelected--position:"+position);
    }

    @Override
    public void onCarouselPageScrollStateChanged(int state) {
        Log.d("----","onCarouselPageScrollStateChanged--state:"+state);
    }
}
