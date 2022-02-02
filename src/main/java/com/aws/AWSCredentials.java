package com.aws;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;

public class AWSCredentials {
	String accessKeyId = "AKIAYNOW4GS3IOLYOGNE";
	String secretAccessKey = "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";
	
	public AwsBasicCredentials credentials() {
		
		return AwsBasicCredentials.create(accessKeyId, secretAccessKey);
		
	}
}
