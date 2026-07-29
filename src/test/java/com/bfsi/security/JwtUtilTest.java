package com.bfsi.security;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.*;

public class JwtUtilTest {

    private JwtUtil jwtUtil;

    @Before
    public void setUp() {
        jwtUtil = new JwtUtil();
        // @Value fields are not injected in a plain unit test — set them manually.
        ReflectionTestUtils.setField(jwtUtil, "secret",
                "bfsi-mutual-funds-super-secret-key-change-me-32bytes-min");
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", 3_600_000L); // 1 hour
    }

    @Test
    public void testGenerateAndParse() {
        String token = jwtUtil.generateToken("INV001", "INVESTOR");

        assertNotNull(token);
        assertTrue(jwtUtil.isValid(token));
        assertEquals("INV001", jwtUtil.getUserId(token));
        assertEquals("INVESTOR", jwtUtil.getRole(token));
    }

    @Test
    public void testInvalidToken() {
        assertFalse(jwtUtil.isValid("not-a-real-token"));
    }

    @Test
    public void testTamperedTokenRejected() {
        String token = jwtUtil.generateToken("INV001", "INVESTOR");
        // Flip the last character to break the signature
        char last = token.charAt(token.length() - 1);
        String tampered = token.substring(0, token.length() - 1)
                + (last == 'a' ? 'b' : 'a');

        assertFalse(jwtUtil.isValid(tampered));
    }

    @Test
    public void testExpiredTokenRejected() {
        // Set expiry into the past so the freshly-issued token is already expired
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", -1000L);
        String token = jwtUtil.generateToken("INV001", "INVESTOR");

        assertFalse(jwtUtil.isValid(token));
    }
}
