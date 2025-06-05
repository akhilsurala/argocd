package com.sunseed.helper;

import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.IOException;

import com.sunseed.exceptions.WebclientException;
import com.sunseed.model.requestDTO.SimulationRequest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.LinkedMultiValueMap;
import com.sunseed.model.responseDTO.SimulationResponseDto;
import com.sunseed.model.responseDTO.SimulationTaskStatusDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import reactor.core.publisher.Mono;

@Component
public class WebClientHelper {
	
	@Value("${auth.url}")
    private String authServiceUrl;
	
	@Value("${mail.url}")
    private String mailServiceUrl;
	
	@Value("${simulation.url}")
    private String simulationUrl;
	
//    private static String authServiceUrl = "http://localhost:8081/auth";
//	private String authServiceUrl = authorisationUrl + "/auth";
//    private static String mailServiceUrl = "http://localhost:8082";
//	private static String mailServiceUrl = mailUrl;
    
    @Autowired
    private WebClient.Builder webClientBuilder;
    @Autowired
    private WebClient webClient;

    public ResponseEntity<Object> signup(Map<String, String> requestBody) {

        ResponseEntity<Object> response = postWithRequestBody(authServiceUrl + "/auth/v1/signup", requestBody);
        return response;
    }

    public ResponseEntity<Object> adminSignup(Map<String, Object> requestBody, String jwtToken) {

        ResponseEntity<Object> response = postWithRequestBodyAdmin(authServiceUrl + "/auth/v1/adminSignup", requestBody,
                jwtToken);
        return response;
    }

    public ResponseEntity<Object> login(Map<String, String> requestBody) {

        ResponseEntity<Object> response = postWithRequestBody(authServiceUrl + "/auth/v1/login", requestBody);
        return response;
    }

    public ResponseEntity<Object> getUser(Map<String, String> requestBody) {

        ResponseEntity<Object> response = postWithRequestBody(authServiceUrl + "/auth/v1/user", requestBody);
        return response;
    }

    public ResponseEntity<Object> verifyUser(Map<String, String> requestBody) {

        ResponseEntity<Object> response = postWithRequestBody(authServiceUrl + "/auth/v1/verify-user", requestBody);
        return response;
    }

    public ResponseEntity<Object> resetPassword(Map<String, String> requestBody) {

        ResponseEntity<Object> response = putWithRequestBody(authServiceUrl + "/auth/v1/reset-password", requestBody);
        return response;
    }

    public String sendMail(MultiValueMap<String, Object> formData) {

        String response = postWithMultiFormData(mailServiceUrl + "/mail/send", formData);
        return response;
    }

    // ****************** start simulation *******************
    public List<SimulationResponseDto> startSimulation(String url, SimulationRequest requestBody) {
        System.out.println("simulation Response Dto in webclient calling method :");
        return makePostRequest(url, requestBody, SimulationResponseDto.class);
    }

    // ************** change status of simulated run *************
    public Map<String, List<SimulationTaskStatusDto>> updateSimulateRun(String url, Map<String, Object> request) {
        Map<String, List<SimulationTaskStatusDto>> response = updateSimulationTaskStatus(request, url);
        return response;
    }

    private ResponseEntity<Object> postWithRequestBody(String url, Map<String, String> requestBody) {

        WebClient webClient = WebClient.create(url);
        Mono<ResponseEntity<Object>> responseMono = webClient.post().body(BodyInserters.fromValue(requestBody))
                .retrieve().toEntity(Object.class).onErrorResume(WebClientResponseException.class, ex -> {
                    if (WebClientResponseHelper.isAllowed(ex.getStatusCode())) {
                        return Mono.just(
                                ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAs(Object.class)));
                    } else {
                        return Mono.just(
                                ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAs(Object.class)));
                    }
                });
        ResponseEntity<Object> response = responseMono.block();
        return response;
    }

    private ResponseEntity<Object> postWithRequestBodyAdmin(String url, Map<String, Object> requestBody,
                                                            String jwtToken) {

        WebClient webClient = WebClient.create(url);
        Mono<ResponseEntity<Object>> responseMono = webClient.post()
//	        	    .uri(url) // specify your endpoint URI
                .header(HttpHeaders.AUTHORIZATION, jwtToken) // Add JWT token here
                .body(BodyInserters.fromValue(requestBody)).retrieve().toEntity(Object.class)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    if (WebClientResponseHelper.isAllowed(ex.getStatusCode())) {
                        return Mono.just(
                                ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAs(Object.class)));
                    } else {
                        return Mono.just(
                                ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAs(Object.class)));
                    }
                });
        ResponseEntity<Object> response = responseMono.block();
        return response;
    }

    private String postWithMultiFormData(String url, MultiValueMap<String, Object> formData) {

        WebClient webClient = WebClient.create(url);
        Mono<String> responseMono = webClient.post().contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(formData)).retrieve().bodyToMono(String.class)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    if (WebClientResponseHelper.isAllowed(ex.getStatusCode())) {
                        return Mono.just("");
                    } else {
                        return Mono.just("");
                    }
                });
        String response = responseMono.block();
        return response;
    }

    private ResponseEntity<Object> putWithRequestBody(String url, Map<String, String> requestBody) {

        WebClient webClient = WebClient.create(url);
        Mono<ResponseEntity<Object>> responseMono = webClient.put().body(BodyInserters.fromValue(requestBody))
                .retrieve().toEntity(Object.class).onErrorResume(WebClientResponseException.class, ex -> {
                    if (WebClientResponseHelper.isAllowed(ex.getStatusCode())) {
                        return Mono.just(
                                ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAs(Object.class)));
                    } else {
                        return Mono.just(
                                ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAs(Object.class)));
                    }
                });
        ResponseEntity<Object> response = responseMono.block();
        return response;
    }

