package com.hulkhiretech.payment.service.client;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payment.http.HttpRequest;
import com.hulkhiretech.payment.http.HttpServiceEngine;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaypalProviderClient {

   
    private final HttpServiceEngine httpServiceEngine;

    public ResponseEntity<String> makeCall(HttpRequest httpRequest) {

        log.info("PaypalClient makeCall called HttpRequest: {}", httpRequest);

      

        ResponseEntity<String> httpResponse =
                httpServiceEngine.makeHttpCall(httpRequest);

        log.info("httpServiceEngine.makeHttpCall response: {}", httpResponse);

        return httpResponse;
    }
}