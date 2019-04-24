package cn.rokevin.app.upgrade;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cn.rokevin.app.update.R;

/**
 * Created by luokaiwen on 15/5/28.
 * <p/>
 * 更新弹框
 */
class UpdateDialog extends BaseDialog {

    private String version;
    private String content;

    TextView tvContent;
    Button btnUpdate;

    /**
     * 构造函数
     *
     * @param context
     */
    public UpdateDialog(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_upgrade;
    }

    @Override
    protected void showView(View view) {

        tvContent = view.findViewById(R.id.tv_content);
        btnUpdate = view.findViewById(R.id.btn_update);

        btnUpdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (null != mOnUpdateListener) {
                    btnUpdate.setEnabled(false);
                    mOnUpdateListener.onUpdateConfirm();
                    btnUpdate.setEnabled(false);
                }
                cancel();
            }
        });

        if (!TextUtils.isEmpty(content)) {

            tvContent.setText(Html.fromHtml(content));
        }
    }

    public void showDialog(String version, String content) {

        this.version = version;
        this.content = content;

        try {
            if (!isShowing()) {
                super.showDialog();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private OnUpdateListener mOnUpdateListener;

    public void setOnUpdateListener(OnUpdateListener onUpdateListener) {
        mOnUpdateListener = onUpdateListener;
    }

    public interface OnUpdateListener {

        void onUpdateConfirm();
    }
}
