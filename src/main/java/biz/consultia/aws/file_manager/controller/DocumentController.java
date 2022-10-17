package biz.consultia.aws.file_manager.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import biz.consultia.aws.file_manager.service.AwsS3DocumentService;

@RestController
@RequestMapping("/documents")
public class DocumentController {
	
	@Autowired
	private AwsS3DocumentService awsS3Service;
	
	@GetMapping
    public List<String> getAllDocuments() {
        return awsS3Service.getAllDocuments();
    }
	
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseStatus(value = HttpStatus.CREATED)
    public Map<String, String> uploadDocument(@RequestParam(value = "file") MultipartFile file) throws IOException {
		String publicURL = awsS3Service.uploadFile(file);
		Map<String, String> response = new HashMap<>();
		response.put("publicURL", publicURL);
		
		return response;
    }
	
	@GetMapping("/{fileName}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileName) throws IOException {
		byte[] bytes = awsS3Service.getFile(fileName);
		ByteArrayResource resource = new ByteArrayResource(bytes);
		
		return ResponseEntity
				.ok()
                .contentLength(bytes.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
                .body(resource);
	}
	
	@DeleteMapping("/{fileName}")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteDocument(@PathVariable String fileName) {
		awsS3Service.deleteDocument(fileName);
    }
	
	

}
