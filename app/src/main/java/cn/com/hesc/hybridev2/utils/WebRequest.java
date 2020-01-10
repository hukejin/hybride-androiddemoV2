package cn.com.hesc.hybridev2.utils;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.com.hesc.request.HttpRequest;

/**
 * ProjectName: Java_JS
 * ClassName: WebRequest
 * Description: 封装附件服务，统一采用公司的服务
 * Author: liujunlin
 * Date: 2018-01-22 15:53
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */
public class WebRequest {

    /**
     * 附件上报
     * url  附件服务地址
     * @param medias 多媒体文件列表
     * @param onResponseLister http调用完成的回调
     */
    public void sendMediaFiles(@NonNull final String url, @NonNull List<String> medias, @NonNull final HttpRequest.OnResponseLister onResponseLister){

        String namespace = "file";

        List<File> files = new ArrayList<>();
        if(medias!=null && medias.size()>0){
            for (String str:medias) {
                File file = new File(str);
                if(file.exists() && file.isFile())
                    files.add(file);
            }
        }

        HttpRequest httpRequest = new HttpRequest();
        httpRequest.uploadFiles_Form(url,files,namespace);
        httpRequest.setOnResponseLister(onResponseLister);
    }
}
