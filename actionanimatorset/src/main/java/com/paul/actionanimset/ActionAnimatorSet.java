package com.paul.actionanimset;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.database.Observable;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by yang on 2016/10/14.
 * paulyung@outlook.com
 */

public class ActionAnimatorSet {
    private static final String TAG = "ActionAnimatorSet";

    private ArrayList<AnimNode> mAllNodes;
    private Map<Animator, AnimNode> mNodeMap;
    private MultiMap<Animator, Builder> mTmpDep;//用来记录在初始化调用addAnimAfter()等函数时，未加入依赖的，在开始动画前，需要加入依赖关系
    private ArrayList<AnimNode> mNoDepNodes;//没有依赖的节点（会同时最先执行的节点）,不是最先执行的动画，需要在后面添加 with 依赖

    private ActionAnimatorSet() {
        mAllNodes = new ArrayList<>();
        mNoDepNodes = new ArrayList<>();
        mNodeMap = new HashMap<>();
        mTmpDep = new MultiMap<>();
    }

    public static ActionAnimatorSet getInstance() {
        return new ActionAnimatorSet();
    }

    //最先执行的动画，效果与 playTogether 相同，只是便于调用者理解
    public void playFirst(Animator... anims) {
        playTogether(anims);
    }

    /**
     * together
     * 没有依赖的就同时执行
     */
    public void playTogether(Animator... anims) {
        Set<Animator> key = mNodeMap.keySet();
        for (Animator anim : anims) {
            if (key.contains(anim))
                throw new IllegalArgumentException("animator has been added! do not add again!");
            else
                new Builder(anim)
                        .first();
        }
    }

    /**
     * 依次执行
     */
    public void playSequence(Animator... anims) {
        Set<Animator> key = mNodeMap.keySet();
        for (Animator anim : anims) {
            if (key.contains(anim))
                throw new IllegalArgumentException("animator has been added! do not add again!");
        }

        playTogether(anims[0]);
        if (anims.length < 2)
            return;
        for (int i = 1; i < anims.length; ++i) {
            addAnimAfter(anims[i], anims[i - 1]);
        }
    }

    /**
     * @param anim          添加的动画
     * @param dependentAnim anim 与 dependentAnim 同时执行，
     *                      值得注意的是，dependentAnim 不能为首个动画
     */
    public void addAnimWith(Animator anim, Animator dependentAnim) {
        addAnimWith(anim, null, null, dependentAnim);
    }

    /**
     * @param anim          添加的动画
     * @param dependentAnim anim 在 dependentAnim 执行完后开始
     */
    public void addAnimAfter(Animator anim, Animator dependentAnim) {
        addAnimAfter(anim, null, null, dependentAnim);
    }

    /**
     * @param anim          添加的动画
     * @param dependentAnim anim 在 dependentAnim 执行到 startPoint 指定的值时开始执行
     * @param startPoint    一个触发点，当达到这个点的时候，anim 开始执行它的动画，这个点是根据 dependentAnim
     *                      的 value 值来确定的。比如 dependentAnim 的值是 float 或者 int 时，可以直接由
     *                      {@link TriggerPoint} 的构造函数的参数指定，如果 dependentAnim 的值是 Object 类型，
     *                      则需要重写 {@link TriggerPoint} 的 whenToStart(Object) 函数，自定义何时触发
     * @see {@link TriggerPoint#whenToStart(Object)}
     */
    public void addAnimBetween(Animator anim, Animator dependentAnim, TriggerPoint startPoint) {
        addAnimBetween(anim, null, null, dependentAnim, startPoint);
    }

    /**
     * @param anim  指定的动画
     * @param start 在指定的动画 anim 开始前，自定义处理一些事情
     */
    public void addStartAction(Animator anim, Action start) {
        AnimNode node = mNodeMap.get(anim);
        if (node == null)
            throw new IllegalArgumentException("don't have this Animator");
        node.startAciton = start;
    }

