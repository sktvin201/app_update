package cn.rokevin.app.update;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import cn.rokevin.app.update.R;

/**
 * Created by luokaiwen on 15/4/30.
 * <p/>
 * 弹框基类
 */
abstract class BaseDialog {
    /**
     * TAG标记
     */
    protected String TAG = getClass().getSimpleName();

    /**
     * 上下文环境
     */
    protected Context mContext;

    /**
     * 弹出的Dialog
     */
    protected Dialog mDialog;

    /**
     * 距上边距的距离，默认是90 + actionBar的dp距离
     */
    private int mMarginTop;

    /**
     * Dialog的宽,如果mWidth为零则宽度为布局中指定的宽度
     */
    private int mWidth;

    /**
     * Dialog的长,如果mHeight为零则长度为布局中指定的长度
     */
    private int mHeight;

    /**
     * 默认对话框显示的位置
     */
    private int mGravity = Gravity.CENTER;

    /**
     * Dialog样式
     */
    private int mStyle = R.style.DialogTheme;

    /**
     * Dialog动画样式
     */
    private int mAnimStyle;

    /**
     * Dialog在此ViewGroup中弹出对话框
     */
    private ViewGroup viewGroup;

    /**
     * 构造函数
     *
     * @param context
     */
    protected BaseDialog(Context context) {

        // 上下文环境
        mContext = context;
    }

    /**
     * 配置布局ID
     *
     * @return
     */
    abstract protected int getLayoutId();

    /**
     * 需要重定义Dialog样式的可复写此方法
     * <p/>
     * 自定义的Dialog样式，默认为R.style.KDialogTheme 全屏为android.R.style.Theme_NoTitleBar
     */
    protected int getStyle() {
        return mStyle;
    }

    protected int getAnimStyle() {
        return mAnimStyle;
    }

    /**
     * 获取Dialog中的ViewGroup
     *
     * @return
     */
    protected ViewGroup getViewGroup() {
        return viewGroup;
    }

    /**
     * 设置Dialog中的ViewGroup，此方法不对子Dialog提供
     *
     * @param viewGroup
     */
    private void setViewGroup(ViewGroup viewGroup) {
        this.viewGroup = viewGroup;
    }

    /**
     * 设置Dialog的宽
     */
    protected int getWidth() {
        return mWidth;
    }

    /**
     * 设置Dialog的长
     */
    protected int getHeight() {
        return mHeight;
    }

    /**
     * 获取弹出框位置，默认居中
     * <p/>
     * 位置参数
     *
     * @return
     */
    protected int getGravity() {
        return mGravity;
    }

    /**
     * 获取距顶部距离
     *
     * @return
     */
    protected int getMarginTop() {
        return mMarginTop;
    }

    /**
     * 显示弹框
     */
    public void showDialog() {
        showDialog(null);
    }

    /**
     * 创建并显示Dialog
     *
     * @param viewGroup
     */
    public void showDialog(ViewGroup viewGroup) {
        // 如果Dialog没有创建，则先创建Dialog
        if (null == mDialog) {

            // 创建Dialog并设置上下文和样式
            mDialog = new Dialog(mContext, getStyle());

            // 设置点击Dialog外部任意区域关闭Dialog
            mDialog.setCanceledOnTouchOutside(isOutSideTouch());

            // 设置弹框的物理返回键监听
            mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        return isDisableBack();
                    } else {
                        return false; // 默认返回 false
                    }
                }
            });

            // 设置Dialog的View
            mDialog.setContentView(createView(viewGroup));

            // 设置Dialog的window属性
            setDialogWindow();
        }

        // 设置初始化操作
        init();

        // 显示对话框
        mDialog.show();
    }

    /**
     * 创建Dialog的布局
     */
    private View createView(ViewGroup viewGroup) {
        // 根据LayoutId创建LayoutView
        View view = LayoutInflater.from(mContext).inflate(getLayoutId(), viewGroup, false);

        // 设置Dialog要在哪个ViewGroup上显示
        setViewGroup(viewGroup);

        // 让子Dialog去自定义自己的布局
        showView(view);

        // 返回创建的View
        return view;
    }

    /**
     * 子类Dialog自定义自己的布局控件
     *
     * @param view
     */
    abstract protected void showView(View view);

    /**
     * 配置Dialog的Window属性
     */
    private void setDialogWindow() {

        // 获取Dialog的Window
        Window dialogWindow = mDialog.getWindow();

        // 获取DialogWindow的Layout参数管理
        WindowManager.LayoutParams params = dialogWindow.getAttributes();

        // 设置Dialog居中
        dialogWindow.setGravity(getGravity());

        // 高版本中dialog有action bar 设置此flag去掉action bar
        dialogWindow.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 设置Dialog距X坐标偏移量，X 和 Y 以手机屏幕左上角为0,0点为基准
        params.x = 0; // 新位置X坐标

        // 设置Dialog距Y坐标偏移量
        params.y = getMarginTop();

        // 设置DialogWindow的宽
        int width = getWidth();

        if (width > 0) {
            params.width = width;
        }

        // 设置DialogWindow的长
        int height = getHeight();

        if (height > 0) {
            params.height = height;
        }

        // 如果没有设置动画样式，不执行动画
        int animStyle = getAnimStyle();

        if (animStyle != 0) {
            // 设置DialogWindow的动画效果
            dialogWindow.setWindowAnimations(animStyle);
        }

        // 把设置好的属性传入DialogWindow
        dialogWindow.setAttributes(params);
    }

    /**
     * 设置重新打开Dialog的一些初始化操作
     */
    protected void init() {

    }

    /**
     * ture : 按返回键不起作用 false: 按返回键起作用
     */
    public boolean isDisableBack() {
        return false;
    }

    /**
     * ture : 可以触摸外面取消弹框 false: 不可以触摸外面取消弹框
     */
    public boolean isOutSideTouch() {
        return true;
    }

    /**
     * Dialog是否显示
     *
     * @return
     */
    public boolean isShowing() {
        // 返回Dialog是否在显示

        if (null == mDialog) {
            return false;
        }

        return mDialog.isShowing();
    }

    /**
     * 取消Dialog的显示
     */
    public void cancel() {

        // 取消Dialog的显示
        try {
            if (null != mDialog && mDialog.isShowing()) {
                mDialog.cancel();
            }
        } catch (Exception e) {

        }
    }
}
