package com.shefrengo.health.ViewHolders;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.nativead.NativeAd;
import com.shefrengo.health.NativeTemplateStyle;
import com.shefrengo.health.R;
import com.shefrengo.health.TemplateView;

public class AdViewHolder extends RecyclerView.ViewHolder {
    TemplateView template;

    public AdViewHolder(@NonNull View itemView) {
        super(itemView);
        template = itemView.findViewById(R.id.native_ad);
        NativeTemplateStyle styles = new
                NativeTemplateStyle.Builder().build();

     template.setStyles(styles);

    }
    public void setNativeAd(NativeAd nativeAd){
        template.setNativeAd(nativeAd);
    }
}
