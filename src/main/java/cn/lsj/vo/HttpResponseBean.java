package cn.lsj.vo;

public class HttpResponseBean<T> {

    private T data;
    private int code;

    public HttpResponseBean(T data, int code) {
        this.data = data;
        this.code = code;
    }

    public HttpResponseBean(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
