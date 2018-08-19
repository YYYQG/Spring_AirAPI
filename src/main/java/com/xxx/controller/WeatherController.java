package com.xxx.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xxx.po.AirInfo;
import com.xxx.service.WeatherService;

@RestController
public class WeatherController {

	@Autowired
	private WeatherService service;

	@RequestMapping("/info")
	public String getInfo() {
		String info = null;
		try {
			info = service.getAirInfo("成都");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return info;
	}

	@RequestMapping("/Coordinate")
	public Object[] coordinate() {

		Object[] objects = null;
		try {
			objects = service.getCoordinate("成都");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return objects;
	}

	@RequestMapping("/cityinfo")
	public List<AirInfo>  city() {
		List<AirInfo> objects = null;
		objects = service.getCityInfo();
		return objects;
	}

}
