package com.tellyouiam.alittlebitaboutspring.utils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubeMetadataCrawler {
	public static void main(String[] args) {
		String link = "";
		// TODO Auto-generated method stub
		try (Scanner s = new Scanner(System.in)) {
			System.out.println("Enter the youtube video for which metadata need to be extracted");
			link = s.nextLine().trim();
			Document doc = Jsoup.connect(link).ignoreContentType(true).timeout(5000).get();
			YoutubeMetadataCrawler ymc = new YoutubeMetadataCrawler();
			String title = ymc.getTitle(doc);
			String desc = ymc.getDesc(doc);
			String thumbNailUrl = ymc.getThumbnailUrl(doc);
			//String views=ymc.getViews(doc);
//			int subscribed=ymc.getPeopleSubscribed(doc);
//			int liked=ymc.getPeopleLiked(doc);
//			int disliked=ymc.getPeopleDisliked(doc);
			
			System.out.println("Title:" + title);
			System.out.println("*****************************************************");
			System.out.println("Description:" + desc);
			System.out.println("ThumbNailUrl:" + thumbNailUrl);
			//System.out.println("Video Views: \n"+views);
//			System.out.println("People subscribed: \n"+subscribed);
//			System.out.println("People who liked the video: \n"+liked);
//			System.out.println("People who disliked the video: \n"+disliked);
			//System.out.println("Top Comments: ");
		} catch (IOException e) {
			System.out.println("JSoup is unable to connect to the website");
		}
	}
	
	public static String removeUTFCharacters(String data){
		Pattern p = Pattern.compile("\\\\u(\\p{XDigit}{4})");
		Matcher m = p.matcher(data);
		StringBuffer buf = new StringBuffer(data.length());
		while (m.find()) {
			String ch = String.valueOf((char) Integer.parseInt(m.group(1), 16));
			m.appendReplacement(buf, Matcher.quoteReplacement(ch));
		}
		m.appendTail(buf);
		return new String(buf);
	}
	
	public String getTitle(Document doc)
	{
		return doc.select("#eow-title").text();
	}
	
	public String getViews(Document doc)
	{
		return doc.select(".watch-view-count").text();
	}
	
	private String getDesc(Document doc)
	{
		return doc.select("#watch-description-text").text();
	}
	
	private String getThumbnailUrl(Document doc) {
		String openGraphImageUrl = null;
		Elements meta = doc.getElementsByTag("meta");
		for (Element e: meta) {
			String property = e.attr("property");
			String name = e.attr("name");
			if (property.equals("og:image") || property.equals("og:image:url") || property.equals("twitter:image")
					|| (!StringUtils.isEmpty(name) && name.equals("twitter:image"))) {
				if (!StringUtils.isEmpty(e.attr("content"))) {
					openGraphImageUrl = e.attr("content");
					break;
				}
			}
		}
		return openGraphImageUrl;
	}
	
	public String getVideoId(String url)
	{
		url=url.substring(url.indexOf("v=")+2);
		if(url.contains("?"))
		{
			url=url.substring(0,url.indexOf("?"));
		}
		return url;
	}
	
	public int getPeopleSubscribed(Document doc)
	{
		return Integer.parseInt(doc.select(".yt-subscriber-count").text().replace(",", ""));
	}
	
	public int getPeopleLiked(Document doc)
	{
		return Integer.parseInt(doc.select("button.like-button-renderer-like-button-unclicked span").text().replace(",", ""));
	}
	
	public int getPeopleDisliked(Document doc)
	{
		return Integer.parseInt(doc.select("button.like-button-renderer-dislike-button-unclicked span").text().replace(",", ""));
	}
	
}
