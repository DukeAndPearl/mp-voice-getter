package org.duke.downloader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class VoiceGetter {

	private static final String LINK = "link";

	private static final String MPVOICE = "mpvoice";

	private static final String NAME = "name";

	private static final String VOICE_ENCODE_FILEID = "voice_encode_fileid";

	private static final String SAVE_DIR = "D:\\Human\\";

	private static final String MP3_URL = "https://res.wx.qq.com/voice/getvoice?mediaid=";

	private static final String MP3_EXT = ".mp3";

	public static void main(String[] args) throws Exception {
		String id = "";
		String name = "";
		getVoiceByIdAndName(id, name);

		String url = "";
		getVoicesFromSinglePage(url);
		getVoicesFromUrl(url);

		String tag = "";
		String jsonText = "";
		getVoicesFromJson(jsonText, tag);

	}

	/**
	 * 用id和name下载语音
	 * 
	 * @param id
	 * @param name
	 * @throws Exception
	 */
	public static void getVoiceByIdAndName(String id, String name) {
		if (StringUtils.isBlank(id)) {
			System.out.println("id is blank.");
			return;
		}
		if (StringUtils.isBlank(name)) {
			System.out.println("name is blank.");
			return;
		}
		try {
			Downloader.download(MP3_URL + id, SAVE_DIR, SAVE_DIR + name + MP3_EXT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 在JSON数据中下载语音
	 * 
	 * @param text
	 * @param listName
	 */
	public static void getVoicesFromJson(String text, String listName) {
		JsonElement je = JsonParser.parseString(text);
		JsonObject jo = je.getAsJsonObject();
		JsonElement list = jo.get(listName);
		JsonArray lst = list.getAsJsonArray();
		lst.forEach(x -> {
			JsonObject y = x.getAsJsonObject();
			String link = y.get(LINK).getAsString();
			getVoicesFromSinglePage(link);
			getVoicesFromUrl(link);
		});
	}

	/**
	 * 下载单页语音
	 * 
	 * @param url
	 */
	public static void getVoicesFromSinglePage(String url) {
		Map<String, String> fileIds = getEncodeFileids(url);
		fileIds.forEach((name, id) -> {
			getVoiceByIdAndName(id, name);
		});

	}

	/**
	 * 获取voiceEncodeFileid
	 * 
	 * @param link
	 * @return
	 */
	private static Map<String, String> getEncodeFileids(String link) {
		try {
			Map<String, String> res = new HashMap<>();
			Document doc = Jsoup.connect(link).get();
			Elements mpvoice = doc.select(MPVOICE);
			mpvoice.forEach(x -> {
				String name = x.attr(NAME);
				String fileid = x.attr(VOICE_ENCODE_FILEID);
				if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(fileid)) {
					res.put(name, fileid);
				}
			});
			return res;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 给定地址下载语音
	 * 
	 * @param url
	 */
	public static void getVoicesFromUrl(String url) {
		Map<String, String> addrs = getAddressFromWebPage(url);
		if (addrs.isEmpty()) {
			return;
		}
		addrs.forEach((name, link) -> {
			getVoicesFromSinglePage(link);
			getVoicesFromUrl(link);
		});
	}

	/**
	 * 获取链接
	 * 
	 * @param pageId
	 * @return
	 */
	private static Map<String, String> getAddressFromWebPage(String url) {
		try {
			Map<String, String> res = new HashMap<>();
			Document doc = Jsoup.connect(url).get();
			Elements select = doc.select("a[href]");
			select.forEach(x -> {
				String text = x.text();
				String link = x.absUrl("href");
				if (StringUtils.isNotBlank(text) && StringUtils.isNotBlank(link)) {
					res.put(text, link);
				}
			});
			return res;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
