import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static com.codeborne.selenide.Selenide.*;

public class InzhenerkaLoginTest {
    @BeforeAll
    public static void setUp() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("src/test/resources/selenide.properties"));
            Configuration.browser = properties.getProperty("selenide.browser");
            Configuration.startMaximized = Boolean.parseBoolean(properties.getProperty("selenide.startMaximized"));
            Configuration.headless = Boolean.parseBoolean(properties.getProperty("selenide.headless"));
            Configuration.remote = properties.getProperty("selenide.remote");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loginTest() {

        // Открыть страницу входа
        Selenide.open("http://qa-stand-login.inzhenerka.tech/login");

        // Ввод логина
        $("#username").setValue("admin");

        // Ввод пароля
        $(By.name("password")).setValue("admin123");

        // Нажатие кнопки входа
        $("button[type='submit']").click();

        // Выход из системы
        $(".mb-3").click();
    }
}