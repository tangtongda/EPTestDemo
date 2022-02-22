package test.service;

import com.temperature.DemoApplication;
import com.temperature.service.TemperatureService;
import com.temperature.service.config.TemperatureProperties;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author <a href="mailto:tangtongda@gmail.com">Tino.Tang</a>
 * @version ${project.version} - 2022/2/22
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = DemoApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TPSLimitedServiceTest {

    @Autowired
    private TemperatureProperties temperatureProperties;

    @Autowired
    private TemperatureService temperatureService;

    /**
     * Boundary test1
     */
    @Test
    public void getTemperature1() {
        Optional<Integer> temperature = temperatureService.getTemperature("江苏", "苏州", "吴中");
        Assert.assertTrue(temperature.isPresent());
    }

    /**
     * Boundary test2
     */
    @Test
    public void getTemperature2() {
        Optional<Integer> temperature = temperatureService.getTemperature("四川", "成都", "成都");
        Assert.assertTrue(temperature.isPresent());
    }

    /**
     * Exception test3：fake province
     */
    @Test
    public void getTemperature3() {
        Optional<Integer> temperature = temperatureService.getTemperature("日本", "东京", "涩谷");
        Assert.assertFalse(temperature.isPresent());
    }

    /**
     * Exception test3:fake city
     */
    @Test
    public void getTemperature4() {
        Optional<Integer> temperature = temperatureService.getTemperature("四川", "东京", "涩谷");
        Assert.assertFalse(temperature.isPresent());
    }

    /**
     * Exception test4：fake county
     */
    @Test
    public void getTemperature5() {
        Optional<Integer> temperature = temperatureService.getTemperature("四川", "成都", "秋叶原");
        Assert.assertFalse(temperature.isPresent());
    }

    /**
     * TPS boundary test
     */
    @Test
    public void getTemperature6() {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 99; i++) {
            fixedThreadPool.execute(() -> {
                Optional<Integer> temperature = temperatureService.getTemperature("江苏", "苏州", "吴中");
                Assert.assertTrue(temperature.isPresent());
            });
        }

    }

    /**
     * TPS exception test
     */
    @Test
    public void getTemperature7() {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(120);
        for (int i = 0; i < 200; i++) {
            fixedThreadPool.execute(() -> {
                Optional<Integer> temperature = temperatureService.getTemperature("江苏", "苏州", "吴中");
                Assert.assertTrue(temperature.isPresent());
            });
        }
    }
}
