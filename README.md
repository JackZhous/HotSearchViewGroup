# 自定义热门搜索组件
## 特点：
1. 可以自行添加任意个热门关键词
2. 支持上下滑动来选取热门关键词
3. 占用屏幕空间小
如下示例图：
![show](show.gif)

## 使用方法：
### layout布局文件添加：
```
	<com.jack.textview_viewgroup.view.TextViewGroup
        android:layout_width="match_parent"
        android:layout_height="100dp"
        android:id="@+id/viewgroup"/>
```
高度必须确定好，不能使用wrap_content，因为需要一个默认的初始高度

### java代码添加热门关键字和监听
```java
		viewGroup = (TextViewGroup)findViewById(R.id.viewgroup);
        int index = 0;
        for (String str : list){
            Log.i("jackzhous", "--- " + str);
            TextView textView = new TextView(this);
            textView.setText(str);
            textView.setTextSize(15);

            textView.setBackground(getResources().getDrawable(R.drawable.text_view_bg));
            textView.setTag(str);
            textView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Toast.makeText(MainActivity.this, "你点击了 " + v.getTag(), Toast.LENGTH_SHORT).show();
                    return false;
                }
            });


            viewGroup.addView(textView, index);
            index++;
        }
```

#### 自定义组件源码为app里面的com.jack.textview_viewgroup.view.TextViewGroup，可以根据自己的需要进行修改


# 自定义viewgroup重点解析

## 重点1，如何引入子组件？

有3种方法：
1. 通过viewgroup自带方法 addView(view, index)添加进去
2. 自定义一个xml布局，xml跟布局标签为merge，merge标签会忽略其内部布局位置，由viewgroup自己来布局，在自定义viewgroup中inflater进入，findview找出组件
3. 直接在自定义viewgroup中new你想要的组件，持有其引用，对其布局即可

__后面两种可以不addView进子组件，看自己的需要__；第一种适合自定义组件内部组件都是相同的组件，后面两种适合不同类型子组件组合的viewgroup

## onMeasure测量组件

测量一遍子组件measureChildren(widthMeasureSpec, heightMeasureSpec)，没有必要测量；这个方法目的就是要设置当前组件的宽高，通过MeasureSpec.getMode和MeasureSpec.getSize来区分viewgroup的属性，如果属性是wrap_content，那对应的这个方向上的值就就设定所有组件宽度之和或者最长的一个组件宽度，具体看你设定；不过一般viewgroup组合，我们可以大致知道这个组件是什么样子，宽度可以设置最长组件宽度，高度设置子组件累加之和，当然这个不一定，看自己需要，粘贴一个网上流行的版本
```java
	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int with = MeasureSpec.getSize(widthMeasureSpec);
        int height =  MeasureSpec.getSize(heightMeasureSpec);
        int withMode = MeasureSpec.getMode(widthMeasureSpec);
        int heigthMode = MeasureSpec.getMode(heightMeasureSpec);
        if (getChildCount() == 0) {//如果没有子View,当前ViewGroup没有存在的意义，不用占用空间
            setMeasuredDimension(0, 0);
            return;
        }
        if (withMode == MeasureSpec.AT_MOST && heigthMode == MeasureSpec.AT_MOST){
            //高度累加，宽度取最大
            setMeasuredDimension(getMaxChildWidth(),getTotleHeight());
        }else if (heigthMode == MeasureSpec.AT_MOST){
            setMeasuredDimension(with,getTotleHeight());
        }else if (withMode == MeasureSpec.AT_MOST){
            setMeasuredDimension(getMaxChildWidth(),height);
        }
    }
```

onMeasure会被调用2次，debug发现第一次调用宽高不准确，测量一遍后第二遍就比较准确了

## 布局onLayou

### 确定每个子组件的安放位置

引入时通过addView添加的，就需要getChildAt()拿到组件引用，其他布局findviewbyid或者new时就已经持有组件引用，直接进行layout即可， __重点关注onLayout的参数l/t/r/b，他们是相对于其父布局的位置，所以用子组件view.layout时，其参数也是相对坐标，是相对，也就是0开头__



