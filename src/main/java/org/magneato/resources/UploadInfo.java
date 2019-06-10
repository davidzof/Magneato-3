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
			String mimeType, String subDir, String path) {
		super();
		this.fileName = fileName;
		this.name = fileName;
		this.len = len;
		this.url = url;
		this.thumbUrl = thumbUrl;
		this.mimeType = mimeType;
		this.subDir = subDir;
		this.name = path;
	}

	public String getName() {
		return name;
	}

	public String toJson() {
		return "\"files\":[{\"url\":\"" + url + "\",\"thumbnailUrl\":\""
				+ thumbUrl + "\",\"name\":\"" + subDir + fileName + "\",\"size\":\""
				+ len + "\",\"type\":\"" + mimeType
				+ "\",\"deleteUrl\":\"/delete/" + subDir + fileName + " "
				+ "\",\"deleteType\":\"DELETE\"}]";
	}

}
