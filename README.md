# carouselview
CarouselView是一个使用简单且支持多种样式的轮播/Banner图/引导页的组合控件  


![图片描述](https://github.com/YijingwanWang/carouselview/blob/master/screenshot/gif1.gif)
#使用方法

**Step 1**  
在你项目的根build.gradle添加**maven { url "https://jitpack.io" }**
```java 
    allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}
```  
**Step 2**  
添加依赖  
```java 
    dependencies {
	        compile 'com.github.YijingwanWang:carouselview:v1.1'
	}
```  
**Step 3**  
添加如下代码到你的布局文件选中  
```java 
    <com.carouselview.yj.carouselviewlibrary.CarouselView
        android:id="@+id/cvPager"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        yj:indicator_paddingTop="10dp"
        yj:indicator_paddingBottom="10dp"
        />
```  
**Step 4**  
代码中初始化  
```java 
    private CarouselView carouselView;
    private final int[] imgId={R.mipmap.img0,R.mipmap.img1,R.mipmap.img2,R.mipmap.img3,R.mipmap.img4};
    private List<View> views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_style_test);
        init();
    }
    private void init(){
        carouselView=(CarouselView)findViewById(R.id.cvPager);
        initData();
        carouselView.setItemData(views);
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
```  
#提供多种样式选择

**1.你可以通过对应的属性设置轮播图页面切换过程中的动画效果（目前只提供三种动画样式）。**


比如这样设置：
```java 
    yj:pageTransformer="depth"
    /**
     * 或者
     * yj:pageTransformer="pageTransformer"
     * yj:pageTransformer="defaultTransformer"   defaultTransformer为默认样式——即ViewPager自己的默认切换动画效果
    */
```
**效果**


![图片描述](https://github.com/YijingwanWang/carouselview/blob/master/screenshot/gif2.gif)
这里需要说明的是该控件中使用的页面切换效果是使用的Google官方的[范例](https://developer.android.com/training/animation/screen-slide.html)  

**2.设置页面的某一方向上（可以是向左边或者右边滑动）的“无限切换"效果，或者是像我们通常使用的ViewPager那样有限滑动。**  
设置：
  ```java 
    yj:carouselType="infinite"//infinite——无限滑动默认样式——可向左右两个方向”无限“滑动
    /**
     * 或者
     * yj:carouselType="finite"   有限滑动
    */
```  
这里必须说明的是这里的“无限循环”并不是正真正意义上的无限循环——只是PagerAdapter中的getCount()方法设置了一个较大的返回值，并在做了一定的优化，详见代码实现。我不得不承认这样的实现方式并不完美。如果有更好的方法请告诉我，谢谢。


**3.你也可以许修改底部指示器样式。**

  设置：
  ```java 
    yj:indicatorModel="text_tag"
    /**
     * 或者
     * yj:indicatorModel="goldenSection_tag"
     * yj:indicatorModel="center_tag"   center_tag默认样式
    */
```
**效果**


![图片描述](https://github.com/YijingwanWang/carouselview/blob/master/screenshot/img4.png)![图片描述](https://github.com/YijingwanWang/carouselview/blob/master/screenshot/img2.png)

（左图设置的是text_tag样式，右图是默认样式并设置了indicator_paddingTop和indicator_paddingBottom 值详见下文说明）

**【注意】**的是上图中——左图右下角的指示器Tag——小圆点的总显示宽度是按控件的总宽度按黄金分割比（约等于0.618）来设置的固定宽度，所以这
**可能导致如果图片过多或者设置Tag的圆直径过大会导致Tag无法全部显示在可见区域——部分被遮盖住了**

**4.自定义指示器样式**

  如果你对CarouselView 所提供的三种样式都不满意你可以自定义底部指示器的样式。
  
  方法如下：
  
  a.自己编写布局文件比如：
  
  
  tag_layout.xml
  ```java 
    <?xml version="1.0" encoding="utf-8"?>
   <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
              xmlns:tools="http://schemas.android.com/tools"
              android:layout_width="match_parent"
              android:layout_height="match_parent"
              android:orientation="vertical"
    >
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:gravity="center_vertical"
        android:orientation="horizontal"
        android:layout_marginBottom="8dp"
        >
        <ImageView
            android:id="@+id/img"
            android:layout_width="25dp"
            android:layout_height="25dp"
            android:scaleType="centerCrop"
            android:src="@mipmap/ic_launcher"
            android:layout_marginRight="10dp"
            />
        <TextView
            android:id="@+id/tvDescription"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            tools:text="描述页面"/>
    </LinearLayout>
    <LinearLayout
        android:id="@+id/llTag"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        />
</LinearLayout>
```
**【注意】**：如果你想让CustomerView自己来管理对应的页面描述以及Tag小标个数，状态切换。你需要确保你提供的布局文件中有对应的  
**TextView**它对对应的id必须是**android:id="@+id/tvDescription"**。  
**LinearLayout**对应的id必须是**android:id="@+id/llTag"**否则无法显示你想要的效果。

b.将对应的布局设置到对应的CarouselView 的属性中如：
```java 
    yj:indicatorLayout="@layout/tag_layout"
```
c.你可能需要在代码中做必要的设置。比如这里的ImageView——暂且所为一个缩略图展示使用。  
在实例化ImageView ，CarouselView之后，通过**setCurrentSelectedItem**方法设置初始选中项，和回调方法**onCarouselPageSelected**中设置
对应的显示数据。  
**【注意】**由于这里的指示器的整体布局的实例化是由CarouselView来处理的，使用者不需要参与.....所以这里实例化ImageView需要**getIndicatorRootLayout**方法
获得指示器布局的实例。
```java 
    View view=carouselView.getIndicatorRootLayout();
    mImg=(ImageView)view.findViewById(R.id.img);
```
如下：
```java 
    private final int[] imgId = {R.mipmap.img0, R.mipmap.img1, R.mipmap.img2, R.mipmap.img3, R.mipmap.img4};
    private final String[] content = {"Page One", "Page Two", "Page Three"};
    private List<View> views;
    private ImageView mImg;
    private CarouselView carouselView;
    .  
    .  
    .
    private void initData() {
        views = new ArrayList<>();
        ImageView imageView;
        for (int i = 0; i < imgId.length; i++) {
            imageView = new ImageView(this);
            imageView.setImageResource(imgId[i]);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setId(i);
            views.add(imageView);
        }
    }  
    .  
    .  
    .
    private void init() {  
      initView();
      initData();
      carouselView.setItemData(views);
      carouselView.setDescriptionContent(content);
      //注册切换页面时的监听事件
      carouselView.addmOnCarouselViewPageChangeListener(this);
      //设置当前选中项
      carouselView.setCurrentSelectedItem(2);  
    }
    .  
    .  
    .
    private void initView() {
        carouselView = (CarouselView) findViewById(R.id.cvPager);
        //获得CarouselView 中指示器的布局
        View view=carouselView.getIndicatorRootLayout();
        mImg=(ImageView)view.findViewById(R.id.img);
    }
    
  @Override
    public void onCarouselPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
  public void onCarouselPageSelected(int position) {
      //设置要显示的缩略图
      mImg.setImageResource(imgId[position]);
    }

    @Override
    public void onCarouselPageScrollStateChanged(int state) {
    }
```  

**效果**

![图片描述](https://github.com/YijingwanWang/carouselview/blob/master/screenshot/gif3.gif)  
自定义指示器样式可能并不觉得使用方便或者不能应对所有可能性所以这也算是目前CarouselView不足的方面之一。

#你可能会使用到的方法介绍

**addmOnCarouselViewPageChangeListener(OnCarouselViewPageChangeListener mOnCarouselViewPageChangeListener)**  
    设置CarouselView 页面切换监听事件 ，回调方法    
    onCarouselPageScrolled(int position, float positionOffset, int positionOffsetPixels)  
    onCarouselPageSelected(int position);  
    onCarouselPageScrollStateChanged(int state);  
    与通常所使用的ViewPager的回调方法返回值（和意义）相同。  
  **setOnItemClickListener(OnItemClickListener onItemClickListener)**  
    设置每个页面的点击响应事件。回调方法  
    onItemClick(int absolutelyPosition, int relativePosition, View view)  
    参数：  
    **absolutelyPosition**当设置属性yj:carouselType="infinite"（或者没有设置该属性——默认值infinite）时，该返回值是当前item在PagerAdapter中  
    getCount()方法返回的实际位置比如是201。但如果是yj:carouselType="finite"有限循环则返回值与relativePosition相同。  
    **relativePosition**返回值为当前选中的Item在整个显示的数数组中的位置，比如是1。  
    **view**返回当前显示的Item布局。  
  **setItemData(List<View> views)**  
    设置需要展示的页面内容不包含底部指示器中的页面描述。  
  **setDescriptionContent(String[] content)**  
    设置对应页面的文字描述内容。该数组的长度可小于setItemData中的集合的size,但如果大于size，则超出部分的描述不显示。  
  **setCurrentSelectedItem(int item, boolean smoothScroll)或者setCurrentSelectedItem(int item)**  
    设置当前选中项。和ViewPager 的setCurrentItem方法是用相同。    
  **startCarousel()**  
    启动页面自动切换。  
  **stopCarousel()**  
    停止页面自动切换。  
  **getIndicatorRootLayout()**  
    返回指示器的根容器，返回类型为FrameLayout。  
  **setCarouselSpeed(int speed)**  
    设置页面间的切换时间。要注意的是，这里的页面切换时间与A页面完全显示后需要停留多长时间开使切换B页面（下一页面）中的时间是不同的。这两个时间  
    是完全独立且不同意义的。  
  **getmVgCarousel()**  
    获得CarouselView中的ViewPager。  
  **getCarouselCount()**  
    获得轮播图的实际个数  
  **getRelPositionInDataSet()**  
    获得当前在数据集合中实际选中项位置  
    
#属性说明

| Attribute               | Value (type)                                | Describe  |
| -------------|:-------------:| -----:|
| carouselType            | enum (infinite/finite)                      | 轮播模式 infinite无限循环默认值，finite有限循环|
| tag_diameter            | dimension                                   |   指示器中的Tag 直径 ，Tag之间的距离更具此值以及黄金分割比（0.618）计算而得|
| tag_selectedColor       | color/reference                             |    当前选中项对应的Tag颜色 |
| tag_unselectedColor     | color/reference                             |    Tag未选中颜色 |
| indicator_backaground   | color/reference                             |    指示器的背景颜色 |
| pageTransformer         | enum(defaultTransformer/zoomOut/depth)      |    页面切换动画 |
| indicatorLayout         | reference                                   |    自定义指示器时所使用的布局资源引用 |
| indicatorModel          | enum(center_tag/text_tag/goldenSection_tag) |    底部指示器样式 |
| rotationInterval        | integer                                     |    页面切换的间隔时间 |
| indicator_padding       | dimension                                   |    指示器内边距 |
| indicator_paddingLeft   | dimension                                   |    指示器左内边距 |
| indicator_paddingTop    | dimension                                   |    指示器上内边距 |
| indicator_paddingRight  | dimension                                   |    指示器右内边距 |
| indicator_paddingBottom | dimension                                   |    指示器下内边距 |
| descriptionTextSize     | dimension                                   |    页面描述文字字体大小|
| descriptionTextColor    | color/reference                             |    页面描述文字的字体颜色 |
| carouselSpeed           | integer                                     |    轮播图自动切换页面时的页面切换时间，单位毫秒 |

#其他你可能要注意/知道的
1，本样例中使用的是几张高清图片如果你下载该样例或在实际使用，测试中也使用高清图片最好将图片放置在资源文件夹的mipmap——xxxhdpi，如果  
放置在mipmap-mdpi可能会出现OOM异常。  
2，本样例中没有提供设置Tag间距的属性或者方法。    
3，如果使用indicatorModel属性且设置其值为text_tag/goldenSection_tag，并同时设置了Tag直径——tag_diameter，较大    值，或者添加的图片过多可能会导致Tag显示不全。  
4，The code is ugly but it works :( . I haven't refactoring the code yet.  
5,如果你开启了自动切换功能，当手指去触摸或者拖动页面自动切换功能会停止，知道你的手指离开屏幕才会从当前页面继续自动切换。  
6,CarouselView 还有许多没有完成，实现的东西可能会在今后的版本中添加。  
......



  








