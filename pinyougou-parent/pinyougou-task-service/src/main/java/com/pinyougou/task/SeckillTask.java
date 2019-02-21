package com.pinyougou.task;

import java.util.Date;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SeckillTask {

	@Scheduled(cron="10 * * * * ?")
	public void reference() {
		System.err.println("定时任务："+new Date());
	}
}
