 package com.secondtrade.webcontroller;

import com.secondtrade.common.result.Result;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BaseController {
    
    protected <T> Result<T> success() {
        return Result.success();
    }

    protected <T> Result<T> success(T data) {
        return Result.success(data);
    }

    protected <T> Result<T> error(String message) {
        return Result.error(message);
    }

    protected <T> Result<T> error(Integer code, String message) {
        return Result.error(code, message);
    }
}