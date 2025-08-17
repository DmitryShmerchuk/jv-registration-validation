package core.basesyntax.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import core.basesyntax.db.Storage;
import core.basesyntax.exception.RegistrationException;
import core.basesyntax.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RegistrationServiceImplTest {

    private static final String VALID_LOGIN = "validLogin";
    private static final String VALID_PASSWORD = "strongPass";
    private static final int MIN_AGE = 18;
    private RegistrationService registrationService;

    @BeforeEach
    void setUp() {
        registrationService = new RegistrationServiceImpl();
        Storage.people.clear();
    }

    @Test
    void register_validUser_Ok() {
        User user = new User();
        user.setLogin(VALID_LOGIN);
        user.setPassword(VALID_PASSWORD);
        user.setAge(30);

        User registered = registrationService.register(user);

        assertNotNull(registered.getId());
        assertEquals(VALID_LOGIN, registered.getLogin());
        assertEquals(VALID_PASSWORD, registered.getPassword());
        assertEquals(30, registered.getAge());
    }

    @Test
    void register_passwordLength6_Ok() {
        User user = new User();
        user.setLogin("login123");
        user.setPassword("abcdef"); // edge case: 6 символів
        user.setAge(25);

        User registered = registrationService.register(user);
        assertNotNull(registered.getId());
        assertEquals("abcdef", registered.getPassword());
    }

    @Test
    void register_age18_Ok() {
        User user = new User();
        user.setLogin("user18");
        user.setPassword("password");
        user.setAge(MIN_AGE); // edge case: 18 років

        User registered = registrationService.register(user);
        assertEquals(MIN_AGE, registered.getAge());
    }

    @Test
    void register_nullUser_notOk() {
        assertThrows(RegistrationException.class,
                () -> registrationService.register(null));
    }

    @Test
    void register_nullLogin_notOk() {
        User user = new User();
        user.setLogin(null);
        user.setPassword(VALID_PASSWORD);
        user.setAge(25);

        assertThrows(RegistrationException.class,
                () -> registrationService.register(user));
    }

    @Test
    void register_loginTooShort_notOk() {
        User user = new User();
        user.setLogin("abc"); // менше 6 символів
        user.setPassword(VALID_PASSWORD);
        user.setAge(25);

        assertThrows(RegistrationException.class,
                () -> registrationService.register(user));
    }

    @Test
    void register_nullPassword_notOk() {
        User user = new User();
        user.setLogin(VALID_LOGIN);
        user.setPassword(null);
        user.setAge(25);

        assertThrows(RegistrationException.class,
                () -> registrationService.register(user));
    }

    @Test
    void register_passwordTooShort_notOk() {
        User user = new User();
        user.setLogin(VALID_LOGIN);
        user.setPassword("abc"); // 3 символи
        user.setAge(25);

        assertThrows(RegistrationException.class,
                () -> registrationService.register(user));
    }

    @Test
    void register_passwordLength5_notOk() {
        User user = new User();
        user.setLogin(VALID_LOGIN);
        user.setPassword("abcde"); // edge case: 5 символів
        user.setAge(25);

        assertThrows(RegistrationException.class,
                () -> registrationService.register(user));
    }

    @Test
    void register_nullAge_notOk() {
        User user = new User();
        user.setLogin(VALID_LOGIN);
        user.setPassword(VALID_PASSWORD);
        user.setAge(null);

        assertThrows(RegistrationException.class,
                () -> registrationService.register(user));
    }

    @Test
    void register_negativeAge_notOk() {
        User user = new User();
        user.setLogin(VALID_LOGIN);
        user.setPassword(VALID_PASSWORD);
        user.setAge(-5);

        assertThrows(RegistrationException.class,
                () -> registrationService.register(user));
    }

    @Test
    void register_ageTooYoung_notOk() {
        User user = new User();
        user.setLogin(VALID_LOGIN);
        user.setPassword(VALID_PASSWORD);
        user.setAge(16);

        assertThrows(RegistrationException.class,
                () -> registrationService.register(user));
    }

    @Test
    void register_duplicateLogin_notOk() {
        User existing = new User();
        existing.setLogin("duplicateLogin");
        existing.setPassword("pass1234");
        existing.setAge(25);
        Storage.people.add(existing); // додаємо напряму

        User newUser = new User();
        newUser.setLogin("duplicateLogin"); // той самий логін
        newUser.setPassword("anotherPass");
        newUser.setAge(30);

        assertThrows(RegistrationException.class,
                () -> registrationService.register(newUser));
    }
}
