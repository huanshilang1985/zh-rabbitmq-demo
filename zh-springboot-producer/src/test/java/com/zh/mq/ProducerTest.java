package com.zh.mq;

import com.zh.mq.producer.MessageProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author he.zhang
 * @date 2020/3/30 8:29
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes={ProducerApplication.class})
public class ProducerTest {

    @Autowired
    private MessageProducer messageProducer;

    @Test
    public void producerMessage(){
        messageProducer.testSend();
    }

}