    /**
     * @param anim 指定的动画
     * @param end  在指定的动画 anim 结束后，自定义处理一些事情
     */
    public void addEndAction(Animator anim, Action end) {
        AnimNode node = mNodeMap.get(anim);
        if (node == null)
            throw new IllegalArgumentException("don't have this Animator");
        node.endAction = end;
    }

    /**
     * with
     * 在所与依赖的非首个动画同时执行
     */
    public void addAnimWith(Animator anim, Action startAciton, Action endAction, Animator dependentAnim) {
        Set<Animator> key = mNodeMap.keySet();
        if (key.contains(anim))
            throw new IllegalArgumentException("animator has been added! do not add again!");

        new Builder(anim)
                .setStartAction(startAciton)
                .setStartAction(endAction)
                .with(dependentAnim);
    }

    /**
     * after
     * 在所依赖的动画执行完成之后执行
     */
    public void addAnimAfter(Animator anim, Action startAciton, Action endAction, Animator dependentAnim) {
        Set<Animator> key = mNodeMap.keySet();
        if (key.contains(anim))
            throw new IllegalArgumentException("animator has been added! do not add again!");

        new Builder(anim)
                .setStartAction(startAciton)
                .setEndAction(endAction)
                .after(dependentAnim);
    }

    /**
     * between
     * 先只加触发开始的触发点
     */
    public void addAnimBetween(Animator anim, Action startAciton, Action endAction, Animator dependentAnim, TriggerPoint startPoint) {
        Set<Animator> key = mNodeMap.keySet();
        if (key.contains(anim))
            throw new IllegalArgumentException("animator has been added! do not add again!");

        new Builder(anim)
                .setStartAction(startAciton)
                .setEndAction(endAction)
                .between(dependentAnim, startPoint);
    }

    /**
     * 定义了一个节点，一个动画对应一个节点
     */
    private static class AnimNode extends ActionNode {
        static final int WITH = 1;//想和某个非首次执行动画同时执行
        static final int AFTER = 2;//在某个动画执行完成之后执行
        static final int BETWEEN = 3;//在动画运行到某一个值时

        int rule;//就是以上三条规则

        TriggerPoint startPoint;//所依赖的node执行到某个点时，触发动画

        //一个节点只允许依赖一个，但是可以被依赖多个
        AnimNode mDependentNode;//该节点依赖的动画（我依赖的谁）

        StatusObservable withAbservable;//所有依赖我 With 的节点都注册进这个观察者
        StatusObservable afterAbservable;
        StatusObservable betweenAbservable;

        AnimNode(Animator anim) {
            mAnim = anim;
        }

        void addToDependency(AnimNode AnimNode) {
            mDependentNode = AnimNode;
        }

        void addDependency(AnimNode animDep, int rule) {//注册进各自的观察者
            switch (rule) {
                case WITH:
                    if (withAbservable == null)
                        withAbservable = new StatusObservable();
                    withAbservable.registerObserver(animDep);
                    break;
                case AFTER:
                    if (afterAbservable == null)
                        afterAbservable = new StatusObservable();
                    afterAbservable.registerObserver(animDep);
                    break;
                case BETWEEN:
                    if (betweenAbservable == null)
                        betweenAbservable = new StatusObservable();
                    betweenAbservable.registerObserver(animDep);
            }
        }
    }

    /**
     * 用来构建节点，同时生成依赖
     */
    private class Builder {
        AnimNode mCurrentNode;
        int mCurrentRule = -1;
        TriggerPoint mStartPoint;

        Builder(Animator anim) {
            mCurrentNode = mNodeMap.get(anim);
            if (mCurrentNode == null) {
                mCurrentNode = new AnimNode(anim);
                mNodeMap.put(anim, mCurrentNode);
                mAllNodes.add(mCurrentNode);
            }
        }

        Builder first() {
            if (!mNoDepNodes.contains(mCurrentNode))
                mNoDepNodes.add(mCurrentNode);
            return this;
        }

