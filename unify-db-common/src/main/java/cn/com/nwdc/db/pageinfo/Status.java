

package cn.com.nwdc.db.pageinfo;

public enum Status {
    OK("200"),
    SYSTEM_BAD_REQUEST("500");

    private String code;

    private Status(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
