package com.driftos.tv;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.util.*;

public class MainActivity extends Activity {
    LinearLayout content;
    int dp(int n){return (int)(n*getResources().getDisplayMetrics().density);}
    GradientDrawable bg(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));return d;}
    TextView tile(String name, int color, Runnable click){
        TextView t=new TextView(this);t.setText(name);t.setTextColor(Color.WHITE);t.setTextSize(20);t.setGravity(Gravity.CENTER);t.setTypeface(null,Typeface.BOLD);
        t.setBackground(bg(color,22));t.setFocusable(true);
        t.setOnFocusChangeListener((v,focus)->{v.setScaleX(focus?1.07f:1f);v.setScaleY(focus?1.07f:1f);v.setAlpha(focus?1f:.85f);});
        t.setOnClickListener(v->click.run());return t;
    }
    void addTile(LinearLayout row,String title,int color,Runnable action){
        TextView t=tile(title,color,action);
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(dp(210),dp(115));p.setMargins(dp(8),dp(8),dp(8),dp(8));row.addView(t,p);
    }
    void start(Intent intent){try{startActivity(intent);}catch(Exception e){Toast.makeText(this,"Не удалось открыть",Toast.LENGTH_SHORT).show();}}
    void launch(String pkg){Intent i=getPackageManager().getLaunchIntentForPackage(pkg);if(i!=null)start(i);else Toast.makeText(this,"Приложение не установлено",Toast.LENGTH_SHORT).show();}
    void openVpn(){
        String[] candidates={"com.happproxy","com.v2ray.ang","com.wireguard.android","com.protonvpn.android","com.nordvpn.android"};
        for(String p:candidates){Intent i=getPackageManager().getLaunchIntentForPackage(p);if(i!=null){start(i);return;}}
        new android.app.AlertDialog.Builder(this).setTitle("VPN").setMessage("Выбери установленное VPN-приложение в разделе «Приложения».").setPositiveButton("Понятно",null).show();
    }
    @Override public void onCreate(Bundle b){super.onCreate(b);
        ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);
        GradientDrawable gradient=new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{0xff08152f,0xff171338,0xff080e1b});
        scroll.setBackground(gradient);
        content=new LinearLayout(this);content.setOrientation(LinearLayout.VERTICAL);content.setPadding(dp(32),dp(26),dp(20),dp(20));scroll.addView(content);setContentView(scroll);
        TextView heading=new TextView(this);heading.setText("DriftOS  TV");heading.setTextColor(Color.WHITE);heading.setTextSize(36);heading.setTypeface(null,Typeface.BOLD);content.addView(heading);
        TextView sub=new TextView(this);sub.setText("Твой телевизор. Твои правила.");sub.setTextColor(0xffb5c5e8);sub.setTextSize(16);content.addView(sub);
        LinearLayout quick=new LinearLayout(this);quick.setOrientation(LinearLayout.HORIZONTAL);content.addView(quick);
        addTile(quick,"VPN",0xff245caa,()->openVpn());
        addTile(quick,"Настройки",0xff45407d,()->start(new Intent("android.settings.SETTINGS")));
        addTile(quick,"Обновить",0xff315d5e,()->showApps());
        TextView label=new TextView(this);label.setText("Приложения");label.setTextColor(Color.WHITE);label.setTextSize(23);label.setPadding(0,dp(22),0,dp(8));content.addView(label);
        showApps();
    }
    LinearLayout apps;
    void showApps(){
        if(apps!=null)content.removeView(apps);
        apps=new LinearLayout(this);apps.setOrientation(LinearLayout.VERTICAL);content.addView(apps);
        Intent q=new Intent(Intent.ACTION_MAIN);q.addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER);
        List<ResolveInfo> found=getPackageManager().queryIntentActivities(q,0);
        Collections.sort(found,new ResolveInfo.DisplayNameComparator(getPackageManager()));
        LinearLayout row=null;int index=0;
        for(ResolveInfo info:found){
            if(info.activityInfo.packageName.equals(getPackageName()))continue;
            if(index%3==0){row=new LinearLayout(this);row.setOrientation(LinearLayout.HORIZONTAL);apps.addView(row);}
            String title=info.loadLabel(getPackageManager()).toString();
            String pkg=info.activityInfo.packageName;
            Intent explicit=new Intent(Intent.ACTION_MAIN);explicit.addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER);explicit.setClassName(pkg,info.activityInfo.name);
            addTile(row,title,0xff29385b,()->start(explicit));index++;
        }
        if(index==0){TextView empty=new TextView(this);empty.setText("Приложения не найдены");empty.setTextColor(Color.WHITE);apps.addView(empty);}
    }
}