        /**
         * 使用以下3个函数，设置对 anim 的依赖
         */
        Builder with(Animator anim) {
            AnimNode node = mNodeMap.get(anim);
            if (node == null) {
                mTmpDep.put(anim, this);
                mCurrentRule = AnimNode.WITH;
                return this;
            }
            node.rule = AnimNode.WITH;
            node.addDependency(mCurrentNode, AnimNode.WITH);
            mCurrentNode.addToDependency(node);
            return this;
        }

        Builder after(Animator anim) {
            AnimNode node = mNodeMap.get(anim);
            if (node == null) {
                //表示所依赖的动画还未添加成AnimNode加入到这个ActionAnimatorSet里来
                //记录一下依赖关系，待会添加
                mTmpDep.put(anim, this);
                mCurrentRule = AnimNode.AFTER;
                return this;
            }
            //已经添加进本Set，则
            node.rule = AnimNode.AFTER;
            node.addDependency(mCurrentNode, AnimNode.AFTER);
            mCurrentNode.addToDependency(node);//将我依赖的节点记录到我的成员，这样我就知道我依赖的是谁了
            return this;
        }

        Builder between(Animator anim, TriggerPoint startPoint) {
            AnimNode node = mNodeMap.get(anim);
            if (node == null) {
                //记录一下依赖关系，待会添加
                mTmpDep.put(anim, this);
                mCurrentRule = AnimNode.BETWEEN;
                mStartPoint = startPoint;
                return this;
            }
            node.rule = AnimNode.BETWEEN;
            node.addDependency(mCurrentNode, AnimNode.BETWEEN);
            mCurrentNode.startPoint = startPoint;
            mCurrentNode.addToDependency(node);
            return this;
        }

        Builder setStartAction(Action action) {
            mCurrentNode.startAciton = action;
            return this;
        }

        Builder setEndAction(Action action) {
            mCurrentNode.endAction = action;
            return this;
        }
    }

    /**
     * Between 的监听
     */
    private static final class BetweenListener implements ValueAnimator.AnimatorUpdateListener {

        private AnimNode mNode;

        BetweenListener(AnimNode node) {
            mNode = node;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            mNode.betweenAbservable.betweenToStart(animation.getAnimatedValue());
        }
    }

    /**
     * With 和 After 的监听
     */
    private static final class WithAndAfterListener implements Animator.AnimatorListener {
        private AnimNode mNode;

        WithAndAfterListener(AnimNode node) {
            mNode = node;
        }

