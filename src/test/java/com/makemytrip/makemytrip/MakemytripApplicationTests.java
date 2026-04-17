package com.makemytrip.makemytrip;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.makemytrip.makemytrip.config.Jwtutils;
import com.makemytrip.makemytrip.repositories.UserRepository;
import com.makemytrip.makemytrip.services.UserServices;

import jakarta.security.auth.message.AuthException;

@SpringBootTest
class MakemytripApplicationTests {

	@Autowired
	UserServices userServices;

	@Autowired
	UserRepository userRepository;

	@Autowired
	private Jwtutils jwt;


	@Test
	String contextLoads() throws AuthException {
		String userID = jwt.extractUserId("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbjEyM0BnbWFpbC5jb20iLCJ1c2VyIjp7ImZpcnN0TmFtZSI6ImtyaXNoIiwibGFzdE5hbWUiOiJwYXRlbCIsImVtYWlsIjoiYWRtaW4xMjNAZ21haWwuY29tIiwicGFzc3dvcmQiOiIkMmEkMTAkM08wV083ZHpCTmdzWE1SeExsMEdnLnZEYXBiS25FdDBZTEk4eS9EMU1IeEhSa0NacUE2dGEiLCJyb2xlIjoiQURNSU4iLCJwaG9uZU51bWJlciI6IjkxMDYzOTM5MDYiLCJpZCI6IjY5ZGNhMTdjOTk3YTkyMmNjNTc3OGVlNiJ9LCJlbWFpbCI6ImFkbWluMTIzQGdtYWlsLmNvbSIsInVzZXJJZCI6IjY5ZGNhMTdjOTk3YTkyMmNjNTc3OGVlNiIsInJvbGUiOiJBRE1JTiIsImlhdCI6MTc3NjA3MjQ3OCwiZXhwIjoxNzc2MTU4ODc4fQ.Uua28g8OofhKfqQ2Ym0f9S32pKvrhFsL5UufC6t-210");
		System.out.println(userID);
		return userID;
	}

}
