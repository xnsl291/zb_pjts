package com.zb.weatherApp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WeatherAppApplicationTests {

	@Test
	void equalTest(){
		assertEquals(1,1);
		System.out.println("equalTest done");
	}

	@Test
	void nullTest(){
		assertNull(null);
		System.out.println("nullTest done");
	}

	@Test
	void trueTest(){
		assertTrue(1==1);
		System.out.println("trueTest don e");
	}

}
