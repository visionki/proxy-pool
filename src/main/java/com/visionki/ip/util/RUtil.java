package com.visionki.ip.util;

import com.visionki.ip.constant.REnum;
import com.visionki.ip.vo.R;

/**
 * @Author: vision
 * @CreateDate: 2020/7/7 11:08
 * @Version: 1.0
 * @Copyright: Copyright (c) 2020
 * @Description: json返回工具类
 */
public class RUtil {

    /**
     * 数据处理成功时的返回处理（无参数模式）
     * @return
     */
    public static R success() {
        return success(null);
    }

    /**
     * 数据处理成功时的返回处理
     * @param object 返回对象
     * @return
     */
    public static<T> R<T> success(T object) {
        R<T> r = new R<>();
        r.setData(object);
        //200表示成功
        r.setCode(200);
        r.setMsg("ok");
        return r;
    }
    /**
     * 数据处理异常时的返回处理，传入枚举值
     * @param rEnum 枚举值
     * @return
     */
    public static R error(REnum rEnum) {
        R r = new R();
        r.setCode(rEnum.getCode());
        r.setMsg(rEnum.getMessage());
        return r;
    }

    /**
     * 数据处理成功时的返回处理（失败的另外一个编码，加上传输数据）
     * @param rEnum
     * @param object
     * @return
     */
    public static<T> R<T> errorTransData(REnum rEnum, T object) {
        R<T> r = new R<>();
        r.setData(object);
        //0表示成功
        r.setCode(rEnum.getCode());
        r.setMsg(rEnum.getMessage());
        return r;
    }
}
