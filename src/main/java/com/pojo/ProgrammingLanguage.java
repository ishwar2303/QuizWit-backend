package com.pojo;

public class ProgrammingLanguage {
	private Long languageId;
	private String code;
	private String version;
	private String description;
	
	public Long getLanguageId() {
		return languageId;
	}
	public String getCode() {
		return code;
	}
	public String getVersion() {
		return version;
	}
	public String getDescription() {
		return description;
	}
	public void setLanguageId(Long languageId) {
		this.languageId = languageId;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