//----------- method for communicate with simulation --------------------

    private <T, R> List<R> makePostRequest(String url, T requestBody, Class<R> responseClass) {
        WebClient client = WebClient.create();
        try {
            List<R> response = client.post()
                    .uri(url)
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), clientResponse ->
                            clientResponse.bodyToMono(Map.class).flatMap(errorBody -> {
                                System.out.println("Client Error: " + errorBody.get("message"));
                                return Mono.error(new RuntimeException(errorBody.get("message").toString()));
                            })
                    )
                    .onStatus(status -> status.is5xxServerError(), clientResponse ->
                            clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                                System.out.println("Server Error: " + errorBody);
                                return Mono.error(new RuntimeException(errorBody));
                            })
                    )
                    .bodyToFlux(responseClass)
                    .collectList()
                    .block();
            System.out.println("Simulation created: " + response);
            return response;
        } catch (WebClientResponseException e) {
            System.out.println("WebClientResponseException: " + e.getResponseBodyAsString());
            throw new WebclientException(e.getResponseBodyAsString(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            throw new WebclientException(e.getMessage());
        }
    }

    // ************ method for change status ****************
    public Map<String, List<SimulationTaskStatusDto>> updateSimulationTaskStatus(Map<String, Object> requestBody, String url) {
        WebClient client = WebClient.create();
        Mono<Map<String, List<SimulationTaskStatusDto>>> respo = client.put()
                .uri(url)
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, List<SimulationTaskStatusDto>>>() {
                })
                .doOnSuccess(response -> {
                    // Handle successful response
                    System.out.println("Received response: " + response);
                })
                .doOnError(WebClientResponseException.class, e -> {
                    // Handle WebClient-specific exceptions (e.g., 4xx, 5xx status codes)
                    System.err.println("Error response code: " + e.getStatusCode());
                    System.err.println("Error response body: " + e.getResponseBodyAsString());
                })
                .doOnError(Exception.class, e -> {
                    // Handle other exceptions (e.g., network issues, etc.)
                    System.err.println("An unexpected error occurred: " + e.getMessage());
                });

        Map<String, List<SimulationTaskStatusDto>> response = respo.block();
        return response;

    }
    
    public Map<String, Object> getSimulation(Long id) {
        WebClient webClient = webClientBuilder.build();
//        String url = "http://localhost:8090/simtool/v1/simulation?id=" + id;
        String url = simulationUrl + "/simtool/v1/simulation?id=" + id;

        Mono<Map<String, Object>> responseMono = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                });

        // Block to get the response synchronously (not recommended for non-blocking applications)
        return responseMono.block();
    }
    
    
    public String postXmlFile(String url, File xmlFile) throws IOException {
  	// Prepare the multipart request body using FileSystemResource
        MultiValueMap<String, Object> file = new LinkedMultiValueMap<>();
        file.add("file", new FileSystemResource(xmlFile));

        // Send the multipart request
        return webClient.post()
                .uri(url)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(file)
                .retrieve()
                .bodyToMono(String.class)
                .block(); // Block for synchronous response
    }
    
    public String postMultipartFile(String url, MultipartFile multipartFile) throws IOException {
	    // Convert MultipartFile to a File for posting
	    File tempFile = File.createTempFile("upload-", multipartFile.getOriginalFilename());
	    multipartFile.transferTo(tempFile);

	    // Prepare the multipart request body using FileSystemResource
	    MultiValueMap<String, Object> file = new LinkedMultiValueMap<>();
	    file.add("file", new FileSystemResource(tempFile));

	    try {
	        // Send the multipart request
	        return webClient.post()
	                .uri(url)
	                .contentType(MediaType.MULTIPART_FORM_DATA)
	                .bodyValue(file)
	                .retrieve()
	                .bodyToMono(String.class)
	                .block(); // Block for synchronous response
	    } finally {
	        // Cleanup the temporary file
	        tempFile.delete();
	    }
	}

}



