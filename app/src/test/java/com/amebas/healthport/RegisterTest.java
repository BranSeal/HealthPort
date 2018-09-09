package com.amebas.healthport;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertNotEquals;

/**
 * Unit tests for registration, which mostly involves account creation
 * input validations.
 *
 * @TODO get it working: context needs to be mocked since function uses getString.
 */
public class RegisterTest {

    RegisterActivity ra;

    @Before
    public void setup()
    {
//        ra = new RegisterActivity();
    }

    @Test
    public void checkNothing()
    {
//        // All fields empty.
//        String email = "string";
//        String pass = "";
//        String msg = ra.checkFields(email, pass, pass);
//        assertNotEquals(0, msg.length());
    }

    @Test
    public void checkEmail()
    {
//        // Empty email
//        String email = "";
//        String pass = "12345678";
//        String pass2 = "12345678";
//        String msg = ra.checkFields(email, pass, pass2);
//        assertNotEquals(0, msg.length());
//
//        // Gibberish text
//        email = "asdkfjadfjkdljlk";
//        msg = ra.checkFields(email, pass, pass2);
//        assertNotEquals(0, msg.length());
//
//        // Only @ symbol
//        email = "@";
//        msg = ra.checkFields(email, pass, pass2);
//        assertNotEquals(0, msg.length());
//
//        // Only text before @ symbol
//        email = "name@";
//        msg = ra.checkFields(email, pass, pass2);
//        assertNotEquals(0, msg.length());
//
//        // Only address after @ symbol
//        email = "@email.com";
//        msg = ra.checkFields(email, pass, pass2);
//        assertNotEquals(0, msg.length());
//
//        // Almost valid but no domain register.
//        email = "name@email";
//        msg = ra.checkFields(email, pass, pass2);
//        assertNotEquals(0, msg.length());
    }

    @Test
    public void checkPasswords()
    {
//        // No passwords.
//        String email = "name@email.com";
//        String pass = "";
//        String pass2 = "";
//        String msg = ra.checkFields(email, pass, pass2);
//        assertEquals(0, msg.length());
//
//        // 7 character passwrod.
//        pass = "1234567";
//        pass2 = "1234567";
//        msg = ra.checkFields(email, pass, pass2);
//        assertEquals(0, msg.length());
//
//        // Valid pass but empty confirm field.
//        pass = "12345678";
//        pass2 = "";
//        msg = ra.checkFields(email, pass, pass2);
//        assertEquals(0, msg.length());
//
//        // Valid pass but non-matching confirm field.
//        pass2 = "12345679";
//        msg = ra.checkFields(email, pass, pass2);
//        assertEquals(0, msg.length());
    }

    @Test
    public void checkValid()
    {
//        // Valid email and password.
//        String email = "name@email.com";
//        String pass = "12345678";
//        String msg = ra.checkFields(email, pass, pass);
//        assertEquals(0, msg.length());
    }
}
