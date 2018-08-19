package com.xxx.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xxx.po.AirInfo;
import com.xxx.util.HttpUtils;

@Service
public class WeatherService {

	public String getAirInfo(String city) throws IOException {
		String host = "https://jisuaqi.market.alicloudapi.com";
		String path = "/aqi/query";
		String method = "GET";
		String appcode = "dc9b39a6097f4eab92053adcd010294f";
		Map<String, String> headers = new HashMap<String, String>();
		// 最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
		headers.put("Authorization", "APPCODE " + appcode);
		HttpResponse response = null;
		String info = null;
		Map<String, String> querys = new HashMap<>();
		String airInfo = null;
		if(city!=null&&!"".equals(city)) {
			querys.put("city", city);
		}
		try {
			response = HttpUtils.doGet(host, path, method, headers, querys);
			/// 获取response的body
			info = EntityUtils.toString(response.getEntity());
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readValue(info, JsonNode.class);
			JsonNode result = jsonNode.get("result");
			JsonNode pm2_5 = result.get("pm2_5");
			airInfo = pm2_5.asText();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return airInfo;
	}

	// 获取所有城市消息
	public List <AirInfo> getCityInfo() {
		String host = "https://jisuaqi.market.alicloudapi.com";
		String path = "/aqi/city";
		String method = "GET";
		String appcode = "dc9b39a6097f4eab92053adcd010294f";
		Map<String, String> headers = new HashMap<String, String>();
		// 最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
		headers.put("Authorization", "APPCODE " + appcode);
		Map<String, String> querys = new HashMap<String, String>();
		List <AirInfo> list = new ArrayList<>();
		try {
			HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
			// System.out.println(response.toString());
			// 获取response的body
			String info = EntityUtils.toString(response.getEntity());
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readValue(info, JsonNode.class);
			// JsonNode result = jsonNode.get("result");
			JsonNode result = jsonNode.get("result");
			Iterator<JsonNode> i = result.elements();
			int count =0;
			while (i.hasNext()) {
				JsonNode node = i.next();
				String city = node.get("city").asText();
				List<Double> infor = new ArrayList<>();
				Double[] coordinate = getCoordinate(city);
				String pm2_5 = getAirInfo(city);
				infor.add(coordinate[0]);
				infor.add(coordinate[1]);
				infor.add(Double.parseDouble(pm2_5));
				AirInfo airInfo = new AirInfo(city, infor);
				list.add(airInfo);
				count++;
				if(count==30) {
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	// 根据城市名获取经纬度
	@SuppressWarnings("unchecked")
	public Double[] getCoordinate(String addr) throws IOException {
		Double lng = null;// 经度
		Double lat = null;// 纬度
		String address = null;
		try {
			address = java.net.URLEncoder.encode(addr, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String key = "iXdSBENDorq8Gg20xsebNf9IRIhWcy7L";
		String url = String.format("http://api.map.baidu.com/geocoder/v2/?address=%s&output=json&ak=%s", address, key);
		URL myURL = null;
		URLConnection httpsConn = null;
		try {
			myURL = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		InputStreamReader insr = null;
		BufferedReader br = null;
		try {
			httpsConn = (URLConnection) myURL.openConnection();// 不使用代理
			if (httpsConn != null) {
				insr = new InputStreamReader(httpsConn.getInputStream(), "UTF-8");
				br = new BufferedReader(insr);
				ObjectMapper mapper = new ObjectMapper();
				String json = br.readLine();
				Map<String, Object> map = mapper.readValue(json, Map.class);
				Map<String, Object> result = (Map<String, Object>) map.get("result");
				Map<String, Object> location = (Map<String, Object>) result.get("location");
				lng = (Double) location.get("lng");
				lat = (Double) location.get("lat");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (insr != null) {
				insr.close();
			}
			if (br != null) {
				br.close();
			}
		}
		return new Double[] { lng, lat };
	}
}
