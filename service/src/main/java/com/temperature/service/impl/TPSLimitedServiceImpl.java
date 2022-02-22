package com.temperature.service.impl;

import com.google.common.util.concurrent.RateLimiter;
import com.temperature.service.TPSLimitedService;
import org.springframework.stereotype.Service;

/**
 * @author <a href="mailto:tangtongda@gmail.com">Tino.Tang</a>
 * @version ${project.version} - 2022/2/22
 */
@Service
public class TPSLimitedServiceImpl implements TPSLimitedService {

    private final RateLimiter rateLimiter = RateLimiter.create(100.0);

    @Override
    public boolean tryAcquire() {
        return rateLimiter.tryAcquire();
    }
}
