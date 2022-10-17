package biz.consultia.aws.file_manager.service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.IOUtils;

@Service
public class AwsS3DocumentService {
	
	@Value("${document.bucket-name}")
    private String bucketName;
	
	@Autowired
	private AmazonS3 amazonS3Client;
	
	/**
	 * Gets the list of all document keys stored in the S3 bucket
	 * @return A list of all document keys stored in the S3 bucket
	 */
	public List<String> getAllDocuments() {
        return amazonS3Client.listObjectsV2(bucketName).getObjectSummaries().stream()
                .map(S3ObjectSummary::getKey)
                .collect(Collectors.toList());
    }
	
	/**
	 * Upload the given multipart file to the S3 bucket
	 * @param file the given multipart file to be uploaded to the S3 bucket
	 * @return the public URL of the uploaded file
	 */
	public String uploadFile(MultipartFile file) {
		
		String filenameExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());
		
		String key = UUID.randomUUID().toString() + "." + filenameExtension;
		
		ObjectMetadata metaData = new ObjectMetadata();
		metaData.setContentLength(file.getSize());
		metaData.setContentType(file.getContentType());
		
		try {
			var putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), metaData)
					.withCannedAcl(CannedAccessControlList.PublicRead);
			
			amazonS3Client.putObject(putObjectRequest);
		}
		catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process it, so it returned an error response.
            e.printStackTrace();
        } 
		catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }
		catch (IOException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An exception occured while uploading the file");
		}
		
		return amazonS3Client.getUrl(bucketName, key).toString();
	}

	/**
	 * Deletes the document with the given file name from the S3 bucket
	 * @param fileName the name of the file to be deleted from the S3 bucket
	 */
	public void deleteDocument(String fileName) {
		amazonS3Client.deleteObject(bucketName, fileName);
	}

	/**
	 * Gets the bytearray of the file with the given key stored in the S3 bucket
	 * @param key the key under which the desired object is stored
	 * @return the byte array of the file
	 * @throws IOException
	 */
	public byte[] getFile(String key) throws IOException {
		S3Object data = amazonS3Client.getObject(bucketName, key);
        S3ObjectInputStream objectContent = data.getObjectContent();
        
        byte[] bytes = IOUtils.toByteArray(objectContent);
        objectContent.close();
        
        return bytes;
	}
}
