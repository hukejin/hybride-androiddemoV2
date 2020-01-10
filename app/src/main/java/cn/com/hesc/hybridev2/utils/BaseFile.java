package cn.com.hesc.hybridev2.utils;

import java.io.Serializable;

/**
 * ProjectName: Java_JS
 * ClassName: BaseFile
 * Description: 附件返回json
 * Author: liujunlin
 * Date: 2018-01-22 17:30
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */
public class BaseFile implements Serializable {


    /**
     * newPath : http://yf.hesc.com.cn/hesc-fileservice/upload/file/2018/04/04/20180404160840998550.jpg
     * originPath : /upload/file/2018/04/04/20180404160840998550.jpg
     * newName : 20180404160840998550.jpg
     * type : image
     * oldName : 20180404160944.jpg
     * extension : .jpg
     * width : 2048
     * height : 1152
     * lengthK : 183
     */

    private String newPath;
    private String originPath;
    private String newName;
    private String type;
    private String oldName;
    private String extension;
    private int width;
    private int height;
    private int lengthK;

    public String getNewPath() {
        return newPath;
    }

    public void setNewPath(String newPath) {
        this.newPath = newPath;
    }

    public String getOriginPath() {
        return originPath;
    }

    public void setOriginPath(String originPath) {
        this.originPath = originPath;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOldName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getLengthK() {
        return lengthK;
    }

    public void setLengthK(int lengthK) {
        this.lengthK = lengthK;
    }
}
