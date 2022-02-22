package com.temperature.service;

import com.google.common.util.concurrent.RateLimiter;

/**
 * TPS limited service limited 100tps
 *
 * @author <a href="mailto:tangtongda@gmail.com">Tino.Tang</a>
 * @version ${project.version} - 2022/2/22
 */
public interface TPSLimitedService {

    /**
     * try to get token
     *
     * @return true or false
     */
    boolean tryAcquire();
}
