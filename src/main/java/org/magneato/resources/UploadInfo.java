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

//	{"files":[{"name":"FF4D00-0.8.png",
// "size":false,
// "type":"image\/png",
// "error":"File upload aborted",
// "deleteUrl":"http:\/\/www.alpacajs.org\/fileupload\/index.php?file=FF4D00-0.8.png",
// "deleteType":"DELETE"}]}
	public String toJson() {
		return "\"files\":[{\"url\":\"" + url + "\",\"thumbnailUrl\":\""
				+ thumbUrl + "\",\"name\":\"" + subDir + fileName + "\",\"size\":\""
				+ len + "\",\"type\":\"" + mimeType
				+ "\",\"deleteUrl\":\"/delete" + url
				+ "\",\"deleteType\":\"DELETE\"}]";
	}

}
