package com.tellyouiam.alittlebitaboutspring.utils.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.util.ArrayList;
import java.util.List;

public class AWSHelper {
	public static void main(String[] args) {
		//https://docs.aws.amazon.com/general/latest/gr/aws-sec-cred-types.html
		BasicAWSCredentials credentials = new BasicAWSCredentials("AKIAIKU7ADJ6E5ANH7KQ", "GQ1VzXWRWjoght01GKn73mzDlUFIYGvu/WJm+H90");
		
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.AP_SOUTHEAST_1)
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.build();
		
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
				.withPrefix("dotcom/")
				.withBucketName("logbasex");

		List<String> keys = new ArrayList<>();

		ObjectListing objects = s3Client.listObjects(listObjectsRequest);
		for (;;) {
			List<S3ObjectSummary> summaries = objects.getObjectSummaries();
			if (summaries.size() < 1) {
				break;
			}
			summaries.forEach(s -> {
				if (!s.getKey() .endsWith("/")) {
					keys.add(s.getKey());
				}
			});
			objects = s3Client.listNextBatchOfObjects(objects);
		}

		keys.forEach(i -> System.out.println(s3Client.getUrl("logbasex", i)));
//		System.out.println(keys);
//		List<Bucket> buckets = s3Client.listBuckets();
//		System.out.println("buckets = " + buckets);
	}
}
