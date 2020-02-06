package com.nowcoder.community;


import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.service.AlphaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes=CommunityApplication.class)
class CommunityApplicationTests implements ApplicationContextAware {
	private ApplicationContext applicationContext;



	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	@Test
	public void testApplicationContext(){
		AlphaDao alphaDao = applicationContext.getBean("alphaDaoHibernateImpl",AlphaDao.class);
		System.out.println(alphaDao.select());
	}


	@Autowired
	private AlphaDao alphaDao;

	@Autowired
	private AlphaService alphaService;

	@Test
	public void testManageBean(){
		AlphaService alphaService = applicationContext.getBean(AlphaService.class);
		alphaService = applicationContext.getBean(AlphaService.class);
	}

	@Test
	public void testBeanConfig(){
		SimpleDateFormat simpleDateFormat = applicationContext.getBean("simpleDateFormat",SimpleDateFormat.class);
		System.out.println(simpleDateFormat.format(new Date()));
	}


	@Autowired
	@Qualifier("alphaDaoHibernateImpl")
	private AlphaDao alphaDao2;

	@Test
	public void testDI(){
		System.out.println(alphaDao.select());
		System.out.println(alphaDao2.select());
	}


}
