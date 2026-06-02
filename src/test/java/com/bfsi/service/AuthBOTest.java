package com.bfsi.service;

import com.bfsi.dto.RegisterInvestorDTO;
import com.bfsi.entity.Role;
import com.bfsi.entity.User;
import com.bfsi.exception.*;

import com.bfsi.repository.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class AuthBOTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private InvestorProfileRepository profileRepo;

    @InjectMocks
    private AuthBO authBO;

    /* =====================
       LOGIN ✅ FIXED
       ===================== */

    @Test
    public void testLogin_Success() {

        User user = new User("INV001", "Test User",
                "test@mail.com", "pass", "INVESTOR");

        when(userRepo.findByEmail("test@mail.com"))
                .thenReturn(user);

        Role role = new Role("INVESTOR", "Investor");

        when(userRepo.findRoleByUserId("INV001"))
                .thenReturn(role);

        User result = authBO.login("test@mail.com", "pass");

        assertEquals("INVESTOR", result.getRoleId());
    }

    @Test(expected = UnauthorizedAccessException.class)
    public void testLogin_InvalidCredentials_UserNotFound() {

        when(userRepo.findByEmail("bad@mail.com"))
                .thenReturn(null);

        authBO.login("bad@mail.com", "wrong");
    }

    @Test(expected = UnauthorizedAccessException.class)
    public void testLogin_InvalidPassword() {

        User user = new User("INV001", "Test",
                "mail", "pass", "INVESTOR");

        when(userRepo.findByEmail("mail"))
                .thenReturn(user);

        authBO.login("mail", "wrong");
    }

    @Test(expected = DataNotFoundException.class)
    public void testLogin_RoleNotFound() {

        User user = new User("INV001", "Test",
                "mail", "pass", "INVESTOR");

        when(userRepo.findByEmail("mail"))
                .thenReturn(user);

        when(userRepo.findRoleByUserId("INV001"))
                .thenReturn(null);

        authBO.login("mail", "pass");
    }

    /* =====================
       REGISTER ✅ SAME + EXTRA
       ===================== */

    @Test
    public void testRegisterInvestor_Success() {

        RegisterInvestorDTO dto = new RegisterInvestorDTO();
        dto.setFirstName("Amit");
        dto.setLastName("Patel");
        dto.setEmail("investor@mail.com");
        dto.setPassword("123");
        dto.setConfirmPassword("123");
        dto.setMobile("9000000006");

        when(userRepo.findByEmail("investor@mail.com"))
                .thenReturn(null);

        User result = authBO.registerInvestor(dto);

        assertNotNull(result);
        assertEquals("INVESTOR", result.getRoleId());

        verify(userRepo).save(any(User.class));
        verify(profileRepo).save(any());
    }

    @Test(expected = InvalidOperationException.class)
    public void testRegister_PasswordMismatch() {

        RegisterInvestorDTO dto = new RegisterInvestorDTO();
        dto.setPassword("123");
        dto.setConfirmPassword("456");

        authBO.registerInvestor(dto);
    }

    @Test(expected = InvalidOperationException.class)
    public void testRegister_EmailAlreadyExists() {

        RegisterInvestorDTO dto = new RegisterInvestorDTO();
        dto.setEmail("investor@mail.com");
        dto.setPassword("123");
        dto.setConfirmPassword("123");

        when(userRepo.findByEmail("investor@mail.com"))
                .thenReturn(new User());

        authBO.registerInvestor(dto);
    }

    /* =====================
       FETCH USER ✅ FIXED
       ===================== */

    @Test
    public void testGetUserByEmail_Success() {

        User user = new User("INV001", "Amit",
                "investor@mail.com", "pass", "INVESTOR");

        when(userRepo.findByEmail("investor@mail.com"))
                .thenReturn(user);

        Role role = new Role("INVESTOR", "Investor");

        when(userRepo.findRoleByUserId("INV001"))
                .thenReturn(role);

        User result = authBO.getUserByEmail("investor@mail.com");

        assertEquals("INVESTOR", result.getRoleId());
    }

    @Test
    public void testGetUserByEmail_WithoutRole() {

        User user = new User("INV001", "Amit",
                "investor@mail.com", "pass", "INVESTOR");

        when(userRepo.findByEmail("investor@mail.com"))
                .thenReturn(user);

        when(userRepo.findRoleByUserId("INV001"))
                .thenReturn(null);

        User result = authBO.getUserByEmail("investor@mail.com");

        assertNotNull(result);
    }

    @Test(expected = DataNotFoundException.class)
    public void testGetUser_NotFound() {

        when(userRepo.findByEmail("missing@mail.com"))
                .thenReturn(null);

        authBO.getUserByEmail("missing@mail.com");
    }
}
