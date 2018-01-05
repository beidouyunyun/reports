package com.yun.reports.utils.api;


import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(includeFieldNames = true)
public class ApiResponseUtils implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@NonNull
    private Integer status;
    @NonNull
    private String message;
    
    private Object data;

    public ApiResponseUtils(int status, String message) {
        super();
        this.status = status;
        this.message = message;
        this.data = null;
    }

    public ApiResponseUtils(int status, String message, Object data) {
        super();
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static ApiResponseUtils ok() {
        return new ApiResponseUtils(0, "SUCCESS");
    }

    public static ApiResponseUtils ok(String message) {
        return new ApiResponseUtils(0, message);
    }
    
    public static ApiResponseUtils ok(Object data) {
        return new ApiResponseUtils(0, "SUCCESS", data);
    }

    public static ApiResponseUtils failed() {
        return new ApiResponseUtils(-1, "FAILED");
    }

    public static ApiResponseUtils failed(String message) {
        return new ApiResponseUtils(-1, message);
    }
}
