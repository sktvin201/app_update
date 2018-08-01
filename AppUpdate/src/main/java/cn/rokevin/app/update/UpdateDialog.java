package cn.rokevin.app.update;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by luokaiwen on 15/5/28.
 * <p/>
 * 更新弹框
 */
public class UpdateDialog extends BaseDialog {

    private VersionData mVersion;

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
        return R.layout.dialog_update;
    }

    @Override
    protected void showView(View view) {

        tvContent = (TextView) view.findViewById(R.id.tv_content);
        btnUpdate = (Button) view.findViewById(R.id.btn_update);

        btnUpdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (null != mOnUpdateListener) {
                    mOnUpdateListener.onUpdateConfirm();
                }
                cancel();
            }
        });

        if (null != mVersion) {

            tvContent.setText(Html.fromHtml(mVersion.getContent()));
        }
    }

    public void showDialog(boolean isForce, String version, String url, String content) {

        VersionData versionData = new VersionData();

        versionData.setForce(isForce);
        versionData.setVersion(version);
        versionData.setUrl(url);
        versionData.setContent(content);

        mVersion = versionData;

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