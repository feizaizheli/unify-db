

package cn.com.nwdc.db.pageinfo;


import java.util.Objects;

public class ApiResponse<BT> {

    private String code;

    private String message;

    private BT body;

    public static <T> ApiResponse<T> ok(T body) {
        ApiResponse<T> resp = new ApiResponse();
        resp.setMessage("success");
        resp.setCode(Status.OK.getCode());
        resp.setBody(body);
        return resp;
    }

    public static <T> ApiResponse<T> error(String message) {
        ApiResponse<T> resp = new ApiResponse();
        resp.setCode(Status.SYSTEM_BAD_REQUEST.getCode());
        resp.setMessage(message);
        return resp;
    }

    public static <T> ApiResponse<T> error(String message, T body) {
        ApiResponse<T> resp = new ApiResponse();
        resp.setCode(Status.SYSTEM_BAD_REQUEST.getCode());
        resp.setMessage(message);
        resp.setBody(body);
        return resp;
    }

    public ApiResponse() {
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public BT getBody() {
        return this.body;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public void setBody(final BT body) {
        this.body = body;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof ApiResponse)) {
            return false;
        } else {
            ApiResponse<?> other = (ApiResponse)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label47: {
                    Object this$code = this.getCode();
                    Object other$code = other.getCode();
                    if (this$code == null) {
                        if (other$code == null) {
                            break label47;
                        }
                    } else if (this$code.equals(other$code)) {
                        break label47;
                    }

                    return false;
                }

                Object this$message = this.getMessage();
                Object other$message = other.getMessage();
                if (this$message == null) {
                    if (other$message != null) {
                        return false;
                    }
                } else if (!this$message.equals(other$message)) {
                    return false;
                }

                Object this$body = this.getBody();
                Object other$body = other.getBody();
                if (this$body == null) {
                    if (other$body != null) {
                        return false;
                    }
                } else if (!this$body.equals(other$body)) {
                    return false;
                }

                return true;
            }
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, message, body);
    }
    protected boolean canEqual(final Object other) {
        return other instanceof ApiResponse;
    }
    @Override
    public String toString() {
        return "ApiResponse{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", body=" + body +
                '}';
    }
}
