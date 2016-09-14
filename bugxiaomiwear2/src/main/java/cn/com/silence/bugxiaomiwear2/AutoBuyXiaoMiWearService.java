package cn.com.silence.bugxiaomiwear2;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class AutoBuyXiaoMiWearService extends AccessibilityService {
    private static final String TAG = "AutoBuyXMWearService";

    /**购物车控件*/
    static AccessibilityNodeInfo cartNode = null;
    /**标记购物车控件是否已被点击*/
    static boolean isClickCart = false;

    /**小米商城程序的主页面*/
    private String XIAOMI_SHOP_MAINACTIVITY ="com.xiaomi.shop2.activity.MainActivity";
    /**购买手环2页面*/
    private String BUY_SWEAR_ACTIVITY = "com.xiaomi.shop2.plugin.PluginRootActivity";
    /**购物车页面*/
    private String SHOPPING_CART_ACTIVITY = "com.xiaomi.shop2.plugin.PluginCartActivity";



    public AutoBuyXiaoMiWearService() {
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        CharSequence className = accessibilityEvent.getClassName();
        Log.d(TAG, "Activity:" + className);
        if (accessibilityEvent.getSource() == null) {
            return;
        }
        if (BUY_SWEAR_ACTIVITY.equals(className)){
            int count = accessibilityEvent.getSource().getChildCount();
            // Log.i(TAG, "view count:" + count);

            for (int i = 0; i < count;i++)
            {
                AccessibilityNodeInfo node = accessibilityEvent.getSource().getChild(i);
                recycleMethod(node);
            }

            if (!isClickCart && cartNode != null){
                cartNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
        if (SHOPPING_CART_ACTIVITY.equals(className)){
            findAndPerformAction("去结算");
        }
        if (XIAOMI_SHOP_MAINACTIVITY.equals(className)){
            int count = accessibilityEvent.getSource().getChildCount();
            for (int i = 0; i < count;i++)
            {
                AccessibilityNodeInfo node = accessibilityEvent.getSource().getChild(i);
                recycleMethod(node);
            }
         //   findAndPerformAction("手环及配件");
        }
        findAndPerformAction("去付款");
    }

    public void recycleMethod(AccessibilityNodeInfo node) {
        if (node != null) {
            int count = node.getChildCount();
            if (count > 0) {
                for (int i = 0; i < count; i++) {
                    Log.i(TAG, "父容器：" + node.getClassName());
                    AccessibilityNodeInfo nodes = node.getChild(i);
                    Log.i(TAG, "node.getClassName()的子控件：" + nodes.getClassName() + "----" + nodes.getText());
                    if (!TextUtils.isEmpty(nodes.getText()) && "加入购物车".equals(nodes.getText().toString())) {
                        Log.e(TAG, "获取到购买按钮" + nodes.getParent());
                        nodes.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (cartNode != null){
                         //    Log.e(TAG, "购物车" + cartNode);
                            cartNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            isClickCart = true;
                        }
                    } else if(nodes.getParent().getClassName().equals("android.widget.FrameLayout") && nodes.getClassName().equals("android.widget.TextView")){

                        Log.e(TAG, "找到购物车" + nodes.getText());
                        if ("分享".equals(nodes.getText().toString()) || "喜欢".equals(nodes.getText().toString())){
                            continue;
                        }

                            //Integer.valueOf(nodes.getText().toString());
                            nodes.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            cartNode = nodes.getParent();


                    }else {
                        recycleMethod(nodes);
                    }
                }
            } else {
                Log.i(TAG, "child:"+node.getClassName());
                Log.i(TAG, "text:"+node.getText());
                if (!TextUtils.isEmpty(node.getText()) && "加入购物车".equals(node.getText().toString())) {
                    Log.e(TAG, "获取到购买按钮" + node.getParent());
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);

                    if (cartNode != null){
                        Log.i(TAG, "购物车");
                        cartNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        isClickCart = true;
                    }
                }

            }
        }
    }

    @Override
    public void onInterrupt() {

    }

    @SuppressLint("NewApi")
    private void findAndPerformAction(String text){
        //查找当前窗口中包含“购物车”文字的按钮
        if(getRootInActiveWindow() == null){
            return;
        }
        //通过文字找到当前的节点
        List<AccessibilityNodeInfo> nodes = getRootInActiveWindow().findAccessibilityNodeInfosByText(text);

        for(int i= 0; i < nodes.size(); i++){
            AccessibilityNodeInfo node = nodes.get(i);
            Log.i(TAG, node.getClassName() + "----" + node.getText());
            //执行按钮点击行为
            if(node.getClassName().equals("android.widget.Button") && node.isEnabled()){
                Log.i(TAG, "获取到结算按钮" + node.getParent());
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }/*else if(text.equals(node.getText().toString())){
                Log.i(TAG, "获取到主页面的手环商品控件" + node.getParent());
                node.getParent().getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
*/

        }

    }


}
