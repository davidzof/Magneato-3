package org.magneato.resources;

public class UploadInfo {
	private String fileName;
	private long len;
	private String url;
	private String thumbUrl;
	private String mimeType;
	private String subDir;
	private String name;

	public UploadInfo(String fileName, long len, String url, String thumbUrl,
			String mimeType, String subDir, String name) {
		super();
		this.fileName = fileName;
		this.len = len;
		this.url = url;
		this.thumbUrl = thumbUrl;
		this.mimeType = mimeType;
		this.subDir = subDir;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String toJson() {
		//url = "http://localhost:9090"+ url;
		return "\"files\":[{\"url\":\"" + url + "\",\"thumbnailUrl\":\""
				+ thumbUrl + "\",\"name\":\"" + fileName + "\",\"size\":\""
				+ len + "\",\"type\":\"" + mimeType
				+ "\",\"deleteUrl\":\"/delete" + url
				+ "\",\"deleteType\":\"DELETE\"}]";
	}

}
