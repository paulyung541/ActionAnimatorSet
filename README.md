# ActionAnimatorSet
一款强大的属性动画操作集合

在开发中经常会遇到，控制多个属性动画协同合作的这种需求，比如先调用动画A，再同时调用动画B和C；亦或是在A执行了一半的时候，再调用动画B之类的。Android的SDK提供了AnimationSet，可以操作多个动画的播放顺序。但**ActionAnimatorSet**提供了更加丰富的操作，譬如动画在执行了某一时刻触发另一个（组）动画。

##添加依赖
>`compile 'com.paulyung:actionanimatorset:1.0.0'`</br>

##效果演示

* 1 同时执行<br/>
![image](https://github.com/paulyung541/ActionAnimatorSet/blob/master/gif_res/together.gif)

* 2 顺序执行<br/>
![image](https://github.com/paulyung541/ActionAnimatorSet/blob/master/gif_res/sequence.gif)

* 3 精确某一点（值为int，float）<br/>
![image](https://github.com/paulyung541/ActionAnimatorSet/blob/master/gif_res/point1.gif)

* 4 精确某一点（值为Object）<br/>
![image](https://github.com/paulyung541/ActionAnimatorSet/master/gif_res/point2.gif)

##与AnimatorSet相同点

提供`playTogether()` 同时执行多个动画和`playSequence()`顺序执行多个动画两个方法

##与AnimatorSet异同点

* 1 **封装了动画监听**：`Animator.AnimatorListener`，不用单独再给属性动画设置监听器，直接用`addStartAction(Animator anim, Action start)`和`addEndAction(Animator anim, Action end)`可以监听动画执行前和动画执行后的动作，`Action`代表了一个动作。
* 2 **动画的精准控制**：允许某个动画执行到某个点时，控制其它动画的开始


##主要使用的方法

```java
void playFirst(Animator... anims)//添加头，最先执行
void playTogether(Animator... anims)//同时执行，和playFirst作用是一样的
void playSequence(Animator... anims)//顺序执行

void addAnimWith(Animator A, Animator B)//将A添加进去，并和B同时开始播放，主要用作一些不是头部，又需要同时操作的动画
void addAnimAfter(Animator A, Animator B)//将A添加进去，并在B播放完之后，开始播放
void addAnimBetween(Animator A, Animator B, TriggerPoint startPoint)//将A添加进去，并定义了一个开始点TriggerPoint ，在B播放到这个点时，开始播放A

void addStartAction(Animator A, Action start);//给A动画添加动画开始时的监听
void addEndAction(Animator A, Action end);//给A动画添加动画结束时的监听
```

##添加动画的方式

在**ActionAnimatorSet**里面添加动画，需要添加一个头，然后依次添加动画和指明该动画所依赖的动画，如需精确控制开始点，还需参数指明。

###示例1（简单的同时执行）
先使用`ObjectAnimatior`定义3个动画，A，B，C
```java
ObjectAnimator A = ObjectAnimator.ofFloat(view1, "y", 1200);
ObjectAnimator B = ObjectAnimator.ofFloat(view2, "y", 1200);
ObjectAnimator C = ObjectAnimator.ofFloat(view3, "y", 1200);
```
然后使用`ActionAnimatorSet`提供的`playTogether()`方法或者`playFirst()`将动画添加进`ActionAnimatorSet`
```java
ActionAnimatorSet animSet = new ActionAnimatorSet();
animSet.playTogether(view1Anim, view2Anim, view3Anim);
animSet.start()//开始执行这个动画集合
```


###示例2（添加依赖）
现在让刚刚的动画A，B，C执行这样一个操作：
A最先执行，等A执行完之后，B和C同时执行
```java
animSet.playFirst(A);//A作为头，最先执行
animSet.addAnimAfter(B, A);//将B添加进ActionAnimatorSet，并在A播放完成之后执行
animSet.addAnimAfter(C, A);//将C添加进ActionAnimatorSet，并在A播放完成之后执行
animSet.start()//开始执行这个动画集合
```
以上方法调用顺序将不影响添加，可以先添加C再添加B，最后添加A也没问题，但是不可以重复添加。

###示例3（精准控制开始点）
A先播放，B在A的值达到600的时候播放，C在B达到600时候播放
```java
animSet.playFirst(A);//A作为头，最先执行
animSet.addAnimBetween(B, A, new TriggerPoint(600));
animSet.addAnimBetween(C, B, new TriggerPoint(600));
```
以上这个例子是动画的值为`Float`的情况，如果值为`Int`型也按照这种构造函数的方式传入开始点

###示例4（精准控制开始点，值为Object的时候）
现在A动画的值是一个叫做`PointF`对象类型的，它的初始化如下
```java
PointF startPoint = new PointF(100, 200);
PointF endPoint = new PointF(400, 500);
ObjectAnimator A = ObjectAnimator.ofObject(this, "Anim", new AnimEvaluator(), startPoint, endPoint);//因为我的set方法是放在Activity里面的，所以直接使用了this
```
B和C任然是Float型，这里省略初始化操作。
现在A的运行轨迹动画是从`startPoint` 到 `endPoint`，当它的横坐标`x`大于300的时候，我们同时执行B和C动画，代码如下：
```java
animSet.playFirst(A);//A作为头，最先执行
animSet.addAnimBetween(B, A, new TriggerPoint<PointF>() {
  @Override
  public boolean whenToStart(PointF p) {
    return p.x > 300;
  }
});//添加B，在A动画的PointF的x大于300时执行动画
animSet.addAnimWith(C, B);//添加C，和B同时执行
```

##最大特色

**TriggerPoint**定义了一个触发时刻点，允许用户定义该点来精确控制一组动画的执行时刻，并支持`Object`类型的属性动画Vaule
* 1 当ObjectAnimator的Value为Int或者float类型时，直接通过构造函数传入参数，指定触发时刻
  eg: `new TriggerPoint(0.3);`

* 2 当ObjectAnimator的Value为某个对象时，创建`TriggerPoint`的时候需要重写`whenToStart(T obj)`方法，在该方法内部定义如何触发动画，该方法返回一个`boolean`值，当返回`true`时，表示此时触发动画。此`TriggerPoint`支持泛型操作，十分方便。


##最后
更多详情请看Sample项目