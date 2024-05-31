package com.pivothy.exception;

/**
 * 
 * 
 * @author 石浩炎
 */
public final class ReportException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	/**
     * 错误码
     */
    private Integer code;

    /**
     * 错误提示
     */
    private String message;

    /**
     * 错误明细，内部调试错误
     *
     */
    private String detailMessage;

    /**
     * 空构造方法，避免反序列化问题
     */
    public ReportException()
    {
    }

    public ReportException(String message)
    {
        this.message = message;
    }

    public ReportException(String message, Integer code)
    {
        this.message = message;
        this.code = code;
    }

    public String getDetailMessage()
    {
        return detailMessage;
    }

    @Override
    public String getMessage()
    {
        return message;
    }

    public Integer getCode()
    {
        return code;
    }

    public ReportException setMessage(String message)
    {
        this.message = message;
        return this;
    }

    public ReportException setDetailMessage(String detailMessage)
    {
        this.detailMessage = detailMessage;
        return this;
    }
}