        @Override
        public void onAnimationStart(Animator animation) {
            if (mNode.withAbservable != null)
                mNode.withAbservable.start();
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (mNode.afterAbservable != null)
                mNode.afterAbservable.start();
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

    private final class ActionListener implements Animator.AnimatorListener {
        private Action start, end;
        private boolean hasCancelled;

        ActionListener(Action start, Action end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public void onAnimationStart(Animator animation) {
            AnimNode node = mNodeMap.get(animation);
            if (!node.isCancel && start != null)
                start.doAction();
            node.isCancel = false;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (!hasCancelled && end != null)
                end.doAction();
            hasCancelled = false;
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            hasCancelled = true;
        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

    /**
     * 定义了一个观察者，在监听里面观察状态变化
     */
    private static final class StatusObservable extends Observable<AnimNode> {
        //with after
        void start() {
            for (int i = 0; i < mObservers.size(); ++i) {
                mObservers.get(i).mAnim.start();
            }
        }

        void betweenToStart(Object value) {
            if (BuildConfig.DEBUG)
                Log.d(TAG, "betweenToStart: value: " + value);
            for (int i = 0; i < mObservers.size(); ++i) {
                AnimNode node = mObservers.get(i);
                if (!node.mAnim.isRunning() && node.startPoint.equals(value)) {
                    node.mAnim.start();
                    if (value instanceof Float || value instanceof Integer)
                        node.startPoint.clear();
                }
            }
        }

        int size() {
            return mObservers.size();
        }
    }

    /**
     * 启动动画
     */
    public void start() {
        //给还未加入依赖的加入依赖关系
        addNotDependence();
        //给有别的动画依赖自己的节点设置监听
        addObservable();
        addActionListener();
        clearDependDelay();//所有有依赖的动画节点，清除delay时间（不允许delay）

        //start animators
        for (int i = 0; i < mNoDepNodes.size(); ++i) {
            mNoDepNodes.get(i).mAnim.start();
        }
    }


    public void setStartDelay(long startDelay) {
        if (mNoDepNodes.isEmpty())
            throw new IllegalStateException("there have no NoDepNodes, please add Animator first");
        for (int i = 0; i < mNoDepNodes.size(); ++i) {
            AnimNode node = mNoDepNodes.get(i);
            node.mAnim.setStartDelay(startDelay);
        }
    }

    public void setDuration(long duration) {
        if (mNoDepNodes.isEmpty())
            throw new IllegalStateException("there have no NoDepNodes, please add Animator first");
        for (int i = 0; i < mAllNodes.size(); ++i) {
            mAllNodes.get(i).mAnim.setDuration(duration);
        }
    }

    /**
     * 取消动画
     */
    public void cancel() {
        for (int i = 0; i < mAllNodes.size(); ++i) {
            mAllNodes.get(i).isCancel = true;
            mAllNodes.get(i).mAnim.cancel();
        }
    }

    private void clearDependDelay() {
        for (int i = 0; i < mAllNodes.size(); i++) {
            AnimNode node = mAllNodes.get(i);
            if (node.mDependentNode != null)
                node.mAnim.setStartDelay(0);
        }
    }

    private void addActionListener() {
        for (int i = 0; i < mAllNodes.size(); ++i) {
            AnimNode node = mAllNodes.get(i);
            boolean hasStart = node.startAciton != null;
            boolean hasEnd = node.endAction != null;
            if (hasStart || hasEnd)
                node.mAnim.addListener(new ActionListener(node.startAciton, node.endAction));
        }
    }

    private void addObservable() {
        //如果是With和After的，则设置AnimatorListener监听，With和After共用一个监听
        //如果是Between的，则设置UpdateListener监听
        for (int i = 0; i < mAllNodes.size(); ++i) {
            AnimNode node = mAllNodes.get(i);
            if (node.withAbservable != null
                    && node.withAbservable.size() > 0)
                node.mAnim.addListener(new WithAndAfterListener(node));
            if (node.afterAbservable != null &&
                    node.afterAbservable.size() > 0)
                if (node.mAnim.getListeners() == null
                        || node.mAnim.getListeners().size() == 0)
                    node.mAnim.addListener(new WithAndAfterListener(node));
            if (node.betweenAbservable != null &&
                    node.betweenAbservable.size() > 0)
                if (node.mAnim instanceof ValueAnimator)
                    ((ValueAnimator) node.mAnim).addUpdateListener(new BetweenListener(node));
        }
    }

    //给未添加依赖的节点，加入依赖关系，以一对多的形式出现
    private void addNotDependence() {
        Set<Animator> setNotDep = mTmpDep.keySet();
        for (Animator beDepAnim : setNotDep) {
            List<Builder> values = mTmpDep.get(beDepAnim);
            for (Builder builder : values) {
                switch (builder.mCurrentRule) {
                    case AnimNode.WITH:
                        builder.with(beDepAnim);
                        break;
                    case AnimNode.AFTER:
                        builder.after(beDepAnim);
                        break;
                    case AnimNode.BETWEEN:
                        builder.between(beDepAnim, builder.mStartPoint);
                        break;
                }
            }
        }
        if (!mTmpDep.isEmpty())
            mTmpDep.clear();
    }

    /**
     * 清除所有节点
     */
    public void clearAll() {
        mAllNodes.clear();
        mNodeMap.clear();
        mNoDepNodes.clear();
    }
}
