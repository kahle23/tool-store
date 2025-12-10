package uiharu.common;

import artoria.common.ErrorCode;

/**
 * 错误类
 * @author Kahle
 */
public enum UiharuErrorCode implements ErrorCode {

    /**
     *
     */
    UIHARU001("UIHARU001", "入参对象不应该是空白的"),

    DEVICE020("DEVICE020", "设备记录对象不能是空的！"),
    DEVICE021("DEVICE021", "设备的唯一标识符不能是空的！"),
    DEVICE022("DEVICE022", "设备型号不能是空的！"),
    DEVICE023("DEVICE023", "新增设备记录失败！"),
    DEVICE024("DEVICE024", "更新设备记录失败！"),

    CLIENT021("CLIENT021", "客户端详情记录不能是空的！"),
    CLIENT022("CLIENT022", "新增客户端记录失败！"),
    CLIENT023("CLIENT023", "更新客户端记录失败！"),

    ;

    UiharuErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    UiharuErrorCode(String code, String description, String message) {
        this.code = code;
        this.description = description;
    }

    private String code;
    private String description;

    @Override
    public String getCode() {

        return code;
    }

    @Override
    public String getDescription() {

        return description;
    }

}
