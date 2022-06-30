package com.icss.gateway.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * 文件类型
 */
public enum FileTypeEnum {

    TXT(0, "txt"),
    CSV(1, "csv"),
    XLSX(2, "xlsx"),
    XLS(3, "xls"),
    DMP(4, "dmp"),
    BAK(5, "bak");


    FileTypeEnum(Integer code, String descp) {
        this.code = code;
        this.descp = descp;
    }

    @EnumValue
    private final Integer code;
    private final String descp;

    public Integer getCode() {
        return code;
    }

    public String getDescp() {
        return descp;
    }

    public static FileTypeEnum getByDesc(String type) {
        for (FileTypeEnum ty : values()) {
            if (ty.getDescp().toUpperCase().equals(type.toUpperCase())) {
                return ty;
            }
        }
        return null;
    }

    public static FileTypeEnum getByCode(Integer type) {
        for (FileTypeEnum ty : values()) {
            if (ty.getCode() == type) {
                return ty;
            }
        }
        return null;
    }
}
