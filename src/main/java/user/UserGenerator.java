package user;

import com.github.javafaker.Faker;

public class UserGenerator {

    private static final Faker faker = new Faker();

    public static String generateUserEmail() {
        return faker.internet().emailAddress();
    }

    public static String generateUserName() {
        return faker.name().fullName();
    }

    public static String generateUserPassword() {
        return faker.internet().password();
    }

}
