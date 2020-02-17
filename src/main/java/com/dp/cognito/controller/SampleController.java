package com.dp.cognito.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("sample")
public class SampleController {

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<String> sample(){
		return ResponseEntity.ok("success");
	}
}
