package com.temperature.commons.exception;

/**
 * @author <a href="mailto:tangtongda@gmail.com">Tino.Tang</a>
 * @version ${project.version} - 2022/2/22
 */
public class BizException extends RuntimeException {

    public BizException(String message) {
        super(message);
    }
}
