package com.amebas.healthport;

import com.amebas.healthport.Model.Account;
import com.amebas.healthport.Model.DatabaseManager;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(JUnit4.class)
@PrepareForTest({ FirebaseFirestore.class})
public class DatabaseManagerUnitTest {


    @Before
    public void before() {
        FirebaseFirestore mockedFirebaseFirestore = Mockito.mock(FirebaseFirestore.class);
        PowerMockito.mockStatic(FirebaseFirestore.class);
        when(FirebaseFirestore.getInstance()).thenReturn(mockedFirebaseFirestore);
    }

    @Test
    public void test_AddAccount() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DatabaseManager dbManager = new DatabaseManager(db);

        dbManager.addAccount(new Account("testEmail", "testPassword", "profiles"));

        assertEquals(dbManager.getAccount(
                "testEmail", "testPassword"),
                new Account("testEmail", "testPassword", "profiles"));
    }
}
