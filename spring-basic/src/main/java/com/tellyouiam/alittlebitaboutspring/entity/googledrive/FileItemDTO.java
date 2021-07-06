package com.tellyouiam.alittlebitaboutspring.entity.googledrive;

import java.io.Serializable;

/**
 * @author : Ho Anh
 * @since : 16/02/2020, Sun
 **/
public class FileItemDTO implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;
	
	private String id;
	
	private String thumbnailLink;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getThumbnailLink() {
		return thumbnailLink;
	}
	
	public void setThumbnailLink(String thumbnailLink) {
		this.thumbnailLink = thumbnailLink;
	}
}
