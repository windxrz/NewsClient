package com.java.group6.ui;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.java.group6.R;

/**
 * One line view used in account fragment
 */

public class MyOneLineView extends LinearLayout {
    private View dividerTop, dividerBottom;
    private LinearLayout llRoot;
    private ImageView ivLeftIcon;
    private TextView tvTextContent;
    private TextView tvRightText;
    private ImageView ivRightIcon;

    public MyOneLineView(Context context) {  //该构造函数使其，可以在Java代码中创建
        super(context);
    }

    public MyOneLineView(Context context, AttributeSet attrs) {//该构造函数使其可以在XML布局中使用
        super(context, attrs);
    }

    /**
     * 初始化各个控件
     */
    public MyOneLineView init() {
        //引入xml布局
        LayoutInflater.from(getContext()).inflate(R.layout.one_line_view_layout, this, true);
        llRoot = findViewById(R.id.ll_root);
        dividerTop = findViewById(R.id.divider_top);
        dividerBottom = findViewById(R.id.divider_bottom);
        ivLeftIcon = findViewById(R.id.iv_left_icon);
        tvTextContent = findViewById(R.id.tv_text_content);
        tvRightText = findViewById(R.id.tv_right_text);
        ivRightIcon = findViewById(R.id.iv_right_icon);
        return this;
    }

    /**
     * 默认情况下的样子  icon + 文字 + 右箭头 + 下分割线
     */
    public MyOneLineView init(int iconRes, String textContent) {
        init();
        showDivider(false, true);
        setLeftIcon(iconRes);
        setTextContent(textContent);
        setRightText("");
        showArrow(true);
        return this;
    }

    /**
     * 我的页面每一行  icon + 文字 + 右箭头（显示/不显示） + 右箭头左边的文字（显示/不显示）+ 下分割线
     */
    public MyOneLineView initMine(int iconRes, String textContent, String textRight, boolean showArrow) {
        init(iconRes, textContent);
        setRightText(textRight);
        showArrow(showArrow);
        return this;
    }

    /**
     * 设置上下分割线的显示情况
     */
    public MyOneLineView showDivider(Boolean showDividerTop, Boolean showDivderBottom) {
        if (showDividerTop) {
            dividerTop.setVisibility(VISIBLE);
        } else {
            dividerTop.setVisibility(GONE);
        }
        if (showDivderBottom) {
            dividerBottom.setVisibility(VISIBLE);
        } else {
            dividerBottom.setVisibility(GONE);
        }
        return this;
    }

    /**
     * 设置左边Icon
     */
    public MyOneLineView setLeftIcon(int iconRes) {
        ivLeftIcon.setImageResource(iconRes);
        return this;
    }

    /**
     * 设置左边Icon显示与否
     */
    public MyOneLineView showLeftIcon(boolean showLeftIcon) {
        if (showLeftIcon) {
            ivLeftIcon.setVisibility(VISIBLE);
        } else {
            ivLeftIcon.setVisibility(GONE);
        }
        return this;
    }

    /**
     * 设置右边Icon
     */
    public MyOneLineView setRightIcon(int iconRes) {
        ivRightIcon.setImageResource(iconRes);
        return this;
    }

    /**
     * 设置右边Icon显示与否
     */
    public MyOneLineView showRightIcon(boolean showLeftIcon) {
        if (showLeftIcon) {
            ivRightIcon.setVisibility(VISIBLE);
        } else {
            ivRightIcon.setVisibility(GONE);
        }
        return this;
    }

    /**
     * 设置中间的文字内容
     */
    public MyOneLineView setTextContent(String textContent) {
        tvTextContent.setText(textContent);
        return this;
    }

    /**
     * 设置中间的文字颜色
     */
    public MyOneLineView setTextContentColor(int colorRes) {
        tvTextContent.setTextColor(ContextCompat.getColor(getContext(), colorRes));
        return this;
    }

    /**
     * 设置中间的文字大小
     */
    public MyOneLineView setTextContentSize(int textSizeSp) {
        tvTextContent.setTextSize(textSizeSp);
        return this;
    }

    /**
     * 设置右边文字内容
     */
    public MyOneLineView setRightText(String rightText) {
        tvRightText.setText(rightText);
        return this;
    }

    /**
     * 设置右边文字颜色
     */
    public MyOneLineView setRightTextColor(int colorRes) {
        tvRightText.setTextColor(ContextCompat.getColor(getContext(), colorRes));
        return this;
    }

    /**
     * 设置右边文字大小
     */
    public MyOneLineView setRightTextSize(int textSize) {
        tvRightText.setTextSize(textSize);
        return this;
    }

    /**
     * 设置右箭头的显示与不显示
     */
    public MyOneLineView showArrow(boolean showArrow) {
        if (showArrow) {
            ivRightIcon.setVisibility(VISIBLE);
        } else {
            ivRightIcon.setVisibility(GONE);
        }
        return this;
    }

    /**
     * 整个一行被点击
     */
    interface OnRootClickListener {
        void onRootClick(View view);
    }
    /**
     * 右边箭头的点击事件
     */
    interface OnArrowClickListener {
        void onArrowClick(View view);
    }
    public MyOneLineView setOnRootClickListener(final OnRootClickListener onRootClickListener, final int tag) {
        llRoot.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                llRoot.setTag(tag);
                onRootClickListener.onRootClick(llRoot);
            }
        });
        return this;
    }
    public MyOneLineView setOnArrowClickListener(final OnArrowClickListener onArrowClickListener, final int tag) {

        ivRightIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ivRightIcon.setTag(tag);
                onArrowClickListener.onArrowClick(ivRightIcon);
            }
        });
        return this;
    }

}
